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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ostracker.cache.RemoteCache;
import com.ostracker.cache.dumpers.SpriteDumper;
import com.ostracker.cache.loaders.ModelFileLoader;
import com.ostracker.cache.tracker.StoreFileTracker;
import com.ostracker.util.CacheStoreUtil;
import com.ostracker.util.FileUtil;
import com.ostracker.util.GameConnectionUtil;
import net.runelite.cache.fs.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class OSTracker {

    private static final Logger LOGGER = LoggerFactory.getLogger(OSTracker.class);

    public static final File CACHE_STORE_ROOT = new File("cachestore");
    /**
     * Live cache store is where the cache that is being handled is copied into and then worked on.
     */
    public static final File LIVE_CACHE_STORE = new File(CACHE_STORE_ROOT, "LIVE");

    public static final File WEB_ROOT = new File("web");
    public static final File ITEM_DUMP_ROOT = new File(WEB_ROOT, "items");
    public static final File MODEL_DUMP_ROOT = new File(WEB_ROOT, "models");
    public static final File SPRITE_DUMP_ROOT = new File(WEB_ROOT, "sprites");

    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    /**
     * args[0] = source version [optional], if not provided, latest is compared to remote
     * args[1] = target version [optional], if not provided, source is compared to remote
     */
    public static void main(String[] args) {
        try {
            File targetStore = null;

            if (args.length > 0) {
                putCacheInLiveStore(args[0]);

                if (args.length > 1) {
                    LOGGER.info("Target cache store is " + args[1]);

                    targetStore = new File(CACHE_STORE_ROOT, args[1]);
                }
            } else {
                putCacheInLiveStore("" + CacheStoreUtil.getLatestCacheInFolder(CACHE_STORE_ROOT));
            }

            Store cacheStore = new Store(LIVE_CACHE_STORE);
            cacheStore.load();

            StoreFileTracker fileTracker = new StoreFileTracker();
            fileTracker.load(cacheStore);

            String worldHost = GameConnectionUtil
                    .getBestWorldHost();

            if (targetStore == null) {
                RemoteCache remoteCache = new RemoteCache();

                remoteCache.connect(worldHost, GameConnectionUtil.GAME_PORT);
                remoteCache.shakeHands(GameConnectionUtil.getRevision(worldHost));
                remoteCache.download(cacheStore);
                remoteCache.close();

                cacheStore.save();
            }

            cacheStore.close();

            // Reload cache to update file contents
            cacheStore = new Store(targetStore != null ? targetStore : LIVE_CACHE_STORE);
            cacheStore.load();

            int cacheVersion = CacheStoreUtil
                    .getCacheVersion(cacheStore);

            ModelFileLoader modelFileLoader = new ModelFileLoader(cacheStore);

            SpriteDumper spriteDumper = new SpriteDumper(modelFileLoader);

            for (Integer spriteId : modelFileLoader.getSpriteFiles().keySet()) {
                spriteDumper.dump(spriteId);
            }

            StoreFileTracker newFileTracker = new StoreFileTracker();
            newFileTracker.load(cacheStore);

            cacheStore.close();

            putLiveCacheInCacheStore(cacheVersion);
        } catch (Exception e) {
            e.printStackTrace();
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

        File[] cacheStoreFiles = new File(CACHE_STORE_ROOT, version).listFiles();

        if (cacheStoreFiles != null) {
            LOGGER.info("Putting cache " + version + " in live store");

            FileUtil.copyFiles(cacheStoreFiles, LIVE_CACHE_STORE);
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
