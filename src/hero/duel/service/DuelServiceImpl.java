// 
// Decompiled by Procyon v0.5.36
// 
package hero.duel.service;

import yoyo.service.base.session.Session;
import hero.share.message.Warning;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.effect.service.EffectServiceImpl;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.packet.AbsResponseMessage;
import hero.duel.message.ResponseDuel;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.HeroPlayer;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.ArrayList;
import java.util.Timer;
import java.util.concurrent.locks.ReentrantLock;
import hero.duel.Duel;
import java.util.List;
import yoyo.service.base.AbsServiceAdaptor;

public class DuelServiceImpl extends AbsServiceAdaptor<PvpServerConfig> {

    private static DuelServiceImpl instance;
    private List<Duel> duelList;
    private ReentrantLock lock;
    private Timer timeOutChecker;
    public static final long DUEL_KEEP_TIME = 300000L;
    public static final long INTERVAL_OF_CHECK = 10000L;
    public static final long DELAY_OF_CHECK = 30000L;

    private DuelServiceImpl() {
        this.lock = new ReentrantLock();
        this.duelList = new ArrayList<Duel>();
        (this.timeOutChecker = new Timer()).schedule(new TimeOutCheckTask(), 30000L, 10000L);
        this.config = new PvpServerConfig();
    }

    public static DuelServiceImpl getInstance() {
        if (DuelServiceImpl.instance == null) {
            DuelServiceImpl.instance = new DuelServiceImpl();
        }
        return DuelServiceImpl.instance;
    }

    public boolean isDueling(final int _userID) {
        return this.getDuelByOneSide(_userID) != null;
    }

    public boolean isDueling(final int _p1UserID, final int _p2UserID) {
        try {
            this.lock.lock();
            for (final Duel d : this.duelList) {
                if ((d.player1UserID != _p1UserID || d.player2UserID != _p2UserID) && (d.player1UserID != _p2UserID || d.player2UserID != _p1UserID)) {
                    continue;
                }
                return true;
            }
        } finally {
            this.lock.unlock();
        }
        this.lock.unlock();
        return false;
    }

    public Duel removeByOneSide(final int _userID) {
        try {
            this.lock.lock();
            for (int i = 0; i < this.duelList.size(); ++i) {
                Duel d = this.duelList.get(i);
                if (d.player1UserID == _userID || d.player2UserID == _userID) {
                    this.duelList.remove(i);
                    return d;
                }
            }
        } finally {
            this.lock.unlock();
        }
        this.lock.unlock();
        return null;
    }

    public Duel getDuelByOneSide(final int _userID) {
        try {
            this.lock.lock();
            for (final Duel d : this.duelList) {
                if (d.player1UserID == _userID || d.player2UserID == _userID) {
                    return d;
                }
            }
        } finally {
            this.lock.unlock();
        }
        this.lock.unlock();
        return null;
    }

    public boolean inviteDuel(final HeroPlayer _player1, final HeroPlayer _player2) {
        if (_player1.where() == _player2.where()) {
            this.duelList.add(new Duel(_player1.getUserID(), _player2.getUserID(), _player1.where().getID()));
            return true;
        }
        return false;
    }

    public void wonDuel(final HeroPlayer _winner) {
        Duel dule = this.removeByOneSide(_winner.getUserID());
        if (dule != null) {
            ResponseMessageQueue.getInstance().put(_winner.getMsgQueueIndex(), new ResponseDuel((byte) 0));
            _winner.endDuel();
            _winner.removePvpTarget((dule.player1UserID == _winner.getUserID()) ? dule.player2UserID : dule.player1UserID);
            HeroPlayer failer = PlayerServiceImpl.getInstance().getPlayerByUserID((dule.player1UserID == _winner.getUserID()) ? dule.player2UserID : dule.player1UserID);
            if (failer != null) {
                EffectServiceImpl.getInstance().removeDuelEffect(failer, _winner);
                ResponseMessageQueue.getInstance().put(failer.getMsgQueueIndex(), new ResponseDuel((byte) 0));
                failer.endDuel();
                failer.removePvpTarget(_winner.getUserID());
                MapSynchronousInfoBroadcast.getInstance().put(_winner.where(), new Warning(String.valueOf(_winner.getName()) + "\u5728\u51b3\u6597\u4e2d\u6218\u80dc\u4e86" + failer.getName()), false, 0);
            } else {
                failer = PlayerServiceImpl.getInstance().getOffLinePlayerInfo((dule.player1UserID == _winner.getUserID()) ? dule.player2UserID : dule.player1UserID);
            }
        }
    }

    @Override
    public void sessionFree(final Session _session) {
        Duel dule = this.removeByOneSide(_session.userID);
        if (dule != null) {
            HeroPlayer another = PlayerServiceImpl.getInstance().getPlayerByUserID((dule.player1UserID == _session.userID) ? dule.player2UserID : dule.player1UserID);
            if (dule.isConfirming) {
                ResponseMessageQueue.getInstance().put(another.getMsgQueueIndex(), new Warning("\u4e0d\u5c51\u4e8e\u4e0e\u4f60\u51b3\u6597"));
            } else {
                ResponseMessageQueue.getInstance().put(another.getMsgQueueIndex(), new ResponseDuel((byte) 1));
                another.endDuel();
                another.removePvpTarget(_session.userID);
            }
        }
    }

