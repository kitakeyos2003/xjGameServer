// 
// Decompiled by Procyon v0.5.36
// 
package hero.micro.teach;

import hero.micro.teach.message.ClearConfirmDialog;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.HeroPlayer;
import java.util.TimerTask;

public class WaitingResponse extends TimerTask {

    private int waitingtime;
    private HeroPlayer player;
    private int time;
    private boolean clear;

    public WaitingResponse(final HeroPlayer _player, final int _time, final boolean _clear) {
        this.waitingtime = 0;
        this.player = _player;
        this.time = _time;
        this.clear = _clear;
    }

    @Override
    public void run() {
        if (this.waitingtime == this.time) {
            ResponseMessageQueue.getInstance().put(this.player.getMsgQueueIndex(), new Warning("\u5bf9\u4e0d\u8d77\uff0c\u5bf9\u65b9\u5fd9\uff01"));
            if (this.clear) {
                ResponseMessageQueue.getInstance().put(this.player.getMsgQueueIndex(), new ClearConfirmDialog());
            }
            this.waitingtime = 0;
            this.player.waitingTimerRunning = false;
            this.cancel();
        } else {
            ++this.waitingtime;
        }
    }
}
