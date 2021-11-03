// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.special;

import hero.log.service.LogServiceImpl;
import hero.skill.message.LearnedSkillListNotify;
import hero.player.message.ResponseInlayHeavenBook;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import yoyo.core.packet.AbsResponseMessage;
import hero.player.message.AskChooseHeavenBookPosition;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.HeroPlayer;
import hero.item.SpecialGoods;

public class HeavenBook extends SpecialGoods {

    private short skillPoint;
    private byte position;

    public HeavenBook(final int _id, final short _stackNums) {
        super(_id, _stackNums);
        this.position = -1;
    }

    @Override
    public boolean beUse(final HeroPlayer _player, final Object _target, final int _location) {
        boolean succ = false;
        if (this.position == -1) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new AskChooseHeavenBookPosition(_player, this.getID()));
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u8bf7\u9009\u62e9\u5929\u4e66\u63d2\u69fd", (byte) 0));
        } else {
            succ = PlayerServiceImpl.getInstance().inlayHeavenBook(_player, this.position, this.getID());
            if (!succ) {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u9576\u5d4c\u5929\u4e66\u4e0d\u6210\u529f", (byte) 0));
            }
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ResponseInlayHeavenBook(this.getID(), this.position, succ, _player.heavenBookSame));
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new LearnedSkillListNotify(_player));
        }
        if (succ) {
            LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID, _player.getLoginInfo().username, _player.getUserID(), _player.getName(), this.getID(), this.getName(), this.getTrait().getDesc(), this.getType().getDescription());
        }
        return succ;
    }

    @Override
    public ESpecialGoodsType getType() {
        return ESpecialGoodsType.HEAVEN_BOOK;
    }

    @Override
    public boolean disappearImmediatelyAfterUse() {
        return true;
    }

    @Override
    public boolean isIOGoods() {
        return true;
    }

    @Override
    public void initDescription() {
    }

    public void setSkillPoint(final short point) {
        this.skillPoint = point;
    }

    public short getSkillPoint() {
        return this.skillPoint;
    }

    public void setPosition(final byte position) {
        this.position = position;
    }

    public int getComBonus() {
        return (int) Math.pow(2.0, this.getTrait().value());
    }
}
