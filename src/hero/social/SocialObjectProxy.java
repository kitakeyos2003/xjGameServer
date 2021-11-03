// 
// Decompiled by Procyon v0.5.36
// 
package hero.social;

import hero.player.define.ESex;
import hero.share.EVocation;

public class SocialObjectProxy {

    public ESocialRelationType socialRelationType;
    public int userID;
    public String name;
    public EVocation vocation;
    public ESex sex;
    public short level;
    public boolean isOnline;

    public SocialObjectProxy(final ESocialRelationType _socialRelationType, final int _userID, final String _name) {
        this.socialRelationType = _socialRelationType;
        this.userID = _userID;
        this.name = _name;
    }

    public SocialObjectProxy(final byte _socialRelationType, final int _userID, final String _name) {
        this.userID = _userID;
        this.name = _name;
        this.socialRelationType = ESocialRelationType.getSocialRelationType(_socialRelationType);
    }

    public void setOnlineStatus(final short _level, final EVocation _vocation, final ESex _sex) {
        this.isOnline = true;
        this.level = _level;
        this.vocation = _vocation;
        this.sex = _sex;
    }
}
