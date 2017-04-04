package com.ostracker.rs;

import net.runelite.cache.models.FaceNormal;
import net.runelite.cache.models.VertexNormal;

public class ModelData extends Renderable {

    public int vertexCount = 0;
    public int[] vertexX;
    public int[] vertexY;
    public int[] vertexZ;
    public VertexNormal[] normals;

    public int triangleFaceCount = 0;
    public int[] trianglePointsX;
    public int[] trianglePointsY;
    public int[] trianglePointsZ;
    public byte[] faceAlphas;
    public short[] faceColor;
    public FaceNormal[] faceNormals;
    public byte[] faceRenderType;
    public byte[] faceRenderPriorities;

    public int texturePointCount;
    public byte[] textureCoords;
    public short[] faceTextures;
    public short[] texTriangleX;
    public short[] texTriangleY;
    public short[] texTriangleZ;
    short[] texturePrimaryColor;
    byte[] textureRenderTypes;

    short[] field1254;

    short[] field1256;

    short[] field1257;

    byte[] field1259;

    int[] vertexSkins;

    int[] triangleSkinValues;

    int[][] field1262;

    int[][] field1263;

    public VertexNormal[] normals2;

    short[] field1271;

    short[] field1274;

    public float[][] textureUCoords;
    public float[][] textureVCoords;

    public ModelData(byte[] var1) {
        if (var1[var1.length - 1] == -1 && var1[var1.length - 2] == -1) {
            this.method1621(var1);
        } else {
            this.method1597(var1);
        }

    }

    static final int method1568(int var0, int var1) {
        var1 = var1 * (var0 & 127) >> 7;
        if (var1 < 2) {
            var1 = 2;
        } else if (var1 > 126) {
            var1 = 126;
        }

        return (var0 & 'ﾀ') + var1;
    }

    static final int method1569(int var0) {
        if (var0 < 2) {
            var0 = 2;
        } else if (var0 > 126) {
            var0 = 126;
        }

        return var0;
    }

    void computeAnimationTables() {
        int[] var1;
        int var2;
        int var3;
        int var4;
        if (this.vertexSkins != null) {
            var1 = new int[256];
            var2 = 0;

            for (var3 = 0; var3 < this.vertexCount; ++var3) {
                var4 = this.vertexSkins[var3];
                ++var1[var4];
                if (var4 > var2) {
                    var2 = var4;
                }
            }

            this.field1262 = new int[var2 + 1][];

            for (var3 = 0; var3 <= var2; ++var3) {
                this.field1262[var3] = new int[var1[var3]];
                var1[var3] = 0;
            }

            for (var3 = 0; var3 < this.vertexCount; this.field1262[var4][var1[var4]++] = var3++) {
                var4 = this.vertexSkins[var3];
            }

            this.vertexSkins = null;
        }

        if (this.triangleSkinValues != null) {
            var1 = new int[256];
            var2 = 0;

            for (var3 = 0; var3 < this.triangleFaceCount; ++var3) {
                var4 = this.triangleSkinValues[var3];
                ++var1[var4];
                if (var4 > var2) {
                    var2 = var4;
                }
            }

            this.field1263 = new int[var2 + 1][];

            for (var3 = 0; var3 <= var2; ++var3) {
                this.field1263[var3] = new int[var1[var3]];
                var1[var3] = 0;
            }

            for (var3 = 0; var3 < this.triangleFaceCount; this.field1263[var4][var1[var4]++] = var3++) {
                var4 = this.triangleSkinValues[var3];
            }

            this.triangleSkinValues = null;
        }

    }

    public void method1562(int var1, int var2, int var3) {
        for (int var4 = 0; var4 < this.vertexCount; ++var4) {
            this.vertexX[var4] = this.vertexX[var4] * (var1 / 128);
            this.vertexY[var4] = this.vertexY[var4] * (var1 / 128);
            this.vertexZ[var4] = this.vertexZ[var4] * (var1 / 128);
        }

        this.method1564();
    }

