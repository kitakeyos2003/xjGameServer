// 
// Decompiled by Procyon v0.5.36
// 
package hero.charge.service;

import hero.share.service.LogWriter;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import hero.player.HeroPlayer;
import java.util.TimerTask;
import java.util.Timer;
import hero.charge.ChargeInfo;
import javolution.util.FastList;

public class ExperienceBookService {

    private FastList<ChargeInfo> timeChargeInfoList;
    private Timer timer;
    private static ExperienceBookService instance;
    private static final long START_RELAY = 7000L;
    private static final byte CALCUL_TIME = 2;
    private static final long CALCUL_INTERVAL = 59000L;

    private ExperienceBookService() {
        this.timeChargeInfoList = (FastList<ChargeInfo>) new FastList();
    }

    public static ExperienceBookService getInstance() {
        if (ExperienceBookService.instance == null) {
            ExperienceBookService.instance = new ExperienceBookService();
        }
        return ExperienceBookService.instance;
    }

    public void start() {
        if (this.timer == null) {
            (this.timer = new Timer()).schedule(new ExpBookOnlineCalTask(), 7000L, 59000L);
        }
    }

    public void put(final ChargeInfo _chargeInfo) {
        synchronized (this.timeChargeInfoList) {
            for (final ChargeInfo chargeInfo : this.timeChargeInfoList) {
                if (chargeInfo.userID == _chargeInfo.userID) {
                    chargeInfo.expBookTimeTotal = _chargeInfo.expBookTimeTotal;
                    // monitorexit(this.timeChargeInfoList)
                    return;
                }
            }
            if (_chargeInfo.huntBookTimeTotal != 0L) {
                this.timeChargeInfoList.add(_chargeInfo);
            }
        }
        // monitorexit(this.timeChargeInfoList)
    }

    public void addExpBookTime(final HeroPlayer _player, final long _time) {
        ChargeInfo chargeInfo2 = _player.getChargeInfo();
        chargeInfo2.expBookTimeTotal += _time;
        ChargeDAO.updateExpBookTimeInfo(_player.getChargeInfo());
        synchronized (this.timeChargeInfoList) {
            for (final ChargeInfo chargeInfo : this.timeChargeInfoList) {
                if (chargeInfo.userID == _player.getUserID()) {
                    // monitorexit(this.timeChargeInfoList)
                    return;
                }
            }
            this.timeChargeInfoList.add(_player.getChargeInfo());
        }
        // monitorexit(this.timeChargeInfoList)
    }

    public void addHuntExpBookTime(final HeroPlayer _player, final long _time) {
        if (_player.getChargeInfo().huntBookTimeTotal == 0L) {
            _player.changeExperienceModulus(1.0f);
        }
        ChargeInfo chargeInfo2 = _player.getChargeInfo();
        chargeInfo2.huntBookTimeTotal += _time;
        ChargeDAO.updateHuntExpBookTimeInfo(_player.getChargeInfo());
        synchronized (this.timeChargeInfoList) {
            for (final ChargeInfo chargeInfo : this.timeChargeInfoList) {
                if (chargeInfo.userID == _player.getUserID()) {
                    // monitorexit(this.timeChargeInfoList)
                    return;
                }
            }
            this.timeChargeInfoList.add(_player.getChargeInfo());
        }
        // monitorexit(this.timeChargeInfoList)
    }

    public ChargeInfo remove(final int _userID) {
        synchronized (this.timeChargeInfoList) {
            for (int i = 0; i < this.timeChargeInfoList.size(); ++i) {
                if (((ChargeInfo) this.timeChargeInfoList.get(i)).userID == _userID) {
                    // monitorexit(this.timeChargeInfoList)
                    return (ChargeInfo) this.timeChargeInfoList.remove(i);
                }
            }
        }
        // monitorexit(this.timeChargeInfoList)
        return null;
    }

    class ExpBookOnlineCalTask extends TimerTask {

        @Override
        public void run() {
            try {
                synchronized (ExperienceBookService.this.timeChargeInfoList) {
                    boolean huntExpBookOverdue = false;
                    int i = 0;
                    while (i < ExperienceBookService.this.timeChargeInfoList.size()) {
                        huntExpBookOverdue = false;
                        ChargeInfo chargeInfo = (ChargeInfo) ExperienceBookService.this.timeChargeInfoList.get(i);
                        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(chargeInfo.userID);
                        if (player == null || !player.isEnable()) {
                            ExperienceBookService.this.timeChargeInfoList.remove(i);
                        } else {
                            if (chargeInfo.huntBookTimeTotal > 0L) {
                                ChargeInfo chargeInfo2 = chargeInfo;
                                chargeInfo2.huntBookTimeTotal -= 59000L;
                                if (chargeInfo.huntBookTimeTotal <= 0L) {
                                    chargeInfo.huntBookTimeTotal = 0L;
                                    player.changeExperienceModulus(-1.0f);
                                    huntExpBookOverdue = true;
                                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u53cc\u500d\u7ecf\u9a8c\u65f6\u95f4\u5df2\u7ed3\u675f", (byte) 1));
                                }
                            }
                            if (huntExpBookOverdue) {
                                ExperienceBookService.this.timeChargeInfoList.remove(i);
                                ChargeDAO.clearExpBookInfo(chargeInfo.userID);
                            } else {
                                ++i;
                                ChargeDAO.updateExpBookInfo(chargeInfo);
                            }
                        }
                    }
                }
                // monitorexit(ExperienceBookService.access$0(this.this$0))
            } catch (Exception e) {
                LogWriter.error("\u7ecf\u9a8c\u4e66\u7ebf\u7a0b\u5f02\u5e38", e);
            }
        }
    }
}
