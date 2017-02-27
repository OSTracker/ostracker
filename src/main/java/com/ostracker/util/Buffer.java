package com.ostracker.util;

public class Buffer {

    private int offset;
    private byte[] payload;

    public Buffer(byte[] payload) {
        this.payload = payload;
    }

    public void skip(int length) {
        this.offset += length;
    }

    public int readInt() {
        this.offset += 4;

        return (this.payload[this.offset - 1] & 255)
                + ((this.payload[this.offset - 4] & 255) << 24)
                + ((this.payload[this.offset - 3] & 255) << 16)
                + ((this.payload[this.offset - 2] & 255) << 8);
    }

    public int readUnsignedShort() {
        this.offset += 2;

        return ((this.payload[this.offset - 2] & 255) << 8)
                + (this.payload[this.offset - 1] & 255);
    }

    public int readShort() {
        this.offset += 2;

        int value = (this.payload[this.offset - 1] & 255)
                + ((this.payload[this.offset - 2] & 255) << 8);

        if(value > Short.MAX_VALUE) {
            value -= 65536;
        }

        return value;
    }

    public int readUnsignedByte() {
        return this.payload[++this.offset - 1] & 255;
    }

    public String readString() {
        int start = this.offset;

        while(this.payload[++this.offset - 1] != 0);

        int length = this.offset - start - 1;

        if (length == 0) {
            return "";
        }

        return new String(this.payload, start, length);
    }
}
