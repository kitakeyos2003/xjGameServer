// 
// Decompiled by Procyon v0.5.36
// 
package hero.novice.service;

import hero.item.EquipmentInstance;
import hero.map.message.PlayerRefreshNotify;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.map.message.ResponseMapGameObjectList;
import hero.map.Map;
import hero.map.message.ResponseMapBottomData;
import yoyo.core.queue.ResponseMessageQueue;
import hero.log.service.CauseLog;
import hero.item.service.GoodsServiceImpl;
import hero.item.service.EquipmentFactory;
import hero.expressions.service.CEService;
import hero.player.service.PlayerServiceImpl;
import hero.map.service.MapServiceImpl;
import hero.player.HeroPlayer;
import org.apache.log4j.Logger;
import yoyo.service.base.AbsServiceAdaptor;

public class NoviceServiceImpl extends AbsServiceAdaptor<NoviceConfig> {

    private static Logger log;
    private static NoviceServiceImpl instance;

    static {
        NoviceServiceImpl.log = Logger.getLogger((Class) NoviceServiceImpl.class);
    }

    private NoviceServiceImpl() {
        this.config = new NoviceConfig();
    }

    public static NoviceServiceImpl getInstance() {
        if (NoviceServiceImpl.instance == null) {
            NoviceServiceImpl.instance = new NoviceServiceImpl();
        }
        return NoviceServiceImpl.instance;
    }

    @Override
    public void clean(final int _userID) {
    }

    public short getNoviceMapID() {
        return ((NoviceConfig) this.config).novice_map_id;
    }

    public short getNoviceBornX() {
        return ((NoviceConfig) this.config).novice_map_born_x;
    }

    public short getNoviceBornY() {
        return ((NoviceConfig) this.config).novice_map_born_y;
    }

