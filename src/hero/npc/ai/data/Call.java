// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.ai.data;

import hero.npc.Monster;

public class Call extends SpecialWisdom {

    public int id;
    public String shoutContent;
    public String[] monsterModelIDs;
    public short[][] monsterDataArray;
    public static final byte MONSTER_DATA_INDEX_OF_NUMBER = 0;
    public static final byte MONSTER_DATA_INDEX_OF_LOCATION_TYPE = 1;
    public static final byte MONSTER_DATA_INDEX_OF_LOCATION_X = 2;
    public static final byte MONSTER_DATA_INDEX_OF_LOCATION_Y = 3;
    public static final byte LOCATION_TYPE_ABSTRACT_OF_MAP = 1;
    public static final byte LOCATION_TYPE_MONSTER_SELF = 2;

    @Override
    public byte getType() {
        return 1;
    }

    @Override
    public void think(final Monster _dominator) {
    }
}
