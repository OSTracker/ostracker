package com.ostracker.rs;

public class Model extends Renderable {

    public static int field1368 = 0;
    public static int[] field1405 = new int[4700];
    public static int field1410 = 0;
    public static int[] field1420 = new int[1000];
    public static int field1423 = 0;
    static int[] field1365 = new int[1600];
    static int[] field1370;
    static int[][] field1376 = new int[12][2000];
    static int[] field1378 = new int[4700];
    static int[] field1391 = new int[2000];
    static int[] field1398 = new int[12];
    static boolean[] field1399 = new boolean[4700];
    static int[] field1400 = new int[4700];
    static int[] field1401 = new int[4700];
    static int[] field1403 = new int[4700];
    static int[] field1404 = new int[4700];
    static int[][] field1408 = new int[1600][512];
    static int[] field1411 = new int[2000];
    static int[] field1413 = new int[12];
    static int[] field1414 = new int[10];
    static int[] field1415 = new int[10];
    static int[] field1421 = new int[10];
    static boolean[] field1422 = new boolean[4700];
    static int[] field1426;
    static int[] field1427;
    static int[] field1428;

    static {
        field1370 = Rasterizer3D.field1460;
        field1426 = Rasterizer3D.field1446;
        field1427 = Rasterizer3D.colorPalette;
        field1428 = Rasterizer3D.field1459;
    }

    public boolean field1388 = false;
    public int[] verticesX;
    int field1369 = 0;
    int[] verticesY;
    int[] verticesZ;
    short[] faceTextures;
    int[] indices2;
    int[] field1379;
    int[] texTriangleZ;
    byte[] field1381;
    byte[] triangleTextureIndex;
    byte[] field1383;
    int field1384 = 0;
    int field1385 = 0;
    int[] texTriangleX;
    int[] texTriangleY;
    int field1392;
    int field1393;
    int field1394;
    int field1395;
    int[] indices3;
    int[] field1412;
    int[] field1416;
    int[] indices1;
    int field1425;

    public void method1649() {
        if (this.field1392 != 1) {
            this.field1392 = 1;
            super.modelHeight = 0;
            this.field1393 = 0;
            this.field1394 = 0;

            for (int var1 = 0; var1 < this.field1369; ++var1) {
                int var2 = this.verticesX[var1];
                int var3 = this.verticesY[var1];
                int var4 = this.verticesZ[var1];
                if (-var3 > super.modelHeight) {
                    super.modelHeight = -var3;
                }

                if (var3 > this.field1393) {
                    this.field1393 = var3;
                }

                int var5 = var2 * var2 + var4 * var4;
                if (var5 > this.field1394) {
                    this.field1394 = var5;
                }
            }

            this.field1394 = (int) (Math.sqrt((double) this.field1394) + 0.99D);
            this.field1425 = (int) (Math.sqrt((double) (this.field1394 * this.field1394 + super.modelHeight * super.modelHeight)) + 0.99D);
            this.field1395 = this.field1425 + (int) (Math.sqrt((double) (this.field1394 * this.field1394 + this.field1393 * this.field1393)) + 0.99D);
        }
    }

    void method1650() {
        if (this.field1392 != 2) {
            this.field1392 = 2;
            this.field1394 = 0;

            for (int var1 = 0; var1 < this.field1369; ++var1) {
                int var2 = this.verticesX[var1];
                int var3 = this.verticesY[var1];
                int var4 = this.verticesZ[var1];
                int var5 = var2 * var2 + var4 * var4 + var3 * var3;
                if (var5 > this.field1394) {
                    this.field1394 = var5;
                }
            }

            this.field1394 = (int) (Math.sqrt((double) this.field1394) + 0.99D);
            this.field1425 = this.field1394;
            this.field1395 = this.field1394 + this.field1394;
        }
    }

