// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.special;

import hero.pet.Pet;
import hero.log.service.LogServiceImpl;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.pet.service.PetServiceImpl;
import hero.player.HeroPlayer;
import hero.item.SpecialGoods;

public class PetArchetype extends SpecialGoods {

    private int petAID;

    public PetArchetype(final int id, final short stackNums) {
        super(id, stackNums);
    }

    @Override
    public boolean beUse(final HeroPlayer _player, final Object _target, final int _location) {
        Pet pet = PetServiceImpl.getInstance().addPet(_player.getUserID(), this.petAID);
        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u83b7\u5f97" + pet.name, (byte) 0));
        LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID, _player.getLoginInfo().username, _player.getUserID(), _player.getName(), this.getID(), this.getName(), this.getTrait().getDesc(), this.getType().getDescription());
        return true;
    }

    public void setPetAID(final int petAID) {
        this.petAID = petAID;
    }

    @Override
    public boolean disappearImmediatelyAfterUse() {
        return true;
    }

    @Override
    public ESpecialGoodsType getType() {
        return ESpecialGoodsType.PET_ARCHETYPE;
    }

    @Override
    public byte getSingleGoodsType() {
        return 5;
    }

    @Override
    public void initDescription() {
    }

    @Override
    public boolean isIOGoods() {
        return true;
    }
}
