// 
// Decompiled by Procyon v0.5.36
// 
package hero.task.service;

import yoyo.service.YOYOSystem;
import org.dom4j.Element;
import java.util.ArrayList;
import yoyo.service.base.AbsConfig;

public class TaskConfig extends AbsConfig {

    private String taskDataPath;
    private String escortNpcTaskTargetDataPath;
    private String foundPathTaskTargetDataPath;
    private String goodsTaskTargetDataPath;
    private String killMonsterTaskTargetDataPath;
    private String openGearTaskTargetDataPath;
    private String descriptionDataPath;
    public int can_receive_task_number;
    public boolean is_proxy_compel_give;
    public boolean is_sms_compel_give;
    private String monsterTaskGoodsDataPath;
    private String pushDataPuth;
    public ArrayList<Integer> confine_publisher_list;
    public boolean is_use_push;

    @Override
    public void init(final Element node) throws Exception {
        Element dataPathE = node.element("para");
        Element config = node.element("taskConfig");
        this.taskDataPath = String.valueOf(YOYOSystem.HOME) + dataPathE.elementTextTrim("task_path");
        this.escortNpcTaskTargetDataPath = String.valueOf(YOYOSystem.HOME) + dataPathE.elementTextTrim("escort_npc_target_data_path");
        this.foundPathTaskTargetDataPath = String.valueOf(YOYOSystem.HOME) + dataPathE.elementTextTrim("found_path_target_data_path");
        this.goodsTaskTargetDataPath = String.valueOf(YOYOSystem.HOME) + dataPathE.elementTextTrim("goods_target_data_path");
        this.killMonsterTaskTargetDataPath = String.valueOf(YOYOSystem.HOME) + dataPathE.elementTextTrim("kill_monster_target_data_path");
        this.openGearTaskTargetDataPath = String.valueOf(YOYOSystem.HOME) + dataPathE.elementTextTrim("open_gear_target_data_path");
        this.descriptionDataPath = String.valueOf(YOYOSystem.HOME) + dataPathE.elementTextTrim("task_description_path");
        this.monsterTaskGoodsDataPath = String.valueOf(YOYOSystem.HOME) + dataPathE.elementTextTrim("monster_task_goods_path");
        this.pushDataPuth = String.valueOf(YOYOSystem.HOME) + dataPathE.elementTextTrim("task_push_path");
        this.can_receive_task_number = Integer.valueOf(config.elementTextTrim("can_receive_task_number"));
        String[] temp = config.elementTextTrim("confine_publisher_list").split(",");
        this.confine_publisher_list = new ArrayList<Integer>();
        if (temp.length > 0 && !temp[0].equals("")) {
            for (int i = 0; i < temp.length; ++i) {
                this.confine_publisher_list.add(Integer.valueOf(temp[i]));
            }
        }
        this.is_use_push = Boolean.valueOf(config.elementTextTrim("is_use_push"));
        this.is_proxy_compel_give = Boolean.valueOf(config.elementTextTrim("is_proxy_compel_give"));
        this.is_sms_compel_give = Boolean.valueOf(config.elementTextTrim("is_sms_compel_give"));
    }

    public String getTaskDataPath() {
        return this.taskDataPath;
    }

    public String getTaskPushPath() {
        return this.pushDataPuth;
    }

    public String getEscortNpcTaskTargetDataPath() {
        return this.escortNpcTaskTargetDataPath;
    }

    public String getFoundPathTaskTargetDataPath() {
        return this.foundPathTaskTargetDataPath;
    }

    public String getGoodsTaskTargetDataPath() {
        return this.goodsTaskTargetDataPath;
    }

    public String getKillMonsterTaskTargetDataPath() {
        return this.killMonsterTaskTargetDataPath;
    }

    public String getOpenGearTaskTargetDataPath() {
        return this.openGearTaskTargetDataPath;
    }

    public String getDescDataPath() {
        return this.descriptionDataPath;
    }

    public String getMonsterTaskGoodsDataPath() {
        return this.monsterTaskGoodsDataPath;
    }
}
