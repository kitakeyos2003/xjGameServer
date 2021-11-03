// 
// Decompiled by Procyon v0.5.36
// 
package hero.player.clienthandler;

import hero.item.special.HeavenBook;
import java.util.List;
import hero.player.HeroPlayer;
import hero.player.message.ResponseHeavenBookList;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.item.service.GoodsServiceImpl;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class RequestInlayHeavenBook extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        int bookID = this.yis.readInt();
        byte position = this.yis.readByte();
        if (bookID == 0) {
            List<HeavenBook> heavenBookList = GoodsServiceImpl.getInstance().getPlayerSepcialBagHeavenBooks(player);
            if (heavenBookList == null || heavenBookList.size() == 0) {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u60a8\u7684\u80cc\u5305\u4e2d\u6ca1\u6709\u591a\u4f59\u7684\u5929\u4e66\uff0c\u9576\u5d4c\u5929\u4e66\u53ef\u4ee5\u4e3a\u89d2\u8272\u63d0\u4f9b\u989d\u5916\u7684\u6280\u80fd\u52a0\u6210\uff0c\u60a8\u53ef\u4ee5\u53bb\u5546\u57ce\u8d2d\u4e70\u5929\u4e66\u3002", (byte) 2, (byte) 1));
                return;
            }
            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseHeavenBookList(heavenBookList));
        } else {
            int booknum = player.getInventory().getSpecialGoodsBag().getGoodsNumber(bookID);
            if (booknum == 0) {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u6ca1\u6709\u627e\u5230\u8fd9\u672c\u5929\u4e66"));
            } else {
                int currPositionBookID = player.heaven_book_ids[position];
                if (currPositionBookID > 0) {
                    player.currInlayHeavenBookID = bookID;
                    player.currInlayHeavenBookPosition = position;
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u786e\u5b9a\u8986\u76d6\u5df2\u7ecf\u9576\u5d4c\u7684\u5929\u4e66?", (byte) 4, (short) 1049));
                } else {
                    PlayerServiceImpl.getInstance().startInlayHeavenBook(player, position, bookID);
                }
            }
        }
    }
}
