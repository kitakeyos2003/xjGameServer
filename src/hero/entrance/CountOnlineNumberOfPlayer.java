// Decompiled with: FernFlower
// Class Version: 6
package hero.entrance;

import hero.player.service.PlayerServiceImpl;
import org.apache.log4j.Logger;

public class CountOnlineNumberOfPlayer implements Runnable {

    private static Logger log = Logger.getLogger(CountOnlineNumberOfPlayer.class);

    public void run() {
        log.info("统计线程已启动......");

        try {
            Thread.sleep(30000L);
        } catch (Exception var2) {
            var2.printStackTrace();
        }

        while (true) {
            while (true) {
                try {
                    log.info("在线玩家数量：" + PlayerServiceImpl.getInstance().getPlayerList().size());
                    Thread.sleep(60000L);
                } catch (Exception var3) {
                    log.error("统计线程错误：" + var3.getMessage());
                    var3.printStackTrace();
                }
            }
        }
    }
}
