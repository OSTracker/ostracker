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
import com.ostracker.cache.dumpers.ItemDefinitionSerializer;
import com.ostracker.cache.loaders.ItemFileLoader;
import net.runelite.cache.ConfigType;
import net.runelite.cache.IndexType;

public class ItemDefinitionTracker {

    private ItemDefinitionSerializer itemDefinitionSerializer;

    public ItemDefinitionTracker(ItemFileLoader itemFileLoader) {
        this.itemDefinitionSerializer = new ItemDefinitionSerializer(itemFileLoader);
    }

    public JsonObject run(StoreFileTracker tracker, StoreFileTracker tracker2) {
        JsonObject rootObject = new JsonObject();

        // Added items
        JsonArray addedItems = new JsonArray();

        StoreFileTracker
                .getAddedFiles(tracker, tracker2, IndexType.CONFIGS.getNumber(), ConfigType.ITEM.getId())
                .forEach(f -> {
                    int itemId = f.getId();

                    itemDefinitionSerializer.dump(itemId);
                    addedItems.add(itemId);
                });

        rootObject.add("added", addedItems);

        // Changed items
        JsonObject changedItems = new JsonObject();

        StoreFileTracker
                .getModifiedFiles(tracker, tracker2, IndexType.CONFIGS.getNumber(), ConfigType.ITEM.getId())
                .forEach((f, f2) -> {
                    int itemId = f.getId();

                    itemDefinitionSerializer.dump(itemId);
                    // TODO: implement definition change output
                });

        rootObject.add("changed", changedItems);

        return rootObject;
    }
}
