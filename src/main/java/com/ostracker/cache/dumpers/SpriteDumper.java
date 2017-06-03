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

import com.ostracker.OSTracker;
import com.ostracker.cache.loaders.ModelFileLoader;
import com.ostracker.util.FileUtil;
import net.runelite.cache.definitions.SpriteDefinition;
import net.runelite.cache.definitions.loaders.SpriteLoader;
import net.runelite.cache.fs.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.util.Map;

public class SpriteDumper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpriteDumper.class);

    private Map<Integer, File> spriteFiles;

    private SpriteLoader spriteLoader = new SpriteLoader();

    public SpriteDumper(ModelFileLoader modelFileLoader) {
        this.spriteFiles = modelFileLoader.getSpriteFiles();
    }

    public void dump(int spriteId, boolean overwriteFiles) {
        File f = spriteFiles.get(spriteId);
        if (f == null) {
            throw new NullPointerException("A file for sprite " + spriteId + " could not be found in the cache");
        }

        SpriteDefinition[] spriteDefinitions = spriteLoader
                .load(spriteId, f.getContents());

        for (SpriteDefinition definition : spriteDefinitions) {
            if (definition.getWidth() > 0
                    && definition.getHeight() > 0) {

                java.io.File frameFile = new java.io.File(OSTracker.SPRITE_DUMP_ROOT,
                        spriteId + "/" + definition.getFrame() + ".png");

                if (!frameFile.exists() || overwriteFiles) {
                    frameFile.getParentFile().mkdirs();

                    BufferedImage bufferedImage = new BufferedImage(
                            definition.getWidth(),
                            definition.getHeight(),
                            BufferedImage.TYPE_INT_ARGB);

                    bufferedImage.setRGB(0, 0,
                            definition.getWidth(), definition.getHeight(),
                            definition.getPixels(),
                            0, definition.getWidth());

                    LOGGER.info("Dumping " + frameFile);

                    FileUtil.writeImage(bufferedImage, "png", frameFile);
                }
            }
        }
    }
}