    public void computeNormals() {
        if (this.normals == null) {
            this.normals = new VertexNormal[this.vertexCount];

            int var1;
            for (var1 = 0; var1 < this.vertexCount; ++var1) {
                this.normals[var1] = new VertexNormal();
            }

            for (var1 = 0; var1 < this.triangleFaceCount; ++var1) {
                int var2 = this.trianglePointsX[var1];
                int var3 = this.trianglePointsY[var1];
                int var4 = this.trianglePointsZ[var1];
                int var5 = this.vertexX[var3] - this.vertexX[var2];
                int var6 = this.vertexY[var3] - this.vertexY[var2];
                int var7 = this.vertexZ[var3] - this.vertexZ[var2];
                int var8 = this.vertexX[var4] - this.vertexX[var2];
                int var9 = this.vertexY[var4] - this.vertexY[var2];
                int var10 = this.vertexZ[var4] - this.vertexZ[var2];
                int var11 = var6 * var10 - var9 * var7;
                int var12 = var7 * var8 - var10 * var5;

                int var13;
                for (var13 = var5 * var9 - var8 * var6; var11 > 8192 || var12 > 8192 || var13 > 8192 || var11 < -8192 || var12 < -8192 || var13 < -8192; var13 >>= 1) {
                    var11 >>= 1;
                    var12 >>= 1;
                }

                int var14 = (int) Math.sqrt((double) (var11 * var11 + var12 * var12 + var13 * var13));
                if (var14 <= 0) {
                    var14 = 1;
                }

                var11 = var11 * 256 / var14;
                var12 = var12 * 256 / var14;
                var13 = var13 * 256 / var14;
                byte var15;
                if (this.faceRenderType == null) {
                    var15 = 0;
                } else {
                    var15 = this.faceRenderType[var1];
                }

                if (var15 == 0) {
                    VertexNormal var16 = this.normals[var2];
                    var16.x += var11;
                    var16.y += var12;
                    var16.z += var13;
                    ++var16.magnitude;
                    var16 = this.normals[var3];
                    var16.x += var11;
                    var16.y += var12;
                    var16.z += var13;
                    ++var16.magnitude;
                    var16 = this.normals[var4];
                    var16.x += var11;
                    var16.y += var12;
                    var16.z += var13;
                    ++var16.magnitude;
                } else if (var15 == 1) {
                    if (this.faceNormals == null) {
                        this.faceNormals = new FaceNormal[this.triangleFaceCount];
                    }

                    FaceNormal var17 = this.faceNormals[var1] = new FaceNormal();
                    var17.x = var11;
                    var17.y = var12;
                    var17.z = var13;
                }
            }

        }
    }

    void method1564() {
        this.normals = null;
        this.normals2 = null;
        this.faceNormals = null;
    }