    public void completeNoviceWizard(final HeroPlayer _player) {
        Map map = MapServiceImpl.getInstance().getNormalMapByID(PlayerServiceImpl.getInstance().getInitBornMapID(_player.getClan()));
        if (map != null) {
            _player.setCellX(PlayerServiceImpl.getInstance().getInitBornX(_player.getClan()));
            _player.setCellY(PlayerServiceImpl.getInstance().getInitBornY(_player.getClan()));
            _player.setLevel(((NoviceConfig) this.config).level_when_complete_novice_teaching);
            _player.setUpgradeNeedExp(CEService.expToNextLevel(_player.getLevel(), (float) _player.getUpgradeNeedExp()));
            PlayerServiceImpl.getInstance().addMoney(_player, ((NoviceConfig) this.config).novice_monster_money + ((NoviceConfig) this.config).novice_task_money, 1.0f, 0, "\u65b0\u624b\u5956\u52b1");
            EquipmentInstance ei = EquipmentFactory.getInstance().build(_player.getUserID(), _player.getUserID(), ((NoviceConfig) this.config).award_equipment_id);
            GoodsServiceImpl.getInstance().addEquipmentInstance2Body(_player, ei, CauseLog.NOVICE);
            this.initSecondWeapon(_player);
            _player.addMoney(5000000);
            PlayerServiceImpl.getInstance().reCalculateRoleProperty(_player);
            PlayerServiceImpl.getInstance().refreshRoleProperty(_player);
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseMapBottomData(_player, map, null));
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseMapGameObjectList(_player.getLoginInfo().clientType, map));
            _player.live(map);
            map.getPlayerList().add(_player);
            MapSynchronousInfoBroadcast.getInstance().put(map, new PlayerRefreshNotify(_player), true, _player.getID());
            NoviceDAO.completeNoviceTeaching(_player);
        }
    }

    public void exitNoviceWizard(final HeroPlayer _player) {
        Map map = MapServiceImpl.getInstance().getNormalMapByID(PlayerServiceImpl.getInstance().getInitBornMapID(_player.getClan()));
        NoviceServiceImpl.log.info("\u9000\u51fa\u65b0\u624b\u5f15\u5bfc ...");
        if (map != null) {
            _player.setCellX(PlayerServiceImpl.getInstance().getInitBornX(_player.getClan()));
            _player.setCellY(PlayerServiceImpl.getInstance().getInitBornY(_player.getClan()));
            _player.live(map);
            map.getPlayerList().add(_player);
            MapSynchronousInfoBroadcast.getInstance().put(map, new PlayerRefreshNotify(_player), true, _player.getID());
            NoviceDAO.exitNoviceTeaching(_player);
            this.initSecondWeapon(_player);
            GoodsServiceImpl.getInstance().addGoods2Package(_player, 50026, 20, CauseLog.NOVICE);
            GoodsServiceImpl.getInstance().addGoods2Package(_player, 50026, 20, CauseLog.NOVICE);
            GoodsServiceImpl.getInstance().addGoods2Package(_player, 50026, 20, CauseLog.NOVICE);
            GoodsServiceImpl.getInstance().addGoods2Package(_player, 50026, 20, CauseLog.NOVICE);
            GoodsServiceImpl.getInstance().addGoods2Package(_player, 70000, 20, CauseLog.NOVICE);
            GoodsServiceImpl.getInstance().addGoods2Package(_player, 70000, 20, CauseLog.NOVICE);
            GoodsServiceImpl.getInstance().addGoods2Package(_player, 70000, 20, CauseLog.NOVICE);
            GoodsServiceImpl.getInstance().addGoods2Package(_player, 69999, 20, CauseLog.NOVICE);
            GoodsServiceImpl.getInstance().addGoods2Package(_player, 69999, 20, CauseLog.NOVICE);
            GoodsServiceImpl.getInstance().addGoods2Package(_player, 69999, 20, CauseLog.NOVICE);
            GoodsServiceImpl.getInstance().addGoods2Package(_player, 4001, 1, CauseLog.NOVICE);
            _player.setLevel(50);
            _player.surplusSkillPoint += 100;
            GoodsServiceImpl.getInstance().addGoods2Package(_player, 11001, 1, CauseLog.NOVICE);
            GoodsServiceImpl.getInstance().addGoods2Package(_player, 19001, 1, CauseLog.NOVICE);
            _player.addMoney(3000000);
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseMapBottomData(_player, map, null));
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseMapGameObjectList(_player.getLoginInfo().clientType, map));
        }
    }

    public void novicePlayerAward(final HeroPlayer _player) {
        PlayerServiceImpl.getInstance().leaveNovice(_player);
        _player.setMp(_player.getBaseProperty().getMpMax());
        if (((NoviceConfig) this.config).is_novice_award) {
            _player.addMoney(((NoviceConfig) this.config).novice_award_money);
            _player.setLevel(((NoviceConfig) this.config).novice_award_level);
            _player.surplusSkillPoint += ((NoviceConfig) this.config).novice_award_skill_point;
            PlayerServiceImpl.getInstance().roleUpgrade(_player);
            int[] goodsList = ((NoviceConfig) this.config).getInitAwardList(_player.getVocation());
            if (goodsList != null && goodsList.length > 0) {
                for (int j = 0; j < goodsList.length; ++j) {
                    GoodsServiceImpl.getInstance().addGoods2Package(_player, goodsList[j], 1, CauseLog.NOVICE);
                }
            }
            for (int i = 0; i < ((NoviceConfig) this.config).novice_award_item.length; ++i) {
                GoodsServiceImpl.getInstance().addGoods2Package(_player, ((NoviceConfig) this.config).novice_award_item[i][0], ((NoviceConfig) this.config).novice_award_item[i][1], CauseLog.NOVICE);
            }
        }
    }

    private void initSecondWeapon(final HeroPlayer _player) {
        int secondGoodsid = PlayerServiceImpl.getInstance().getConfig().getInitSecondWeapon(_player.getVocation());
        if (secondGoodsid > 0) {
            GoodsServiceImpl.getInstance().addGoods2Package(_player, secondGoodsid, 1, CauseLog.NOVICE);
        }
    }
}
