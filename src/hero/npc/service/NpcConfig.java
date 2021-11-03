// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.service;

import yoyo.service.YOYOSystem;
import org.dom4j.Element;
import yoyo.service.base.AbsConfig;

public class NpcConfig extends AbsConfig {

    public String MonsterImageHPath;
    public String MonsterImageLPath;
    public String MonsterImageCfgPath;
    public String NPCImagePath;
    public String NPCImageCfgPath;
    public int NPC_NAME_LENGTH;
    public String MONSTER_DATA_PATH;
    public String NPC_DATA_PATH;
    public String NPC_HELLO_PATH;
    public int MONSTER_AI_INTERVAL;
    public short MONSTER_MOVE_MOST_FAST_GRID;
    public short MONSTER_MOVE_GRID_NUM_PER_TIME;
    public int MONSTER_ACTIVE_MOVE_INTERVAL;
    public short NPC_FOLLOW_GRID_DISTANCE_OF_TARGET;
    public short NPC_FOLLOW_MOST_FAST_GRID;
    public short ANIMAL_WALK_GRID_NUM_PER_TIME;
    public String ANIMAL_DATA_PATH;
    public String ANIMAL_IMAGE_PATH;
    public String GEAR_DATA_PATH;
    public String other_task_object_image_path;
    public String ROAD_PLATE_DATA_PATH;
    public String door_plate_data_path;
    public String BOX_DATA_PATH;
    public String TASK_GOODS_ON_MAP_DATA_PATH;
    public String MONSTER_AI_DATA_PATH;
    public String MONSTER_SKILL_AI_DATA_PATH;
    public String MONSTER_SPECIAL_AI_DATA_PATH;
    public String MONSTER_CALL_DATA_PATH;
    public String MONSTER_CHANGES_DATA_PATH;
    public String MONSTER_DISAPPEAR_DATA_PATH;
    public String MONSTER_RUN_AWAY_DATA_PATH;
    public String MONSTER_SHOUT_DATA_PATH;
    public String npc_function_data_question;
    public String npc_function_data_award;
    public String npc_function_data_anwser_question;
    public String npc_function_data_evidenve_award;
    public String npc_function_data_evidenve_gift;
    public String dungeonManagerDataPath;
    public String npc_fun_icon_path;
    public String trader_sell_content_data_path;
    public String trader_exchange_content_data_path;
    public int task_call_monster_exist_time;
    public int ai_call_monster_exist_time;
    public int ai_follow_distance;
    public int ai_follow_grid;
    public int task_gather_rebirth_interval;
    public int task_gather_thread_run_interval;

