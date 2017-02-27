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

import java.io.IOException;
import java.io.OutputStream;

public class BinaryWriter {

    private OutputStream stream;

    public BinaryWriter(OutputStream stream) {
        this.stream = stream;
    }

    public void write(int data) throws IOException {
        ByteBuf buffer = Unpooled.buffer(4);
        buffer.writeInt(data);
        write(buffer);
    }

    public void write(byte[] data) throws IOException {
        stream.write(data);
    }

    public void write(byte data) throws IOException {
        stream.write(data);
    }

    public void write(ByteBuf byteBuf) throws IOException {
        write(byteBuf.array());
    }

    public void flush() throws IOException {
        stream.flush();
    }

    public OutputStream getStream() {
        return stream;
    }
}
