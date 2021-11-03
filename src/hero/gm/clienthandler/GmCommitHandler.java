// 
// Decompiled by Procyon v0.5.36
// 
package hero.gm.clienthandler;

import java.io.IOException;
import hero.gm.service.GmServiceImpl;
import hero.gm.ResponseToGmTool;
import hero.gm.EResponseType;
import yoyo.core.process.AbsClientProcess;

public class GmCommitHandler extends AbsClientProcess {

    @Override
    public void read() throws Exception {
        try {
            int questionID = this.yis.readInt();
            byte appraise = this.yis.readByte();
            ResponseToGmTool rtgt = new ResponseToGmTool(EResponseType.SEND_QUESTION_APPRAISE, 0);
            rtgt.setQuestionAppraise(questionID, appraise);
            GmServiceImpl.addGmToolMsg(rtgt);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
