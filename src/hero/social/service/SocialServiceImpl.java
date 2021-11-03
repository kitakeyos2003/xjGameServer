// 
// Decompiled by Procyon v0.5.36
// 
package hero.social.service;

import hero.chat.service.ChatServiceImpl;
import yoyo.service.base.session.Session;
import java.util.Iterator;
import hero.player.service.PlayerServiceImpl;
import java.util.ArrayList;
import hero.social.SocialObjectProxy;
import hero.social.message.AddSocialMemberNotify;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.social.ESocialRelationType;
import hero.player.HeroPlayer;
import java.util.Collections;
import java.util.HashMap;
import hero.social.SocialRelationList;
import java.util.Map;
import org.apache.log4j.Logger;
import yoyo.service.base.AbsServiceAdaptor;

public class SocialServiceImpl extends AbsServiceAdaptor<SocialServerConfig> {

    private static Logger log;
    protected Map<Integer, SocialRelationList> container;
    private static SocialServiceImpl instance;
    public static final int MAX_NUMBER_OF_FRIEND = 200;
    public static final int MAX_NUMBER_OF_BLACK = 50;
    public static final int MAX_NUMBER_OF_ENEMY = 50;

    static {
        SocialServiceImpl.log = Logger.getLogger((Class) SocialServiceImpl.class);
    }

    private SocialServiceImpl() {
        this.container = Collections.synchronizedMap(new HashMap<Integer, SocialRelationList>());
        this.config = new SocialServerConfig();
    }

    public static SocialServiceImpl getInstance() {
        if (SocialServiceImpl.instance == null) {
            SocialServiceImpl.instance = new SocialServiceImpl();
        }
        return SocialServiceImpl.instance;
    }

