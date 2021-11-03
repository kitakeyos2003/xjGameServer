// 
// Decompiled by Procyon v0.5.36
// 
package hero.manufacture.message;

import java.io.IOException;
import hero.item.dictionary.GoodsContents;
import hero.player.HeroPlayer;
import hero.item.Goods;
import java.util.ArrayList;
import yoyo.core.packet.AbsResponseMessage;

public class ManufNeedGoodsMessage extends AbsResponseMessage {

    private int manufID;
    private String des;
    private ArrayList<Goods> goodsList;
    private ArrayList<Short> goodsNums;
    private HeroPlayer player;

    public ManufNeedGoodsMessage(final int _manufID, final String _des, final HeroPlayer _player) {
        this.manufID = _manufID;
        this.des = _des;
        this.player = _player;
        this.goodsList = new ArrayList<Goods>();
        this.goodsNums = new ArrayList<Short>();
    }

    public void addNeedGoods(final int _goodsID, final short goodsNum) {
        Goods g = GoodsContents.getGoods(_goodsID);
        this.goodsList.add(g);
        this.goodsNums.add(goodsNum);
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.manufID);
        this.yos.writeUTF(this.des);
        this.yos.writeByte(this.goodsList.size());
        for (int i = 0; i < this.goodsList.size(); ++i) {
            Goods goods = this.goodsList.get(i);
            this.yos.writeShort(goods.getIconID());
            this.yos.writeUTF(goods.getName());
            this.yos.writeShort(this.goodsNums.get(i));
            int num = this.player.getInventory().getMaterialBag().getGoodsNumber(goods.getID());
            if (num >= this.goodsNums.get(i)) {
                this.yos.writeByte(goods.getTrait().value());
            } else {
                this.yos.writeByte(0);
            }
            this.yos.writeShort(num);
        }
    }
}
