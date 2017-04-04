package com.ostracker.rs;

public class Rasterizer2D {

    public static int field3190 = 0;

    public static int graphicsPixelsWidth;

    public static int graphicsPixelsHeight;

    public static int field3193 = 0;

    public static int[] graphicsPixels;

    protected static int field3194 = 0;

    protected static int field3195 = 0;

    public static void method4015(int[] var0) {
        var0[0] = field3195;
        var0[1] = field3193;
        var0[2] = field3194;
        var0[3] = field3190;
    }

    public static void method4016(int[] var0) {
        field3195 = var0[0];
        field3193 = var0[1];
        field3194 = var0[2];
        field3190 = var0[3];
    }

    public static void method4017() {
        int var0 = 0;

        int var1;
        for (var1 = graphicsPixelsWidth * graphicsPixelsHeight - 7; var0 < var1; graphicsPixels[var0++] = 0) {
            graphicsPixels[var0++] = 0;
            graphicsPixels[var0++] = 0;
            graphicsPixels[var0++] = 0;
            graphicsPixels[var0++] = 0;
            graphicsPixels[var0++] = 0;
            graphicsPixels[var0++] = 0;
            graphicsPixels[var0++] = 0;
        }

        for (var1 += 7; var0 < var1; graphicsPixels[var0++] = 0) ;
    }

    public static void method4091(int var0, int var1, int var2, int var3) {
        if (var0 < 0) {
            var0 = 0;
        }

        if (var1 < 0) {
            var1 = 0;
        }

        if (var2 > graphicsPixelsWidth) {
            var2 = graphicsPixelsWidth;
        }

        if (var3 > graphicsPixelsHeight) {
            var3 = graphicsPixelsHeight;
        }

        field3195 = var0;
        field3193 = var1;
        field3194 = var2;
        field3190 = var3;
    }

    public static void setRasterBuffer(int[] var0, int var1, int var2) {
        graphicsPixels = var0;
        graphicsPixelsWidth = var1;
        graphicsPixelsHeight = var2;
        method4091(0, 0, var1, var2);
    }
}