    @Override
    public void init(final Element _xmlNode) throws Exception {
        Element eMap = _xmlNode.element("npc");
        this.ai_follow_distance = Integer.valueOf(eMap.elementTextTrim("ai_follow_distance"));
        this.ai_follow_grid = Integer.valueOf(eMap.elementTextTrim("ai_follow_grid"));
        this.task_gather_rebirth_interval = Integer.valueOf(eMap.elementTextTrim("task_gather_rebirth_interval"));
        this.task_gather_thread_run_interval = Integer.valueOf(eMap.elementTextTrim("task_gather_thread_run_interval"));
        this.MonsterImageHPath = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("monsterImageH");
        this.MonsterImageLPath = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("monsterImageL");
        this.MonsterImageCfgPath = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("monsterImageCfg");
        this.NPCImagePath = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("NPCImage");
        this.NPCImageCfgPath = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("NPCImageCfg");
        this.MONSTER_DATA_PATH = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("MONSTER_DATA_PATH");
        this.NPC_DATA_PATH = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("NPC_DATA_PATH");
        this.NPC_HELLO_PATH = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("NPC_HELLO_PATH");
        this.NPC_NAME_LENGTH = Integer.parseInt(eMap.elementTextTrim("NPC_NAME_LENGTH"));
        this.MONSTER_AI_INTERVAL = Integer.parseInt(eMap.elementTextTrim("MONSTER_AI_INTERVAL"));
        this.MONSTER_MOVE_MOST_FAST_GRID = Short.parseShort(eMap.elementTextTrim("MONSTER_MOVE_MOST_FAST_GRID"));
        this.MONSTER_MOVE_GRID_NUM_PER_TIME = Short.parseShort(eMap.elementTextTrim("MONSTER_MOVE_GRID_NUM_PER_TIME"));
        this.MONSTER_ACTIVE_MOVE_INTERVAL = Integer.parseInt(eMap.elementTextTrim("MONSTER_ACTIVE_MOVE_INTERVAL"));
        this.NPC_FOLLOW_GRID_DISTANCE_OF_TARGET = Short.parseShort(eMap.elementTextTrim("NPC_FOLLOW_GRID_DISTANCE_OF_TARGET"));
        this.NPC_FOLLOW_MOST_FAST_GRID = Short.parseShort(eMap.elementTextTrim("NPC_FOLLOW_MOST_FAST_GRID"));
        this.ANIMAL_WALK_GRID_NUM_PER_TIME = Short.parseShort(eMap.elementTextTrim("ANIMAL_WALK_GRID_NUM_PER_TIME"));
        this.ANIMAL_DATA_PATH = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("ANIMAL_DATA_PATH");
        this.ANIMAL_IMAGE_PATH = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("ANIMAL_IMAGE_PATH");
        this.GEAR_DATA_PATH = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("GEAR_DATA_PATH");
        this.other_task_object_image_path = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("other_task_object_image_path");
        this.ROAD_PLATE_DATA_PATH = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("ROAD_PLATE_DATA_PATH");
        this.door_plate_data_path = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("door_plate_data_path");
        this.npc_function_data_question = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("npc_function_data_question");
        this.npc_function_data_award = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("npc_function_data_award");
        this.npc_function_data_anwser_question = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("npc_function_data_anwser_question");
        this.npc_function_data_evidenve_award = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("npc_function_data_evidenve_award");
        this.npc_function_data_evidenve_gift = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("npc_function_data_evidenve_gift");
        this.BOX_DATA_PATH = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("BOX_DATA_PATH");
        this.TASK_GOODS_ON_MAP_DATA_PATH = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("TASK_GOODS_ON_MAP_DATA_PATH");
        this.MONSTER_AI_DATA_PATH = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("MONSTER_AI_DATA_PATH");
        this.MONSTER_SKILL_AI_DATA_PATH = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("MONSTER_SKILL_AI_DATA_PATH");
        this.MONSTER_SPECIAL_AI_DATA_PATH = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("MONSTER_SPECIAL_AI_DATA_PATH");
        this.MONSTER_CALL_DATA_PATH = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("MONSTER_CALL_DATA_PATH");
        this.MONSTER_CHANGES_DATA_PATH = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("MONSTER_CHANGES_DATA_PATH");
        this.MONSTER_DISAPPEAR_DATA_PATH = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("MONSTER_DISAPPEAR_DATA_PATH");
        this.MONSTER_RUN_AWAY_DATA_PATH = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("MONSTER_RUN_AWAY_DATA_PATH");
        this.MONSTER_SHOUT_DATA_PATH = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("MONSTER_SHOUT_DATA_PATH");
        this.dungeonManagerDataPath = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("DUNGEON_MANAGER_DATA_PATH");
        this.trader_sell_content_data_path = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("trader_sell_content_data_path");
        this.trader_exchange_content_data_path = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("trader_exchange_content_data_path");
        this.task_call_monster_exist_time = Integer.parseInt(eMap.elementTextTrim("task_call_monster_exist_time"));
        this.ai_call_monster_exist_time = Integer.parseInt(eMap.elementTextTrim("ai_call_monster_exist_time"));
        this.npc_fun_icon_path = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("npc_fun_icon_path");
    }
}
