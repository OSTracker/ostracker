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
import com.ostracker.cache.loaders.TrackFileLoader;
import net.runelite.cache.definitions.TrackDefinition;
import net.runelite.cache.definitions.loaders.TrackLoader;
import net.runelite.cache.fs.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

public class TrackDumper {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrackDumper.class);

    private Map<Integer, File> track1Files;
    private Map<Integer, File> track2Files;

    private TrackLoader trackLoader = new TrackLoader();

    public TrackDumper(TrackFileLoader trackFileLoader) {
        this.track1Files = trackFileLoader.getTrack1Files();
        this.track2Files = trackFileLoader.getTrack2Files();
    }

    public void dump(int trackId, boolean isTracks1, boolean overwriteFiles) {
        File f = (isTracks1 ? track1Files : track2Files).get(trackId);
        if (f == null) {
            throw new NullPointerException("A track file with the id " +
                    trackId + " could not be found in the cache");
        }

        java.io.File trackFile = new java.io.File(isTracks1 ? OSTracker.TRACKS1_DUMP_ROOT : OSTracker.TRACKS2_DUMP_ROOT,
                trackId + "-" + f.getArchive().getNameHash() + ".mid");

        if (!trackFile.exists() || overwriteFiles) {
            trackFile.getParentFile().mkdirs();

            TrackDefinition trackDefinition = trackLoader
                    .load(f.getContents());

            LOGGER.info("Dumping " + trackFile);

            try {
                Files.write(trackFile.toPath(), trackDefinition.midi);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