    void method1597(byte[] var1) {
        boolean var2 = false;
        boolean var3 = false;
        Buffer var4 = new Buffer(var1);
        Buffer var5 = new Buffer(var1);
        Buffer var6 = new Buffer(var1);
        Buffer var7 = new Buffer(var1);
        Buffer var8 = new Buffer(var1);
        var4.offset = var1.length - 18;
        int var9 = var4.readUnsignedShort();
        int var10 = var4.readUnsignedShort();
        int var11 = var4.readUnsignedByte();
        int var12 = var4.readUnsignedByte();
        int var13 = var4.readUnsignedByte();
        int var14 = var4.readUnsignedByte();
        int var15 = var4.readUnsignedByte();
        int var16 = var4.readUnsignedByte();
        int var17 = var4.readUnsignedShort();
        int var18 = var4.readUnsignedShort();
        int var19 = var4.readUnsignedShort();
        int var20 = var4.readUnsignedShort();
        byte var21 = 0;
        int var45 = var21 + var9;
        int var23 = var45;
        var45 += var10;
        int var24 = var45;
        if (var13 == 255) {
            var45 += var10;
        }

        int var25 = var45;
        if (var15 == 1) {
            var45 += var10;
        }

        int var26 = var45;
        if (var12 == 1) {
            var45 += var10;
        }

        int var27 = var45;
        if (var16 == 1) {
            var45 += var9;
        }

        int var28 = var45;
        if (var14 == 1) {
            var45 += var10;
        }

        int var29 = var45;
        var45 += var20;
        int var30 = var45;
        var45 += var10 * 2;
        int var31 = var45;
        var45 += var11 * 6;
        int var32 = var45;
        var45 += var17;
        int var33 = var45;
        var45 += var18;
        this.vertexCount = var9;
        this.triangleFaceCount = var10;
        this.texturePointCount = var11;
        this.vertexX = new int[var9];
        this.vertexY = new int[var9];
        this.vertexZ = new int[var9];
        this.trianglePointsX = new int[var10];
        this.trianglePointsY = new int[var10];
        this.trianglePointsZ = new int[var10];
        if (var11 > 0) {
            this.textureRenderTypes = new byte[var11];
            this.texTriangleX = new short[var11];
            this.texTriangleY = new short[var11];
            this.texTriangleZ = new short[var11];
        }

        if (var16 == 1) {
            this.vertexSkins = new int[var9];
        }

        if (var12 == 1) {
            this.faceRenderType = new byte[var10];
            this.textureCoords = new byte[var10];
            this.faceTextures = new short[var10];
        }

        if (var13 == 255) {
            this.faceRenderPriorities = new byte[var10];
        } else {
        }

        if (var14 == 1) {
            this.faceAlphas = new byte[var10];
        }

        if (var15 == 1) {
            this.triangleSkinValues = new int[var10];
        }

        this.faceColor = new short[var10];
        var4.offset = var21;
        var5.offset = var32;
        var6.offset = var33;
        var7.offset = var45;
        var8.offset = var27;
        int var35 = 0;
        int var36 = 0;
        int var37 = 0;

        int var38;
        int var39;
        int var40;
        int var41;
        int var42;
        for (var38 = 0; var38 < var9; ++var38) {
            var39 = var4.readUnsignedByte();
            var40 = 0;
            if ((var39 & 1) != 0) {
                var40 = var5.readShortSmart();
            }

            var41 = 0;
            if ((var39 & 2) != 0) {
                var41 = var6.readShortSmart();
            }

            var42 = 0;
            if ((var39 & 4) != 0) {
                var42 = var7.readShortSmart();
            }

            this.vertexX[var38] = var35 + var40;
            this.vertexY[var38] = var36 + var41;
            this.vertexZ[var38] = var37 + var42;
            var35 = this.vertexX[var38];
            var36 = this.vertexY[var38];
            var37 = this.vertexZ[var38];
            if (var16 == 1) {
                this.vertexSkins[var38] = var8.readUnsignedByte();
            }
        }

        var4.offset = var30;
        var5.offset = var26;
        var6.offset = var24;
        var7.offset = var28;
        var8.offset = var25;

        for (var38 = 0; var38 < var10; ++var38) {
            this.faceColor[var38] = (short) var4.readUnsignedShort();
            if (var12 == 1) {
                var39 = var5.readUnsignedByte();
                if ((var39 & 1) == 1) {
                    this.faceRenderType[var38] = 1;
                    var2 = true;
                } else {
                    this.faceRenderType[var38] = 0;
                }

                if ((var39 & 2) == 2) {
                    this.textureCoords[var38] = (byte) (var39 >> 2);
                    this.faceTextures[var38] = this.faceColor[var38];
                    this.faceColor[var38] = 127;
                    if (this.faceTextures[var38] != -1) {
                        var3 = true;
                    }
                } else {
                    this.textureCoords[var38] = -1;
                    this.faceTextures[var38] = -1;
                }
            }

            if (var13 == 255) {
                this.faceRenderPriorities[var38] = var6.readByte();
            }

            if (var14 == 1) {
                this.faceAlphas[var38] = var7.readByte();
            }

            if (var15 == 1) {
                this.triangleSkinValues[var38] = var8.readUnsignedByte();
            }
        }

        var4.offset = var29;
        var5.offset = var23;
        var38 = 0;
        var39 = 0;
        var40 = 0;
        var41 = 0;

        int var43;
        int var44;
        for (var42 = 0; var42 < var10; ++var42) {
            var43 = var5.readUnsignedByte();
            if (var43 == 1) {
                var38 = var4.readShortSmart() + var41;
                var39 = var4.readShortSmart() + var38;
                var40 = var4.readShortSmart() + var39;
                var41 = var40;
                this.trianglePointsX[var42] = var38;
                this.trianglePointsY[var42] = var39;
                this.trianglePointsZ[var42] = var40;
            }

            if (var43 == 2) {
                var39 = var40;
                var40 = var4.readShortSmart() + var41;
                var41 = var40;
                this.trianglePointsX[var42] = var38;
                this.trianglePointsY[var42] = var39;
                this.trianglePointsZ[var42] = var40;
            }

            if (var43 == 3) {
                var38 = var40;
                var40 = var4.readShortSmart() + var41;
                var41 = var40;
                this.trianglePointsX[var42] = var38;
                this.trianglePointsY[var42] = var39;
                this.trianglePointsZ[var42] = var40;
            }

            if (var43 == 4) {
                var44 = var38;
                var38 = var39;
                var39 = var44;
                var40 = var4.readShortSmart() + var41;
                var41 = var40;
                this.trianglePointsX[var42] = var38;
                this.trianglePointsY[var42] = var44;
                this.trianglePointsZ[var42] = var40;
            }
        }

        var4.offset = var31;

        for (var42 = 0; var42 < var11; ++var42) {
            this.textureRenderTypes[var42] = 0;
            this.texTriangleX[var42] = (short) var4.readUnsignedShort();
            this.texTriangleY[var42] = (short) var4.readUnsignedShort();
            this.texTriangleZ[var42] = (short) var4.readUnsignedShort();
        }

        if (this.textureCoords != null) {
            boolean var46 = false;

            for (var43 = 0; var43 < var10; ++var43) {
                var44 = this.textureCoords[var43] & 255;
                if (var44 != 255) {
                    if ((this.texTriangleX[var44] & '\uffff') == this.trianglePointsX[var43]
                            && (this.texTriangleY[var44] & '\uffff') == this.trianglePointsY[var43]
                            && (this.texTriangleZ[var44] & '\uffff') == this.trianglePointsZ[var43]) {
                        this.textureCoords[var43] = -1;
                    } else {
                        var46 = true;
                    }
                }
            }

            if (!var46) {
                this.textureCoords = null;
            }
        }

        if (!var3) {
            this.faceTextures = null;
        }

        if (!var2) {
            this.faceRenderType = null;
        }

    }

