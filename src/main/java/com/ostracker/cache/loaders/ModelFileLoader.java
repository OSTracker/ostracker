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
import net.runelite.cache.fs.Store;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ModelFileLoader {

    private Map<Integer, File> modelFiles = new HashMap<>();
    private Map<Integer, File> textureFiles;
    private Map<Integer, File> spriteFiles = new HashMap<>();

    public ModelFileLoader(Store store) {
        for (Archive archive : store
                .getIndex(IndexType.MODELS)
                .getArchives()) {

            this.modelFiles.put(archive.getArchiveId(),
                    archive.getFiles().get(0));
        }

        this.textureFiles = store
                .getIndex(IndexType.TEXTURES)
                .getArchive(0)
                .getFiles()
                .stream()
                .collect(Collectors.toMap(File::getFileId, Function.identity()));

        for (Archive archive : store
                .getIndex(IndexType.SPRITES)
                .getArchives()) {

            this.spriteFiles.put(archive.getArchiveId(),
                    archive.getFiles().get(0));
        }
    }

    public Map<Integer, File> getModelFiles() {
        return modelFiles;
    }

    public Map<Integer, File> getTextureFiles() {
        return textureFiles;
    }

    public Map<Integer, File> getSpriteFiles() {
        return spriteFiles;
    }
}
