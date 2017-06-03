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

import com.ostracker.cache.loaders.ItemFileLoader;
import com.ostracker.rs.*;
import net.runelite.cache.IndexType;
import net.runelite.cache.definitions.ItemDefinition;
import net.runelite.cache.definitions.loaders.ItemLoader;
import net.runelite.cache.fs.File;
import net.runelite.cache.fs.Index;
import net.runelite.cache.fs.Store;

import java.awt.image.BufferedImage;
import java.util.Map;

public class ItemRasterizer {

    private static final int WIDTH = 36;
    private static final int HEIGHT = 32;

    private static final double BRIGHTNESS_LEVEL = 0.6D;

    private Map<Integer, File> itemFiles;
    private Index modelIndex;

    private ItemLoader itemLoader = new ItemLoader();

    public ItemRasterizer(ItemFileLoader itemFileLoader, Store store) {
        this.itemFiles = itemFileLoader.getItemFiles();
        this.modelIndex = store.getIndex(IndexType.MODELS);

        Index textureIndex = store.getIndex(IndexType.TEXTURES);
        Index spriteIndex = store.getIndex(IndexType.SPRITES);

        Rasterizer3D.field1457 = new TextureProvider(textureIndex, spriteIndex, 20, BRIGHTNESS_LEVEL, 128);
        Rasterizer3D.method1728(BRIGHTNESS_LEVEL);
    }

