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
import com.google.gson.annotations.Expose;
import com.ostracker.OSTracker;
import com.ostracker.cache.dumpers.ItemDefinitionSerializer;
import com.ostracker.cache.loaders.ItemFileLoader;
import net.runelite.cache.ConfigType;
import net.runelite.cache.IndexType;
import net.runelite.cache.definitions.ItemDefinition;
import net.runelite.cache.definitions.loaders.ItemLoader;

import java.util.Arrays;

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

                    ItemLoader itemLoader = new ItemLoader();
                    ItemDefinition definition = itemLoader.load(itemId, f.getData());
                    ItemDefinition definition2 = itemLoader.load(itemId, f2.getData());

                    JsonObject itemObject = new JsonObject();

                    Arrays.stream(ItemDefinition.class.getDeclaredFields())
                            .filter(field -> field.isAnnotationPresent(Expose.class))
                            .forEach(field -> {
                                try {
                                    Object o = field.get(definition);
                                    Object o2 = field.get(definition2);

                                    if (!o.equals(o2)) {
                                        JsonObject differenceObject = new JsonObject();

                                        differenceObject.add("old", OSTracker.GSON.toJsonTree(o));
                                        differenceObject.add("new", OSTracker.GSON.toJsonTree(o2));

                                        itemObject.add(field.getName(), differenceObject);
                                    }
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            });

                    changedItems.add("" + itemId, itemObject);
                });

        rootObject.add("changed", changedItems);

        return rootObject;
    }
}
