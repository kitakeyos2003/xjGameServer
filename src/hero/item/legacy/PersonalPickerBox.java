// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.legacy;

import hero.item.Goods;
import hero.task.service.TaskServiceImpl;
import hero.log.service.CauseLog;
import hero.item.service.GoodsServiceImpl;
import hero.player.HeroPlayer;
import java.util.ArrayList;
import hero.map.Map;
import org.apache.log4j.Logger;

public class PersonalPickerBox extends MonsterLegacyBox {

    private static Logger log;

    static {
        PersonalPickerBox.log = Logger.getLogger((Class) PersonalPickerBox.class);
    }

    public PersonalPickerBox(final int _pickerUserID, final int _monsterID, final Map _map, final ArrayList<int[]> _legacyList, final ArrayList<TaskGoodsLegacyInfo> _taskGoodsList, final short _locationX, final short _locationY) {
        super(_pickerUserID, _monsterID, _map, _legacyList, _taskGoodsList, _locationX, _locationY);
    }

    @Override
    public byte getPickerType() {
        return 1;
    }

    @Override
    public boolean bePicked(final HeroPlayer _player) {
        boolean changed = false;
        if (this.legacyList != null && this.legacyList.size() > 0) {
            PersonalPickerBox.log.debug((Object) "personal picker picked : null != legacyList && legacyList.size() > 0");
            int i = 0;
            while (i < this.legacyList.size()) {
                int[] monsterLegacy = this.legacyList.get(i);
                PersonalPickerBox.log.debug((Object) ("monsterLegacy goods id = " + monsterLegacy[0]));
                Goods goods = GoodsServiceImpl.getInstance().getGoodsByID(monsterLegacy[0]);
                if (goods != null) {
                    PersonalPickerBox.log.debug((Object) ("monsterLegacy goods name = " + goods.getName()));
                    if (GoodsServiceImpl.getInstance().addGoods2Package(_player, goods, monsterLegacy[1], CauseLog.DROP) != null) {
                        this.legacyList.remove(i);
                        changed = true;
                    } else {
                        ++i;
                    }
                } else {
                    this.legacyList.remove(i);
                    changed = true;
                }
            }
        }
        if (this.taskGoodsInfoList != null) {
            PersonalPickerBox.log.debug((Object) "null != taskGoodsInfoList ");
            int j = 0;
            while (j < this.taskGoodsInfoList.size()) {
                TaskGoodsLegacyInfo taskGoodsLegacyInfo = this.taskGoodsInfoList.get(j);
                PersonalPickerBox.log.debug((Object) ("task goodsID = " + taskGoodsLegacyInfo.getTaskGoodsID()));
                Goods goods = GoodsServiceImpl.getInstance().getGoodsByID(taskGoodsLegacyInfo.getTaskGoodsID());
                if (goods != null) {
                    PersonalPickerBox.log.debug((Object) ("task goodsname = " + goods.getName()));
                    if (GoodsServiceImpl.getInstance().addGoods2Package(_player, goods, taskGoodsLegacyInfo.getGoodsNumber(), CauseLog.DROP) == null) {
                        break;
                    }
                    TaskServiceImpl.getInstance().addTaskGoods(_player, taskGoodsLegacyInfo.getTaskID(), goods, taskGoodsLegacyInfo.getGoodsNumber());
                    this.taskGoodsInfoList.remove(j);
                    changed = true;
                }
            }
        }
        return changed;
    }
}
