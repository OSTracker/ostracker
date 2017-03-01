package com.ostracker.cache.loaders;

import net.runelite.cache.io.InputStream;

public class ItemDefinition {

    public int id;
    public transient int noteId = -1;
    public transient int boughtId = -1;
    public transient int placeholderId = -1;

    public String name = "null";

    public int storePrice = 1;

    /**
     * The item ID for the item that should be displayed behind this item when this item is noted.
     * <p>
     * It is mostly 799, which is a paper-like item.
     */
    public transient int noteTemplateId = -1;

    public transient int boughtTemplateId = -1;

    public transient int placeholderTemplateId = -1;

    public boolean isMembers;

    /**
     * Holds whether this item will appear in the results when searching for it in the Grand Exchange price checker.
     */
    public boolean isTradeable;

    /**
     * Holds whether there can be more than one of this item in one inventory slot or not.
     * <p>
     * 0 = not stackable
     * 1 = stackable
     */
    public transient int isStackable;

    /**
     * Holds which "team" the wearer of this item is in.
     * <p>
     * If the local player wears an item with this field set,
     * other players wearing an item with the same ID will show up as blue dots on the minimap.
     */
    public transient int teamId;

    public transient String[] groundOptions = new String[]{null, null, "Take", null, null};
    public transient String[] widgetOptions = new String[]{null, null, null, null, "Drop"};

    /**
     * The index in widgetOptions for the option that should be shown
     * when hovering over an inventory item while holding shift.
     * <p>
     * Also determines what action is invoked when shift-clicking on inventory items.
     */
    public transient int shiftClickDropIndex = -2;

    /**
     * *2d fields describe how the 2D rasterizer should render the inventoryModelId.
     */
    public transient int zoom2d = 2000;
    public transient int xRotation2d;
    public transient int yRotation2d;
    public transient int zRotation2d;
    public transient int xOffset2d;
    public transient int yOffset2d;

    public transient int resizeX = 128;
    public transient int resizeY = 128;
    public transient int resizeZ = 128;

    public transient short[] textureToReplace;
    public transient short[] textureToReplaceWith;

    public transient short[] colorToReplace;
    public transient short[] colorToReplaceWith;

    public transient int inventoryModelId;

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

    /**
     * These arrays contain minimum amounts of an item for the item to transform into another item.
     * <p>
     * This is applied on for example coins, where having 1, 2, 3, 100, 1000
     * and so on amount of coins alters how the stack of coins looks.
     */
    public transient int[] transformIds;
    public transient int[] transformAmounts;

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
            this.xRotation2d = stream.readUnsignedShort();
        } else if (opcode == 6) {
            this.yRotation2d = stream.readUnsignedShort();
        } else if (opcode == 7) {
            this.xOffset2d = stream.readUnsignedShort();
            if (this.xOffset2d > Short.MAX_VALUE) {
                this.xOffset2d -= 65536;
            }
        } else if (opcode == 8) {
            this.yOffset2d = stream.readUnsignedShort();
            if (this.yOffset2d > Short.MAX_VALUE) {
                this.yOffset2d -= 65536;
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
            this.zRotation2d = stream.readUnsignedShort();
        } else if (opcode == 97) {
            this.noteId = stream.readUnsignedShort();
        } else if (opcode == 98) {
            this.noteTemplateId = stream.readUnsignedShort();
        } else if (opcode >= 100 && opcode < 110) {
            if (null == this.transformIds) {
                this.transformIds = new int[10];
                this.transformAmounts = new int[10];
            }

            this.transformIds[opcode - 100] = stream.readUnsignedShort();
            this.transformAmounts[opcode - 100] = stream.readUnsignedShort();
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
            this.boughtId = stream.readUnsignedShort();
        } else if (opcode == 140) {
            this.boughtTemplateId = stream.readUnsignedShort();
        } else if (opcode == 148) {
            this.placeholderId = stream.readUnsignedShort();
        } else if (opcode == 149) {
            this.placeholderTemplateId = stream.readUnsignedShort();
        }
    }
}
