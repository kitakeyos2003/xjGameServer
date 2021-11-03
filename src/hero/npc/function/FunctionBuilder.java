// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.function;

import hero.npc.function.system.EvidenveGetGift;
import hero.npc.function.system.AnswerQuestion;
import hero.npc.function.system.ChangeVocation;
import hero.npc.function.system.DungeonTransmit;
import hero.npc.dict.DungeonManagerDict;
import hero.npc.function.system.WedEmceeNPC;
import hero.npc.function.system.MarryNPC;
import hero.npc.function.system.LoverTree;
import hero.npc.function.system.ManufNpc;
import hero.npc.function.system.GatherNpc;
import hero.npc.function.system.WeaponRecord;
import hero.npc.function.system.GuildManager;
import hero.npc.function.system.Transmit;
import hero.npc.function.system.Trade;
import hero.npc.function.system.trade.TraderSellContentDict;
import hero.npc.function.system.Storage;
import hero.npc.function.system.SkillEducate;
import hero.npc.function.system.Repair;
import hero.npc.function.system.PostBox;
import hero.npc.function.system.Exchange;
import hero.npc.function.system.exchange.TraderExchangeContentDict;
import hero.npc.function.system.Auction;
import hero.npc.function.system.TaskPassageway;
import hero.player.define.EClan;
import hero.share.ESystemFeature;
import hero.share.EVocation;
import org.apache.log4j.Logger;

public class FunctionBuilder {

    public static Logger log;

    static {
        FunctionBuilder.log = Logger.getLogger((Class) FunctionBuilder.class);
    }

    public static BaseNpcFunction build(final String _npcModelID, final int _npcObjectID, final int _functionID, final EVocation _vocation, final ESystemFeature feature, final EClan _clan) {
        BaseNpcFunction function = null;
        ENpcFunctionType functionType = ENpcFunctionType.getType(_functionID);
        FunctionBuilder.log.debug((Object) ("function builder npc modelID = " + _npcModelID + " vocation=" + _vocation));
        switch (functionType) {
            case TASK: {
                function = new TaskPassageway(_npcObjectID, _npcModelID);
                break;
            }
            case AUCTION: {
                function = new Auction(_npcObjectID);
                break;
            }
            case EXCHANGE: {
                function = new Exchange(_npcObjectID, TraderExchangeContentDict.getInstance().getExchangeGoodsList(_npcModelID));
                break;
            }
            case POST_BOX: {
                function = new PostBox(_npcObjectID);
                break;
            }
            case REPAIR: {
                function = new Repair(_npcObjectID);
                break;
            }
            case SKILL_EDUCATE: {
                function = new SkillEducate(_npcObjectID, _vocation, feature);
                break;
            }
            case STORAGE: {
                function = new Storage(_npcObjectID);
                break;
            }
            case TRADE: {
                function = new Trade(_npcObjectID, TraderSellContentDict.getInstance().getSellGoodsList(_npcModelID.toLowerCase()));
                break;
            }
            case TRANSMIT: {
                function = new Transmit(_npcObjectID, _npcModelID);
                break;
            }
            case GUILD_MANAGE: {
                function = new GuildManager(_npcObjectID);
                break;
            }
            case WEAPON_RECORD: {
                function = new WeaponRecord(_npcObjectID);
                break;
            }
            case GATHER_NPC: {
                function = new GatherNpc(_npcObjectID);
                break;
            }
            case MANUF_NPC: {
                function = new ManufNpc(_npcObjectID);
                break;
            }
            case LOVER_TREE: {
                function = new LoverTree(_npcObjectID);
                break;
            }
            case MARRY_NPC: {
                function = new MarryNPC(_npcObjectID);
                break;
            }
            case WEDDING: {
                function = new WedEmceeNPC(_npcObjectID);
                break;
            }
            case DUNGEON_TRANSMIT: {
                function = new DungeonTransmit(_npcObjectID, DungeonManagerDict.getInstance().getDungeonID(_npcModelID.toLowerCase()));
                break;
            }
            case CHANGE_VOCATION: {
                function = new ChangeVocation(_npcObjectID, _vocation, _clan);
                break;
            }
            case ANSWER_QUESTION: {
                function = new AnswerQuestion(_npcObjectID);
                break;
            }
            case EVIDENVE_GET_GIFT: {
                function = new EvidenveGetGift(_npcObjectID);
                break;
            }
        }
        return function;
    }
}
