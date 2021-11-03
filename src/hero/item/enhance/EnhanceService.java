// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.enhance;

import hero.item.detail.EGoodsTrait;
import hero.item.message.ResponseSingleHoleEnhanceProperty;
import hero.log.service.ServiceType;
import hero.charge.service.ChargeServiceImpl;
import hero.item.bag.EquipmentContainer;
import hero.log.service.LoctionLog;
import hero.item.bag.exception.BagException;
import hero.log.service.CauseLog;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.item.EqGoods;
import hero.item.message.ClothesOrWeaponChangeNotify;
import hero.player.service.PlayerServiceImpl;
import hero.item.message.EquipmentEnhanceChangeNotify;
import hero.item.detail.EBodyPartOfEquipment;
import hero.item.Weapon;
import hero.chat.service.ChatQueue;
import hero.item.Goods;
import hero.item.service.GoodsServiceImpl;
import hero.item.service.GoodsConfig;
import hero.item.message.EnhanceAnswer;
import hero.share.message.Warning;
import hero.item.special.Crystal;
import hero.item.dictionary.GoodsContents;
import hero.share.ME2GameObject;
import hero.item.service.GoodsDAO;
import yoyo.core.packet.AbsResponseMessage;
import hero.item.message.AddWeaponBloodyEnhanceNotify;
import yoyo.core.queue.ResponseMessageQueue;
import hero.item.EquipmentInstance;
import hero.player.HeroPlayer;
import hero.share.service.LogWriter;
import hero.effect.service.WeaponPvpAndPveEffectDict;
import hero.effect.dictionry.EffectDictionary;
import hero.effect.detail.TouchEffect;
import java.util.Random;
import org.apache.log4j.Logger;

public class EnhanceService {

    private static Logger log;
    private static EnhanceService instance;
    private static final byte[][] PERFORATE_ODDS_LIST;
    public static final int ENHANCE_RESULT_OF_FLASH = 2;
    public static final int ENHANCE_RESULT_OF_CHIP = 0;
    public static final int ENHANCE_RESULT_OF_SUCCESS = 1;
    public static final int ENHANCE_RESULT_OF_HAS_FULL = 2;
    public static final int MAX_LEVEL_OF_PVE_AND_PVP = 12;
    public static final int MAX_LEVEL_OF_ENHANCE = 12;
    private Random RANDOM_BUILDER;
    private static final int BUY_JEWEL_NEED_POINT = 100;

    static {
        EnhanceService.log = Logger.getLogger((Class) EnhanceService.class);
        PERFORATE_ODDS_LIST = new byte[][]{{1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, {0, 1, 1, 1, 0, 1, 1, 1, 1, 1}, {0, 0, 1, 0, 1, 0, 1, 0, 1, 0}, {0, 0, 0, 1, 0, 0, 0, 1, 0, 0}};
    }

    public static EnhanceService getInstance() {
        if (EnhanceService.instance == null) {
            EnhanceService.instance = new EnhanceService();
        }
        return EnhanceService.instance;
    }

    private EnhanceService() {
        this.RANDOM_BUILDER = new Random();
    }

    public TouchEffect getEffect(final int _enhanceDataID, final byte _enhanceLevel) {
        TouchEffect effect = (TouchEffect) EffectDictionary.getInstance().getEffectInstance(WeaponPvpAndPveEffectDict.getInstance().getEffectID(_enhanceDataID, _enhanceLevel));
        if (effect == null) {
            LogWriter.println("\u9519\u8bef\u7684\u6740\u622e\u3001\u5c60\u9b54\u6548\u679c\u7f16\u53f7\uff1a" + _enhanceDataID + "--\u7b49\u7ea7\uff1a" + _enhanceLevel);
        }
        return effect;
    }

    public void addPve(final HeroPlayer _player, final EquipmentInstance _weapon) {
        if (_weapon.getWeaponBloodyEnhance() != null) {
            _weapon.getWeaponBloodyEnhance().addPveNumber();
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new AddWeaponBloodyEnhanceNotify(_weapon));
            GoodsDAO.updateWeaponBloodyEnhance(_weapon.getInstanceID(), this.buildBloodyEnhanceDesc(_weapon));
        }
    }

