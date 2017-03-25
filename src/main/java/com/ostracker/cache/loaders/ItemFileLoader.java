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

package com.ostracker.cache.loaders;

import net.runelite.cache.ConfigType;
import net.runelite.cache.IndexType;
import net.runelite.cache.fs.Archive;
import net.runelite.cache.fs.File;
import net.runelite.cache.fs.Index;
import net.runelite.cache.fs.Store;

import java.util.List;
import java.util.stream.Collectors;

public class ItemFileLoader {

    private List<File> itemFiles;

    public ItemFileLoader(Store store) {
        Index configIndex = store.getIndex(IndexType.CONFIGS);
        Archive itemArchive = configIndex.getArchive(ConfigType.ITEM.getId());
        this.itemFiles = itemArchive.getFiles();
    }

    public List<File> getItemFiles() {
        return itemFiles;
    }

    public List<Integer> getItemIds() {
        return itemFiles
                .stream()
                .map(File::getFileId)
                .collect(Collectors.toList());
    }
}
