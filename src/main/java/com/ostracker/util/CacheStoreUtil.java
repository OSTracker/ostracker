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

package com.ostracker.util;

import net.runelite.cache.fs.Index;
import net.runelite.cache.fs.Store;

import java.io.File;
import java.util.Optional;

public class CacheStoreUtil {

    public static int getCacheVersion(Store store) {
        int revision = 0;

        for (Index index : store.getIndexes()) {
            revision += index.getRevision();
        }

        return revision;
    }

    public static Optional<Integer> getLatestCacheInFolder(File folder) {
        File[] caches = folder.listFiles();

        boolean foundCache = false;
        int latestVersion = 0;

        if (caches != null
                && caches.length > 0) {

            for (File f : caches) {
                if (f.isDirectory()) {
                    try {
                        int version = Integer.parseInt(f.getName());

                        if (!foundCache || version > latestVersion) {
                            latestVersion = version;
                            foundCache = true;
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }

        if (foundCache) {
            return Optional.of(latestVersion);
        }

        return Optional.empty();
    }
}
