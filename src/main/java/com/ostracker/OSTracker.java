/*
 * Copyright (C) 2017 OSTracker
 *
 * This file is a part of OSTracker.
 *
 * OSTracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OSTracker is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OSTracker. If not, see <http://www.gnu.org/licenses/>.
 */

package com.ostracker;

import com.google.common.base.Stopwatch;
import com.ostracker.cache.RemoteCache;
import com.ostracker.cache.dumpers.*;
import com.ostracker.cache.loaders.ItemFileLoader;
import com.ostracker.cache.loaders.ModelFileLoader;
import com.ostracker.cache.loaders.NpcFileLoader;
import com.ostracker.cache.loaders.TrackFileLoader;
import com.ostracker.util.CacheStoreUtil;
import com.ostracker.util.FileUtil;
import com.ostracker.util.GameConnectionUtil;
import net.runelite.cache.fs.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class OSTracker {

    private static final Logger LOGGER = LoggerFactory.getLogger(OSTracker.class);

    public static final File CACHE_STORE_ROOT = new File("cachestore");
    /**
     * Live cache store is where the cache that is being handled is copied into and then worked on.
     */
    public static final File LIVE_CACHE_STORE = new File(CACHE_STORE_ROOT, "LIVE");

    public static final File FILES_ROOT = new File("files");
    public static final File ITEM_DUMP_ROOT = new File(FILES_ROOT, "items");
    public static final File NPC_DUMP_ROOT = new File(FILES_ROOT, "npcs");
    public static final File MODEL_DUMP_ROOT = new File(FILES_ROOT, "models");
    public static final File SPRITE_DUMP_ROOT = new File(FILES_ROOT, "sprites");
    public static final File TRACKS1_DUMP_ROOT = new File(FILES_ROOT, "tracks1");
    public static final File TRACKS2_DUMP_ROOT = new File(FILES_ROOT, "tracks2");

    /**
     * -s [version] = load a cache from the cache store.
     * If this argument isn't provided, it either loads the latest cache in
     * the cachestore or loads it from user.home/jagexcache/oldschool/LIVE
     *
     * -o = dump a file even if it has already been dumped
     */
    public static void main(String[] args) {
        try {
            String sourceVersion = null;
            AtomicBoolean overwriteFiles = new AtomicBoolean();

            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "-s":
                        sourceVersion = args[i + 1];
                        break;

                    case "-o":
                        overwriteFiles.set(true);
                        break;
                }
            }

            if (sourceVersion == null) {
                Optional<Integer> latestCache = CacheStoreUtil
                        .getLatestCacheInFolder(CACHE_STORE_ROOT);

                if (latestCache.isPresent()) {
                    putCacheInLiveStore("" + latestCache.get());
                } else {
                    putHomeCacheInLiveStore();
                }
            } else {
                putCacheInLiveStore(sourceVersion);
            }

            Store cacheStore = new Store(LIVE_CACHE_STORE);
            cacheStore.load();

            String worldHost = GameConnectionUtil
                    .getBestWorldHost();

            RemoteCache remoteCache = new RemoteCache();

            remoteCache.connect(worldHost, GameConnectionUtil.GAME_PORT);
            remoteCache.shakeHands(GameConnectionUtil.getRevision(worldHost));
            remoteCache.download(cacheStore);
            remoteCache.close();

            cacheStore.save();
            cacheStore.close();

            // Reload cache to update file contents
            cacheStore = new Store(LIVE_CACHE_STORE);
            cacheStore.load();

            // Dump item definitions
            ItemFileLoader itemFileLoader = new ItemFileLoader(cacheStore);
            ItemDefinitionSerializer itemDefinitionSerializer = new ItemDefinitionSerializer(itemFileLoader);

            Stopwatch itemDefinitionStopwatch = Stopwatch.createStarted();

            itemFileLoader
                    .getItemIds()
                    .parallelStream()
                    .forEach(itemId -> itemDefinitionSerializer.dump(itemId, overwriteFiles.get()));

            itemDefinitionStopwatch.stop();

            // Dump NPC definitions
            NpcFileLoader npcFileLoader = new NpcFileLoader(cacheStore);
            NpcDefinitionSerializer npcDefinitionSerializer = new NpcDefinitionSerializer(npcFileLoader);

            Stopwatch npcDefinitionStopwatch = Stopwatch.createStarted();

            npcFileLoader
                    .getNpcIds()
                    .parallelStream()
                    .forEach(npcId -> npcDefinitionSerializer.dump(npcId, overwriteFiles.get()));

            npcDefinitionStopwatch.stop();

            // Dump models
            ModelFileLoader modelFileLoader = new ModelFileLoader(cacheStore);
            ModelConverter modelConverter = new ModelConverter(modelFileLoader);

            Stopwatch modelStopwatch = Stopwatch.createStarted();

            modelFileLoader
                    .getModelFiles()
                    .keySet()
                    .parallelStream()
                    .forEach(modelId -> modelConverter.convert(modelId, overwriteFiles.get()));

            modelStopwatch.stop();

            // Dump sprites
            SpriteDumper spriteDumper = new SpriteDumper(modelFileLoader);

            Stopwatch spriteStopwatch = Stopwatch.createStarted();

            modelFileLoader
                    .getSpriteFiles()
                    .keySet()
                    .parallelStream()
                    .forEach(spriteId -> spriteDumper.dump(spriteId, overwriteFiles.get()));

            spriteStopwatch.stop();

            // Dump tracks
            TrackFileLoader trackFileLoader = new TrackFileLoader(cacheStore);
            TrackDumper trackDumper = new TrackDumper(trackFileLoader);

            Stopwatch trackStopwatch = Stopwatch.createStarted();

            trackFileLoader
                    .getTrack1Files()
                    .keySet()
                    .parallelStream()
                    .forEach(trackHash -> trackDumper.dump(trackHash, true, overwriteFiles.get()));

            trackFileLoader
                    .getTrack2Files()
                    .keySet()
                    .parallelStream()
                    .forEach(trackHash -> trackDumper.dump(trackHash, false, overwriteFiles.get()));

            trackStopwatch.stop();

            LOGGER.info("Spent " + itemDefinitionStopwatch + " dumping item definitions");
            LOGGER.info("Spent " + npcDefinitionStopwatch + " dumping npc definitions");
            LOGGER.info("Spent " + modelStopwatch + " dumping models");
            LOGGER.info("Spent " + spriteStopwatch + " dumping sprites");
            LOGGER.info("Spent " + trackStopwatch + " dumping tracks");

            int cacheVersion = CacheStoreUtil
                    .getCacheVersion(cacheStore);

            cacheStore.close();

            putLiveCacheInCacheStore(cacheVersion);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void putHomeCacheInLiveStore() throws IOException {
        // Clean live cache store
        File[] liveCacheStoreFiles = LIVE_CACHE_STORE.listFiles();

        if (liveCacheStoreFiles != null
                && liveCacheStoreFiles.length > 0) {

            for (File f : liveCacheStoreFiles) {
                f.delete();
            }
        }

        File userHomeCache = new File(System.getProperty("user.home") + "/jagexcache/oldschool/LIVE");

        if (userHomeCache.exists()) {
            File[] cacheFiles = userHomeCache.listFiles();

            if (cacheFiles != null) {
                LOGGER.info("Putting " + userHomeCache + " cache in live store");

                FileUtil.copyFiles(cacheFiles, LIVE_CACHE_STORE);
            }
        } else {
            throw new IllegalStateException("Cache missing from " + userHomeCache + ". Please run the OSRS client.");
        }
    }

    private static void putCacheInLiveStore(String version) throws IOException {
        // Clean live cache store
        File[] liveCacheStoreFiles = LIVE_CACHE_STORE.listFiles();

        if (liveCacheStoreFiles != null
                && liveCacheStoreFiles.length > 0) {

            for (File f : liveCacheStoreFiles) {
                f.delete();
            }
        }

        File cacheStoreCache = new File(CACHE_STORE_ROOT, version);

        if (cacheStoreCache.exists()) {
            File[] cacheStoreFiles = cacheStoreCache.listFiles();

            if (cacheStoreFiles != null) {
                LOGGER.info("Putting cache " + version + " in live store");

                FileUtil.copyFiles(cacheStoreFiles, LIVE_CACHE_STORE);
            }
        } else {
            throw new IllegalStateException("Cache missing from " + cacheStoreCache);
        }
    }

    private static void putLiveCacheInCacheStore(int version) throws IOException {
        File[] liveStoreFiles = LIVE_CACHE_STORE.listFiles();

        if (liveStoreFiles != null) {
            File targetFolder = new File(CACHE_STORE_ROOT, "" + version);

            if (!targetFolder.exists()) {
                LOGGER.info("Dumping cache " + version + " to store");

                FileUtil.copyFiles(liveStoreFiles, targetFolder);
            }
        }
    }
}
