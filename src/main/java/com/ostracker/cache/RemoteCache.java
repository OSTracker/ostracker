/*
 * This file is a derivative work of files covered by a BSD license.
 *
 * Those files could be found at https://github.com/runelite/runelite at the time of writing this notice, inside
 * directory "cache/src/main/java/net/runelite/cache/downloader". The following copyright notice, list of conditions
 * and disclaimer is copied verbatim from the derived work.
 *
 *
 * Copyright (c) 2016-2017, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * The parts of this file that has not been derived from the above
 * mentioned work is licensed under the GPL license, as described below.
 *
 *
 * Copyright (C) 2017 OSTracker <https://github.com/OSTracker>
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

package com.ostracker.cache;

import com.ostracker.net.BinaryReader;
import com.ostracker.net.BinaryWriter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.runelite.cache.downloader.FileResult;
import net.runelite.cache.fs.Archive;
import net.runelite.cache.fs.Index;
import net.runelite.cache.fs.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.concurrent.ExecutionException;

public class RemoteCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteCache.class);

    private static final int HANDSHAKE_SUCCESS = 0;
    private static final int HANDSHAKE_OUTDATED = 6;

    private Socket connection;
    private BinaryReader reader;
    private BinaryWriter writer;

    public void connect(String host, int port) throws IOException {
        connection = new Socket(host, port);
        reader = new BinaryReader(connection.getInputStream());
        writer = new BinaryWriter(connection.getOutputStream());
    }

    public void shakeHands(int revision) throws IOException {
        // 15 = JS5 header
        writer.write((byte) 15);
        writer.write(revision);

        writer.flush();

        int response = reader.readByte();

        if (response != HANDSHAKE_SUCCESS) {
            if (response == HANDSHAKE_OUTDATED) {
                throw new IllegalStateException("Handshake failed, wrong revision specified");
            }
            throw new IllegalStateException("Handshake failed, unknown error encountered");
        }

        ByteBuf buffer = Unpooled.buffer(4);
        // Login state
        // 2 = In-game
        // 3 = Not in-game
        buffer.writeByte(3);
        // Padding
        buffer.writeMedium(0);

        writer.write(buffer);
        writer.flush();
    }

    public void download(Store store)
            throws IOException, ExecutionException, InterruptedException {

        FileResult indicesFile = downloadFile(255, 255);

        ByteBuf buffer = Unpooled
                .wrappedBuffer(indicesFile.getContents());

        int indexCount = indicesFile
                .getContents()
                .length / 8;

        for (int i = 0; i < indexCount; i++) {
            int crc = buffer.readInt();
            buffer.readInt(); // index revision

            LOGGER.info("Downloading index " + i);

            FileResult indexFile = downloadFile(255, i);

            LOGGER.info("Done downloading index " + i);

            if (indexFile.getCrc() != crc) {
                LOGGER.error("Crc mismatch between downloaded file and expected checksum for index " + i);
                continue;
            }

            Index oldIndex = null;

            Index index = store.findIndex(i);

            if (index != null) {
                store.removeIndex(index);
                oldIndex = index;
            }

            index = store.addIndex(i);

            index.readIndexData(indexFile.getContents());
            index.setCrc(crc);

            LOGGER.info("Index " + i + " has " + index.getArchives().size() + " archives");

            for (Archive archive : index.getArchives()) {
                Archive oldArchive = null;

                if (oldIndex != null) {
                    oldArchive = oldIndex.getArchive(archive.getArchiveId());
                }

                if (oldArchive == null || oldArchive.getRevision() != archive.getRevision()) {
                    LOGGER.info(MessageFormat.format("Archive {0} in index {1} is out of date, downloading..",
                            archive.getArchiveId(),
                            i));

                    FileResult archiveFile = downloadFile(i, archive.getArchiveId(), false);

                    archive.setData(archiveFile.getCompressedData());
                } else {
                    LOGGER.info(MessageFormat.format("Archive {0} in index {1} is up to date",
                            archive.getArchiveId(),
                            i));

                    if (oldArchive.getData() != null) {
                        archive.setData(oldArchive.getData());
                    } else {
                        archive.loadContents(oldArchive.saveContents());
                        archive.setCompression(oldArchive.getCompression());
                    }
                }
            }
        }
    }

    private FileResult downloadFile(int indexId, int fileId)
            throws IOException {

        return downloadFile(indexId, fileId, true);
    }

    private FileResult downloadFile(int indexId, int fileId, boolean shouldDecompress)
            throws IOException {

        ByteBuf buffer = Unpooled.buffer(4);
        // Type
        buffer.writeByte(indexId == 255 ? 1 : 0);
        // Hash
        buffer.writeMedium(indexId << 16 | fileId);

        writer.write(buffer);
        writer.flush();

        int remoteIndexId = reader.readUnsignedByte();
        if (indexId != remoteIndexId) {
            throw new IllegalStateException("Remote index id "
                    + remoteIndexId + " did not match input index id "
                    + indexId);
        }

        int remoteFileId = reader.readUnsignedShort();
        if (fileId != remoteFileId) {
            throw new IllegalStateException("Remote file id "
                    + remoteFileId + " did not match input file id "
                    + fileId);
        }

        FileResult result = downloadCompressedFile(indexId, fileId);

        if (shouldDecompress) {
            result.decompress(null);
        }

        return result;
    }

    private FileResult downloadCompressedFile(int indexId, int fileId)
            throws IOException {

        reader.getStream().mark(5);

        int compressionType = reader.readUnsignedByte();
        int compressedFileSize = reader.readInt();

        reader.getStream().reset();

        int dataSize = compressedFileSize + 5 + (compressionType != 0 ? 4 : 0);

        int breakCount = calculateBreaks(dataSize);

        byte[] compressedData = new byte[dataSize];
        int compressedDataOffset = 0;

        int totalRead = 3;

        for (int i = 0; i < breakCount + 1; i++) {
            int bytesInBlock = 512 - (totalRead % 512);
            int bytesToRead = Math.min(bytesInBlock, dataSize - compressedDataOffset);

            while (bytesToRead != 0) {
                int readBytes = reader.getStream().read(compressedData, compressedDataOffset, bytesToRead);
                if (readBytes > 0) {
                    bytesToRead -= readBytes;

                    compressedDataOffset += readBytes;
                    totalRead += readBytes;
                }
            }

            LOGGER.trace(MessageFormat.format(
                    "{0} {1} - read block {2}/{3}, bytes in this block: {4}, file status: {5}/{6}",
                    indexId, fileId, i, breakCount, bytesInBlock, compressedDataOffset, dataSize));

            if (i < breakCount) {
                int b = reader.readUnsignedByte();
                if (b != 0xFF) {
                    throw new IllegalStateException("First byte of all chunks except the first one should be 0xFF, was "
                            + b);
                }
                totalRead++;
            }
        }

        if (compressedDataOffset != dataSize) {
            throw new IllegalStateException("Found data size did not match expected size");
        }

        return new FileResult(indexId, fileId, compressedData);
    }

    /** Calculate how many breaks there are in the file stream.
     * There are calculateBreaks()+1 total chunks in the file stream
     *
     * File contents are sent in 512 byte chunks, with the first byte of
     * each chunk except for the first one being 0xff.
     *
     * The first chunk has an 8 byte header (index, file, compression,
     * compressed size). So, the first chunk can contain 512 - 8 bytes
     * of the file, and each chunk after 511 bytes.
     *
     * The 'size' parameter has the compression type and size included in
     * it, since they haven't been read yet by the buffer stream, as
     * decompress() reads it, so we use  512 - 3 (because 8-5) = 3
     */
    private int calculateBreaks(int size) {
        int initialSize = 512 - 3;

        if (size <= initialSize) {
            // First in the initial chunk, no breaks
            return 0;
        }

        int left = size - initialSize;

        if (left % 511 == 0) {
            return (left / 511);
        } else {
            // / 511 because 511 bytes of the file per chunk.
            // + 1 if there is some left over, it still needs its
            // own chunk
            return (left / 511) + 1;
        }
    }

    public void close() {
        try {
            connection.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