    public void method1612(short var1, short var2) {
        if (this.faceTextures != null) {
            for (int var3 = 0; var3 < this.triangleFaceCount; ++var3) {
                if (this.faceTextures[var3] == var1) {
                    this.faceTextures[var3] = var2;
                }
            }

        }
    }

    public void method1617(short var1, short var2) {
        for (int var3 = 0; var3 < this.triangleFaceCount; ++var3) {
            if (this.faceColor[var3] == var1) {
                this.faceColor[var3] = var2;
            }
        }

    }

    void method1621(byte[] var1) {
        Buffer var2 = new Buffer(var1);
        Buffer var3 = new Buffer(var1);
        Buffer var4 = new Buffer(var1);
        Buffer var5 = new Buffer(var1);
        Buffer var6 = new Buffer(var1);
        Buffer var7 = new Buffer(var1);
        Buffer var8 = new Buffer(var1);
        var2.offset = var1.length - 23;
        int var9 = var2.readUnsignedShort();
        int var10 = var2.readUnsignedShort();
        int var11 = var2.readUnsignedByte();
        int var12 = var2.readUnsignedByte();
        int var13 = var2.readUnsignedByte();
        int var14 = var2.readUnsignedByte();
        int var15 = var2.readUnsignedByte();
        int var16 = var2.readUnsignedByte();
        int var17 = var2.readUnsignedByte();
        int var18 = var2.readUnsignedShort();
        int var19 = var2.readUnsignedShort();
        int var20 = var2.readUnsignedShort();
        int var21 = var2.readUnsignedShort();
        int var22 = var2.readUnsignedShort();
        int var23 = 0;
        int var24 = 0;
        int var25 = 0;
        int var26;
        if (var11 > 0) {
            this.textureRenderTypes = new byte[var11];
            var2.offset = 0;

            for (var26 = 0; var26 < var11; ++var26) {
                byte var27 = this.textureRenderTypes[var26] = var2.readByte();
                if (var27 == 0) {
                    ++var23;
                }

                if (var27 >= 1 && var27 <= 3) {
                    ++var24;
                }

                if (var27 == 2) {
                    ++var25;
                }
            }
        }

        var26 = var11 + var9;
        int var28 = var26;
        if (var12 == 1) {
            var26 += var10;
        }

        int var29 = var26;
        var26 += var10;
        int var30 = var26;
        if (var13 == 255) {
            var26 += var10;
        }

        int var31 = var26;
        if (var15 == 1) {
            var26 += var10;
        }

        int var32 = var26;
        if (var17 == 1) {
            var26 += var9;
        }

        int var33 = var26;
        if (var14 == 1) {
            var26 += var10;
        }

        int var34 = var26;
        var26 += var21;
        int var35 = var26;
        if (var16 == 1) {
            var26 += var10 * 2;
        }

        int var36 = var26;
        var26 += var22;
        int var37 = var26;
        var26 += var10 * 2;
        int var38 = var26;
        var26 += var18;
        int var39 = var26;
        var26 += var19;
        int var40 = var26;
        var26 += var20;
        int var41 = var26;
        var26 += var23 * 6;
        int var42 = var26;
        var26 += var24 * 6;
        int var43 = var26;
        var26 += var24 * 6;
        int var44 = var26;
        var26 += var24 * 2;
        int var45 = var26;
        var26 += var24;
        int var46 = var26;
        var26 += var24 * 2 + var25 * 2;
        this.vertexCount = var9;
        this.triangleFaceCount = var10;
        this.texturePointCount = var11;
        this.vertexX = new int[var9];
        this.vertexY = new int[var9];
        this.vertexZ = new int[var9];
        this.trianglePointsX = new int[var10];
        this.trianglePointsY = new int[var10];
        this.trianglePointsZ = new int[var10];
        if (var17 == 1) {
            this.vertexSkins = new int[var9];
        }

        if (var12 == 1) {
            this.faceRenderType = new byte[var10];
        }

        if (var13 == 255) {
            this.faceRenderPriorities = new byte[var10];
        } else {
        }

        if (var14 == 1) {
            this.faceAlphas = new byte[var10];
        }

        if (var15 == 1) {
            this.triangleSkinValues = new int[var10];
        }

        if (var16 == 1) {
            this.faceTextures = new short[var10];
        }

        if (var16 == 1 && var11 > 0) {
            this.textureCoords = new byte[var10];
        }

        this.faceColor = new short[var10];
        if (var11 > 0) {
            this.texTriangleX = new short[var11];
            this.texTriangleY = new short[var11];
            this.texTriangleZ = new short[var11];
            if (var24 > 0) {
                this.field1271 = new short[var24];
                this.field1254 = new short[var24];
                this.field1274 = new short[var24];
                this.field1256 = new short[var24];
                this.field1259 = new byte[var24];
                this.field1257 = new short[var24];
            }

            if (var25 > 0) {
                this.texturePrimaryColor = new short[var25];
            }
        }

        var2.offset = var11;
        var3.offset = var38;
        var4.offset = var39;
        var5.offset = var40;
        var6.offset = var32;
        int var48 = 0;
        int var49 = 0;
        int var50 = 0;

        int var51;
        int var52;
        int var53;
        int var54;
        int var55;
        for (var51 = 0; var51 < var9; ++var51) {
            var52 = var2.readUnsignedByte();
            var53 = 0;
            if ((var52 & 1) != 0) {
                var53 = var3.readShortSmart();
            }

            var54 = 0;
            if ((var52 & 2) != 0) {
                var54 = var4.readShortSmart();
            }

            var55 = 0;
            if ((var52 & 4) != 0) {
                var55 = var5.readShortSmart();
            }

            this.vertexX[var51] = var48 + var53;
            this.vertexY[var51] = var49 + var54;
            this.vertexZ[var51] = var50 + var55;
            var48 = this.vertexX[var51];
            var49 = this.vertexY[var51];
            var50 = this.vertexZ[var51];
            if (var17 == 1) {
                this.vertexSkins[var51] = var6.readUnsignedByte();
            }
        }

        var2.offset = var37;
        var3.offset = var28;
        var4.offset = var30;
        var5.offset = var33;
        var6.offset = var31;
        var7.offset = var35;
        var8.offset = var36;

        for (var51 = 0; var51 < var10; ++var51) {
            this.faceColor[var51] = (short) var2.readUnsignedShort();
            if (var12 == 1) {
                this.faceRenderType[var51] = var3.readByte();
            }

            if (var13 == 255) {
                this.faceRenderPriorities[var51] = var4.readByte();
            }

            if (var14 == 1) {
                this.faceAlphas[var51] = var5.readByte();
            }

            if (var15 == 1) {
                this.triangleSkinValues[var51] = var6.readUnsignedByte();
            }

            if (var16 == 1) {
                this.faceTextures[var51] = (short) (var7.readUnsignedShort() - 1);
            }

            if (this.textureCoords != null && this.faceTextures[var51] != -1) {
                this.textureCoords[var51] = (byte) (var8.readUnsignedByte() - 1);
            }
        }

        var2.offset = var34;
        var3.offset = var29;
        var51 = 0;
        var52 = 0;
        var53 = 0;
        var54 = 0;

        int var56;
        for (var55 = 0; var55 < var10; ++var55) {
            var56 = var3.readUnsignedByte();
            if (var56 == 1) {
                var51 = var2.readShortSmart() + var54;
                var52 = var2.readShortSmart() + var51;
                var53 = var2.readShortSmart() + var52;
                var54 = var53;
                this.trianglePointsX[var55] = var51;
                this.trianglePointsY[var55] = var52;
                this.trianglePointsZ[var55] = var53;
            }

            if (var56 == 2) {
                var52 = var53;
                var53 = var2.readShortSmart() + var54;
                var54 = var53;
                this.trianglePointsX[var55] = var51;
                this.trianglePointsY[var55] = var52;
                this.trianglePointsZ[var55] = var53;
            }

            if (var56 == 3) {
                var51 = var53;
                var53 = var2.readShortSmart() + var54;
                var54 = var53;
                this.trianglePointsX[var55] = var51;
                this.trianglePointsY[var55] = var52;
                this.trianglePointsZ[var55] = var53;
            }

            if (var56 == 4) {
                int var57 = var51;
                var51 = var52;
                var52 = var57;
                var53 = var2.readShortSmart() + var54;
                var54 = var53;
                this.trianglePointsX[var55] = var51;
                this.trianglePointsY[var55] = var57;
                this.trianglePointsZ[var55] = var53;
            }
        }

        var2.offset = var41;
        var3.offset = var42;
        var4.offset = var43;
        var5.offset = var44;
        var6.offset = var45;
        var7.offset = var46;

        for (var55 = 0; var55 < var11; ++var55) {
            var56 = this.textureRenderTypes[var55] & 255;
            if (var56 == 0) {
                this.texTriangleX[var55] = (short) var2.readUnsignedShort();
                this.texTriangleY[var55] = (short) var2.readUnsignedShort();
                this.texTriangleZ[var55] = (short) var2.readUnsignedShort();
            }

            if (var56 == 1) {
                this.texTriangleX[var55] = (short) var3.readUnsignedShort();
                this.texTriangleY[var55] = (short) var3.readUnsignedShort();
                this.texTriangleZ[var55] = (short) var3.readUnsignedShort();
                this.field1271[var55] = (short) var4.readUnsignedShort();
                this.field1254[var55] = (short) var4.readUnsignedShort();
                this.field1274[var55] = (short) var4.readUnsignedShort();
                this.field1256[var55] = (short) var5.readUnsignedShort();
                this.field1259[var55] = var6.readByte();
                this.field1257[var55] = (short) var7.readUnsignedShort();
            }

            if (var56 == 2) {
                this.texTriangleX[var55] = (short) var3.readUnsignedShort();
                this.texTriangleY[var55] = (short) var3.readUnsignedShort();
                this.texTriangleZ[var55] = (short) var3.readUnsignedShort();
                this.field1271[var55] = (short) var4.readUnsignedShort();
                this.field1254[var55] = (short) var4.readUnsignedShort();
                this.field1274[var55] = (short) var4.readUnsignedShort();
                this.field1256[var55] = (short) var5.readUnsignedShort();
                this.field1259[var55] = var6.readByte();
                this.field1257[var55] = (short) var7.readUnsignedShort();
                this.texturePrimaryColor[var55] = (short) var7.readUnsignedShort();
            }

            if (var56 == 3) {
                this.texTriangleX[var55] = (short) var3.readUnsignedShort();
                this.texTriangleY[var55] = (short) var3.readUnsignedShort();
                this.texTriangleZ[var55] = (short) var3.readUnsignedShort();
                this.field1271[var55] = (short) var4.readUnsignedShort();
                this.field1254[var55] = (short) var4.readUnsignedShort();
                this.field1274[var55] = (short) var4.readUnsignedShort();
                this.field1256[var55] = (short) var5.readUnsignedShort();
                this.field1259[var55] = var6.readByte();
                this.field1257[var55] = (short) var7.readUnsignedShort();
            }
        }

        var2.offset = var26;
        var55 = var2.readUnsignedByte();
        if (var55 != 0) {
            var2.readUnsignedShort();
            var2.readUnsignedShort();
            var2.readUnsignedShort();
            var2.readInt();
        }

    }

