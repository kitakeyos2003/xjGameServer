// 
// Decompiled by Procyon v0.5.36
// 
package hero.charge.message;

import java.io.IOException;
import hero.item.Goods;
import java.util.Iterator;
import hero.item.dictionary.GoodsContents;
import java.util.Map;
import hero.charge.service.ChargeServiceImpl;
import hero.charge.service.ChargeConfig;
import hero.charge.MallGoods;
import java.util.ArrayList;
import java.util.Hashtable;
import org.apache.log4j.Logger;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseMallGoodsList extends AbsResponseMessage {

    private static Logger log;
    private byte goodsType;
    private Hashtable<Byte, ArrayList<MallGoods>> goodsTable;
    private int nullListSize;
    private ArrayList<MallGoods> goodsList;
    private short clientVersion;

    static {
        ResponseMallGoodsList.log = Logger.getLogger((Class) ResponseMallGoodsList.class);
    }

    public ResponseMallGoodsList(final Hashtable<Byte, ArrayList<MallGoods>> _goodsTable, final short _clientVersion) {
        this.goodsTable = _goodsTable;
        this.clientVersion = _clientVersion;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        if (this.clientVersion == ChargeServiceImpl.getInstance().getConfig().now_version) {
            this.yos.writeByte(0);
            return;
        }
        this.yos.writeByte(1);
        this.yos.writeShort(ChargeServiceImpl.getInstance().getConfig().now_version);
        String[][] bagUpgradeData = ChargeServiceImpl.getInstance().getConfig().bag_upgrade_data;
        this.yos.writeByte(bagUpgradeData.length);
        for (int i = 0; i < bagUpgradeData.length; ++i) {
            this.yos.writeByte(Byte.parseByte(bagUpgradeData[i][1]));
            this.yos.writeUTF(bagUpgradeData[i][0]);
        }
        this.yos.writeByte(this.goodsTable.size());
        this.yos.writeUTF(ChargeServiceImpl.getInstance().getConfig().notice_string);
        if (this.goodsTable.size() > 0) {
            for (final Map.Entry<Byte, ArrayList<MallGoods>> entry : this.goodsTable.entrySet()) {
                this.goodsType = entry.getKey();
                this.goodsList = entry.getValue();
                this.yos.writeUTF(ChargeServiceImpl.getInstance().getConfig().getTypeDesc(this.goodsType));
                this.yos.writeShort(this.goodsList.size());
                ResponseMallGoodsList.log.info((Object) ("goodsList.size()-->" + this.goodsList.size()));
                int needLevel = 1;
                for (int j = 0; j < this.goodsList.size(); ++j) {
                    MallGoods goods = this.goodsList.get(j);
                    Goods item = GoodsContents.getGoods(goods.goodsList[0][0]);
                    needLevel = item.getNeedLevel();
                    this.yos.writeInt(goods.id);
                    this.yos.writeUTF(goods.name);
                    this.yos.writeByte(goods.trait.value());
                    this.yos.writeShort(goods.icon);
                    this.yos.writeUTF(goods.desc);
                    this.yos.writeInt(goods.price);
                    this.yos.writeShort(needLevel);
                    this.yos.writeByte(goods.buyNumberPerTime);
                    if (1 == goods.goodsList.length) {
                        this.yos.writeShort(goods.goodsList[0][1]);
                    } else {
                        this.yos.writeShort(1);
                    }
                }
            }
        }
    }
}
