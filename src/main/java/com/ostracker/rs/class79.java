package com.ostracker.rs;

import net.runelite.cache.fs.Index;

public class class79 extends Node {

    public static int[] field2013;
    public static int[] field2775;
    public static int field3234;
    public static int[] field3235;
    public static int[] field3236;
    public static int[] field3237;
    public static byte[][] field211;
    public static int field2;
    static int field3233;

    int field1312;
    int[] field1314;
    boolean field1315;
    boolean field1317;
    int[] field1319;
    int[] field1320;
    int[] field1321;
    int[] field1324;

    class79(Buffer var1) {
        this.field1312 = var1.readUnsignedShort();
        this.field1317 = var1.readUnsignedByte() == 1;
        int var2 = var1.readUnsignedByte();
        if (var2 >= 1 && var2 <= 4) {
            this.field1314 = new int[var2];

            int var3;
            for (var3 = 0; var3 < var2; ++var3) {
                this.field1314[var3] = var1.readUnsignedShort();
            }

            if (var2 > 1) {
                this.field1319 = new int[var2 - 1];

                for (var3 = 0; var3 < var2 - 1; ++var3) {
                    this.field1319[var3] = var1.readUnsignedByte();
                }
            }

            if (var2 > 1) {
                this.field1320 = new int[var2 - 1];

                for (var3 = 0; var3 < var2 - 1; ++var3) {
                    this.field1320[var3] = var1.readUnsignedByte();
                }
            }

            this.field1321 = new int[var2];

            for (var3 = 0; var3 < var2; ++var3) {
                this.field1321[var3] = var1.readInt();
            }

            var1.readUnsignedByte();
            var1.readUnsignedByte();
            this.field1324 = null;
        } else {
            throw new RuntimeException();
        }
    }

    public static void method1986() {
        field3236 = null;
        field2013 = null;
        field2775 = null;
        field3235 = null;
        field3237 = null;
        field211 = null;
    }

    public static void method2186(byte[] var0) {
        Buffer var1 = new Buffer(var0);
        var1.offset = var0.length - 2;
        field3233 = var1.readUnsignedShort();
        field3236 = new int[field3233];
        field2013 = new int[field3233];
        field2775 = new int[field3233];
        field3235 = new int[field3233];
        field211 = new byte[field3233][];
        var1.offset = var0.length - 7 - field3233 * 8;
        field3234 = var1.readUnsignedShort();
        field2 = var1.readUnsignedShort();
        int var2 = (var1.readUnsignedByte() & 255) + 1;

        int var3;
        for (var3 = 0; var3 < field3233; ++var3) {
            field3236[var3] = var1.readUnsignedShort();
        }

        for (var3 = 0; var3 < field3233; ++var3) {
            field2013[var3] = var1.readUnsignedShort();
        }

        for (var3 = 0; var3 < field3233; ++var3) {
            field2775[var3] = var1.readUnsignedShort();
        }

        for (var3 = 0; var3 < field3233; ++var3) {
            field3235[var3] = var1.readUnsignedShort();
        }

        var1.offset = var0.length - 7 - field3233 * 8 - 3 * (var2 - 1);
        field3237 = new int[var2];

        for (var3 = 1; var3 < var2; ++var3) {
            field3237[var3] = var1.read24BitInt();
            if (field3237[var3] == 0) {
                field3237[var3] = 1;
            }
        }

        var1.offset = 0;

        for (var3 = 0; var3 < field3233; ++var3) {
            int var4 = field2775[var3];
            int var5 = field3235[var3];
            int var6 = var5 * var4;
            byte[] var7 = new byte[var6];
            field211[var3] = var7;
            int var8 = var1.readUnsignedByte();
            int var9;
            if (var8 == 0) {
                for (var9 = 0; var9 < var6; ++var9) {
                    var7[var9] = var1.readByte();
                }
            } else if (var8 == 1) {
                for (var9 = 0; var9 < var4; ++var9) {
                    for (int var10 = 0; var10 < var5; ++var10) {
                        var7[var4 * var10 + var9] = var1.readByte();
                    }
                }
            }
        }
    }

