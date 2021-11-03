// 
// Decompiled by Procyon v0.5.36
// 
package hero.gm.clienthandler;

import hero.player.HeroPlayer;
import hero.log.service.LogServiceImpl;
import hero.gm.service.GmServiceImpl;
import hero.gm.ResponseToGmTool;
import hero.gm.EResponseType;
import hero.chat.service.ChatServiceImpl;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.process.AbsClientProcess;

public class ToGmChatHandler extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        String gmName = this.yis.readUTF();
        int sid = this.yis.readInt();
        int questionID = this.yis.readInt();
        String content = this.yis.readUTF();
        HeroPlayer speaker = PlayerServiceImpl.getInstance().getPlayerBySessionID(this.contextData.sessionID);
        ChatServiceImpl.getInstance().sendSinglePlayer(speaker, gmName, speaker, content, true);
        ResponseToGmTool rtgt = new ResponseToGmTool(EResponseType.SEND_QUEATION_EACH, 0);
        rtgt.setQuestionEach(sid, questionID, content);
        GmServiceImpl.addGmToolMsg(rtgt);
        LogServiceImpl.getInstance().talkLog(speaker.getLoginInfo().accountID, speaker.getUserID(), speaker.getName(), speaker.getLoginInfo().loginMsisdn, 10000, gmName, "GM\u79c1\u804a", speaker.where().getName(), content);
    }
}
