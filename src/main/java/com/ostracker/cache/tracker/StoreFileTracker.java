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
import net.runelite.cache.fs.Index;
import net.runelite.cache.fs.Store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StoreFileTracker {

    private Map<Integer, List<StoreFile>> files = new HashMap<>();

    public void load(Store store) {
        for (Index index : store.getIndexes()) {
            List<Archive> archives = index.getArchives();

            boolean singleFileArchives = true;

            for (Archive archive : archives) {
                if (archive.getFiles().size() != 1) {
                    singleFileArchives = false;
                    break;
                }
            }

            if (singleFileArchives) {
                List<StoreFile> archiveFiles = new ArrayList<>();

                archives.forEach(a -> archiveFiles.add(new StoreFile(a.getFiles().get(0), a)));

                files.put(index.getId() << 16, archiveFiles);
            } else {
                for (Archive archive : archives) {
                    List<StoreFile> archiveFiles = archive
                            .getFiles()
                            .stream()
                            .map(f -> new StoreFile(f, archive))
                            .collect(Collectors.toList());

                    int hash = index.getId() << 16 | archive.getArchiveId();

                    files.put(hash, archiveFiles);
                }
            }
        }
    }

    public List<StoreFile> getFiles(int indexId, int archiveId) {
        int hash = indexId << 16 | archiveId;

        if (!files.containsKey(hash)) {
            throw new IllegalStateException("That archive has not been loaded");
        }

        return files.get(hash);
    }

    public static List<StoreFile> getAddedFiles(StoreFileTracker tracker, StoreFileTracker tracker2,
                                                int indexId, int archiveId) {

        List<StoreFile> files = tracker.getFiles(indexId, archiveId);
        List<StoreFile> files2 = tracker2.getFiles(indexId, archiveId);

        if (files.size() <= files2.size()) {
            return new ArrayList<>();
        }

        List<StoreFile> addedFiles = new ArrayList<>();

        for (int i = files.size() - 1; i < files2.size(); i++) {
            addedFiles.add(files2.get(i));
        }

        return addedFiles;
    }

    public static Map<StoreFile, StoreFile> getModifiedFiles(StoreFileTracker tracker, StoreFileTracker tracker2,
                                                             int indexId, int archiveId) {

        List<StoreFile> files = tracker.getFiles(indexId, archiveId);
        List<StoreFile> files2 = tracker2.getFiles(indexId, archiveId);

        Map<StoreFile, StoreFile> modifiedFiles = new HashMap<>();

        for (int i = 0; i < files.size(); i++) {
            StoreFile file = files.get(i);
            StoreFile file2 = files2.get(i);

            if (!file.equals(file2)) {
                modifiedFiles.put(file, file2);
            }
        }

        return modifiedFiles;
    }
}
