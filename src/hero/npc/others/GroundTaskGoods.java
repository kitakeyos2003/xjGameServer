// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.others;

import hero.npc.dict.NpcImageDict;
import hero.npc.dict.GroundTaskGoodsDataDict;

public class GroundTaskGoods extends ME2OtherGameObject {

    private String name;
    private int taskIDAbout;
    private int taskToolIDAbout;
    private long lastDisappearTime;
    private byte[] image;
    public static int REBIRTH_INTERVAL;

    static {
        GroundTaskGoods.REBIRTH_INTERVAL = 15000;
    }

    public GroundTaskGoods(final GroundTaskGoodsDataDict.GroundTaskGoodsData _data) {
        super(_data.modelID, _data.imageID);
        this.name = _data.name;
        this.taskIDAbout = _data.taskID;
        this.taskToolIDAbout = _data.taskToolID;
        this.image = NpcImageDict.getInstance().getImageBytes(_data.imageID);
    }

    public String getName() {
        return this.name;
    }

    public int getTaskIDAbout() {
        return this.taskIDAbout;
    }

    public int getTaskToolIDAbout() {
        return this.taskToolIDAbout;
    }

    public void disappear() {
        this.lastDisappearTime = System.currentTimeMillis();
    }

    public long getDisappearTime() {
        return this.lastDisappearTime;
    }

    public byte[] getImage() {
        return this.image;
    }
}
