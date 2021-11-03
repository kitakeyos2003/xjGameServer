// 
// Decompiled by Procyon v0.5.36
// 
package hero.pet.clienthandler;

import hero.pet.Pet;
import hero.player.HeroPlayer;
import hero.share.message.Warning;
import yoyo.core.packet.AbsResponseMessage;
import hero.pet.message.ResponseTransactPet;
import yoyo.core.queue.ResponseMessageQueue;
import hero.pet.service.PetServiceImpl;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class TransactPet extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        int buyerID = this.yis.readInt();
        int sellerID = this.yis.readInt();
        int petID = this.yis.readInt();
        HeroPlayer buyer = PlayerServiceImpl.getInstance().getPlayerByUserID(buyerID);
        HeroPlayer seller = PlayerServiceImpl.getInstance().getPlayerByUserID(sellerID);
        Pet pet = PetServiceImpl.getInstance().getPet(sellerID, petID);
        if (pet != null) {
            if (pet.bind == 0) {
                int res = PetServiceImpl.getInstance().transactPet(buyerID, sellerID, petID);
                ResponseMessageQueue.getInstance().put(buyer.getMsgQueueIndex(), new ResponseTransactPet(buyerID, petID, res));
                ResponseMessageQueue.getInstance().put(seller.getMsgQueueIndex(), new ResponseTransactPet(sellerID, petID, res));
            } else {
                ResponseMessageQueue.getInstance().put(buyer.getMsgQueueIndex(), new Warning("\u4ea4\u6613\u5931\u8d25\uff01"));
                ResponseMessageQueue.getInstance().put(seller.getMsgQueueIndex(), new Warning("\u4ea4\u6613\u5931\u8d25\uff01\u5df2\u7ed1\u5b9a\u7684\u5ba0\u7269\u4e0d\u80fd\u4ea4\u6613\uff01"));
            }
        } else {
            ResponseMessageQueue.getInstance().put(buyer.getMsgQueueIndex(), new Warning("\u4ea4\u6613\u5931\u8d25\uff01\u5356\u5bb6\u6ca1\u6709\u6b64\u5ba0\u7269"));
            ResponseMessageQueue.getInstance().put(seller.getMsgQueueIndex(), new Warning("\u4ea4\u6613\u5931\u8d25\uff01\u5356\u5bb6\u6ca1\u6709\u6b64\u5ba0\u7269"));
        }
    }
}
