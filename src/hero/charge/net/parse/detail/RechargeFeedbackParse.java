// 
// Decompiled by Procyon v0.5.36
// 
package hero.charge.net.parse.detail;

import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.ResultFeeTip;
import yoyo.core.queue.ResponseMessageQueue;
import java.util.HashMap;
import hero.player.HeroPlayer;
import hero.charge.net.parse.XmlParamModel;

public class RechargeFeedbackParse extends XmlParamModel {

    private HeroPlayer player;
    private String resultStr;

    public RechargeFeedbackParse(final HashMap<String, String> _param) {
        super(_param);
    }

    @Override
    public void process() {
        if (this.player != null && this.player.isEnable()) {
            ResponseMessageQueue.getInstance().put(this.player.getMsgQueueIndex(), new ResultFeeTip(this.resultStr));
        }
    }

    @Override
    protected void parse(final HashMap<String, String> _param) {
    }
}