    public final void method1661(int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
        field1365[0] = -1;
        if (this.field1392 != 2 && this.field1392 != 1) {
            this.method1650();
        }

        int var8 = Rasterizer3D.field1447;
        int var9 = Rasterizer3D.field1448;

        int var12 = field1370[var2];
        int var13 = field1426[var2];
        int var14 = field1370[var3];
        int var15 = field1426[var3];
        int var16 = field1370[var4];
        int var17 = field1426[var4];
        int var18 = var6 * var16 + var7 * var17 >> 16;

        for (int var19 = 0; var19 < this.field1369; ++var19) {
            int var20 = this.verticesX[var19];
            int var21 = this.verticesY[var19];
            int var22 = this.verticesZ[var19];
            int var23;
            if (var3 != 0) {
                var23 = var21 * var14 + var20 * var15 >> 16;
                var21 = var21 * var15 - var20 * var14 >> 16;
                var20 = var23;
            }

            if (var2 != 0) {
                var23 = var22 * var12 + var20 * var13 >> 16;
                var22 = var22 * var13 - var20 * var12 >> 16;
                var20 = var23;
            }

            var20 += var5;
            var21 += var6;
            var22 += var7;
            var23 = var21 * var17 - var22 * var16 >> 16;
            var22 = var21 * var16 + var22 * var17 >> 16;
            if (var22 == 0) {
                var22 = 1;
            }
            field1378[var19] = var22 - var18;
            field1400[var19] = var8 + var20 * Rasterizer3D.field1453 / var22;
            field1401[var19] = var9 + var23 * Rasterizer3D.field1453 / var22;

            if (this.field1385 > 0) {
                field1403[var19] = var20;
                field1404[var19] = var23;
                field1405[var19] = var22;
            }
        }

        try {
            this.method1678(false, false, 0);
        } catch (Exception var25) {
        }

    }

    final void method1665(int var1) {
        if (field1399[var1]) {
            this.method1717(var1);
        } else {
            int var2 = this.indices1[var1];
            int var3 = this.indices2[var1];
            int var4 = this.indices3[var1];
            Rasterizer3D.rasterClipEnable = field1422[var1];
            if (this.field1381 == null) {
                Rasterizer3D.rasterAlpha = 0;
            } else {
                Rasterizer3D.rasterAlpha = this.field1381[var1] & 255;
            }

            if (this.faceTextures != null && this.faceTextures[var1] != -1) {
                int var5;
                int var6;
                int var7;
                if (this.triangleTextureIndex != null && this.triangleTextureIndex[var1] != -1) {
                    int var8 = this.triangleTextureIndex[var1] & 255;
                    var5 = this.texTriangleX[var8];
                    var6 = this.texTriangleY[var8];
                    var7 = this.texTriangleZ[var8];
                } else {
                    var5 = var2;
                    var6 = var3;
                    var7 = var4;
                }

                if (this.field1379[var1] == -1) {
                    Rasterizer3D.rasterTextureAffine(field1401[var2], field1401[var3], field1401[var4], field1400[var2], field1400[var3], field1400[var4], this.field1412[var1], this.field1412[var1], this.field1412[var1], field1403[var5], field1403[var6], field1403[var7], field1404[var5], field1404[var6], field1404[var7], field1405[var5], field1405[var6], field1405[var7], this.faceTextures[var1]);
                } else {
                    Rasterizer3D.rasterTextureAffine(field1401[var2], field1401[var3], field1401[var4], field1400[var2], field1400[var3], field1400[var4], this.field1412[var1], this.field1416[var1], this.field1379[var1], field1403[var5], field1403[var6], field1403[var7], field1404[var5], field1404[var6], field1404[var7], field1405[var5], field1405[var6], field1405[var7], this.faceTextures[var1]);
                }
            } else if (this.field1379[var1] == -1) {
                Rasterizer3D.rasterFlat(field1401[var2], field1401[var3], field1401[var4], field1400[var2], field1400[var3], field1400[var4], field1427[this.field1412[var1]]);
            } else {
                Rasterizer3D.rasterGouraud(field1401[var2], field1401[var3], field1401[var4], field1400[var2], field1400[var3], field1400[var4], this.field1412[var1], this.field1416[var1], this.field1379[var1]);
            }

        }
    }

    final boolean method1667(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8) {
        return !(var2 < var3 && var2 < var4 && var2 < var5) && (!(var2 > var3 && var2 > var4 && var2 > var5) && (!(var1 < var6 && var1 < var7 && var1 < var8) && (var1 <= var6 || var1 <= var7 || var1 <= var8)));
    }