    public BufferedImage rasterize(int itemId) {
        ItemDefinition definition = getItemDefinitionForId(itemId);

        if (definition.placeholderTemplateId == -1
                && definition.boughtTemplateId == -1) {

            SpritePixels sprite = createItemSprite(definition, false, true);

            if (sprite != null) {
                try {
                    BufferedImage image = new BufferedImage(sprite.width, sprite.height, BufferedImage.TYPE_INT_ARGB);
                    for (int i = 0; i < sprite.width; ++i) {
                        for (int j = 0; j < sprite.height; ++j) {
                            int pixel = sprite.image[j * sprite.width + i];
                            if ((pixel & 0xFF000000) == 0 && (pixel & 0xFFFFFF) != 0) {
                                pixel |= 0xFF000000;
                            }
                            image.setRGB(i, j, pixel);
                        }
                    }

                    return image;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return null;
    }

    private SpritePixels createItemSprite(ItemDefinition itemDefinition, boolean zoomOut, boolean addOutline) {
        ModelData modelData = getModelDataForId(itemDefinition.inventoryModel);

        SpritePixels underlayPixels = null;

        if (itemDefinition.notedTemplate != -1) {
            underlayPixels = createItemSprite(getItemDefinitionForId(itemDefinition.notedID),
                    true, true);

            if (underlayPixels == null) {
                return null;
            }
        } else if (itemDefinition.boughtTemplateId != -1) {
            underlayPixels = createItemSprite(getItemDefinitionForId(itemDefinition.boughtId),
                    false, addOutline);

            if (underlayPixels == null) {
                return null;
            }
        } else if (itemDefinition.placeholderTemplateId != -1) {
            underlayPixels = createItemSprite(getItemDefinitionForId(itemDefinition.placeholderId),
                    false, false);

            if (underlayPixels == null) {
                return null;
            }
        }

        if(itemDefinition.resizeX != 128 || itemDefinition.resizeY != 128 || itemDefinition.resizeZ != 128) {
            modelData.method1562(itemDefinition.resizeX, itemDefinition.resizeY, itemDefinition.resizeZ);
        }

        if(null != itemDefinition.colorFind) {
            for(int var4 = 0; var4 < itemDefinition.colorFind.length; ++var4) {
                modelData.method1617(itemDefinition.colorFind[var4], itemDefinition.colorReplace[var4]);
            }
        }

        if(null != itemDefinition.textureFind) {
            for(int var4 = 0; var4 < itemDefinition.textureFind.length; ++var4) {
                modelData.method1612(itemDefinition.textureFind[var4], itemDefinition.textureReplace[var4]);
            }
        }

        Model model = modelData.method1624(itemDefinition.ambient + 64,
                768 + itemDefinition.contrast,
                -50, -10, -50);
        model.field1388 = true;

        int[] var12 = Rasterizer2D.graphicsPixels;
        int var13 = Rasterizer2D.graphicsPixelsWidth;
        int var14 = Rasterizer2D.graphicsPixelsHeight;

        int[] var15 = new int[4];
        Rasterizer2D.method4015(var15);

        SpritePixels spritePixels = new SpritePixels(WIDTH, HEIGHT);
        Rasterizer2D.setRasterBuffer(spritePixels.image, WIDTH, HEIGHT);
        Rasterizer2D.method4017();

        Rasterizer3D.method1747();
        Rasterizer3D.method1726(spritePixels.width / 2, spritePixels.height / 2);
        Rasterizer3D.rasterGouraudLowRes = false;

        if (itemDefinition.placeholderTemplateId != -1) {
            underlayPixels.method4174(0, 0);
        }

        int zoom = itemDefinition.zoom2d;
        if(zoomOut) {
            zoom = (int)((double)zoom * 1.5D);
        }

        int var17 = Rasterizer3D.field1460[itemDefinition.xan2d] * zoom >> 16;
        int var18 = Rasterizer3D.field1446[itemDefinition.xan2d] * zoom >> 16;

        model.method1649();
        model.method1661(0, itemDefinition.yan2d, itemDefinition.zan2d, itemDefinition.xan2d,
                itemDefinition.xOffset2d, itemDefinition.yOffset2d + model.modelHeight / 2 + var17,
                itemDefinition.yOffset2d + var18);

        if (itemDefinition.boughtTemplateId != -1) {
            underlayPixels.method4174(0, 0);
        }

        if (addOutline) {
            spritePixels.method4222(1);
        }

        Rasterizer2D.setRasterBuffer(spritePixels.image, WIDTH, HEIGHT);

        if (itemDefinition.notedTemplate != -1) {
            underlayPixels.method4174(0, 0);
        }

        Rasterizer2D.setRasterBuffer(var12, var13, var14);
        Rasterizer2D.method4016(var15);
        Rasterizer3D.method1747();
        Rasterizer3D.rasterGouraudLowRes = true;

        return spritePixels;
    }

    private ItemDefinition getItemDefinitionForId(int itemId) {
        ItemDefinition definition = itemLoader
                .load(itemId, itemFiles.get(itemId).getContents());

        if (definition.notedTemplate != -1) {
            method3706(definition,
                    getItemDefinitionForId(definition.notedTemplate),
                    getItemDefinitionForId(definition.notedID));
        }

        if (definition.boughtTemplateId != -1) {
            method3763(definition,
                    getItemDefinitionForId(definition.boughtTemplateId),
                    getItemDefinitionForId(definition.boughtId));
        }

        if (definition.placeholderTemplateId != -1) {
            method3704(definition,
                    getItemDefinitionForId(definition.placeholderTemplateId),
                    getItemDefinitionForId(definition.placeholderId));
        }

        return definition;
    }

    private static void method3706(ItemDefinition var0, ItemDefinition var1, ItemDefinition var2) {
        var0.inventoryModel = var1.inventoryModel;
        var0.zoom2d = var1.zoom2d;
        var0.xan2d = var1.xan2d;
        var0.yan2d = var1.yan2d;
        var0.zan2d = var1.zan2d;
        var0.xOffset2d = var1.xOffset2d;
        var0.yOffset2d = var1.yOffset2d;
        var0.colorFind = var1.colorFind;
        var0.colorReplace = var1.colorReplace;
        var0.textureFind = var1.textureFind;
        var0.textureReplace = var1.textureReplace;
        var0.name = var2.name;
        var0.members = var2.members;
        var0.cost = var2.cost;
        var0.stackable = 1;
    }

    private static void method3763(ItemDefinition var0, ItemDefinition var1, ItemDefinition var2) {
        var0.inventoryModel = var1.inventoryModel;
        var0.zoom2d = var1.zoom2d;
        var0.xan2d = var1.xan2d;
        var0.yan2d = var1.yan2d;
        var0.zan2d = var1.zan2d;
        var0.xOffset2d = var1.xOffset2d;
        var0.yOffset2d = var1.yOffset2d;
        var0.colorFind = var2.colorFind;
        var0.colorReplace = var2.colorReplace;
        var0.textureFind = var2.textureFind;
        var0.textureReplace = var2.textureReplace;
        var0.name = var2.name;
        var0.members = var2.members;
        var0.stackable = var2.stackable;
        var0.maleModel0 = var2.maleModel0;
        var0.maleModel1 = var2.maleModel1;
        var0.maleModel2 = var2.maleModel2;
        var0.femaleModel0 = var2.femaleModel0;
        var0.femaleModel1 = var2.femaleModel1;
        var0.femaleModel2 = var2.femaleModel2;
        var0.maleHeadModel = var2.maleHeadModel;
        var0.maleHeadModel2 = var2.maleHeadModel2;
        var0.femaleHeadModel = var2.femaleHeadModel;
        var0.femaleHeadModel2 = var2.femaleHeadModel2;
        var0.team = var2.team;
        var0.options = var2.options;

        var0.interfaceOptions = new String[5];
        if(var2.interfaceOptions != null) {
            System.arraycopy(var2.interfaceOptions, 0, var0.interfaceOptions, 0, 4);
        }
        var0.interfaceOptions[4] = "Discard";

        var0.cost = 0;
    }

    private static void method3704(ItemDefinition var0, ItemDefinition var1, ItemDefinition var2) {
        var0.inventoryModel = var1.inventoryModel;
        var0.zoom2d = var1.zoom2d;
        var0.xan2d = var1.xan2d;
        var0.yan2d = var1.yan2d;
        var0.zan2d = var1.zan2d;
        var0.xOffset2d = var1.xOffset2d;
        var0.yOffset2d = var1.yOffset2d;
        var0.colorFind = var1.colorFind;
        var0.colorReplace = var1.colorReplace;
        var0.textureFind = var1.textureFind;
        var0.textureReplace = var1.textureReplace;
        var0.stackable = var1.stackable;
        var0.name = var2.name;
        var0.cost = 0;
        var0.members = false;
        var0.isTradeable = false;
    }

    private ModelData getModelDataForId(int modelId) {
        net.runelite.cache.fs.File modelFile = modelIndex
                .getArchive(modelId)
                .getFiles()
                .get(0);

        return new ModelData(modelFile.getContents());
    }
}