    public final Model method1624(int var1, int var2, int var3, int var4, int var5) {
        this.computeNormals();
        int var6 = (int) Math.sqrt((double) (var3 * var3 + var4 * var4 + var5 * var5));
        int var7 = var2 * var6 >> 8;
        Model var8 = new Model();
        var8.field1412 = new int[this.triangleFaceCount];
        var8.field1416 = new int[this.triangleFaceCount];
        var8.field1379 = new int[this.triangleFaceCount];
        if (this.texturePointCount > 0 && this.textureCoords != null) {
            // var16 contains how many faces use a specific texture
            int[] var16 = new int[this.texturePointCount];

            int var10;
            for (var10 = 0; var10 < this.triangleFaceCount; ++var10) {
                if (this.textureCoords[var10] != -1) {
                    ++var16[this.textureCoords[var10] & 255];
                }
            }

            var8.field1385 = 0;

            for (var10 = 0; var10 < this.texturePointCount; ++var10) {
                if (var16[var10] > 0 && this.textureRenderTypes[var10] == 0) {
                    ++var8.field1385;
                }
            }

            var8.texTriangleX = new int[var8.field1385];
            var8.texTriangleY = new int[var8.field1385];
            var8.texTriangleZ = new int[var8.field1385];
            var10 = 0;

            int var11;
            for (var11 = 0; var11 < this.texturePointCount; ++var11) {
                if (var16[var11] > 0 && this.textureRenderTypes[var11] == 0) {
                    var8.texTriangleX[var10] = this.texTriangleX[var11] & '\uffff';
                    var8.texTriangleY[var10] = this.texTriangleY[var11] & '\uffff';
                    var8.texTriangleZ[var10] = this.texTriangleZ[var11] & '\uffff';
                    var16[var11] = var10++;
                } else {
                    var16[var11] = -1;
                }
            }

            var8.triangleTextureIndex = new byte[this.triangleFaceCount];

            for (var11 = 0; var11 < this.triangleFaceCount; ++var11) {
                if (this.textureCoords[var11] != -1) {
                    var8.triangleTextureIndex[var11] = (byte) var16[this.textureCoords[var11] & 255];
                } else {
                    var8.triangleTextureIndex[var11] = -1;
                }
            }
        }

        for (int var9 = 0; var9 < this.triangleFaceCount; ++var9) {
            byte var17;
            if (this.faceRenderType == null) {
                var17 = 0;
            } else {
                var17 = this.faceRenderType[var9];
            }

            byte var18;
            if (this.faceAlphas == null) {
                var18 = 0;
            } else {
                var18 = this.faceAlphas[var9];
            }

            short var12;
            if (this.faceTextures == null) {
                var12 = -1;
            } else {
                var12 = this.faceTextures[var9];
            }

            if (var18 == -2) {
                var17 = 3;
            }

            if (var18 == -1) {
                var17 = 2;
            }

            VertexNormal var13;
            int var14;
            FaceNormal var19;
            if (var12 == -1) {
                if (var17 != 0) {
                    if (var17 == 1) {
                        var19 = this.faceNormals[var9];
                        var14 = var1 + (var3 * var19.x + var4 * var19.y + var5 * var19.z) / (var7 + var7 / 2);
                        var8.field1412[var9] = method1568(this.faceColor[var9] & '\uffff', var14);
                        var8.field1379[var9] = -1;
                    } else if (var17 == 3) {
                        var8.field1412[var9] = 128;
                        var8.field1379[var9] = -1;
                    } else {
                        var8.field1379[var9] = -2;
                    }
                } else {
                    int var15 = this.faceColor[var9] & '\uffff';
                    if (this.normals2 != null && this.normals2[this.trianglePointsX[var9]] != null) {
                        var13 = this.normals2[this.trianglePointsX[var9]];
                    } else {
                        var13 = this.normals[this.trianglePointsX[var9]];
                    }

                    var14 = var1 + (var3 * var13.x + var4 * var13.y + var5 * var13.z) / (var7 * var13.magnitude);
                    var8.field1412[var9] = method1568(var15, var14);
                    if (this.normals2 != null && this.normals2[this.trianglePointsY[var9]] != null) {
                        var13 = this.normals2[this.trianglePointsY[var9]];
                    } else {
                        var13 = this.normals[this.trianglePointsY[var9]];
                    }

                    var14 = var1 + (var3 * var13.x + var4 * var13.y + var5 * var13.z) / (var7 * var13.magnitude);
                    var8.field1416[var9] = method1568(var15, var14);
                    if (this.normals2 != null && this.normals2[this.trianglePointsZ[var9]] != null) {
                        var13 = this.normals2[this.trianglePointsZ[var9]];
                    } else {
                        var13 = this.normals[this.trianglePointsZ[var9]];
                    }

                    var14 = var1 + (var3 * var13.x + var4 * var13.y + var5 * var13.z) / (var7 * var13.magnitude);
                    var8.field1379[var9] = method1568(var15, var14);
                }
            } else if (var17 != 0) {
                if (var17 == 1) {
                    var19 = this.faceNormals[var9];
                    var14 = var1 + (var3 * var19.x + var4 * var19.y + var5 * var19.z) / (var7 + var7 / 2);
                    var8.field1412[var9] = method1569(var14);
                    var8.field1379[var9] = -1;
                } else {
                    var8.field1379[var9] = -2;
                }
            } else {
                if (this.normals2 != null && this.normals2[this.trianglePointsX[var9]] != null) {
                    var13 = this.normals2[this.trianglePointsX[var9]];
                } else {
                    var13 = this.normals[this.trianglePointsX[var9]];
                }

                var14 = var1 + (var3 * var13.x + var4 * var13.y + var5 * var13.z) / (var7 * var13.magnitude);
                var8.field1412[var9] = method1569(var14);
                if (this.normals2 != null && this.normals2[this.trianglePointsY[var9]] != null) {
                    var13 = this.normals2[this.trianglePointsY[var9]];
                } else {
                    var13 = this.normals[this.trianglePointsY[var9]];
                }

                var14 = var1 + (var3 * var13.x + var4 * var13.y + var5 * var13.z) / (var7 * var13.magnitude);
                var8.field1416[var9] = method1569(var14);
                if (this.normals2 != null && this.normals2[this.trianglePointsZ[var9]] != null) {
                    var13 = this.normals2[this.trianglePointsZ[var9]];
                } else {
                    var13 = this.normals[this.trianglePointsZ[var9]];
                }

                var14 = var1 + (var3 * var13.x + var4 * var13.y + var5 * var13.z) / (var7 * var13.magnitude);
                var8.field1379[var9] = method1569(var14);
            }
        }

        this.computeAnimationTables();
        var8.field1369 = this.vertexCount;
        var8.verticesX = this.vertexX;
        var8.verticesY = this.vertexY;
        var8.verticesZ = this.vertexZ;
        var8.field1384 = this.triangleFaceCount;
        var8.indices1 = this.trianglePointsX;
        var8.indices2 = this.trianglePointsY;
        var8.indices3 = this.trianglePointsZ;
        var8.field1383 = this.faceRenderPriorities;
        var8.field1381 = this.faceAlphas;
        var8.faceTextures = this.faceTextures;
        return var8;
    }

