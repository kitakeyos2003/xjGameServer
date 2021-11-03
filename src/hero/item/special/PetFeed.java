// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.special;

import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.log.service.LogServiceImpl;
import hero.pet.service.PetServiceImpl;
import hero.pet.Pet;
import hero.player.HeroPlayer;
import hero.pet.FeedType;
import hero.item.SpecialGoods;

public class PetFeed extends SpecialGoods {

    public static final short FEED_TYPE_NORMAL = 1;
    public static final short FEED_TYPE_HERBIVORE_GROW = 2;
    public static final short FEED_TYPE_CARNIVORE_GROW = 3;
    public static final short FEED_TYPE_DADIJH = 4;
    public static final short FEED_TYPE_LYCZ = 5;
    private FeedType feedType;

    public PetFeed(final int id, final short stackNums) {
        super(id, stackNums);
    }

    @Override
    public boolean beUse(final HeroPlayer _player, final Object target, final int location) {
        Pet pet = (Pet) target;
        if (pet.pk.getStage() > 0) {
            boolean r = PetServiceImpl.getInstance().feedPet(_player.getUserID(), pet, this.getID());
            if (r) {
                LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID, _player.getLoginInfo().username, _player.getUserID(), _player.getName(), this.getID(), this.getName(), this.getTrait().getDesc(), this.getType().getDescription());
            }
            return r;
        }
        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u5ba0\u7269\u672a\u5230\u5e7c\u5e74\uff0c\u4e0d\u80fd\u5582\u517b\uff01", (byte) 0));
        return false;
    }

    @Override
    public boolean disappearImmediatelyAfterUse() {
        return true;
    }

    @Override
    public byte getSingleGoodsType() {
        return 5;
    }

    @Override
    public ESpecialGoodsType getType() {
        return ESpecialGoodsType.PET_FEED;
    }

    @Override
    public void initDescription() {
    }

    @Override
    public boolean isIOGoods() {
        return true;
    }

    public FeedType getFeedType() {
        return this.feedType;
    }

    public void setFeedType(final FeedType feedType) {
        this.feedType = feedType;
    }
}
