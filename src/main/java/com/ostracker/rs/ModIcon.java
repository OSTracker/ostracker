package com.ostracker.rs;

public final class ModIcon extends Rasterizer2D {

    public int originalHeight;

    public int[] palette;

    public byte[] pixels;

    public int height;

    public int offsetX;

    public int offsetY;

    public int width;

    public int originalWidth;

    public void method4113() {
        if (this.originalWidth != this.width || this.height != this.originalHeight) {
            byte[] var1 = new byte[this.width * this.originalHeight];
            int var2 = 0;

            for (int var3 = 0; var3 < this.height; ++var3) {
                for (int var4 = 0; var4 < this.originalWidth; ++var4) {
                    var1[var4 + this.offsetX + (var3 + this.offsetY) * this.width] = this.pixels[var2++];
                }
            }

            this.pixels = var1;
            this.originalWidth = this.width;
            this.height = this.originalHeight;
            this.offsetX = 0;
            this.offsetY = 0;
        }
    }
}
