// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.special;

import hero.item.SpecialGoods;
import org.apache.log4j.Logger;

public class SpecialGoodsBuilder {

    private static Logger log;

    static {
        SpecialGoodsBuilder.log = Logger.getLogger((Class) SpecialGoodsBuilder.class);
    }

    private SpecialGoodsBuilder() {
    }

    public static SpecialGoods build(final int _goodsID, final short _stackNumber, final String _typeDesc) {
        ESpecialGoodsType goodsType = ESpecialGoodsType.get(_typeDesc);
        if (goodsType != null) {
            SpecialGoods specialGoods = null;
            switch (goodsType) {
                case GOURD: {
                    specialGoods = new Gourd(_goodsID, _stackNumber);
                    break;
                }
                case RATTAN: {
                    specialGoods = new Rattan(_goodsID, _stackNumber);
                    break;
                }
                case CRYSTAL: {
                    specialGoods = new Crystal(_goodsID, _stackNumber);
                    break;
                }
                case DRAWINGS: {
                    specialGoods = new Drawings(_goodsID, _stackNumber);
                    break;
                }
                case SEAL_PRAY: {
                    specialGoods = new SealPray(_goodsID, _stackNumber);
                    break;
                }
                case WORLD_HORN: {
                    specialGoods = new WorldHorn(_goodsID, _stackNumber);
                    break;
                }
                case MASS_HORN: {
                    specialGoods = new MassHorn(_goodsID, _stackNumber);
                    break;
                }
                case EXPERIENCE_BOOK: {
                    specialGoods = new ExperienceBook(_goodsID, _stackNumber);
                    break;
                }
                case EXP_BOOK_OFFLINE: {
                    specialGoods = new ExpBookOffline(_goodsID, _stackNumber);
                    break;
                }
                case HUNT_EXP_BOOK: {
                    specialGoods = new HuntExperienceBook(_goodsID, _stackNumber);
                    break;
                }
                case SOUL_MARK: {
                    specialGoods = new SoulMark(_goodsID, _stackNumber);
                    break;
                }
                case SOUL_CHANNEL: {
                    specialGoods = new SoulChannel(_goodsID, _stackNumber);
                    break;
                }
                case EQUIPMENT_REPAIR: {
                    specialGoods = new EquipmentRepairSolvent(_goodsID, _stackNumber);
                    break;
                }
                case PET_ARCHETYPE: {
                    specialGoods = new PetArchetype(_goodsID, _stackNumber);
                    break;
                }
                case SKILL_BOOK: {
                    specialGoods = new SkillBook(_goodsID, _stackNumber);
                    break;
                }
                case PET_FEED: {
                    specialGoods = new PetFeed(_goodsID, _stackNumber);
                    SpecialGoodsBuilder.log.debug((Object) " specialGoods is new petFeed");
                    break;
                }
                case PET_REVIVE: {
                    specialGoods = new PetRevive(_goodsID, _stackNumber);
                    SpecialGoodsBuilder.log.debug((Object) " specialGoods is new PetRevive");
                    break;
                }
                case PET_DICARD: {
                    specialGoods = new PetDicard(_goodsID, _stackNumber);
                    SpecialGoodsBuilder.log.debug((Object) " specialGoods is new PetDicard");
                    break;
                }
                case PET_SKILL_BOOK: {
                    specialGoods = new PetSkillBook(_goodsID, _stackNumber);
                    SpecialGoodsBuilder.log.debug((Object) " specialGoods is pet skill book ");
                    break;
                }
                case MARRY_RING: {
                    specialGoods = new MarryRing(_goodsID, _stackNumber);
                    break;
                }
                case DIVORCE: {
                    specialGoods = new Divorce(_goodsID, _stackNumber);
                    break;
                }
                case HEAVEN_BOOK: {
                    specialGoods = new HeavenBook(_goodsID, _stackNumber);
                    break;
                }
                case TASK_TRANSPORT: {
                    specialGoods = new TaskTransportItem(_goodsID, _stackNumber);
                    break;
                }
                case BIG_TONIC: {
                    specialGoods = new BigTonicBall(_goodsID, _stackNumber);
                    break;
                }
                case RINSE_SKILL: {
                    specialGoods = new RinseSkill(_goodsID, _stackNumber);
                    break;
                }
                case REVIVE_STONE: {
                    specialGoods = new ReviveStone(_goodsID, _stackNumber);
                    break;
                }
                case PET_PER: {
                    specialGoods = new PetPerCard(_goodsID, _stackNumber);
                }
                case FLOWER: {
                    specialGoods = new Flower(_goodsID, _stackNumber);
                    break;
                }
                case CHOCOLATE: {
                    specialGoods = new Chocolate(_goodsID, _stackNumber);
                    break;
                }
                case GUILD_BUILD: {
                    specialGoods = new GuildBuild(_goodsID, _stackNumber);
                    break;
                }
                case SPOUSE_TRANSPORT: {
                    specialGoods = new SpouseTransport(_goodsID, _stackNumber);
                    break;
                }
                case BAG_EXPAN: {
                    specialGoods = new BagExpan(_goodsID, _stackNumber);
                    break;
                }
                case PET_FOREVER: {
                    specialGoods = new PetForeverCard(_goodsID, _stackNumber);
                    break;
                }
                case GIFT_BAG: {
                    specialGoods = new GiftBag(_goodsID, _stackNumber);
                    break;
                }
                case HOOK_EXP: {
                    specialGoods = new HookExp(_goodsID, _stackNumber);
                    break;
                }
                case REPEATE_TASK_EXPAN: {
                    specialGoods = new RepeateTaskExpan(_goodsID, _stackNumber);
                    break;
                }
            }
            return specialGoods;
        }
        return null;
    }
}
