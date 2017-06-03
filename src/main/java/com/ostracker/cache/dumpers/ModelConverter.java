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
import net.runelite.cache.definitions.ModelDefinition;
import net.runelite.cache.definitions.TextureDefinition;
import net.runelite.cache.definitions.loaders.ModelLoader;
import net.runelite.cache.definitions.loaders.TextureLoader;
import net.runelite.cache.fs.File;
import net.runelite.cache.models.VertexNormal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;

public class ModelConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelConverter.class);

    private Map<Integer, File> modelFiles;
    private Map<Integer, File> textureFiles;

    private ModelLoader modelLoader = new ModelLoader();
    private TextureLoader textureLoader = new TextureLoader();

    public ModelConverter(ModelFileLoader modelFileLoader) {
        this.modelFiles = modelFileLoader.getModelFiles();
        this.textureFiles = modelFileLoader.getTextureFiles();
    }

    public void convert(int modelId, boolean overwriteFiles) {
        File f = modelFiles.get(modelId);
        if (f == null) {
            throw new NullPointerException("A file for model " + modelId + " could not be found in the cache");
        }

        java.io.File materialsFile = new java.io.File(OSTracker.MODEL_DUMP_ROOT, modelId + "/materials.mtl");
        java.io.File modelFile = new java.io.File(OSTracker.MODEL_DUMP_ROOT, modelId + "/model.obj");

        if (!materialsFile.exists() || !modelFile.exists() || overwriteFiles) {
            materialsFile.getParentFile().mkdirs();

            byte[] modelBytes = f.getContents();

            ModelDefinition definition = modelLoader.load(modelId, modelBytes);
            definition.computeNormals();
            definition.computeTextureUVCoordinates();

            LOGGER.info("Dumping " + materialsFile);

            try (PrintWriter writer = new PrintWriter(materialsFile)) {
                for (int i = 0; i < definition.faceCount; i++) {
                    short textureId = -1;
                    if (definition.faceTextures != null) {
                        textureId = definition.faceTextures[i];
                    }

                    writer.println("newmtl m" + i);

                    if (textureId == -1) {
                        int hsb = definition.faceColors[i] & 0xFFFF;

                        int hue = (hsb >> 10) & 0x3F;
                        int saturation = (hsb >> 7) & 0x7;
                        int brightness = hsb & 0x7F;

                        int rgb = Color.HSBtoRGB(hue / 63F,
                                saturation / 7F,
                                brightness / 127F);

                        int red = (rgb >> 16) & 0xFF;
                        int green = (rgb >> 8) & 0xFF;
                        int blue = rgb & 0xFF;

                        double r = red / 255F;
                        double g = green / 255F;
                        double b = blue / 255F;

                        writer.println("     Kd " + r + " " + g + " " + b);
                    } else {
                        File textureFile = textureFiles.get((int) textureId);

                        if (textureFile != null) {
                            TextureDefinition textureDefinition = textureLoader
                                    .load(textureId, textureFile.getContents());

                            writer.println("     map_Kd ../../sprites/" + textureDefinition.getFileIds()[0] + "/0.png");
                        } else {
                            LOGGER.error("Model " + modelId +
                                    " was not dumped, because texture " + textureId + " was missing");
                            return;
                        }
                    }

                    int alpha = 0;

                    if (definition.faceAlphas != null) {
                        alpha = definition.faceAlphas[i] & 0xFF;
                    }

                    if (alpha != 0) {
                        writer.println("     d " + alpha / 255F);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            LOGGER.info("Dumping " + modelFile);

            try (PrintWriter writer = new PrintWriter(modelFile)) {
                writer.println("mtllib materials.mtl");

                for (int i = 0; i < definition.vertexCount; i++) {
                    writer.println("v "
                            + definition.vertexPositionsX[i] + " "
                            + definition.vertexPositionsY[i] * -1 + " "
                            + definition.vertexPositionsZ[i] * -1);
                }

                if (definition.faceTextures != null) {
                    for (int i = 0; i < definition.faceCount; i++) {
                        writer.println("vt "
                                + definition.faceTextureUCoordinates[i][0] + " "
                                + definition.faceTextureVCoordinates[i][0]);
                        writer.println("vt "
                                + definition.faceTextureUCoordinates[i][1] + " "
                                + definition.faceTextureVCoordinates[i][1]);
                        writer.println("vt "
                                + definition.faceTextureUCoordinates[i][2] + " "
                                + definition.faceTextureVCoordinates[i][2]);
                    }
                }

                for (int i = 0; i < definition.vertexCount; i++) {
                    VertexNormal normal = definition.vertexNormals[i];

                    writer.println("vn "
                            + normal.x + " "
                            + normal.y + " "
                            + normal.z);
                }

                for (int i = 0; i < definition.faceCount; i++) {
                    // Render type 2 seems to be for
                    // texture triangles.
                    // Those shouldn't be drawn.
                    if (definition.faceRenderTypes == null
                            || definition.faceRenderTypes[i] != 2) {
                        int x = definition.faceVertexIndices1[i] + 1;
                        int y = definition.faceVertexIndices2[i] + 1;
                        int z = definition.faceVertexIndices3[i] + 1;

                        writer.println("usemtl m" + i);
                        if (definition.faceTextures != null) {
                            writer.println("f "
                                    + x + "/" + (i * 3 + 1) + " "
                                    + y + "/" + (i * 3 + 2) + " "
                                    + z + "/" + (i * 3 + 3));
                        } else {
                            writer.println("f "
                                    + x + " "
                                    + y + " "
                                    + z);
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
