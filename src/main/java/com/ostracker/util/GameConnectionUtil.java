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
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OSTracker. If not, see <http://www.gnu.org/licenses/>.
 */

package com.ostracker.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;

public class GameConnectionUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameConnectionUtil.class);

    public static final int GAME_PORT = 43594;

    private static final String WORLD_DOWNLOAD_URL = "http://www.runescape.com/g=oldscape/slr.ws?order=LPWM";

    public static String getBestWorldHost() throws IOException {
        String[] worldHosts = getWorldHosts();

        LOGGER.info("Found " + worldHosts.length + " online worlds, finding the best one..");

        String bestHost = worldHosts[0];
        long bestPing = Long.MAX_VALUE;

        for (String host : worldHosts) {
            try {
                long elapsedTime = System.nanoTime();
                Socket socket = new Socket(host, GAME_PORT);
                elapsedTime = System.nanoTime() - elapsedTime;
                socket.close();

                if (elapsedTime < bestPing) {
                    bestHost = host;
                    bestPing = elapsedTime;
                }
            } catch (IOException e) {
                // ignored
            }
        }

        LOGGER.info(String.format("Selected world host %s with %.1fms ping", bestHost, bestPing / 1E6));

        return bestHost;
    }

    private static String[] getWorldHosts() throws IOException {
        URL url = new URL(WORLD_DOWNLOAD_URL);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream stream = connection.getInputStream();

            byte[] bytes = new byte[connection.getContentLength()];

            if (stream.read(bytes, 0, bytes.length) == bytes.length) {
                net.runelite.cache.io.InputStream buffer = new net.runelite.cache.io.InputStream(bytes);
                buffer.skip(4);

                int worldCount = buffer.readUnsignedShort();

                String[] worlds = new String[worldCount];

                for (int i = 0; i < worldCount; i++) {
                    buffer.readUnsignedShort(); // id
                    buffer.readInt(); // mask
                    String host = buffer.readString();
                    buffer.readString(); // activity
                    buffer.readUnsignedByte(); // location
                    buffer.readShort(); // playerCount

                    worlds[i] = host;
                }

                return worlds;
            } else {
                LOGGER.error("World file could not be fully downloaded");
            }
        } else {
            LOGGER.error("Downloading worlds failed with HTTP response code "
                    + connection.getResponseCode());
        }

        // Fallback in the case that slr.ws is inaccessible
        return new String[] {
                "oldschool1.runescape.com"
        };
    }

    public static int getRevision(String host) throws IOException {
        // Current revision at the time of writing this code
        int revision = 135;
        int tries = 0;

        do {
            Socket socket = new Socket(host, GAME_PORT);

            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            outputStream.writeByte(15);
            outputStream.writeInt(revision);
            outputStream.flush();

            try {
                if (socket.getInputStream().read() == 0) {
                    socket.close();
                    return revision;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            socket.close();

            revision++;
            tries++;
        } while (tries < 200);

        throw new IllegalStateException("Gamepack version could not be found within " + tries + " iterations");
    }
}