    public void add(final HeroPlayer _player, final HeroPlayer _target, final byte _type) {
        byte complexRelation = 0;
        SocialRelationList socialRelationList = this.container.get(_player.getUserID());
        SocialObjectProxy nowSocial = socialRelationList.getSocialObjectProxy(_target.getUserID());
        SocialObjectProxy oldSocial = null;
        ESocialRelationType newSocialType = ESocialRelationType.getSocialRelationType(_type);
        if (nowSocial != null) {
            if (ESocialRelationType.FRIEND == newSocialType && ESocialRelationType.FRIEND == nowSocial.socialRelationType) {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(String.valueOf(_target.getName()) + "\u5df2\u5728\u4f60\u7684\u597d\u53cb\u540d\u5355\u4e2d"));
                return;
            }
            if (ESocialRelationType.BLACK == newSocialType && ESocialRelationType.BLACK == nowSocial.socialRelationType) {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u5df2\u5728\u5c4f\u853d\u540d\u5355\u4e2d"));
                return;
            }
            if (ESocialRelationType.ENEMY == newSocialType && ESocialRelationType.ENEMY == nowSocial.socialRelationType) {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u5df2\u5728\u4ec7\u4eba\u540d\u5355\u4e2d"));
                return;
            }
            if (ESocialRelationType.ENEMY == newSocialType && ESocialRelationType.FRIEND == nowSocial.socialRelationType) {
                complexRelation = 1;
            } else if (ESocialRelationType.BLACK == newSocialType && ESocialRelationType.FRIEND == nowSocial.socialRelationType) {
                complexRelation = 2;
            } else if (ESocialRelationType.FRIEND == newSocialType && ESocialRelationType.ENEMY == nowSocial.socialRelationType) {
                complexRelation = 3;
            } else if (ESocialRelationType.BLACK == newSocialType && ESocialRelationType.ENEMY == nowSocial.socialRelationType) {
                complexRelation = 4;
            } else if (ESocialRelationType.FRIEND == newSocialType && ESocialRelationType.BLACK == nowSocial.socialRelationType) {
                complexRelation = 5;
            } else if (ESocialRelationType.ENEMY == newSocialType && ESocialRelationType.BLACK == nowSocial.socialRelationType) {
                complexRelation = 6;
            }
        }
        switch (newSocialType) {
            case FRIEND: {
                if (_player.getClan() != _target.getClan()) {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u4e0d\u540c\u7684\u79cd\u65cf"));
                    return;
                }
                nowSocial = socialRelationList.add(ESocialRelationType.FRIEND, _target);
                if (nowSocial == null) {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u597d\u53cb\u5217\u8868\u5df2\u6ee1"));
                    return;
                }
                break;
            }
            case BLACK: {
                nowSocial = socialRelationList.add(ESocialRelationType.BLACK, _target);
                if (nowSocial == null) {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u5c4f\u853d\u540d\u5355\u5df2\u6ee1"));
                    return;
                }
                break;
            }
            case ENEMY: {
                if (_player.getClan() == _target.getClan()) {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u76f8\u540c\u79cd\u65cf\u4e0d\u80fd\u6dfb\u52a0\u4ec7\u4eba"));
                    return;
                }
                nowSocial = socialRelationList.add(ESocialRelationType.ENEMY, _target);
                if (nowSocial == null) {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u597d\u53cb\u5217\u8868\u5df2\u6ee1"));
                    return;
                }
                break;
            }
        }
        if (nowSocial != null) {
            SocialServiceImpl.log.info((Object) ("\u4ed6\u4eec\u7684\u5173\u7cfb:" + complexRelation));
            if (complexRelation == 1) {
                this.remove(_player, _target.getName(), (byte) 1, true);
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new AddSocialMemberNotify(nowSocial));
                SocialDAO.add(_player.getUserID(), _target.getUserID(), _target.getName(), _type, _target.getVocation().value(), _target.getLevel());
            } else if (complexRelation == 2) {
                this.remove(_player, _target.getName(), (byte) 1, true);
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new AddSocialMemberNotify(nowSocial));
                SocialDAO.add(_player.getUserID(), _target.getUserID(), _target.getName(), _type, _target.getVocation().value(), _target.getLevel());
            } else if (complexRelation == 3) {
                this.remove(_player, _target.getName(), (byte) 3, true);
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new AddSocialMemberNotify(nowSocial));
                SocialDAO.add(_player.getUserID(), _target.getUserID(), _target.getName(), _type, _target.getVocation().value(), _target.getLevel());
            } else if (complexRelation == 5) {
                this.remove(_player, _target.getName(), (byte) 2, true);
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new AddSocialMemberNotify(nowSocial));
                SocialDAO.add(_player.getUserID(), _target.getUserID(), _target.getName(), _type, _target.getVocation().value(), _target.getLevel());
            } else {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new AddSocialMemberNotify(nowSocial));
                SocialDAO.add(_player.getUserID(), _target.getUserID(), _target.getName(), _type, _target.getVocation().value(), _target.getLevel());
            }
        }
    }

    public void remove(final HeroPlayer _player, final String _targetName, final byte _type, final boolean _real) {
        SocialRelationList socialRelationList = this.container.get(_player.getUserID());
        if (socialRelationList != null) {
            SocialObjectProxy socialObjectProxy = socialRelationList.remove(_targetName, _type);
            if (socialObjectProxy != null) {
                SocialDAO.removeOne(_player.getUserID(), socialObjectProxy.userID);
            }
        }
    }

    public ArrayList<SocialObjectProxy> getSocialRelationList(final int _userID, final ESocialRelationType _socialRelationType) {
        SocialRelationList socialRelationList = this.container.get(_userID);
        if (socialRelationList != null) {
            ArrayList<SocialObjectProxy> socialObjectProxyList = socialRelationList.getSocialRelationList(_socialRelationType);
            if (socialObjectProxyList != null && socialObjectProxyList.size() > 0) {
                for (final SocialObjectProxy socialObjectProxy : socialObjectProxyList) {
                    HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(socialObjectProxy.userID);
                    if (player != null && player.isEnable()) {
                        socialObjectProxy.setOnlineStatus(player.getLevel(), player.getVocation(), player.getSex());
                    } else {
                        socialObjectProxy.isOnline = false;
                    }
                }
                return socialObjectProxyList;
            }
        }
        return null;
    }

    public boolean beBlack(final int _beUserID, final int _hostUserID) {
        SocialRelationList socialRelationList = this.container.get(_hostUserID);
        return socialRelationList != null && socialRelationList.isBlack(_beUserID);
    }

    public boolean beFriend(final int _beUserID, final int _hostUserID) {
        SocialRelationList socialRelationList = this.container.get(_hostUserID);
        return socialRelationList != null && socialRelationList.isFriend(_beUserID);
    }

    public boolean beFriend(final String _beUser, final String _hostUser, final boolean _bySql) {
        int hostUserID = PlayerServiceImpl.getInstance().getPlayerByName(_hostUser).getUserID();
        int beUserID = PlayerServiceImpl.getInstance().getPlayerByName(_beUser).getUserID();
        SocialRelationList socialRelationList = this.container.get(hostUserID);
        if (socialRelationList != null) {
            return socialRelationList.isFriend(beUserID);
        }
        return SocialDAO.beFriend(_beUser, hostUserID);
    }

    public void deleteRole(final int _userID) {
        SocialDAO.removeAll(_userID);
        for (final SocialRelationList socialRelationList : this.container.values()) {
            socialRelationList.remove(_userID);
        }
    }

    @Override
    public void createSession(final Session _session) {
        SocialRelationList socialRelationList = this.container.get(_session.userID);
        if (socialRelationList == null) {
            socialRelationList = new SocialRelationList();
            SocialDAO.load(_session.userID, socialRelationList);
            this.container.put(_session.userID, socialRelationList);
        }
        for (final Map.Entry pairs : this.container.entrySet()) {
            int userID = Integer.valueOf(pairs.getKey().toString());
            HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(userID);
            if (player == null) {
                continue;
            }
            socialRelationList = (SocialRelationList) pairs.getValue();
            if (!socialRelationList.isEnemy(_session.userID)) {
                continue;
            }
            HeroPlayer beNotifier = PlayerServiceImpl.getInstance().getPlayerByUserID(userID);
            if (beNotifier == null || !beNotifier.isEnable()) {
                continue;
            }
            ChatServiceImpl.getInstance().sendSinglePlayer(beNotifier.getName(), "\u8bf7\u6ce8\u610f" + _session.nickName + "\u5df2\u7ecf\u4e0a\u7ebf\uff0c\u8bf7\u901f\u53bb\u8ffd\u6740\uff01");
        }
    }

    @Override
    public void sessionFree(final Session _session) {
        for (final SocialRelationList socialRelationList : this.container.values()) {
            if (socialRelationList.isEnemy(_session.userID)) {
                HeroPlayer beNotifier = PlayerServiceImpl.getInstance().getPlayerByUserID(_session.userID);
                if (beNotifier == null || !beNotifier.isEnable()) {
                    continue;
                }
                ResponseMessageQueue.getInstance().put(beNotifier.getMsgQueueIndex(), new Warning("\u4ec7\u4eba\u4e0b\u7ebf\u4e86\uff1a" + _session.nickName));
            }
        }
    }

    @Override
    public void clean(final int _userID) {
        synchronized (this.container) {
            this.container.remove(_userID);
        }
        // monitorexit(this.container)
    }
}
