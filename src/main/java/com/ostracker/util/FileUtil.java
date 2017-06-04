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

package com.ostracker.util;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileUtil {

    public static void copyFiles(File[] files, File targetFolder) throws IOException {
        for (File f : files) {
            File targetFile = new File(targetFolder, f.getName());
            targetFile.getParentFile().mkdirs();
            Files.copy(f.toPath(), targetFile.toPath());
        }
    }

    private static final int TIMES_TO_ATTEMPT_WRITING_IMAGE = 5;

    public static void writeImage(RenderedImage image, String fileFormat, File file) {
        int timesAttempted = 0;

        do {
            try {
                ImageIO.write(image, fileFormat, file);
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (timesAttempted++ < TIMES_TO_ATTEMPT_WRITING_IMAGE);
    }
}
