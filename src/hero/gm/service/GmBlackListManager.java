// 
// Decompiled by Procyon v0.5.36
// 
package hero.gm.service;

import hero.gm.EBlackType;
import hero.gm.BlackContainer;

public class GmBlackListManager {

    private static GmBlackListManager instance;
    private static BlackContainer accountBlContainer;
    private static BlackContainer roleBlContainer;
    private static BlackContainer chatBlContainer;

    static {
        GmBlackListManager.instance = null;
        GmBlackListManager.accountBlContainer = null;
        GmBlackListManager.roleBlContainer = null;
        GmBlackListManager.chatBlContainer = null;
    }

    public static GmBlackListManager getInstance() {
        if (GmBlackListManager.instance == null) {
            GmBlackListManager.instance = new GmBlackListManager();
        }
        return GmBlackListManager.instance;
    }

    public void init() {
    }

    public BlackContainer getContainer(final EBlackType _type) {
        switch (_type) {
            case ACCOUNT_LOGIN: {
                return GmBlackListManager.accountBlContainer;
            }
            case ROLE_LOGIN: {
                return GmBlackListManager.roleBlContainer;
            }
            case ROLE_CHAT: {
                return GmBlackListManager.chatBlContainer;
            }
            default: {
                return null;
            }
        }
    }

    public void removeBlack(final EBlackType _type, final String _name) {
        BlackContainer bc = this.getContainer(_type);
    }

    public String getBlackEndTime(final EBlackType _type, final String _name) {
        BlackContainer bc = this.getContainer(_type);
        return "";
    }
}
