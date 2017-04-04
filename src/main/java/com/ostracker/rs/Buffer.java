package com.ostracker.rs;

public class Buffer {

    public byte[] payload;
    public int offset;

    public Buffer(byte[] var1) {
        this.payload = var1;
        this.offset = 0;
    }

    public byte readByte() {
        return this.payload[++this.offset - 1];
    }

    public int read24BitInt() {
        this.offset += 3;
        return (this.payload[this.offset - 1] & 255) + ((this.payload[this.offset - 2] & 255) << 8) + ((this.payload[this.offset - 3] & 255) << 16);
    }

    public int readInt() {
        this.offset += 4;
        return (this.payload[this.offset - 1] & 255) + ((this.payload[this.offset - 2] & 255) << 8) + ((this.payload[this.offset - 3] & 255) << 16) + ((this.payload[this.offset - 4] & 255) << 24);
    }

    public int readUnsignedShort() {
        this.offset += 2;
        return ((this.payload[this.offset - 2] & 255) << 8) + (this.payload[this.offset - 1] & 255);
    }

    public int readShortSmart() {
        int var1 = this.payload[this.offset] & 255;
        return var1 < 128 ? this.readUnsignedByte() - 64 : this.readUnsignedShort() - '쀀';
    }

    public int readUnsignedByte() {
        return this.payload[++this.offset - 1] & 255;
    }
}
