// 
// Decompiled by Procyon v0.5.36
// 
package hero.player.clienthandler;

import hero.manufacture.Manufacture;
import java.util.List;
import hero.item.bag.Inventory;
import hero.map.Map;
import hero.item.EquipmentInstance;
import hero.player.HeroPlayer;
import hero.task.message.NotifyPlayerReciveRepeateTaskTimes;
import hero.map.message.ResponsePetInfoList;
import hero.charge.message.PointAmountNotify;
import hero.share.ME2GameObject;
import hero.effect.message.AddEffectNotify;
import hero.item.service.GoodsConfig;
import hero.effect.Effect;
import hero.effect.detail.StaticEffect;
import hero.player.message.HotKeySumByMedicament;
import hero.player.message.ShortcutKeyListNotify;
import hero.skill.message.LearnedSkillListNotify;
import hero.group.service.GroupServiceImpl;
import hero.manufacture.message.ManufNotify;
import hero.manufacture.service.ManufactureServerImpl;
import hero.item.message.SendBagSize;
import hero.gather.message.GourdNotify;
import hero.gather.message.GatherSkillNotify;
import hero.gather.service.GatherServerImpl;
import hero.item.service.GoodsServiceImpl;
import hero.npc.function.system.postbox.MailService;
import hero.share.message.MailStatusChanges;
import hero.share.letter.LetterService;
import hero.map.message.ResponseBoxList;
import hero.map.message.ResponseAnimalInfoList;
import hero.map.message.ResponseMapDecorateData;
import hero.map.message.ResponseMapElementList;
import yoyo.core.packet.AbsResponseMessage;
import hero.map.message.ResponseSceneElement;
import yoyo.core.queue.ResponseMessageQueue;
import hero.guild.service.GuildServiceImpl;
import hero.task.service.TaskServiceImpl;
import hero.player.service.PlayerServiceImpl;
import org.apache.log4j.Logger;
import yoyo.core.process.AbsClientProcess;

public class InitRoleSecondly extends AbsClientProcess {

    private static Logger log;

    static {
        InitRoleSecondly.log = Logger.getLogger((Class) InitRoleSecondly.class);
    }

    @Override
    public void read() throws Exception {
        InitRoleSecondly.log.info((Object) "@@@@@@@@ InitRoleSecondly ..............");
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        TaskServiceImpl.getInstance().sendPlayerTaskList(player);
        EquipmentInstance weapon = player.getBodyWear().getWeapon();
        GuildServiceImpl.getInstance().sendGuildRank(player);
        Map where = player.where();
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseSceneElement(player.getLoginInfo().clientType, where));
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseMapElementList(player.getLoginInfo().clientType, where));
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseMapDecorateData(where, player.getLoginInfo().clientType));
        if (where.getAnimalList().size() > 0) {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseAnimalInfoList(where));
        }
        if (where.getBoxList().size() > 0) {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseBoxList(where.getBoxList()));
        }
        if (LetterService.getInstance().existsUnreadedLetter(player.getUserID())) {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new MailStatusChanges(MailStatusChanges.TYPE_OF_LETTER, true));
        }
        if (MailService.getInstance().getUnreadMailNumber(player.getUserID()) > 0) {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new MailStatusChanges(MailStatusChanges.TYPE_OF_POST_BOX, true));
        }
        GoodsServiceImpl.getInstance().sendLegacyBoxList(where, player);
        TaskServiceImpl.getInstance().notifyMapNpcTaskMark(player, where);
        TaskServiceImpl.getInstance().notifyMapGearOperateMark(player, where);
        TaskServiceImpl.getInstance().notifyGroundTaskGoodsOperateMark(player, where);
        if (GatherServerImpl.getInstance().getGatherByUserID(player.getUserID()) != null) {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new GatherSkillNotify(true));
            int gourdID = GatherServerImpl.getInstance().getGourdID(player);
            if (gourdID > 0) {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new GourdNotify(true));
            }
        }
        Inventory inventory = player.getInventory();
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new SendBagSize(inventory.getEquipmentBag().getSize(), inventory.getMedicamentBag().getSize(), inventory.getMaterialBag().getSize(), inventory.getSpecialGoodsBag().getSize(), inventory.getPetEquipmentBag().getSize(), inventory.getPetContainer().getSize(), inventory.getPetGoodsBag().getSize()));
        List<Manufacture> _manufList = ManufactureServerImpl.getInstance().getManufactureListByUserID(player.getUserID());
        if (_manufList != null) {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ManufNotify(_manufList));
        }
        GroupServiceImpl.getInstance().login(player);
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new LearnedSkillListNotify(player));
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ShortcutKeyListNotify(player));
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new HotKeySumByMedicament(player));
        if (player.getChargeInfo().huntBookTimeTotal > 0L) {
            StaticEffect sef = new StaticEffect(1, "\u53cc\u500d\u7ecf\u9a8c");
            sef.desc = "\u53cc\u500d\u7ecf\u9a8c";
            sef.releaser = player;
            sef.trait = Effect.EffectTrait.BUFF;
            sef.keepTimeType = Effect.EKeepTimeType.LIMITED;
            sef.traceTime = (short) (player.getChargeInfo().huntBookTimeTotal / 1000L);
            sef.iconID = GoodsServiceImpl.getInstance().getConfig().getSpecialConfig().experience_book_icon;
            sef.viewType = 0;
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new AddEffectNotify(player, sef));
        }
        if (player.getChargeInfo().pointAmount > 0) {
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new PointAmountNotify(player.getChargeInfo().pointAmount));
        }
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponsePetInfoList(player));
        player.init();
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new NotifyPlayerReciveRepeateTaskTimes(player));
    }
}
