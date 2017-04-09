package com.ostracker.rs;

public final class SpritePixels extends Rasterizer2D {

    public int width;
    public int height;

    public int offsetX;
    public int offsetY;

    public int[] image;

    public SpritePixels(int var1, int var2) {
        this.image = new int[var1 * var2];
        this.width = var1;
        this.height = var2;
        this.offsetX = 0;
        this.offsetY = 0;
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

    public void method4174(int var1, int var2) {
        var1 += this.offsetX;
        var2 += this.offsetY;
        int var3 = var1 + var2 * Rasterizer2D.graphicsPixelsWidth;
        int var4 = 0;
        int var5 = this.height;
        int var6 = this.width;
        int var7 = Rasterizer2D.graphicsPixelsWidth - var6;
        int var8 = 0;
        int var9;
        if(var2 < Rasterizer2D.field3193) {
            var9 = Rasterizer2D.field3193 - var2;
            var5 -= var9;
            var2 = Rasterizer2D.field3193;
            var4 += var9 * var6;
            var3 += var9 * Rasterizer2D.graphicsPixelsWidth;
        }

        if(var2 + var5 > Rasterizer2D.field3190) {
            var5 -= var2 + var5 - Rasterizer2D.field3190;
        }

        if(var1 < Rasterizer2D.field3195) {
            var9 = Rasterizer2D.field3195 - var1;
            var6 -= var9;
            var1 = Rasterizer2D.field3195;
            var4 += var9;
            var3 += var9;
            var8 += var9;
            var7 += var9;
        }

        if(var1 + var6 > Rasterizer2D.field3194) {
            var9 = var1 + var6 - Rasterizer2D.field3194;
            var6 -= var9;
            var8 += var9;
            var7 += var9;
        }

        if(var6 > 0 && var5 > 0) {
            method4175(Rasterizer2D.graphicsPixels, this.image, 0, var4, var3, var6, var5, var7, var8);
        }
    }

    static void method4175(int[] var0, int[] var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8) {
        int var9 = -(var5 >> 2);
        var5 = -(var5 & 3);

        for(int var10 = -var6; var10 < 0; ++var10) {
            int var11;
            for(var11 = var9; var11 < 0; ++var11) {
                var2 = var1[var3++];
                if(var2 != 0) {
                    var0[var4++] = var2;
                } else {
                    ++var4;
                }

                var2 = var1[var3++];
                if(var2 != 0) {
                    var0[var4++] = var2;
                } else {
                    ++var4;
                }

                var2 = var1[var3++];
                if(var2 != 0) {
                    var0[var4++] = var2;
                } else {
                    ++var4;
                }

                var2 = var1[var3++];
                if(var2 != 0) {
                    var0[var4++] = var2;
                } else {
                    ++var4;
                }
            }

            for(var11 = var5; var11 < 0; ++var11) {
                var2 = var1[var3++];
                if(var2 != 0) {
                    var0[var4++] = var2;
                } else {
                    ++var4;
                }
            }

            var4 += var7;
            var3 += var8;
        }
    }
}
