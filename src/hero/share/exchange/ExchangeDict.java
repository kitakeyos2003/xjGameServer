// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.exchange;

import hero.share.service.LogWriter;
import hero.share.service.ShareServiceImpl;
import java.util.Iterator;
import hero.player.service.PlayerServiceImpl;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.ResponseExchange;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.HeroPlayer;
import java.util.concurrent.locks.ReentrantLock;
import java.util.HashMap;

public class ExchangeDict {

    private static ExchangeDict instance;
    private static int MAX_EXCHANGE_ID;
    private HashMap<Integer, Exchange> exchangeList;
    private ReentrantLock lock;

    static {
        ExchangeDict.MAX_EXCHANGE_ID = Integer.MIN_VALUE;
    }

    private ExchangeDict() {
        this.lock = new ReentrantLock();
        this.exchangeList = new HashMap<Integer, Exchange>();
    }

    public static ExchangeDict getInstance() {
        if (ExchangeDict.instance == null) {
            ExchangeDict.instance = new ExchangeDict();
        }
        return ExchangeDict.instance;
    }

    public Exchange getExchangeByID(final int _exchangeID) {
        try {
            this.lock.lock();
            return this.exchangeList.get(_exchangeID);
        } finally {
            this.lock.unlock();
        }
    }

    public void removeExchangeByID(final int _exchangeID) {
        try {
            this.lock.lock();
            this.exchangeList.remove(_exchangeID);
        } finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }

    public int addExchange(final HeroPlayer _player1, final HeroPlayer _player2) {
        try {
            this.lock.lock();
            Exchange exchange = new Exchange(this.getExchangeID(), _player1.getName(), _player2.getName());
            exchange.setRequestExchangeUserID(_player1.getUserID());
            this.exchangeList.put(exchange.getExchangeID(), exchange);
            return exchange.getExchangeID();
        } finally {
            this.lock.unlock();
        }
    }

    private int getExchangeID() {
        try {
            this.lock.lock();
            if (ExchangeDict.MAX_EXCHANGE_ID == Integer.MAX_VALUE) {
                ExchangeDict.MAX_EXCHANGE_ID = Integer.MIN_VALUE;
            }
            return ExchangeDict.MAX_EXCHANGE_ID++;
        } finally {
            this.lock.unlock();
        }
    }

    public void startExchange(final HeroPlayer player, final HeroPlayer other) {
        int exchangeID = this.addExchange(player, other);
        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new ResponseExchange(exchangeID, other.getName()));
        ResponseMessageQueue.getInstance().put(other.getMsgQueueIndex(), new ResponseExchange(exchangeID, player.getName()));
        player.swapBegin();
        other.swapBegin();
    }

    public void playerOutline(final String _nickname) {
        try {
            this.lock.lock();
            for (final Exchange exchange : this.exchangeList.values()) {
                ExchangePlayer me = exchange.getPlayerByNickname(_nickname);
                if (me != null) {
                    HeroPlayer other = PlayerServiceImpl.getInstance().getPlayerByName(exchange.getTargetByNickname(_nickname).nickname);
                    this.exchangeCancel(exchange.getExchangeID(), other, null);
                    return;
                }
            }
        } finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }

    public void exchangeCancel(final int _exchangeID, final HeroPlayer _player1, final HeroPlayer _player2) {
        if (_player1 != null && _player1.isEnable()) {
            _player1.swapOver();
            ResponseMessageQueue.getInstance().put(_player1.getMsgQueueIndex(), new ResponseExchange((byte) 6));
        }
        if (_player2 != null && _player2.isEnable()) {
            _player2.swapOver();
            ResponseMessageQueue.getInstance().put(_player2.getMsgQueueIndex(), new ResponseExchange((byte) 6));
        }
        Exchange exchange = this.getExchangeByID(_exchangeID);
        if (exchange != null) {
            ShareServiceImpl.getInstance().removePlayerFromRequestExchangeList(exchange.getRequestExchangeUserID());
            getInstance().removeExchangeByID(_exchangeID);
        } else {
            LogWriter.error("error:\u901a\u8fc7exchangeID=" + String.valueOf(_exchangeID) + "\u83b7\u5f97\u4ea4\u6613\u5bf9\u8c61\u4e3anull", null);
        }
    }
}