    public void method1635() {
        this.field1324 = null;
    }

    boolean method1628(double var1, int var3, Index var4) {
        int var5;
        for (var5 = 0; var5 < this.field1314.length; ++var5) {
            int i = this.field1314[var5];
            byte[] item;

            if (var4.getArchives().size() == 1) {
                item = var4.getArchive(0)
                        .getFiles()
                        .get(i)
                        .getContents();
            } else {
                item = var4.getArchive(i)
                        .getFiles()
                        .get(0)
                        .getContents();
            }

            if (item == null) {
                return false;
            }
        }

        var5 = var3 * var3;
        this.field1324 = new int[var5];

        for (int var6 = 0; var6 < this.field1314.length; ++var6) {
            int var8 = this.field1314[var6];
            byte[] var10 = var4
                    .getArchive(var8)
                    .getFiles()
                    .get(0)
                    .getContents();
            boolean var9;
            if (null == var10) {
                var9 = false;
            } else {
                method2186(var10);
                var9 = true;
            }

            ModIcon var7;
            if (!var9) {
                var7 = null;
            } else {
                ModIcon var11 = new ModIcon();
                var11.width = field3234;
                var11.originalHeight = field2;
                var11.offsetX = field3236[0];
                var11.offsetY = field2013[0];
                var11.originalWidth = field2775[0];
                var11.height = field3235[0];
                var11.palette = field3237;
                var11.pixels = field211[0];
                method1986();
                var7 = var11;
            }

            var7.method4113();
            var10 = var7.pixels;
            int[] var18 = var7.palette;
            int var12 = this.field1321[var6];
            if ((var12 & -16777216) == 16777216) {
            }

            if ((var12 & -16777216) == 33554432) {
            }

            int var13;
            int var14;
            int var15;
            int var16;
            if ((var12 & -16777216) == 50331648) {
                var13 = var12 & 16711935;
                var14 = var12 >> 8 & 255;

                for (var15 = 0; var15 < var18.length; ++var15) {
                    var16 = var18[var15];
                    if ((var16 & '\uffff') == var16 >> 8) {
                        var16 &= 255;
                        var18[var15] = var13 * var16 >> 8 & 16711935 | var14 * var16 & '\uff00';
                    }
                }
            }

            for (var13 = 0; var13 < var18.length; ++var13) {
                var18[var13] = Rasterizer3D.method1730(var18[var13], var1);
            }

            if (var6 == 0) {
                var13 = 0;
            } else {
                var13 = this.field1319[var6 - 1];
            }

            if (var6 == 0) {
            }

            if (var13 == 0) {
                if (var7.originalWidth == var3) {
                    for (var14 = 0; var14 < var5; ++var14) {
                        this.field1324[var14] = var18[var10[var14] & 255];
                    }
                } else if (var7.originalWidth == 64 && var3 == 128) {
                    var14 = 0;

                    for (var15 = 0; var15 < var3; ++var15) {
                        for (var16 = 0; var16 < var3; ++var16) {
                            this.field1324[var14++] = var18[var10[(var16 >> 1) + (var15 >> 1 << 6)] & 255];
                        }
                    }
                } else {
                    if (var7.originalWidth != 128 || var3 != 64) {
                        throw new RuntimeException();
                    }

                    var14 = 0;

                    for (var15 = 0; var15 < var3; ++var15) {
                        for (var16 = 0; var16 < var3; ++var16) {
                            this.field1324[var14++] = var18[var10[(var16 << 1) + (var15 << 1 << 7)] & 255];
                        }
                    }
                }
            }

            if (var13 == 1) {
            }

            if (var13 == 2) {
            }

            if (var13 == 3) {
            }
        }

        return true;
    }
}