    final void method1678(boolean var1, boolean var2, int var3) {
        if (this.field1395 < 1600) {
            int var4;
            for (var4 = 0; var4 < this.field1395; ++var4) {
                field1365[var4] = 0;
            }

            int var5;
            int var6;
            int var7;
            int var8;
            int var9;
            int var10;
            int var12;
            int var13;
            int var15;
            for (var4 = 0; var4 < this.field1384; ++var4) {
                if (this.field1379[var4] != -2) {
                    var5 = this.indices1[var4];
                    var6 = this.indices2[var4];
                    var7 = this.indices3[var4];
                    var8 = field1400[var5];
                    var9 = field1400[var6];
                    var10 = field1400[var7];

                    int var25;
                    if (!var1 || var8 != -5000 && var9 != -5000 && var10 != -5000) {
                        if (var2 && this.method1667(field1410, field1368, field1401[var5], field1401[var6], field1401[var7], var8, var9, var10)) {
                            field1420[field1423++] = var3;
                            var2 = false;
                        }

                        if ((var8 - var9) * (field1401[var7] - field1401[var6]) - (field1401[var5] - field1401[var6]) * (var10 - var9) > 0) {
                            field1399[var4] = false;
                            field1422[var4] = !(var8 >= 0 && var9 >= 0 && var10 >= 0 && var8 <= Rasterizer3D.rasterClipX && var9 <= Rasterizer3D.rasterClipX && var10 <= Rasterizer3D.rasterClipX);

                            var25 = (field1378[var5] + field1378[var6] + field1378[var7]) / 3 + this.field1425;
                            field1408[var25][field1365[var25]++] = var4;
                        }
                    } else {
                        var25 = field1403[var5];
                        var12 = field1403[var6];
                        var13 = field1403[var7];
                        int var14 = field1404[var5];
                        var15 = field1404[var6];
                        int var16 = field1404[var7];
                        int var17 = field1405[var5];
                        int var18 = field1405[var6];
                        int var19 = field1405[var7];
                        var25 -= var12;
                        var13 -= var12;
                        var14 -= var15;
                        var16 -= var15;
                        var17 -= var18;
                        var19 -= var18;
                        int var20 = var14 * var19 - var17 * var16;
                        int var21 = var17 * var13 - var25 * var19;
                        int var22 = var25 * var16 - var14 * var13;

                        if (var12 * var20 + var15 * var21 + var18 * var22 > 0) {
                            field1399[var4] = true;
                            int var23 = (field1378[var5] + field1378[var6] + field1378[var7]) / 3 + this.field1425;
                            field1408[var23][field1365[var23]++] = var4;
                        }
                    }
                }
            }

            int[] var27;
            if (this.field1383 == null) {
                for (var4 = this.field1395 - 1; var4 >= 0; --var4) {
                    var5 = field1365[var4];
                    if (var5 > 0) {
                        var27 = field1408[var4];

                        for (var7 = 0; var7 < var5; ++var7) {
                            this.method1665(var27[var7]);
                        }
                    }
                }

            } else {
                for (var4 = 0; var4 < 12; ++var4) {
                    field1398[var4] = 0;
                    field1413[var4] = 0;
                }

                for (var4 = this.field1395 - 1; var4 >= 0; --var4) {
                    var5 = field1365[var4];
                    if (var5 > 0) {
                        var27 = field1408[var4];

                        for (var7 = 0; var7 < var5; ++var7) {
                            var8 = var27[var7];
                            byte var28 = this.field1383[var8];
                            var10 = field1398[var28]++;
                            field1376[var28][var10] = var8;
                            if (var28 < 10) {
                                field1413[var28] += var4;
                            } else if (var28 == 10) {
                                field1411[var10] = var4;
                            } else {
                                field1391[var10] = var4;
                            }
                        }
                    }
                }

                var4 = 0;
                if (field1398[1] > 0 || field1398[2] > 0) {
                    var4 = (field1413[1] + field1413[2]) / (field1398[1] + field1398[2]);
                }

                var5 = 0;
                if (field1398[3] > 0 || field1398[4] > 0) {
                    var5 = (field1413[3] + field1413[4]) / (field1398[3] + field1398[4]);
                }

                var6 = 0;
                if (field1398[6] > 0 || field1398[8] > 0) {
                    var6 = (field1413[6] + field1413[8]) / (field1398[6] + field1398[8]);
                }

                var8 = 0;
                var9 = field1398[10];
                int[] var26 = field1376[10];
                int[] var11 = field1411;
                if (var8 == var9) {
                    var8 = 0;
                    var9 = field1398[11];
                    var26 = field1376[11];
                    var11 = field1391;
                }

                if (var8 < var9) {
                    var7 = var11[var8];
                } else {
                    var7 = -1000;
                }

                for (var12 = 0; var12 < 10; ++var12) {
                    while (var12 == 0 && var7 > var4) {
                        this.method1665(var26[var8++]);
                        if (var8 == var9 && var26 != field1376[11]) {
                            var8 = 0;
                            var9 = field1398[11];
                            var26 = field1376[11];
                            var11 = field1391;
                        }

                        if (var8 < var9) {
                            var7 = var11[var8];
                        } else {
                            var7 = -1000;
                        }
                    }

                    while (var12 == 3 && var7 > var5) {
                        this.method1665(var26[var8++]);
                        if (var8 == var9 && var26 != field1376[11]) {
                            var8 = 0;
                            var9 = field1398[11];
                            var26 = field1376[11];
                            var11 = field1391;
                        }

                        if (var8 < var9) {
                            var7 = var11[var8];
                        } else {
                            var7 = -1000;
                        }
                    }

                    while (var12 == 5 && var7 > var6) {
                        this.method1665(var26[var8++]);
                        if (var8 == var9 && var26 != field1376[11]) {
                            var8 = 0;
                            var9 = field1398[11];
                            var26 = field1376[11];
                            var11 = field1391;
                        }

                        if (var8 < var9) {
                            var7 = var11[var8];
                        } else {
                            var7 = -1000;
                        }
                    }

                    var13 = field1398[var12];
                    int[] var24 = field1376[var12];

                    for (var15 = 0; var15 < var13; ++var15) {
                        this.method1665(var24[var15]);
                    }
                }

                while (var7 != -1000) {
                    this.method1665(var26[var8++]);
                    if (var8 == var9 && var26 != field1376[11]) {
                        var8 = 0;
                        var26 = field1376[11];
                        var9 = field1398[11];
                        var11 = field1391;
                    }

                    if (var8 < var9) {
                        var7 = var11[var8];
                    } else {
                        var7 = -1000;
                    }
                }

            }
        }
    }