    private void check() {
        long nowTime = System.currentTimeMillis();
        int i = 0;
        while (i < this.duelList.size()) {
            Duel duel = this.duelList.get(i);
            if (nowTime - duel.startTime >= 300000L) {
                this.duelList.remove(i);
                HeroPlayer oneSide = PlayerServiceImpl.getInstance().getPlayerByUserID(duel.player1UserID);
                AbsResponseMessage msg = new ResponseDuel((byte) 2);
                if (oneSide != null) {
                    oneSide.endDuel();
                    oneSide.removePvpTarget(duel.player2UserID);
                    if (oneSide.isEnable()) {
                        ResponseMessageQueue.getInstance().put(oneSide.getMsgQueueIndex(), msg);
                    }
                }
                HeroPlayer anotherSide = PlayerServiceImpl.getInstance().getPlayerByUserID(duel.player2UserID);
                if (anotherSide == null) {
                    continue;
                }
                anotherSide.endDuel();
                anotherSide.removePvpTarget(duel.player1UserID);
                if (!anotherSide.isEnable()) {
                    continue;
                }
                ResponseMessageQueue.getInstance().put(anotherSide.getMsgQueueIndex(), msg);
            } else {
                HeroPlayer oneSide = PlayerServiceImpl.getInstance().getPlayerByUserID(duel.player1UserID);
                HeroPlayer anotherSide2 = PlayerServiceImpl.getInstance().getPlayerByUserID(duel.player2UserID);
                if (oneSide == null) {
                    AbsResponseMessage msg2 = new ResponseDuel((byte) 1);
                    if (anotherSide2 != null) {
                        anotherSide2.endDuel();
                        anotherSide2.removePvpTarget(duel.player1UserID);
                        if (anotherSide2.isEnable()) {
                            ResponseMessageQueue.getInstance().put(anotherSide2.getMsgQueueIndex(), msg2);
                        }
                    }
                    this.duelList.remove(i);
                } else if (!oneSide.isEnable()) {
                    oneSide.endDuel();
                    oneSide.removePvpTarget(duel.player2UserID);
                    AbsResponseMessage msg2 = new ResponseDuel((byte) 1);
                    if (anotherSide2 != null) {
                        anotherSide2.endDuel();
                        anotherSide2.removePvpTarget(duel.player1UserID);
                        if (anotherSide2.isEnable()) {
                            ResponseMessageQueue.getInstance().put(anotherSide2.getMsgQueueIndex(), msg2);
                        }
                    }
                    this.duelList.remove(i);
                } else if (anotherSide2 == null) {
                    AbsResponseMessage msg2 = new ResponseDuel((byte) 1);
                    oneSide.endDuel();
                    oneSide.removePvpTarget(duel.player2UserID);
                    if (oneSide.isEnable()) {
                        ResponseMessageQueue.getInstance().put(oneSide.getMsgQueueIndex(), msg2);
                    }
                    this.duelList.remove(i);
                } else if (!anotherSide2.isEnable()) {
                    anotherSide2.endDuel();
                    anotherSide2.removePvpTarget(duel.player1UserID);
                    AbsResponseMessage msg2 = new ResponseDuel((byte) 1);
                    oneSide.endDuel();
                    oneSide.removePvpTarget(duel.player2UserID);
                    if (oneSide.isEnable()) {
                        ResponseMessageQueue.getInstance().put(oneSide.getMsgQueueIndex(), msg2);
                    }
                    this.duelList.remove(i);
                } else if (oneSide.where().getID() != duel.duleMapID) {
                    AbsResponseMessage msg2 = new ResponseDuel((byte) 0);
                    oneSide.endDuel();
                    oneSide.removePvpTarget(duel.player2UserID);
                    anotherSide2.endDuel();
                    anotherSide2.removePvpTarget(duel.player1UserID);
                    if (oneSide.isEnable()) {
                        ResponseMessageQueue.getInstance().put(oneSide.getMsgQueueIndex(), msg2);
                    }
                    if (anotherSide2.isEnable()) {
                        ResponseMessageQueue.getInstance().put(anotherSide2.getMsgQueueIndex(), msg2);
                    }
                    this.duelList.remove(i);
                } else if (anotherSide2.where().getID() != duel.duleMapID) {
                    AbsResponseMessage msg2 = new ResponseDuel((byte) 0);
                    oneSide.endDuel();
                    oneSide.removePvpTarget(duel.player2UserID);
                    anotherSide2.endDuel();
                    anotherSide2.removePvpTarget(duel.player1UserID);
                    if (oneSide.isEnable()) {
                        ResponseMessageQueue.getInstance().put(oneSide.getMsgQueueIndex(), msg2);
                    }
                    if (anotherSide2.isEnable()) {
                        ResponseMessageQueue.getInstance().put(anotherSide2.getMsgQueueIndex(), msg2);
                    }
                    this.duelList.remove(i);
                } else {
                    ++i;
                }
            }
        }
    }

    class TimeOutCheckTask extends TimerTask {

        @Override
        public void run() {
            try {
                DuelServiceImpl.this.check();
            } catch (Exception ex) {
            }
        }
    }
}