    public void addPvp(final HeroPlayer _player, final EquipmentInstance _weapon) {
        if (_weapon.getWeaponBloodyEnhance() != null) {
            _weapon.getWeaponBloodyEnhance().addPvpNumber();
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new AddWeaponBloodyEnhanceNotify(_weapon));
            GoodsDAO.updateWeaponBloodyEnhance(_weapon.getInstanceID(), this.buildBloodyEnhanceDesc(_weapon));
        }
    }

    public void processWeaponEnhance(final HeroPlayer _player, final ME2GameObject _dier) {
    }

    public void enhanceQuest(final HeroPlayer _player, final int _crystalID, final byte _jewelIndex, final EquipmentInstance _ei) {
        boolean result = false;
        int stoneNum = _player.getInventory().getSpecialGoodsBag().getGoodsNumber(_crystalID);
        Goods goods = GoodsContents.getGoods(_crystalID);
        if (goods == null || !(goods instanceof Crystal)) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u8bf7\u4e0d\u8981\u4f7f\u7528\u5947\u602a\u7684\u4e1c\u897f", (byte) 0));
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new EnhanceAnswer((byte) 0));
            result = true;
        }
        if (stoneNum <= 0) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6ca1\u6709\u5f3a\u5316\u5b9d\u77f3", (byte) 0));
            result = true;
        }
        Crystal crystal = null;
        if (goods instanceof Crystal) {
            crystal = (Crystal) goods;
        }
        if (crystal == null || crystal.getUseType() != 1) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u8bf7\u4e0d\u8981\u4f7f\u7528\u5947\u602a\u7684\u4e1c\u897f", (byte) 0));
            result = true;
        }
        if (_ei.getGeneralEnhance().getLevel() == 12) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u88c5\u5907\u9576\u5d4c\u5df2\u6ee1", (byte) 0));
            result = true;
        }
        if (!_ei.getGeneralEnhance().haveHole(_jewelIndex)) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u8be5\u4f4d\u7f6e\u8fd8\u672a\u6253\u5b54,\u65e0\u6cd5\u9576\u5d4c", (byte) 0));
            result = true;
        }
        byte index = _ei.getGeneralEnhance().getJewelLevel(_jewelIndex);
        if (index > 0) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u8be5\u4f4d\u7f6e\u4e0a\u5df2\u7ecf\u6709\u5b9d\u77f3\u4e86", (byte) 0));
            result = true;
        }
        if (_jewelIndex < 9 && crystal != null && crystal.getIsUltimaNeed()) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u73cd\u8d35\u7684\u5973\u5a32\u77f3\u53ea\u80fd\u7528\u4e8e\u6700\u540e3\u4e2a\u5b54\u7684\u9576\u5d4c", (byte) 0));
            result = true;
        }
        if (_jewelIndex >= 9 && crystal != null && !crystal.getIsUltimaNeed()) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6700\u540e3\u4e2a\u5b54\u5fc5\u987b\u7528\u5973\u5a32\u77f3", (byte) 0));
            result = true;
        }
        if (!crystal.conformLevel(_ei.getArchetype().getNeedLevel())) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u5b9d\u77f3\u4e0e\u88c5\u5907\u7684\u7b49\u7ea7\u4e0d\u7b26", (byte) 0));
            result = true;
        }
        if (_player.getMoney() < GoodsServiceImpl.getInstance().getConfig().enhance_money_list[_jewelIndex]) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u9576\u5d4c\u5931\u8d25,\u91d1\u94b1\u4e0d\u8db3", (byte) 0));
            result = true;
        }
        if (result) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new EnhanceAnswer((byte) 0));
        } else {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new EnhanceAnswer((byte) 1));
        }
    }

    public void enhanceEquipment(final HeroPlayer _player, final int _crystalID, final byte _jewelIndex, final EquipmentInstance _ei) {
        int stoneNum = _player.getInventory().getSpecialGoodsBag().getGoodsNumber(_crystalID);
        Goods goods = GoodsContents.getGoods(_crystalID);
        boolean isFlash = false;
        if (goods == null || !(goods instanceof Crystal)) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u8bf7\u4e0d\u8981\u4f7f\u7528\u5947\u602a\u7684\u4e1c\u897f", (byte) 0));
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new EnhanceAnswer((byte) 0));
            return;
        }
        if (stoneNum <= 0) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6ca1\u6709\u5f3a\u5316\u5b9d\u77f3", (byte) 0));
            return;
        }
        Crystal crystal = null;
        if (goods instanceof Crystal) {
            crystal = (Crystal) goods;
        }
        if (crystal == null || crystal.getUseType() != 1) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u8bf7\u4e0d\u8981\u4f7f\u7528\u5947\u602a\u7684\u4e1c\u897f", (byte) 0));
            return;
        }
        if (_player.getMoney() < GoodsServiceImpl.getInstance().getConfig().enhance_money_list[_jewelIndex]) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u9576\u5d4c\u5931\u8d25,\u91d1\u94b1\u4e0d\u8db3", (byte) 0));
            return;
        }
        if (_ei.getGeneralEnhance().getLevel() == 12) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u88c5\u5907\u9576\u5d4c\u5df2\u6ee1", (byte) 0));
            return;
        }
        if (!_ei.getGeneralEnhance().haveHole(_jewelIndex)) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u8be5\u4f4d\u7f6e\u8fd8\u672a\u6253\u5b54,\u65e0\u6cd5\u9576\u5d4c", (byte) 0));
            return;
        }
        byte index = _ei.getGeneralEnhance().getJewelLevel(_jewelIndex);
        if (index > 0) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u8be5\u4f4d\u7f6e\u4e0a\u5df2\u7ecf\u6709\u5b9d\u77f3\u4e86", (byte) 0));
            return;
        }
        if (_jewelIndex < 9 && crystal.getIsUltimaNeed()) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u73cd\u8d35\u7684\u5973\u5a32\u77f3\u53ea\u80fd\u7528\u4e8e\u6700\u540e3\u4e2a\u5b54\u7684\u9576\u5d4c", (byte) 0));
            return;
        }
        if (_jewelIndex >= 9 && !crystal.getIsUltimaNeed()) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6700\u540e3\u4e2a\u5b54\u5fc5\u987b\u7528\u5973\u5a32\u77f3", (byte) 0));
            return;
        }
        if (!crystal.conformLevel(_ei.getArchetype().getNeedLevel())) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u5b9d\u77f3\u4e0e\u88c5\u5907\u7684\u7b49\u7ea7\u4e0d\u7b26", (byte) 0));
            return;
        }
        int random = this.RANDOM_BUILDER.nextInt(100) + 1;
        byte level = (byte) crystal.getEnhanceOdds(random);
        if (level == 1) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u5b9d\u77f3\u5c06\u589e\u52a0\u88c5\u5907\u5fae\u91cf\u7684\u5c5e\u6027\u52a0\u6210", (byte) 0));
        } else if (level == 2) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u5b9d\u77f3\u5c06\u589e\u52a0\u88c5\u5907\u5c11\u91cf\u7684\u5c5e\u6027\u52a0\u6210", (byte) 0));
        } else {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u5b9d\u77f3\u5c06\u589e\u52a0\u88c5\u5907\u8f83\u591a\u7684\u5c5e\u6027\u52a0\u6210\n\u8fde\u7eed\u95ea\u5149\u6709\u989d\u5916\u52a0\u6210\n\u540e\u4e09\u5b54\u7684\u9576\u5d4c\u52a0\u6210\u5747\u6709\u989d\u5916\u63d0\u9ad8", (byte) 0));
            isFlash = true;
        }
        _ei.getGeneralEnhance().addDetailEnhance(_jewelIndex, level);
        int flash = _ei.getGeneralEnhance().getFlash();
        int lvl = _ei.getGeneralEnhance().getLevel();
        if (flash == 1 && isFlash) {
            String content = "\u606d\u559c[%p]\u6253\u9020\u51fa\u4e86[%e+%s\uff08\u95ea1\uff09]\uff01[%p]\u5df2\u7ecf\u662f\u521d\u9732\u950b\u8292\u4e86\uff01".replaceAll("%p", _player.getName()).replaceAll("%s", String.valueOf(lvl)).replaceAll("%e", _ei.getArchetype().getName());
            ChatQueue.getInstance().add((byte) 5, null, null, null, content);
        } else if (flash == 2 && isFlash) {
            String content = "\u606d\u559c[%p]\u6253\u9020\u51fa\u4e86[%e+%s\uff08\u95ea2\uff09]\uff01[%p]\u5df2\u7ecf\u662f\u7565\u6709\u5c0f\u6210\u4e86\uff01".replaceAll("%p", _player.getName()).replaceAll("%s", String.valueOf(lvl)).replaceAll("%e", _ei.getArchetype().getName());
            ChatQueue.getInstance().add((byte) 5, null, null, null, content);
        } else if (flash == 3 && isFlash) {
            String content = "\u606d\u559c[%p]\u6253\u9020\u51fa\u4e86[%e+%s\uff08\u95ea3\uff09]\uff01[%p]\u5df2\u7ecf\u79f0\u9738\u4e00\u65b9\u4e86\uff01".replaceAll("%p", _player.getName()).replaceAll("%s", String.valueOf(lvl)).replaceAll("%e", _ei.getArchetype().getName());
            ChatQueue.getInstance().add((byte) 5, null, null, null, content);
        } else if (flash == 4 && isFlash) {
            String content = "\u606d\u559c[%p]\u6253\u9020\u51fa\u4e86[%e+%s\uff08\u95ea4\uff09]\uff01[%p]\u5df2\u7ecf\u662f\u5a01\u540d\u8fdc\u64ad\u4e86\uff01".replaceAll("%p", _player.getName()).replaceAll("%s", String.valueOf(lvl)).replaceAll("%e", _ei.getArchetype().getName());
            ChatQueue.getInstance().add((byte) 5, null, null, null, content);
        } else if (flash == 5 && isFlash) {
            String content = "\u606d\u559c[%p]\u6253\u9020\u51fa\u4e86[%e+%s\uff08\u95ea5\uff09]\uff01[%p]\u5df2\u7ecf\u662f\u540d\u52a8\u6c5f\u6e56\u4e86\uff01".replaceAll("%p", _player.getName()).replaceAll("%s", String.valueOf(lvl)).replaceAll("%e", _ei.getArchetype().getName());
            ChatQueue.getInstance().add((byte) 5, null, null, null, content);
        } else if (flash == 6 && isFlash) {
            String content = "\u606d\u559c[%p]\u6253\u9020\u51fa\u4e86[%e+%s\uff08\u95ea6\uff09]\uff01[%p]\u5df2\u7ecf\u540d\u626c\u5929\u4e0b\u4e86\uff01".replaceAll("%p", _player.getName()).replaceAll("%s", String.valueOf(lvl)).replaceAll("%e", _ei.getArchetype().getName());
            ChatQueue.getInstance().add((byte) 5, null, null, null, content);
        } else if (flash == 7 && isFlash) {
            String content = "\u606d\u559c[%p]\u6253\u9020\u51fa\u4e86[%e+%s\uff08\u95ea7\uff09]\uff01[%p]\u5df2\u7ecf\u662f\u4e00\u4ee3\u5b97\u5e08\u4e86\uff01".replaceAll("%p", _player.getName()).replaceAll("%s", String.valueOf(lvl)).replaceAll("%e", _ei.getArchetype().getName());
            ChatQueue.getInstance().add((byte) 5, null, null, null, content);
        } else if (flash == 8 && isFlash) {
            String content = "\u606d\u559c[%p]\u6253\u9020\u51fa\u4e86[%e+%s\uff08\u95ea8\uff09]\uff01[%p]\u5df2\u7ecf\u662f\u7b11\u50b2\u6c5f\u6e56\u4e86\uff01".replaceAll("%p", _player.getName()).replaceAll("%s", String.valueOf(lvl)).replaceAll("%e", _ei.getArchetype().getName());
            ChatQueue.getInstance().add((byte) 5, null, null, null, content);
        } else if (flash == 9 && isFlash) {
            String content = "\u606d\u559c[%p]\u6253\u9020\u51fa\u4e86[%e+%s\uff08\u95ea9\uff09]\uff01[%p]\u5df2\u7ecf\u65e0\u4eba\u80fd\u654c\u4e86\uff01".replaceAll("%p", _player.getName()).replaceAll("%s", String.valueOf(lvl)).replaceAll("%e", _ei.getArchetype().getName());
            ChatQueue.getInstance().add((byte) 5, null, null, null, content);
        } else if (flash == 10 && isFlash) {
            String content = "\u606d\u559c[%p]\u6253\u9020\u51fa\u4e86[%e+%s\uff08\u95ea10\uff09]\uff01[%p]\u5df2\u7ecf\u72ec\u5b64\u6c42\u8d25\u4e86\uff01".replaceAll("%p", _player.getName()).replaceAll("%s", String.valueOf(lvl)).replaceAll("%e", _ei.getArchetype().getName());
            ChatQueue.getInstance().add((byte) 5, null, null, null, content);
        } else if (flash == 11 && isFlash) {
            String content = "\u606d\u559c[%p]\u6253\u9020\u51fa\u4e86[%e+%s\uff08\u95ea11\uff09]\uff01[%p]\u5df2\u7ecf\u5929\u4e0b\u7b2c\u4e00\u4e86\uff01".replaceAll("%p", _player.getName()).replaceAll("%s", String.valueOf(lvl)).replaceAll("%e", _ei.getArchetype().getName());
            ChatQueue.getInstance().add((byte) 5, null, null, null, content);
        } else if (flash == 12 && isFlash) {
            String content = "\u606d\u559c[%p]\u6253\u9020\u51fa\u4e86[%e+12\uff08\u95ea12\uff09]\uff01[%p]\u5df2\u7ecf\u5929\u5916\u98de\u4ed9\u4e86\uff01".replaceAll("%p", _player.getName()).replaceAll("%s", String.valueOf(lvl)).replaceAll("%e", _ei.getArchetype().getName());
            ChatQueue.getInstance().add((byte) 5, null, null, null, content);
        }
        short pngID = -1;
        short aunID = -1;
        if (_ei.getArchetype() instanceof Weapon) {
            pngID = _ei.getGeneralEnhance().getFlashView()[0];
            aunID = _ei.getGeneralEnhance().getFlashView()[1];
        } else if (_ei.getArchetype().getWearBodyPart() == EBodyPartOfEquipment.BOSOM) {
            pngID = _ei.getGeneralEnhance().getArmorFlashView()[0];
            aunID = _ei.getGeneralEnhance().getArmorFlashView()[1];
        }
        String changeValue = _ei.getGeneralEnhance().getUpEndString();
        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new EquipmentEnhanceChangeNotify(_ei.getInstanceID(), _ei.getGeneralEnhance().detail, pngID, aunID, _ei.getArchetype().getWearBodyPart().value(), changeValue));
        GoodsDAO.updateEquipmentEnhance(_ei.getInstanceID(), this.buildGenericEnhanceDesc(_ei));
        PlayerServiceImpl.getInstance().addMoney(_player, -GoodsServiceImpl.getInstance().getConfig().enhance_money_list[_jewelIndex], 1.0f, 2, "\u9576\u5d4c\u91d1\u94b1\u6d88\u8017");
        if (_ei.getArchetype() instanceof Weapon) {
            _ei.resetWeaponMaxMagicAttack(_ei.getGeneralEnhance().getAttackModulus());
            _ei.resetWeaponMinMagicAttack(_ei.getGeneralEnhance().getAttackModulus());
            _ei.resetWeaponMaxPhysicsAttack(_ei.getGeneralEnhance().getAttackModulus());
            _ei.resetWeaponMinPhysicsAttack(_ei.getGeneralEnhance().getAttackModulus());
            ClothesOrWeaponChangeNotify msg = new ClothesOrWeaponChangeNotify(_player, ((Weapon) _ei.getArchetype()).getWearBodyPart(), _ei.getArchetype().getImageID(), _ei.getArchetype().getAnimationID(), _ei.getGeneralEnhance().getLevel(), _ei.getArchetype(), false, pngID, aunID);
            MapSynchronousInfoBroadcast.getInstance().put(_player.where(), msg, true, _player.getID());
        }
        PlayerServiceImpl.getInstance().reCalculateRoleProperty(_player);
        PlayerServiceImpl.getInstance().refreshRoleProperty(_player);
        try {
            GoodsServiceImpl.getInstance().deleteSingleGoods(_player, _player.getInventory().getSpecialGoodsBag(), crystal, 1, CauseLog.ENHANCE);
        } catch (BagException e) {
        }
    }

    public void perforateEquipment(final HeroPlayer _player, final EquipmentInstance _ei) throws BagException {
        int stoneNum = _player.getInventory().getSpecialGoodsBag().getGoodsNumber(Crystal.STONE_PWEDOEATE_LIST[0]);
        Crystal crystal = (Crystal) GoodsContents.getGoods(Crystal.STONE_PWEDOEATE_LIST[0]);
        if (stoneNum <= 0) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6ca1\u6709\u6253\u5b54\u77f3", (byte) 0));
            return;
        }
        if (crystal == null) {
            EnhanceService.log.error((Object) "\u51fa\u73b0\u4e25\u91cd\u9519\u8bef,\u83b7\u5f97\u6253\u5b54\u77f3\u4e3aNULL");
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6253\u5b54\u5931\u8d25,\u8bf7\u91cd\u8bd5", (byte) 0));
            return;
        }
        byte hole = _ei.getGeneralEnhance().getHole();
        if (crystal.getUseType() != 0) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u8bf7\u4e0d\u8981\u4f7f\u7528\u5947\u602a\u7684\u4e1c\u897f " + crystal.getUseType(), (byte) 0));
            return;
        }
        if (hole >= 12) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u5b8c\u7f8e\u768412\u4e2a\u5b54\uff0c\u5df2\u7ecf\u5168\u6253\u597d\u4e86", (byte) 0));
            return;
        }
        if (_player.getMoney() < GoodsServiceImpl.getInstance().getConfig().perforate_money_list[hole]) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6253\u5b54\u5931\u8d25,\u91d1\u94b1\u4e0d\u8db3", (byte) 0));
            return;
        }
        int random = this.RANDOM_BUILDER.nextInt(10);
        byte isDamage = 0;
        EnhanceService.log.info((Object) ("\u6253\u5b54,\u53d6\u5230\u968f\u673a\u6570:" + random));
        if (EnhanceService.PERFORATE_ODDS_LIST[hole][random] == 1) {
            if (_ei.getGeneralEnhance().addPerforate()) {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u606d\u559c\u60a8\u6253\u5b54\u6210\u529f", (byte) 0));
            }
            if (hole == 10) {
                String content = "\u606d\u559c[%p]\u6253\u9020\u51fa\u4e00\u4ef611\u5b54\u7684[%e]\uff01\u5e78\u8fd0\u4e4b\u795e\u5df2\u7ecf\u5f00\u59cb\u7737\u987e[%p]\u4e86\uff01".replaceAll("%p", _player.getName()).replaceAll("%e", _ei.getArchetype().getName());
                ChatQueue.getInstance().add((byte) 5, null, null, null, content);
            } else if (hole == 11) {
                String content = "\u606d\u559c[%p]\u6253\u9020\u51fa\u4e00\u4ef612\u5b54\u7684[%e]\uff01\u96f7\u795e\u5df2\u7ecf\u5f00\u59cb\u6ce8\u610f[%p]\u4e86\uff01".replaceAll("%p", _player.getName()).replaceAll("%e", _ei.getArchetype().getName());
                ChatQueue.getInstance().add((byte) 5, null, null, null, content);
            }
        } else {
            GoodsServiceImpl.getInstance().diceEquipmentOfBag(_player, _player.getInventory().getEquipmentBag(), _ei, LoctionLog.BAG, CauseLog.DICEEQUIP);
            isDamage = 1;
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u5f88\u9057\u61be\uff0c\u6253\u5b54\u5931\u8d25,\u60a8\u7684\u88c5\u5907\u5df2\u7ecf\u635f\u6bc1\u6d88\u5931\u4e86", (byte) 0));
        }
        short pngID = -1;
        short aunID = -1;
        if (_ei.getArchetype() instanceof Weapon) {
            pngID = _ei.getGeneralEnhance().getFlashView()[0];
            aunID = _ei.getGeneralEnhance().getFlashView()[1];
        } else if (_ei.getArchetype().getWearBodyPart() == EBodyPartOfEquipment.BOSOM) {
            pngID = _ei.getGeneralEnhance().getArmorFlashView()[0];
            aunID = _ei.getGeneralEnhance().getArmorFlashView()[1];
        }
        PlayerServiceImpl.getInstance().addMoney(_player, -GoodsServiceImpl.getInstance().getConfig().perforate_money_list[hole], 1.0f, 2, "\u6253\u5b54\u91d1\u94b1\u6d88\u8017");
        String changeValue = _ei.getGeneralEnhance().getUpEndString();
        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new EquipmentEnhanceChangeNotify(_ei.getInstanceID(), _ei.getGeneralEnhance().detail, pngID, aunID, _ei.getArchetype().getWearBodyPart().value(), changeValue, isDamage));
        GoodsDAO.updateEquipmentEnhance(_ei.getInstanceID(), this.buildGenericEnhanceDesc(_ei));
        try {
            GoodsServiceImpl.getInstance().deleteSingleGoods(_player, _player.getInventory().getSpecialGoodsBag(), crystal, 1, CauseLog.PERFORATE);
        } catch (BagException e) {
        }
    }

    public void addJewelForPlayer(final HeroPlayer _player) {
        EnhanceService.log.debug((Object) "add jewel for palyer ... \u73a9\u5bb6\u6dfb\u52a0\u4e00\u4e2a\u5265\u79bb\u5b9d\u77f3");
        Crystal crystal = (Crystal) GoodsContents.getGoods(Crystal.STONE_WRECK_LIST[0]);
        if (ChargeServiceImpl.getInstance().reducePoint(_player, 100, crystal.getID(), crystal.getName(), 1, ServiceType.BUY_TOOLS)) {
            EnhanceService.log.debug((Object) "\u6263\u9664\u70b9\u6570\u6210\u529f....");
            GoodsServiceImpl.getInstance().addGoods2Package(_player, Crystal.STONE_WRECK_LIST[0], 1, CauseLog.BUY);
        }
    }

    public void jewelWreck(final HeroPlayer _player, final EquipmentInstance _ei, final int _jewelIndex) {
        EnhanceService.log.debug((Object) "\u5b9d\u77f3\u5265\u79bb start ....");
        int stoneID = Crystal.STONE_WRECK_LIST[0];
        int stoneNum = _player.getInventory().getSpecialGoodsBag().getGoodsNumber(stoneID);
        Crystal crystal = (Crystal) GoodsContents.getGoods(stoneID);
        if (stoneNum == 0) {
            int player_point = _player.getChargeInfo().pointAmount;
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6ca1\u6709%fn,\u662f\u5426\u53bb\u5546\u57ce\u8d2d\u4e70".replaceAll("%fn", crystal.getName()), (byte) 2, (byte) 1));
        } else {
            if (crystal == null) {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u5265\u79bb\u5b9d\u77f3\u5931\u8d25,\u8bf7\u91cd\u8bd5", (byte) 0));
                EnhanceService.log.error((Object) "\u51fa\u73b0\u4e25\u91cd\u9519\u8bef,\u83b7\u53d6\u5265\u79bb\u5b9d\u77f3\u5931\u8d25.");
                return;
            }
            EnhanceService.log.debug((Object) "\u4f7f\u7528\u5265\u79bb\u5b9d\u77f3...");
            this.jewelWreck(_player, crystal, _ei, _jewelIndex);
        }
    }

    private void jewelWreck(final HeroPlayer _player, final Crystal crystal, final EquipmentInstance _ei, final int _jewelIndex) {
        if (crystal.getUseType() != 2) {
            EnhanceService.log.debug((Object) "\u8bf7\u4e0d\u8981\u4f7f\u7528\u5947\u602a\u7684\u4e1c\u897f");
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u8bf7\u4e0d\u8981\u4f7f\u7528\u5947\u602a\u7684\u4e1c\u897f", (byte) 0));
            return;
        }
        if (!_ei.getGeneralEnhance().haveJewel(_jewelIndex)) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u8be5\u88c5\u5907\u6ca1\u6709\u53ef\u4f9b\u5265\u79bb\u7684\u5b9d\u77f3", (byte) 0));
            return;
        }
        if (_ei.getGeneralEnhance().getJewelLevel(_jewelIndex) == 3) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u95ea\u5149\u7684\u5b9d\u77f3\u4e0d\u80fd\u88ab\u5265\u79bb", (byte) 0));
            return;
        }
        if (_player.getMoney() < GoodsServiceImpl.getInstance().getConfig().wreck_money_list[_jewelIndex]) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u5265\u79bb\u5931\u8d25,\u91d1\u94b1\u4e0d\u8db3", (byte) 0));
            return;
        }
        boolean result = _ei.getGeneralEnhance().resetEnhanceLevel(_jewelIndex);
        if (result) {
            EnhanceService.log.info((Object) "\u6467\u6bc1\u5b9d\u77f3\u6210\u529f..");
            short pngID = -1;
            short aunID = -1;
            if (_ei.getArchetype() instanceof Weapon) {
                pngID = _ei.getGeneralEnhance().getFlashView()[0];
                aunID = _ei.getGeneralEnhance().getFlashView()[1];
            } else if (_ei.getArchetype().getWearBodyPart() == EBodyPartOfEquipment.BOSOM) {
                pngID = _ei.getGeneralEnhance().getArmorFlashView()[0];
                aunID = _ei.getGeneralEnhance().getArmorFlashView()[1];
            }
            PlayerServiceImpl.getInstance().addMoney(_player, -GoodsServiceImpl.getInstance().getConfig().wreck_money_list[_jewelIndex], 1.0f, 2, "\u5265\u79bb\u91d1\u94b1\u6d88\u8017");
            String changeValue = _ei.getGeneralEnhance().getUpEndString();
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new EquipmentEnhanceChangeNotify(_ei.getInstanceID(), _ei.getGeneralEnhance().detail, pngID, aunID, _ei.getArchetype().getWearBodyPart().value(), changeValue));
            GoodsDAO.updateEquipmentEnhance(_ei.getInstanceID(), this.buildGenericEnhanceDesc(_ei));
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6210\u529f\u5265\u79bb\u4e86\u5b9d\u77f3", (byte) 0));
            try {
                GoodsServiceImpl.getInstance().deleteSingleGoods(_player, _player.getInventory().getSpecialGoodsBag(), crystal, 1, CauseLog.PULLOUT);
            } catch (BagException e) {
                return;
            }
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseSingleHoleEnhanceProperty(_ei));
        } else {
            EnhanceService.log.debug((Object) "\u60a8\u7684\u5fae\u64cd\u592a\u5feb\u4e86\uff0c\u8bf7\u91cd\u8bd5");
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u60a8\u7684\u5fae\u64cd\u592a\u5feb\u4e86\uff0c\u8bf7\u91cd\u8bd5", (byte) 0));
        }
    }

    public void parseEnhanceDesc(final EquipmentInstance _ei, final String _genericEnhanceDesc, final String _bloodyEnhanceDesc) {
        try {
            if (!_genericEnhanceDesc.equals("")) {
                String[] genericEnhanceDataDesc = _genericEnhanceDesc.split("#");
                for (int i = 0; i < genericEnhanceDataDesc.length; ++i) {
                    byte enhance = Byte.parseByte(genericEnhanceDataDesc[i]);
                    if (enhance == -1) {
                        _ei.getGeneralEnhance().initAllGrid(i, false, (byte) 0);
                    } else {
                        _ei.getGeneralEnhance().initAllGrid(i, true, enhance);
                    }
                }
                _ei.getGeneralEnhance().initModulus();
                _ei.resetWeaponMaxMagicAttack(_ei.getGeneralEnhance().getAttackModulus());
                _ei.resetWeaponMinMagicAttack(_ei.getGeneralEnhance().getAttackModulus());
                _ei.resetWeaponMaxPhysicsAttack(_ei.getGeneralEnhance().getAttackModulus());
                _ei.resetWeaponMinPhysicsAttack(_ei.getGeneralEnhance().getAttackModulus());
            }
            if (!_bloodyEnhanceDesc.equals("")) {
                String[] bloodyEnhanceDataDesc = _bloodyEnhanceDesc.split("#");
                Weapon weapon = (Weapon) _ei.getArchetype();
                if (weapon.getTrait() != EGoodsTrait.YU_ZHI) {
                    weapon.getTrait();
                    EGoodsTrait sheng_QI = EGoodsTrait.SHENG_QI;
                }
            }
        } catch (Exception ex) {
        }
    }

    public String buildGenericEnhanceDesc(final EquipmentInstance _ei) {
        StringBuffer desc = new StringBuffer();
        byte[][] generalEnhance = _ei.getGeneralEnhance().detail;
        for (int i = 0; i < generalEnhance.length; ++i) {
            if (generalEnhance[i][0] == 0) {
                desc.append("-1#");
            } else {
                desc.append(String.valueOf(generalEnhance[i][1]) + "#");
            }
        }
        return desc.toString();
    }

    public String buildBloodyEnhanceDesc(final EquipmentInstance _ei) {
        StringBuffer desc = new StringBuffer();
        if (_ei.getArchetype() instanceof Weapon && _ei.getWeaponBloodyEnhance() != null) {
            desc.append(_ei.getWeaponBloodyEnhance().getPveNumber()).append("#").append(_ei.getWeaponBloodyEnhance().getPvpNumber()).append("#");
        }
        return desc.toString();
    }
}
