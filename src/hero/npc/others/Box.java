// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.others;

import hero.npc.message.BoxDisappearNofity;
import yoyo.core.packet.AbsResponseMessage;
import hero.npc.message.BoxRefreshNofity;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.share.service.LogWriter;
import hero.item.service.GoodsServiceImpl;
import hero.npc.dict.PickType;
import hero.npc.dict.BoxDataDict;
import java.util.Random;
import java.util.ArrayList;
import hero.item.Goods;
import java.util.HashMap;

public class Box extends ME2OtherGameObject {

    private int rebirthInterval;
    private HashMap<Goods, Integer> actualGoodsTable;
    private ArrayList<short[]> randomLocationList;
    private long lastDisappearTime;
    private static final Random random;
    private static boolean needGatherSkill;

    static {
        random = new Random();
        Box.needGatherSkill = false;
    }

    public Box(final BoxDataDict.BoxData _data) {
        super(_data.modelID);
        this.randomLocationList = new ArrayList<short[]>();
        this.rebirthInterval = _data.rebirthInterval;
        this.actualGoodsTable = new HashMap<Goods, Integer>();
        if (_data.pickType == PickType.GATHER) {
            Box.needGatherSkill = true;
        }
    }

    public boolean isNeedGatherSkill() {
        return Box.needGatherSkill;
    }

    private boolean buildActualGoodsTable() {
        this.actualGoodsTable.clear();
        BoxDataDict.BoxData data = BoxDataDict.getInstance().getBoxData(this.getModelID());
        int[][] goodsInfos = data.fixedGoodsInfos;
        int goodsNumber = Box.random.nextInt(data.fixedGoodsTypeNumsPerTimes) + 1;
        int i = 0;
        while (i < goodsNumber) {
            int randomIndex = Box.random.nextInt(goodsInfos.length);
            Goods goods = GoodsServiceImpl.getInstance().getGoodsByID(goodsInfos[randomIndex][0]);
            if (goods == null) {
                LogWriter.println("\u5b9d\u7bb1\u4e2d\u5b58\u5728\u9519\u8bef\u7684\u7269\u54c1\u7f16\u53f7\uff1a" + goodsInfos[randomIndex][0]);
                break;
            }
            if (this.actualGoodsTable.containsKey(goods)) {
                continue;
            }
            this.actualGoodsTable.put(goods, Box.random.nextInt(goodsInfos[randomIndex][1]) + 1);
            ++i;
        }
        goodsInfos = data.randomGoodsInfos;
        if (data.randomGoodsInfos != null && data.randomGoodsTypeNumsPerTimes > 0) {
            goodsNumber = Box.random.nextInt(data.randomGoodsTypeNumsPerTimes) + 1;
            i = 0;
            while (i < goodsNumber) {
                int randomIndex = Box.random.nextInt(goodsInfos.length);
                if (Box.random.nextInt(10000) <= goodsInfos[randomIndex][1]) {
                    Goods goods = GoodsServiceImpl.getInstance().getGoodsByID(goodsInfos[randomIndex][0]);
                    if (goods == null) {
                        LogWriter.println("\u5b9d\u7bb1\u4e2d\u5b58\u5728\u9519\u8bef\u7684\u7269\u54c1\u7f16\u53f7\uff1a" + goodsInfos[randomIndex][0]);
                        break;
                    }
                    if (this.actualGoodsTable.containsKey(goods)) {
                        continue;
                    }
                    this.actualGoodsTable.put(goods, Box.random.nextInt(goodsInfos[randomIndex][2]) + 1);
                    ++i;
                } else {
                    ++i;
                }
            }
        }
        return this.actualGoodsTable.size() > 0;
    }

    public void rebirth(final boolean _isBuild) {
        if (this.buildActualGoodsTable()) {
            int location = Box.random.nextInt(this.randomLocationList.size());
            this.setCellX(this.randomLocationList.get(location)[0]);
            this.setCellY(this.randomLocationList.get(location)[1]);
            if (!_isBuild) {
                MapSynchronousInfoBroadcast.getInstance().put(this.where, new BoxRefreshNofity(this.getID(), this.getCellX(), this.getCellY()), false, 0);
            }
        }
    }

    public void disappear() {
        this.lastDisappearTime = System.currentTimeMillis();
        if (this.where.getPlayerList().size() > 0) {
            MapSynchronousInfoBroadcast.getInstance().put(this.where, new BoxDisappearNofity(this.getID(), this.getCellY()), false, 0);
        }
    }

    public void addRandomLocation(final short _x, final short _y) {
        this.randomLocationList.add(new short[]{_x, _y});
    }

    public long getDisappearTime() {
        return this.lastDisappearTime;
    }

    public int getRebirthInterval() {
        return this.rebirthInterval;
    }

    public HashMap<Goods, Integer> getActualGoodsTable() {
        return this.actualGoodsTable;
    }
}
