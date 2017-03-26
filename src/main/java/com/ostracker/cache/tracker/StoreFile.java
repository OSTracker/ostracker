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

package com.ostracker.cache.tracker;

import net.runelite.cache.fs.Archive;
import net.runelite.cache.fs.File;

import java.util.Arrays;

public class StoreFile {

    private int id;
    private int archiveId;
    private int hash;
    private byte[] data;

    public StoreFile(File file, Archive archive) {
        this.id = file.getFileId();
        this.archiveId = archive.getArchiveId();
        this.hash = file.getNameHash();
        this.data = file.getContents();
    }

    public int getId() {
        return id;
    }

    public int getArchiveId() {
        return archiveId;
    }

    public int getHash() {
        return hash;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StoreFile storeFile = (StoreFile) o;

        return id == storeFile.id
                && archiveId == storeFile.archiveId
                && hash == storeFile.hash
                && Arrays.equals(data, storeFile.data);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + archiveId;
        result = 31 * result + hash;
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }
}
