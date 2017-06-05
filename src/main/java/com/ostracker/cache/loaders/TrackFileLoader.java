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

import net.runelite.cache.IndexType;
import net.runelite.cache.fs.Archive;
import net.runelite.cache.fs.File;
import net.runelite.cache.fs.Index;
import net.runelite.cache.fs.Store;

import java.util.HashMap;
import java.util.Map;

public class TrackFileLoader {

    private Map<Integer, File> track1Files = new HashMap<>();
    private Map<Integer, File> track2Files = new HashMap<>();

    public TrackFileLoader(Store store) {
        Index trackIndex = store.getIndex(IndexType.TRACK1);

        for (Archive archive : trackIndex.getArchives()) {
            File file = archive
                    .getFiles()
                    .get(0);

            this.track1Files.put(archive.getArchiveId(), file);
        }

        Index track2Index = store.getIndex(IndexType.TRACK2);

        for (Archive archive : track2Index.getArchives()) {
            File file = archive
                    .getFiles()
                    .get(0);

            this.track2Files.put(archive.getArchiveId(), file);
        }
    }

    public Map<Integer, File> getTrack1Files() {
        return track1Files;
    }

    public Map<Integer, File> getTrack2Files() {
        return track2Files;
    }
}
