package com.ostracker.rs;

import net.runelite.cache.fs.Archive;
import net.runelite.cache.fs.File;
import net.runelite.cache.fs.Index;

import java.util.List;

public class TextureProvider implements class93 {

    Deque field1213 = new Deque();

    int field1215 = 0;

    Index field1216;

    int field1217 = 128;

    class79[] field1218;

    int field1219;

    double field1221 = 1.0D;

    public TextureProvider(Index var1, Index var2, int var3, double var4, int var6) {
        this.field1216 = var2;
        this.field1219 = var3;
        this.field1215 = this.field1219;
        this.field1221 = var4;
        this.field1217 = var6;

        Archive textureArchive = var1.getArchive(0);

        List<File> files = textureArchive.getFiles();

        this.field1218 = new class79[files.size()];

        for (int i = 0; i < files.size(); i++) {
            this.field1218[i] = new class79(new Buffer(files.get(i).getContents()));
        }
    }

    public int[] load(int var1) {
        class79 var2 = this.field1218[var1];
        if (null != var2) {
            if (null != var2.field1324) {
                this.field1213.method2500(var2);
                var2.field1315 = true;
                return var2.field1324;
            }

            boolean var3 = var2.method1628(this.field1221, this.field1217, this.field1216);
            if (var3) {
                if (this.field1215 == 0) {
                    class79 var4 = (class79) this.field1213.method2499();
                    var4.method1635();
                } else {
                    --this.field1215;
                }

                this.field1213.method2500(var2);
                var2.field1315 = true;
                return var2.field1324;
            }
        }

        return null;
    }

    public int vmethod1999(int var1) {
        return this.field1218[var1] != null ? this.field1218[var1].field1312 : 0;
    }

    public boolean vmethod2009(int var1) {
        return this.field1217 == 64;
    }

    public boolean vmethod2000(int var1) {
        return this.field1218[var1].field1317;
    }
}
