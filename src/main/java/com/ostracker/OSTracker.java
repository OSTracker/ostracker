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

import com.google.gson.*;
import com.ostracker.cache.RemoteCache;
import com.ostracker.cache.dumpers.SpriteDumper;
import com.ostracker.cache.loaders.ItemFileLoader;
import com.ostracker.cache.loaders.ModelFileLoader;
import com.ostracker.cache.tracker.ItemDefinitionTracker;
import com.ostracker.cache.tracker.ModelDefinitionTracker;
import com.ostracker.cache.tracker.StoreFileTracker;
import com.ostracker.util.CacheStoreUtil;
import com.ostracker.util.FileUtil;
import com.ostracker.util.GameConnectionUtil;
import net.runelite.cache.fs.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OSTracker {

    private static final Logger LOGGER = LoggerFactory.getLogger(OSTracker.class);

    public static final File CACHE_STORE_ROOT = new File("cachestore");
    /**
     * Live cache store is where the cache that is being handled is copied into and then worked on.
     */
    public static final File LIVE_CACHE_STORE = new File(CACHE_STORE_ROOT, "LIVE");

    public static final File WEB_ROOT = new File("web");
    public static final File CHANGES_ROOT = new File(WEB_ROOT,"changes");
    public static final File ITEM_DUMP_ROOT = new File(WEB_ROOT, "items");
    public static final File MODEL_DUMP_ROOT = new File(WEB_ROOT, "models");
    public static final File SPRITE_DUMP_ROOT = new File(WEB_ROOT, "sprites");

    public static final File VERSIONS_FILE = new File(CHANGES_ROOT, "versions.json");

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

            StoreFileTracker newFileTracker = new StoreFileTracker();
            newFileTracker.load(cacheStore);

            Map<String, JsonObject> changeLogParts = new HashMap<>();

            // Dump item definitions for items that have changed
            ItemFileLoader itemFileLoader = new ItemFileLoader(cacheStore);

            ItemDefinitionTracker itemDefinitionTracker = new ItemDefinitionTracker(itemFileLoader);
            changeLogParts.put("items", itemDefinitionTracker.run(fileTracker, newFileTracker));

            // Dump models that have changed
            ModelFileLoader modelFileLoader = new ModelFileLoader(cacheStore);

            ModelDefinitionTracker modelDefinitionTracker = new ModelDefinitionTracker(modelFileLoader);
            changeLogParts.put("models", modelDefinitionTracker.run(fileTracker, newFileTracker));

            // Dump sprites
            SpriteDumper spriteDumper = new SpriteDumper(modelFileLoader);

            for (Integer spriteId : modelFileLoader.getSpriteFiles().keySet()) {
                spriteDumper.dump(spriteId);
            }

            dumpVersionFile(cacheVersion, changeLogParts);
            updateVersionsFile(cacheVersion);

            cacheStore.close();

            putLiveCacheInCacheStore(cacheVersion);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void dumpVersionFile(int cacheVersion,
                                        Map<String, JsonObject> changeLogParts) {

        JsonObject root = new JsonObject();

        changeLogParts.forEach(root::add);

        CHANGES_ROOT.mkdirs();

        try (Writer writer = new FileWriter(new File(CHANGES_ROOT, cacheVersion + ".json"))) {
            writer.write(GSON.toJson(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void updateVersionsFile(int cacheVersion) throws FileNotFoundException {
        JsonArray versions;

        if (VERSIONS_FILE.exists()) {
            versions = new JsonParser()
                    .parse(new FileReader(VERSIONS_FILE))
                    .getAsJsonArray();
        } else {
            versions = new JsonArray();
        }

        for (JsonElement version : versions) {
            if (version
                    .getAsJsonObject()
                    .getAsJsonPrimitive("version")
                    .getAsInt() == cacheVersion) {
                return;
            }
        }

        JsonObject version = new JsonObject();

        version.addProperty("version", cacheVersion);
        version.addProperty("time", new Date().getTime() / 1000);

        versions.add(version);

        try (Writer writer = new FileWriter(VERSIONS_FILE)) {
            writer.write(GSON.toJson(versions));
        } catch (IOException e) {
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