    final void method1717(int var1) {
        int var2 = Rasterizer3D.field1447;
        int var3 = Rasterizer3D.field1448;
        int var4 = 0;
        int var5 = this.indices1[var1];
        int var6 = this.indices2[var1];
        int var7 = this.indices3[var1];
        int var8 = field1405[var5];
        int var9 = field1405[var6];
        int var10 = field1405[var7];
        if (this.field1381 == null) {
            Rasterizer3D.rasterAlpha = 0;
        } else {
            Rasterizer3D.rasterAlpha = this.field1381[var1] & 255;
        }

        int var11;
        int var12;
        int var13;
        int var14;
        if (var8 >= 50) {
            field1414[var4] = field1400[var5];
            field1415[var4] = field1401[var5];
            field1421[var4++] = this.field1412[var1];
        } else {
            var11 = field1403[var5];
            var12 = field1404[var5];
            var13 = this.field1412[var1];
            if (var10 >= 50) {
                var14 = (50 - var8) * field1428[var10 - var8];
                field1414[var4] = var2 + (var11 + ((field1403[var7] - var11) * var14 >> 16)) * Rasterizer3D.field1453 / 50;
                field1415[var4] = var3 + (var12 + ((field1404[var7] - var12) * var14 >> 16)) * Rasterizer3D.field1453 / 50;
                field1421[var4++] = var13 + ((this.field1379[var1] - var13) * var14 >> 16);
            }

            if (var9 >= 50) {
                var14 = (50 - var8) * field1428[var9 - var8];
                field1414[var4] = var2 + (var11 + ((field1403[var6] - var11) * var14 >> 16)) * Rasterizer3D.field1453 / 50;
                field1415[var4] = var3 + (var12 + ((field1404[var6] - var12) * var14 >> 16)) * Rasterizer3D.field1453 / 50;
                field1421[var4++] = var13 + ((this.field1416[var1] - var13) * var14 >> 16);
            }
        }

        if (var9 >= 50) {
            field1414[var4] = field1400[var6];
            field1415[var4] = field1401[var6];
            field1421[var4++] = this.field1416[var1];
        } else {
            var11 = field1403[var6];
            var12 = field1404[var6];
            var13 = this.field1416[var1];
            if (var8 >= 50) {
                var14 = (50 - var9) * field1428[var8 - var9];
                field1414[var4] = var2 + (var11 + ((field1403[var5] - var11) * var14 >> 16)) * Rasterizer3D.field1453 / 50;
                field1415[var4] = var3 + (var12 + ((field1404[var5] - var12) * var14 >> 16)) * Rasterizer3D.field1453 / 50;
                field1421[var4++] = var13 + ((this.field1412[var1] - var13) * var14 >> 16);
            }

            if (var10 >= 50) {
                var14 = (50 - var9) * field1428[var10 - var9];
                field1414[var4] = var2 + (var11 + ((field1403[var7] - var11) * var14 >> 16)) * Rasterizer3D.field1453 / 50;
                field1415[var4] = var3 + (var12 + ((field1404[var7] - var12) * var14 >> 16)) * Rasterizer3D.field1453 / 50;
                field1421[var4++] = var13 + ((this.field1379[var1] - var13) * var14 >> 16);
            }
        }

        if (var10 >= 50) {
            field1414[var4] = field1400[var7];
            field1415[var4] = field1401[var7];
            field1421[var4++] = this.field1379[var1];
        } else {
            var11 = field1403[var7];
            var12 = field1404[var7];
            var13 = this.field1379[var1];
            if (var9 >= 50) {
                var14 = (50 - var10) * field1428[var9 - var10];
                field1414[var4] = var2 + (var11 + ((field1403[var6] - var11) * var14 >> 16)) * Rasterizer3D.field1453 / 50;
                field1415[var4] = var3 + (var12 + ((field1404[var6] - var12) * var14 >> 16)) * Rasterizer3D.field1453 / 50;
                field1421[var4++] = var13 + ((this.field1416[var1] - var13) * var14 >> 16);
            }

            if (var8 >= 50) {
                var14 = (50 - var10) * field1428[var8 - var10];
                field1414[var4] = var2 + (var11 + ((field1403[var5] - var11) * var14 >> 16)) * Rasterizer3D.field1453 / 50;
                field1415[var4] = var3 + (var12 + ((field1404[var5] - var12) * var14 >> 16)) * Rasterizer3D.field1453 / 50;
                field1421[var4++] = var13 + ((this.field1412[var1] - var13) * var14 >> 16);
            }
        }

        var11 = field1414[0];
        var12 = field1414[1];
        var13 = field1414[2];
        var14 = field1415[0];
        int var15 = field1415[1];
        int var16 = field1415[2];
        Rasterizer3D.rasterClipEnable = false;
        int var17;
        int var18;
        int var19;
        int var20;
        if (var4 == 3) {
            if (var11 < 0 || var12 < 0 || var13 < 0 || var11 > Rasterizer3D.rasterClipX || var12 > Rasterizer3D.rasterClipX || var13 > Rasterizer3D.rasterClipX) {
                Rasterizer3D.rasterClipEnable = true;
            }

            if (this.faceTextures != null && this.faceTextures[var1] != -1) {
                if (this.triangleTextureIndex != null && this.triangleTextureIndex[var1] != -1) {
                    var20 = this.triangleTextureIndex[var1] & 255;
                    var17 = this.texTriangleX[var20];
                    var18 = this.texTriangleY[var20];
                    var19 = this.texTriangleZ[var20];
                } else {
                    var17 = var5;
                    var18 = var6;
                    var19 = var7;
                }

                if (this.field1379[var1] == -1) {
                    Rasterizer3D.rasterTextureAffine(var14, var15, var16, var11, var12, var13, this.field1412[var1], this.field1412[var1], this.field1412[var1], field1403[var17], field1403[var18], field1403[var19], field1404[var17], field1404[var18], field1404[var19], field1405[var17], field1405[var18], field1405[var19], this.faceTextures[var1]);
                } else {
                    Rasterizer3D.rasterTextureAffine(var14, var15, var16, var11, var12, var13, field1421[0], field1421[1], field1421[2], field1403[var17], field1403[var18], field1403[var19], field1404[var17], field1404[var18], field1404[var19], field1405[var17], field1405[var18], field1405[var19], this.faceTextures[var1]);
                }
            } else if (this.field1379[var1] == -1) {
                Rasterizer3D.rasterFlat(var14, var15, var16, var11, var12, var13, field1427[this.field1412[var1]]);
            } else {
                Rasterizer3D.rasterGouraud(var14, var15, var16, var11, var12, var13, field1421[0], field1421[1], field1421[2]);
            }
        }

        if (var4 == 4) {
            if (var11 < 0 || var12 < 0 || var13 < 0 || var11 > Rasterizer3D.rasterClipX || var12 > Rasterizer3D.rasterClipX || var13 > Rasterizer3D.rasterClipX || field1414[3] < 0 || field1414[3] > Rasterizer3D.rasterClipX) {
                Rasterizer3D.rasterClipEnable = true;
            }

            if (this.faceTextures != null && this.faceTextures[var1] != -1) {
                if (this.triangleTextureIndex != null && this.triangleTextureIndex[var1] != -1) {
                    var20 = this.triangleTextureIndex[var1] & 255;
                    var17 = this.texTriangleX[var20];
                    var18 = this.texTriangleY[var20];
                    var19 = this.texTriangleZ[var20];
                } else {
                    var17 = var5;
                    var18 = var6;
                    var19 = var7;
                }

                short var21 = this.faceTextures[var1];
                if (this.field1379[var1] == -1) {
                    Rasterizer3D.rasterTextureAffine(var14, var15, var16, var11, var12, var13, this.field1412[var1], this.field1412[var1], this.field1412[var1], field1403[var17], field1403[var18], field1403[var19], field1404[var17], field1404[var18], field1404[var19], field1405[var17], field1405[var18], field1405[var19], var21);
                    Rasterizer3D.rasterTextureAffine(var14, var16, field1415[3], var11, var13, field1414[3], this.field1412[var1], this.field1412[var1], this.field1412[var1], field1403[var17], field1403[var18], field1403[var19], field1404[var17], field1404[var18], field1404[var19], field1405[var17], field1405[var18], field1405[var19], var21);
                } else {
                    Rasterizer3D.rasterTextureAffine(var14, var15, var16, var11, var12, var13, field1421[0], field1421[1], field1421[2], field1403[var17], field1403[var18], field1403[var19], field1404[var17], field1404[var18], field1404[var19], field1405[var17], field1405[var18], field1405[var19], var21);
                    Rasterizer3D.rasterTextureAffine(var14, var16, field1415[3], var11, var13, field1414[3], field1421[0], field1421[2], field1421[3], field1403[var17], field1403[var18], field1403[var19], field1404[var17], field1404[var18], field1404[var19], field1405[var17], field1405[var18], field1405[var19], var21);
                }
            } else if (this.field1379[var1] == -1) {
                var17 = field1427[this.field1412[var1]];
                Rasterizer3D.rasterFlat(var14, var15, var16, var11, var12, var13, var17);
                Rasterizer3D.rasterFlat(var14, var16, field1415[3], var11, var13, field1414[3], var17);
            } else {
                Rasterizer3D.rasterGouraud(var14, var15, var16, var11, var12, var13, field1421[0], field1421[1], field1421[2]);
                Rasterizer3D.rasterGouraud(var14, var16, field1415[3], var11, var13, field1414[3], field1421[0], field1421[2], field1421[3]);
            }
        }

    }
}
