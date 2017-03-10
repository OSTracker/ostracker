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

package com.ostracker.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BinaryReader {

    private InputStream stream;

    public BinaryReader(InputStream stream) {
        this.stream = new BufferedInputStream(stream);
    }

    public int readInt() throws IOException {
        return getBuffer(4).readInt();
    }

    public int readUnsignedShort() throws IOException {
        return getBuffer(2).readUnsignedShort();
    }

    public byte readByte() throws IOException {
        return (byte) stream.read();
    }

    public int readUnsignedByte() throws IOException {
        return stream.read();
    }

    private ByteBuf getBuffer(int length) throws IOException {
        ByteBuf buffer = Unpooled.buffer(4);
        buffer.writeBytes(readBytes(length));
        return buffer;
    }

    public byte[] readBytes(int length) throws IOException {
        byte[] bytes = new byte[length];
        stream.read(bytes, 0, length);
        return bytes;
    }

    public InputStream getStream() {
        return stream;
    }
}
