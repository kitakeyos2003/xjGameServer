// 
// Decompiled by Procyon v0.5.36
// 
package hero.social;

import hero.player.HeroPlayer;
import java.util.Iterator;
import java.util.ArrayList;

public class SocialRelationList {

    private ArrayList<SocialObjectProxy> list;
    private short friendNumber;
    private short enemyNumber;
    private short blackNumber;

    public SocialRelationList() {
        this.list = new ArrayList<SocialObjectProxy>();
    }

    public ArrayList<SocialObjectProxy> getSocialRelationList(final ESocialRelationType _socialRelationType) {
        ArrayList<SocialObjectProxy> socialRelationList = new ArrayList<SocialObjectProxy>();
        for (final SocialObjectProxy socialObjectProxy : this.list) {
            if (socialObjectProxy.socialRelationType == _socialRelationType) {
                socialRelationList.add(socialObjectProxy);
            }
        }
        if (socialRelationList.size() > 0) {
            return socialRelationList;
        }
        return null;
    }

    public SocialObjectProxy getSocialObjectProxy(final int _userID) {
        for (final SocialObjectProxy socialObjectProxy : this.list) {
            if (socialObjectProxy.userID == _userID) {
                return socialObjectProxy;
            }
        }
        return null;
    }

    public SocialObjectProxy getSocialObjectProxy(final String _name) {
        for (final SocialObjectProxy socialObjectProxy : this.list) {
            if (socialObjectProxy.name.equals(_name)) {
                return socialObjectProxy;
            }
        }
        return null;
    }

    public boolean isBlack(final int _userID) {
        if (this.blackNumber > 0) {
            for (final SocialObjectProxy socialObjectProxy : this.list) {
                if (socialObjectProxy.socialRelationType == ESocialRelationType.BLACK && socialObjectProxy.userID == _userID) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isEnemy(final int _userID) {
        if (this.enemyNumber > 0) {
            for (final SocialObjectProxy socialObjectProxy : this.list) {
                if (socialObjectProxy.socialRelationType == ESocialRelationType.ENEMY && socialObjectProxy.userID == _userID) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isFriend(final int _userID) {
        if (this.friendNumber > 0) {
            for (final SocialObjectProxy socialObjectProxy : this.list) {
                if (socialObjectProxy.socialRelationType == ESocialRelationType.FRIEND && socialObjectProxy.userID == _userID) {
                    return true;
                }
            }
        }
        return false;
    }

    public SocialObjectProxy add(final ESocialRelationType _socialRelationType, final HeroPlayer _target) {
        SocialObjectProxy socialObjectProxy = null;
        switch (_socialRelationType) {
            case FRIEND: {
                if (this.friendNumber < 200) {
                    socialObjectProxy = new SocialObjectProxy(ESocialRelationType.FRIEND, _target.getUserID(), _target.getName());
                    this.list.add(socialObjectProxy);
                    socialObjectProxy.setOnlineStatus(_target.getLevel(), _target.getVocation(), _target.getSex());
                    ++this.friendNumber;
                    break;
                }
                break;
            }
            case ENEMY: {
                if (this.enemyNumber < 50) {
                    socialObjectProxy = new SocialObjectProxy(ESocialRelationType.ENEMY, _target.getUserID(), _target.getName());
                    this.list.add(socialObjectProxy);
                    socialObjectProxy.setOnlineStatus(_target.getLevel(), _target.getVocation(), _target.getSex());
                    ++this.enemyNumber;
                    break;
                }
                break;
            }
            case BLACK: {
                if (this.blackNumber < 50) {
                    socialObjectProxy = new SocialObjectProxy(ESocialRelationType.BLACK, _target.getUserID(), _target.getName());
                    this.list.add(socialObjectProxy);
                    socialObjectProxy.setOnlineStatus(_target.getLevel(), _target.getVocation(), _target.getSex());
                    ++this.blackNumber;
                    break;
                }
                break;
            }
            default: {
                return null;
            }
        }
        return socialObjectProxy;
    }

    public void add(final byte _socialRelationTypeValue, final int _playerUserID, final String _playerName) {
        SocialObjectProxy socialObjectProxy = new SocialObjectProxy(_socialRelationTypeValue, _playerUserID, _playerName);
        this.list.add(socialObjectProxy);
        switch (socialObjectProxy.socialRelationType) {
            case FRIEND: {
                ++this.friendNumber;
                break;
            }
            case ENEMY: {
                ++this.enemyNumber;
                break;
            }
            case BLACK: {
                ++this.blackNumber;
                break;
            }
        }
    }

    public SocialObjectProxy remove(final String _name, final byte _type) {
        for (int i = 0; i < this.list.size(); ++i) {
            if (this.list.get(i).name.equals(_name)) {
                SocialObjectProxy socialObjectProxy = this.list.get(i);
                if (socialObjectProxy.socialRelationType.value == _type) {
                    socialObjectProxy = this.list.remove(i);
                    switch (socialObjectProxy.socialRelationType) {
                        case FRIEND: {
                            --this.friendNumber;
                            break;
                        }
                        case ENEMY: {
                            --this.enemyNumber;
                            break;
                        }
                        case BLACK: {
                            --this.blackNumber;
                            break;
                        }
                    }
                    return socialObjectProxy;
                }
            }
        }
        return null;
    }

    public SocialObjectProxy remove(final int _userID) {
        for (int i = 0; i < this.list.size(); ++i) {
            if (this.list.get(i).userID == _userID) {
                SocialObjectProxy socialObjectProxy = this.list.remove(i);
                switch (socialObjectProxy.socialRelationType) {
                    case FRIEND: {
                        --this.friendNumber;
                        break;
                    }
                    case ENEMY: {
                        --this.enemyNumber;
                        break;
                    }
                    case BLACK: {
                        --this.blackNumber;
                        break;
                    }
                }
                return socialObjectProxy;
            }
        }
        return null;
    }
}
