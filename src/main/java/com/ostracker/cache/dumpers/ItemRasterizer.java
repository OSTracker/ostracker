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
import net.runelite.cache.fs.Index;
import net.runelite.cache.fs.Store;

import java.awt.image.BufferedImage;
import java.util.List;

public class ItemRasterizer {

    private static final int WIDTH = 36;
    private static final int HEIGHT = 32;

    private static final double BRIGHTNESS_LEVEL = 0.6D;

    private List<net.runelite.cache.fs.File> itemFiles;
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
        ItemDefinition definition = itemLoader.load(itemId, itemFiles.get(itemId).getContents());

        if (definition.notedTemplate == -1
                && definition.placeholderTemplateId == -1
                && definition.boughtTemplateId == -1) {

            net.runelite.cache.fs.File modelFile = modelIndex
                    .getArchive(definition.inventoryModel)
                    .getFiles()
                    .get(0);

            if (modelFile.getContents().length > 0) {
                return rasterize(definition, new ModelData(modelFile.getContents()));
            }
        }

        return null;
    }

    private BufferedImage rasterize(ItemDefinition itemDefinition, ModelData modelDefinition) {
        if(itemDefinition.resizeX != 128 || itemDefinition.resizeY != 128 || itemDefinition.resizeZ != 128) {
            modelDefinition.method1562(itemDefinition.resizeX, itemDefinition.resizeY, itemDefinition.resizeZ);
        }

        if(null != itemDefinition.colorFind) {
            for(int var4 = 0; var4 < itemDefinition.colorFind.length; ++var4) {
                modelDefinition.method1617(itemDefinition.colorFind[var4], itemDefinition.colorReplace[var4]);
            }
        }

        if(null != itemDefinition.textureFind) {
            for(int var4 = 0; var4 < itemDefinition.textureFind.length; ++var4) {
                modelDefinition.method1612(itemDefinition.textureFind[var4], itemDefinition.textureReplace[var4]);
            }
        }

        Model model = modelDefinition.method1624(itemDefinition.ambient + 64,
                768 + itemDefinition.contrast,
                -50, -10, -50);
        model.field1388 = true;

        int[] var12 = Rasterizer2D.graphicsPixels;
        int var13 = Rasterizer2D.graphicsPixelsWidth;
        int var14 = Rasterizer2D.graphicsPixelsHeight;

        int[] var15 = new int[4];
        Rasterizer2D.method4015(var15);

        SpritePixels var8 = new SpritePixels(WIDTH, HEIGHT);
        Rasterizer2D.setRasterBuffer(var8.image, WIDTH, HEIGHT);
        Rasterizer2D.method4017();

        Rasterizer3D.method1747();
        Rasterizer3D.method1726(var8.width / 2, var8.height / 2);
        Rasterizer3D.rasterGouraudLowRes = false;

        int zoom = itemDefinition.zoom2d;

        int var17 = Rasterizer3D.field1460[itemDefinition.xan2d] * zoom >> 16;
        int var18 = Rasterizer3D.field1446[itemDefinition.xan2d] * zoom >> 16;

        model.method1649();
        model.method1661(0, itemDefinition.yan2d, itemDefinition.zan2d, itemDefinition.xan2d,
                itemDefinition.xOffset2d, itemDefinition.yOffset2d + model.modelHeight / 2 + var17,
                itemDefinition.yOffset2d + var18);

        // Adds black outline
        var8.method4222(1);

        Rasterizer2D.setRasterBuffer(var8.image, WIDTH, HEIGHT);

        Rasterizer2D.setRasterBuffer(var12, var13, var14);
        Rasterizer2D.method4016(var15);
        Rasterizer3D.method1747();
        Rasterizer3D.rasterGouraudLowRes = true;

        try {
            BufferedImage image = new BufferedImage(var8.width, var8.height, BufferedImage.TYPE_INT_ARGB);
            for (int i = 0; i < var8.width; ++i) {
                for (int j = 0; j < var8.height; ++j) {
                    int pixel = var8.image[j * var8.width + i];
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
