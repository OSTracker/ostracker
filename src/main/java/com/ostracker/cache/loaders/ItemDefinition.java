package com.ostracker.cache.loaders;

import net.runelite.cache.io.InputStream;

public class ItemDefinition {

    public int id;
    public transient int notedId = -1;
    public transient int placeholderId = -1;

    /**
     * The item ID for the item that should be displayed behind this item when this item is noted.
     * <p>
     * It is mostly 799, which is a paper-like item.
     */
    public transient int notedTemplateId = -1;

    public transient int placeholderTemplateId = -1;

    public String name = "null";
    public transient int teamId;
    public int storePrice = 1;
    public boolean isMembers;
    public transient int isStackable;
    public boolean isTradeable;

    public transient String[] groundOptions = new String[]{null, null, "Take", null, null};
    public transient String[] widgetOptions = new String[]{null, null, null, null, "Drop"};

    /**
     * The index in widgetOptions for the option that should be shown
     * when hovering over an inventory item while holding shift.
     * <p>
     * Also determines what action is invoked when shift-clicking on inventory items.
     */
    public transient int shiftClickDropIndex = -2;

    public transient int zoom2d = 2000;
    public transient int xan2d;
    public transient int yan2d;
    public transient int zan2d;
    public transient int offsetX2d;
    public transient int offsetY2d;

    public transient int resizeX = 128;
    public transient int resizeY = 128;
    public transient int resizeZ = 128;

    public transient short[] textureToReplace;
    public transient short[] textureToReplaceWith;

    public transient short[] colorToReplace;
    public transient short[] colorToReplaceWith;

    public transient int maleModel = -1;
    public transient int maleModel2 = -1;
    public transient int maleModel3 = -1;
    public transient int maleHeadModel = -1;
    public transient int maleHeadModel2 = -1;
    public transient int maleOffset;

    public transient int femaleModel = -1;
    public transient int femaleModel2 = -1;
    public transient int femaleModel3 = -1;
    public transient int femaleHeadModel = -1;
    public transient int femaleHeadModel2 = -1;
    public transient int femaleOffset;

    public transient int contrast;
    public transient int ambient;

    public transient int[] countCo;
    public transient int[] countObj;

    public transient int inventoryModelId;

    // Unknown fields
    public transient int unknown = -1; // "bw" #135
    public transient int unknown2 = -1; // "bd" #135

    public ItemDefinition(int id) {
        this.id = id;
    }

    public void load(InputStream stream) {
        do {
            int opcode = stream.readUnsignedByte();

            if (opcode == 0) {
                break;
            }

            load(opcode, stream);
        } while (true);
    }

    private void load(int opcode, InputStream stream) {
        if (opcode == 1) {
            this.inventoryModelId = stream.readUnsignedShort();
        } else if (opcode == 2) {
            this.name = stream.readString();
        } else if (opcode == 4) {
            this.zoom2d = stream.readUnsignedShort();
        } else if (opcode == 5) {
            this.xan2d = stream.readUnsignedShort();
        } else if (opcode == 6) {
            this.yan2d = stream.readUnsignedShort();
        } else if (opcode == 7) {
            this.offsetX2d = stream.readUnsignedShort();
            if (this.offsetX2d > Short.MAX_VALUE) {
                this.offsetX2d -= 65536;
            }
        } else if (opcode == 8) {
            this.offsetY2d = stream.readUnsignedShort();
            if (this.offsetY2d > Short.MAX_VALUE) {
                this.offsetY2d -= 65536;
            }
        } else if (opcode == 11) {
            this.isStackable = 1;
        } else if (opcode == 12) {
            this.storePrice = stream.readInt();
        } else if (opcode == 16) {
            this.isMembers = true;
        } else if (opcode == 23) {
            this.maleModel = stream.readUnsignedShort();
            this.maleOffset = stream.readUnsignedByte();
        } else if (opcode == 24) {
            this.maleModel2 = stream.readUnsignedShort();
        } else if (opcode == 25) {
            this.femaleModel = stream.readUnsignedShort();
            this.femaleOffset = stream.readUnsignedByte();
        } else if (opcode == 26) {
            this.femaleModel2 = stream.readUnsignedShort();
        } else if (opcode >= 30 && opcode < 35) {
            this.groundOptions[opcode - 30] = stream.readString();
            if (this.groundOptions[opcode - 30].equalsIgnoreCase("Hidden")) {
                this.groundOptions[opcode - 30] = null;
            }
        } else if (opcode >= 35 && opcode < 40) {
            this.widgetOptions[opcode - 35] = stream.readString();
        } else if (opcode == 40) {
            int var3 = stream.readUnsignedByte();
            this.colorToReplace = new short[var3];
            this.colorToReplaceWith = new short[var3];

            for (int var4 = 0; var4 < var3; ++var4) {
                this.colorToReplace[var4] = (short) stream.readUnsignedShort();
                this.colorToReplaceWith[var4] = (short) stream.readUnsignedShort();
            }
        } else if (opcode == 41) {
            int var3 = stream.readUnsignedByte();
            this.textureToReplace = new short[var3];
            this.textureToReplaceWith = new short[var3];

            for (int var4 = 0; var4 < var3; ++var4) {
                this.textureToReplace[var4] = (short) stream.readUnsignedShort();
                this.textureToReplaceWith[var4] = (short) stream.readUnsignedShort();
            }
        } else if (opcode == 42) {
            this.shiftClickDropIndex = stream.readByte();
        } else if (opcode == 65) {
            this.isTradeable = true;
        } else if (opcode == 78) {
            this.maleModel3 = stream.readUnsignedShort();
        } else if (opcode == 79) {
            this.femaleModel3 = stream.readUnsignedShort();
        } else if (opcode == 90) {
            this.maleHeadModel = stream.readUnsignedShort();
        } else if (opcode == 91) {
            this.femaleHeadModel = stream.readUnsignedShort();
        } else if (opcode == 92) {
            this.maleHeadModel2 = stream.readUnsignedShort();
        } else if (opcode == 93) {
            this.femaleHeadModel2 = stream.readUnsignedShort();
        } else if (opcode == 95) {
            this.zan2d = stream.readUnsignedShort();
        } else if (opcode == 97) {
            this.notedId = stream.readUnsignedShort();
        } else if (opcode == 98) {
            this.notedTemplateId = stream.readUnsignedShort();
        } else if (opcode >= 100 && opcode < 110) {
            if (null == this.countObj) {
                this.countObj = new int[10];
                this.countCo = new int[10];
            }

            this.countObj[opcode - 100] = stream.readUnsignedShort();
            this.countCo[opcode - 100] = stream.readUnsignedShort();
        } else if (opcode == 110) {
            this.resizeX = stream.readUnsignedShort();
        } else if (opcode == 111) {
            this.resizeY = stream.readUnsignedShort();
        } else if (opcode == 112) {
            this.resizeZ = stream.readUnsignedShort();
        } else if (opcode == 113) {
            this.ambient = stream.readByte();
        } else if (opcode == 114) {
            this.contrast = stream.readByte();
        } else if (opcode == 115) {
            this.teamId = stream.readUnsignedByte();
        } else if (opcode == 139) {
            this.unknown = stream.readUnsignedShort();
        } else if (opcode == 140) {
            this.unknown2 = stream.readUnsignedShort();
        } else if (opcode == 148) {
            this.placeholderId = stream.readUnsignedShort();
        } else if (opcode == 149) {
            this.placeholderTemplateId = stream.readUnsignedShort();
        }
    }
}
