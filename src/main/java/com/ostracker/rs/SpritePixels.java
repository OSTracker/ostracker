package com.ostracker.rs;

public final class SpritePixels extends Rasterizer2D {

    public int width;

    public int height;

    public int[] image;

    public SpritePixels(int var1, int var2) {
        this.image = new int[var1 * var2];
        this.width = var1;
        this.height = var2;
    }

    public void method4222(int var1) {
        int[] var2 = new int[this.width * this.height];
        int var3 = 0;

        for(int var4 = 0; var4 < this.height; ++var4) {
            for(int var5 = 0; var5 < this.width; ++var5) {
                int var6 = this.image[var3];
                if(var6 == 0) {
                    if(var5 > 0 && this.image[var3 - 1] != 0) {
                        var6 = var1;
                    } else if(var4 > 0 && this.image[var3 - this.width] != 0) {
                        var6 = var1;
                    } else if(var5 < this.width - 1 && this.image[var3 + 1] != 0) {
                        var6 = var1;
                    } else if(var4 < this.height - 1 && this.image[var3 + this.width] != 0) {
                        var6 = var1;
                    }
                }

                var2[var3++] = var6;
            }
        }

        this.image = var2;
    }
}
