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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ostracker.cache.dumpers.ModelConverter;
import com.ostracker.cache.loaders.ModelFileLoader;
import net.runelite.cache.IndexType;

public class ModelDefinitionTracker {

    private ModelConverter modelConverter;

    public ModelDefinitionTracker(ModelFileLoader modelFileLoader) {
        this.modelConverter = new ModelConverter(modelFileLoader);
    }

    public JsonObject run(StoreFileTracker tracker, StoreFileTracker tracker2) {
        JsonObject rootObject = new JsonObject();

        // Added models
        JsonArray addedItems = new JsonArray();

        StoreFileTracker
                .getAddedFiles(tracker, tracker2, IndexType.MODELS.getNumber(), 0)
                .forEach(f -> {
                    int modelId = f.getArchiveId();

                    modelConverter.convert(modelId);
                    addedItems.add(modelId);
                });

        rootObject.add("added", addedItems);

        // Changed models
        JsonObject changedItems = new JsonObject();

        StoreFileTracker
                .getModifiedFiles(tracker, tracker2, IndexType.MODELS.getNumber(), 0)
                .forEach((f, f2) -> {
                    int modelId = f.getArchiveId();

                    modelConverter.convert(modelId);
                    // TODO: implement definition change output
                });

        rootObject.add("changed", changedItems);

        return rootObject;
    }
}
