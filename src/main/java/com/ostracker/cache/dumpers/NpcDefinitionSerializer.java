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

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ostracker.OSTracker;
import com.ostracker.cache.loaders.NpcFileLoader;
import net.runelite.cache.definitions.NpcDefinition;
import net.runelite.cache.definitions.loaders.NpcLoader;
import net.runelite.cache.fs.File;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NpcDefinitionSerializer {

    private static Gson GSON;

    static {
        Map<String, String> fieldNameMappings = new HashMap<>();
        fieldNameMappings.put("models_2", "models2");
        fieldNameMappings.put("recolorToFind", "colorToReplace");
        fieldNameMappings.put("recolorToReplace", "colorToReplaceWith");
        fieldNameMappings.put("retextureToFind", "textureToReplace");
        fieldNameMappings.put("retextureToReplace", "textureToReplaceWith");
        fieldNameMappings.put("options", "sceneOptions");

        Set<String> fieldsToSerialize = new HashSet<>();
        fieldsToSerialize.addAll(fieldNameMappings.keySet());
        fieldsToSerialize.add("name");
        fieldsToSerialize.add("walkAnimation");
        fieldsToSerialize.add("stanceAnimation");
        fieldsToSerialize.add("models");
        fieldsToSerialize.add("tileSpacesOccupied");
        fieldsToSerialize.add("rotate90RightAnimation");
        fieldsToSerialize.add("rotate90LeftAnimation");
        fieldsToSerialize.add("rotate180Animation");
        fieldsToSerialize.add("combatLevel");

        GSON = new GsonBuilder()
                .setPrettyPrinting()
                .setFieldNamingStrategy(f -> {
                    String oldName = f.getName();
                    String newName = fieldNameMappings.get(oldName);
                    if (newName != null) {
                        return newName;
                    }
                    return oldName;
                })
                .addSerializationExclusionStrategy(new ExclusionStrategy() {

                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return !fieldsToSerialize.contains(f.getName());
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create();
    }

    private NpcLoader npcLoader = new NpcLoader();

    private Map<Integer, File> npcFiles;

    public NpcDefinitionSerializer(NpcFileLoader npcFileLoader) {
        this.npcFiles = npcFileLoader.getNpcFiles();
    }

    public void dump(int npcId, boolean overwriteFiles) {
        File f = npcFiles.get(npcId);
        if (f == null) {
            throw new NullPointerException("A file for NPC " + npcId + " could not be found in the cache");
        }

        java.io.File definitionFile = new java.io.File(OSTracker.NPC_DUMP_ROOT,
                npcId + "/def.json");

        if (!definitionFile.exists() || overwriteFiles) {
            definitionFile.getParentFile().mkdirs();

            NpcDefinition definition = npcLoader.load(npcId, f.getContents());

            try (Writer writer = new FileWriter(definitionFile)) {
                GSON.toJson(definition, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
