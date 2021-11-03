// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.special;

import hero.item.bag.exception.BagException;
import hero.log.service.LogServiceImpl;
import hero.share.ME2GameObject;
import hero.fight.service.FightServiceImpl;
import hero.item.message.ResponseSpecialGoodsBag;
import hero.item.service.GoodsDAO;
import hero.item.service.GoodsServiceImpl;
import hero.item.service.GoodsConfig;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.HeroPlayer;
import hero.item.SpecialGoods;

public class BigTonicBall extends SpecialGoods {

    private int[][] TONIC_LIST;
    public static final int TONINC_CODE = 0;
    public static final int TONINC_ACTIVATE = 1;
    public static final int TONINC_UNAUTO = 2;
    public static final int TONINC_RED = 0;
    public static final int TONINC_BULE = 1;
    public int surplusPoint;
    private String oldDescription;
    private String nowDescription;
    private int location;
    public int tonincType;
    public int isActivate;

    public void initData(final int _surplusPoint, final int _type, final int _index) {
        this.isActivate = _type;
        this.surplusPoint = _surplusPoint;
        this.location = _index;
    }

    public void copyGoodsData(final SpecialGoods _goods, final HeroPlayer _player) {
        this.setName(_goods.getName());
        this.setIconID(_goods.getIconID());
        this.setTrait(_goods.getTrait());
        if (_goods.useable()) {
            this.setUseable();
        }
        this.oldDescription = _goods.getDescription();
        this.nowDescription = "\n\u5269\u4f59:" + this.surplusPoint;
        this.replaceDescription(this.oldDescription, this.nowDescription);
        if (this.isActivate == 1) {
            if (this.tonincType == 1) {
                _player.setBuleTonicBall(this);
            } else {
                _player.setRedTonicBall(this);
            }
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u5f00\u59cb\u4f7f\u7528%fname".replaceAll("%fname", this.getName()), (byte) 0));
        }
    }

    public BigTonicBall(final int _id, final short nums) {
        super(_id, nums);
        this.TONIC_LIST = GoodsServiceImpl.getInstance().getConfig().getSpecialConfig().big_tonic;
        for (int i = 0; i < this.TONIC_LIST.length; ++i) {
            if (_id == this.TONIC_LIST[i][0]) {
                this.surplusPoint = this.TONIC_LIST[i][3];
                this.isActivate = this.TONIC_LIST[i][2];
                this.tonincType = this.TONIC_LIST[i][1];
            }
        }
    }

    public static boolean getRedOrBule(final int i) {
        boolean result = false;
        return result;
    }

    private void descriptionUpdate(final HeroPlayer _player) {
        this.nowDescription = "\n\u5269\u4f59:" + this.surplusPoint;
        this.replaceDescription(this.oldDescription, this.nowDescription);
        GoodsDAO.updateTonic(_player.getUserID(), this.location, this.getID(), this.surplusPoint, this.isActivate);
        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseSpecialGoodsBag(_player.getInventory().getSpecialGoodsBag(), _player.getShortcutKeyList()));
    }

    private boolean eatBall(final HeroPlayer _player) {
        boolean remove = false;
        if (this.tonincType == 0) {
            int hp = _player.getActualProperty().getHpMax() - _player.getHp();
            if (hp > 0) {
                if (this.surplusPoint > hp) {
                    this.surplusPoint -= hp;
                    FightServiceImpl.getInstance().processAddHp(_player, _player, hp, true, false);
                } else {
                    FightServiceImpl.getInstance().processAddHp(_player, _player, this.surplusPoint, true, false);
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u4f60\u5df2\u7ecf\u7528\u5b8c\u4e86\u4e00\u4e2a%fname".replaceAll("%fname", this.getName()), (byte) 0));
                    _player.setRedTonicBall(null);
                    remove = true;
                }
                this.descriptionUpdate(_player);
            } else {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u751f\u547d\u503c\u5df2\u6ee1", (byte) 0));
            }
        } else {
            int mp = _player.getActualProperty().getMpMax() - _player.getMp();
            if (mp > 0) {
                if (this.surplusPoint > mp) {
                    this.surplusPoint -= mp;
                    _player.addMp(mp);
                    FightServiceImpl.getInstance().processSingleTargetMpChange(_player, true);
                } else {
                    _player.addMp(this.surplusPoint);
                    FightServiceImpl.getInstance().processSingleTargetMpChange(_player, true);
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u4f60\u5df2\u7ecf\u7528\u5b8c\u4e86\u4e00\u4e2a%fname".replaceAll("%fname", this.getName()), (byte) 0));
                    _player.setBuleTonicBall(null);
                    remove = true;
                }
                this.descriptionUpdate(_player);
            } else {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u9b54\u6cd5\u503c\u5df2\u6ee1", (byte) 0));
            }
        }
        return remove;
    }

    public boolean use(final HeroPlayer _player) {
        boolean remove = false;
        remove = this.eatBall(_player);
        if (remove && this.disappearImmediatelyAfterUse()) {
            try {
                this.remove(_player, (short) this.location);
                LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID, _player.getLoginInfo().username, _player.getUserID(), _player.getName(), this.getID(), this.getName(), this.getTrait().getDesc(), this.getType().getDescription());
            } catch (BagException e) {
                e.printStackTrace();
            }
        }
        return remove;
    }

    @Override
    public boolean beUse(final HeroPlayer _player, final Object _target, final int _location) {
        boolean remove = false;
        this.location = _location;
        if (this.isActivate == 2) {
            remove = this.eatBall(_player);
        } else if (this.tonincType == 1) {
            _player.setBuleTonicBall(this);
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u5f00\u59cb\u4f7f\u7528%fname".replaceAll("%fname", this.getName()), (byte) 0));
        } else {
            _player.setRedTonicBall(this);
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u5f00\u59cb\u4f7f\u7528%fname".replaceAll("%fname", this.getName()), (byte) 0));
        }
        if (remove) {
            try {
                this.remove(_player, (short) _location);
                LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID, _player.getLoginInfo().username, _player.getUserID(), _player.getName(), this.getID(), this.getName(), this.getTrait().getDesc(), this.getType().getDescription());
            } catch (BagException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean disappearImmediatelyAfterUse() {
        return true;
    }

    @Override
    public ESpecialGoodsType getType() {
        return ESpecialGoodsType.BIG_TONIC;
    }

    @Override
    public void initDescription() {
    }

    @Override
    public boolean isIOGoods() {
        return true;
    }
}