    public void computeTextureUVCoordinates() {
        this.textureUCoords = new float[triangleFaceCount][];
        this.textureVCoords = new float[triangleFaceCount][];

        for (int i = 0; i < triangleFaceCount; i++) {
            int textureCoord;
            if (textureCoords == null) {
                textureCoord = -1;
            } else {
                textureCoord = textureCoords[i];
            }

            int textureIdx;
            if (faceTextures == null) {
                textureIdx = -1;
            } else {
                textureIdx = faceTextures[i] & 0xFFFF;
            }

            if (textureIdx != -1) {
                float[] u = new float[3];
                float[] v = new float[3];

                if (textureCoord == -1) {
                    u[0] = 0.0F;
                    v[0] = 1.0F;

                    u[1] = 1.0F;
                    v[1] = 1.0F;

                    u[2] = 0.0F;
                    v[2] = 0.0F;
                } else {
                    textureCoord &= 0xFF;

                    byte textureRenderType = 0;
                    if (textureRenderTypes != null) {
                        textureRenderType = textureRenderTypes[textureCoord];
                    }

                    if (textureRenderType == 0) {
                        int x = trianglePointsX[i];
                        int y = trianglePointsY[i];
                        int z = trianglePointsZ[i];

                        short pI = texTriangleX[textureCoord];
                        short mI = texTriangleY[textureCoord];
                        short nI = texTriangleZ[textureCoord];

                        float pX = (float)vertexX[pI];
                        float pY = (float)vertexY[pI];
                        float pZ = (float)vertexZ[pI];

                        float f_882_ = (float)vertexX[mI] - pX;
                        float f_883_ = (float)vertexY[mI] - pY;
                        float f_884_ = (float)vertexZ[mI] - pZ;
                        float f_885_ = (float)vertexX[nI] - pX;
                        float f_886_ = (float)vertexY[nI] - pY;
                        float f_887_ = (float)vertexZ[nI] - pZ;
                        float f_888_ = (float)vertexX[x] - pX;
                        float f_889_ = (float)vertexY[x] - pY;
                        float f_890_ = (float)vertexZ[x] - pZ;
                        float f_891_ = (float)vertexX[y] - pX;
                        float f_892_ = (float)vertexY[y] - pY;
                        float f_893_ = (float)vertexZ[y] - pZ;
                        float f_894_ = (float)vertexX[z] - pX;
                        float f_895_ = (float)vertexY[z] - pY;
                        float f_896_ = (float)vertexZ[z] - pZ;

                        float f_897_ = f_883_ * f_887_ - f_884_ * f_886_;
                        float f_898_ = f_884_ * f_885_ - f_882_ * f_887_;
                        float f_899_ = f_882_ * f_886_ - f_883_ * f_885_;
                        float f_900_ = f_886_ * f_899_ - f_887_ * f_898_;
                        float f_901_ = f_887_ * f_897_ - f_885_ * f_899_;
                        float f_902_ = f_885_ * f_898_ - f_886_ * f_897_;
                        float f_903_ = 1.0F / (f_900_ * f_882_ + f_901_
                                * f_883_ + f_902_ * f_884_);

                        u[0] = (f_900_ * f_888_ + f_901_ * f_889_ + f_902_
                                * f_890_)
                                * f_903_;
                        u[1] = (f_900_ * f_891_ + f_901_ * f_892_ + f_902_
                                * f_893_)
                                * f_903_;
                        u[2] = (f_900_ * f_894_ + f_901_ * f_895_ + f_902_
                                * f_896_)
                                * f_903_;

                        f_900_ = f_883_ * f_899_ - f_884_ * f_898_;
                        f_901_ = f_884_ * f_897_ - f_882_ * f_899_;
                        f_902_ = f_882_ * f_898_ - f_883_ * f_897_;
                        f_903_ = 1.0F / (f_900_ * f_885_ + f_901_ * f_886_ + f_902_
                                * f_887_);

                        v[0] = (f_900_ * f_888_ + f_901_ * f_889_ + f_902_
                                * f_890_)
                                * f_903_;
                        v[1] = (f_900_ * f_891_ + f_901_ * f_892_ + f_902_
                                * f_893_)
                                * f_903_;
                        v[2] = (f_900_ * f_894_ + f_901_ * f_895_ + f_902_
                                * f_896_)
                                * f_903_;
                    }
                }

                this.textureUCoords[i] = u;
                this.textureVCoords[i] = v;
            }
        }
    }
}
