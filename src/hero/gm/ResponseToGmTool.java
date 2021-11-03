// 
// Decompiled by Procyon v0.5.36
// 
package hero.gm;

public class ResponseToGmTool {

    public EResponseType responseType;
    public int sessionID;
    public ParamChatContent chatContent;
    public ParamRoleOnline roleOnline;
    public ParamRoleOutline roleOutline;
    public ParamRoleLvlUpdate roleLvlUpdate;
    public ParamNewQuestion newQuestion;
    public ParamQuestionEach questionEach;
    public ParamQuestionAppraise questionAppraise;

    public ResponseToGmTool(final EResponseType _responseType, final int _sessionID) {
        this.responseType = _responseType;
        this.sessionID = _sessionID;
    }

    public void setRoleOutline(final String _nickname) {
        this.roleOutline = new ParamRoleOutline(_nickname);
    }

    public void setRoleLvlUpdate(final String _nickname, final int _lvl, final String _occupetion) {
        this.roleLvlUpdate = new ParamRoleLvlUpdate(_nickname, _lvl, _occupetion);
    }

    public void setNewQuestion(final String _nickname, final byte _type, final String _info) {
        this.newQuestion = new ParamNewQuestion(_nickname, _type, _info);
    }

    public void setQuestionEach(final int _sid, final int _id, final String _content) {
        this.questionEach = new ParamQuestionEach(_sid, _id, _content);
    }

    public void setQuestionAppraise(final int _id, final byte _appraise) {
        this.questionAppraise = new ParamQuestionAppraise(_id, _appraise);
    }
}
