// 
// Decompiled by Procyon v0.5.36
// 
package hero.gm.service;

import org.dom4j.Element;
import yoyo.service.base.AbsConfig;

public class GmServiceConfig extends AbsConfig {

    private int port;
    private String accountDBurl;
    private String accountDBname;
    private String accountDBusername;
    private String accountDBpassword;
    private String commitDBurl;
    private String commitDBname;
    private String commitDBusername;
    private String commitDBpassword;
    private int serverID;
    private String addChatContentURL;
    private int gameID;

    @Override
    public void init(final Element _root) throws Exception {
        try {
            Element eRoot = _root.element("config");
            this.port = Integer.parseInt(eRoot.elementTextTrim("port"));
            this.serverID = Integer.parseInt(eRoot.elementTextTrim("serverID"));
            this.gameID = Integer.parseInt(eRoot.elementTextTrim("gameID"));
            this.addChatContentURL = eRoot.elementTextTrim("add_chat_content_to_gm_url");
            this.accountDBurl = eRoot.elementTextTrim("gm_db_url");
            this.accountDBname = eRoot.elementTextTrim("gm_db_name");
            this.accountDBusername = eRoot.elementTextTrim("gm_name");
            this.accountDBpassword = eRoot.elementTextTrim("gm_password");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getPort() {
        return this.port;
    }

    public String getAccountDBurl() {
        return this.accountDBurl;
    }

    public String getAccountDBname() {
        return this.accountDBname;
    }

    public String getAccountDBusername() {
        return this.accountDBusername;
    }

    public String getAccountDBpassword() {
        return this.accountDBpassword;
    }

    public String getCommitDBurl() {
        return this.commitDBurl;
    }

    public String getCommitDBname() {
        return this.commitDBname;
    }

    public String getCommitDBusername() {
        return this.commitDBusername;
    }

    public String getCommitDBpassword() {
        return this.commitDBpassword;
    }

    public int getServerID() {
        return this.serverID;
    }

    public void setServerID(final int serverID) {
        this.serverID = serverID;
    }

    public String getAddChatContentURL() {
        return this.addChatContentURL;
    }

    public void setAddChatContentURL(final String addChatContentURL) {
        this.addChatContentURL = addChatContentURL;
    }

    public int getGameID() {
        return this.gameID;
    }
}
