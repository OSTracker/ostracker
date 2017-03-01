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

package com.ostracker.cache.dumpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ostracker.cache.loaders.ItemDefinition;
import net.runelite.cache.ConfigType;
import net.runelite.cache.IndexType;
import net.runelite.cache.fs.Archive;
import net.runelite.cache.fs.Index;
import net.runelite.cache.fs.Store;
import net.runelite.cache.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class ItemDefinitionDumper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemDefinitionDumper.class);

    private File folder;

    public ItemDefinitionDumper(File folder) {
        this.folder = folder;

        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                throw new IllegalStateException("Failed to create " + folder);
            }
        }
    }

    public void dump(Store store) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        Index index = store.getIndex(IndexType.CONFIGS);
        Archive archive = index.getArchive(ConfigType.ITEM.getId());

        for (net.runelite.cache.fs.File file : archive.getFiles()) {
            ItemDefinition definition = new ItemDefinition(file.getFileId());
            definition.load(new InputStream(file.getContents()));

            File definitionFile = new File(folder, definition.id + ".json");

            LOGGER.info("Dumping item definition " + definition.id + " to " + definitionFile);

            try (Writer writer = new FileWriter(definitionFile)) {
                gson.toJson(definition, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
