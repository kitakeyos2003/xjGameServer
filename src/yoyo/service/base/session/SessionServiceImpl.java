// Decompiled with: CFR 0.151
// Class Version: 6
package yoyo.service.base.session;

import java.util.Calendar;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yoyo.core.event.AbsEvent;
import yoyo.core.queue.ResponseMessageQueue;
import yoyo.service.MonitorEvent;
import yoyo.service.ServiceManager;
import yoyo.service.base.AbsServiceAdaptor;
import yoyo.service.base.session.ISessionService;
import yoyo.service.base.session.Session;
import yoyo.service.base.session.SessionConfig;

public class SessionServiceImpl
        extends AbsServiceAdaptor<SessionConfig>
        implements ISessionService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ReentrantLock countLock;
    private static final Random RANDOM = new Random();
    private static SessionServiceImpl instance;
    private Timer checkTimer;
    private FastList<Session> sessionList;
    private FastMap<Integer, Session> sessionMap = new FastMap();
    private FastMap<Integer, Session> uIdSessionMap;
    private int count;

    private SessionServiceImpl() {
        this.sessionList = new FastList();
        this.uIdSessionMap = new FastMap();
        this.config = new SessionConfig();
        this.checkTimer = new Timer();
        this.countLock = new ReentrantLock();
    }

    public static SessionServiceImpl getInstance() {
        if (instance == null) {
            instance = new SessionServiceImpl();
        }
        return instance;
    }

    public Session getSession(int n) {
        return (Session) this.sessionMap.get((Object) n);
    }

    @Override
    public AbsEvent<Map<String, String>> montior() {
        MonitorEvent monitorEvent = new MonitorEvent(this.getName());
        monitorEvent.put("onlineNumber", String.valueOf(this.sessionList.size()));
        return monitorEvent;
    }

    @Override
    protected void start() {
        this.checkTimer.schedule((TimerTask) new CheckTask(), ((SessionConfig) this.config).checkInterval, (long) ((SessionConfig) this.config).checkInterval);
    }

    @Override
    public int createSession(int n, int n2) {
        int n3 = ResponseMessageQueue.getInstance().createItem();
        if (-1 != n3) {
            Session session = new Session();
            session.ID = this.createSessionID();
            session.index = n3;
            session.userID = n;
            session.accountID = n2;
            session.refreshTime = System.currentTimeMillis();
            this.sessionList.add(session);
            this.sessionMap.put(session.ID, session);
            this.uIdSessionMap.put(n, session);
            return session.ID;
        }
        return -1;
    }

    @Override
    public void freeSessionByAccountID(int n) {
        int n2 = 0;
        while (n2 < this.sessionList.size()) {
            Session session = (Session) this.sessionList.get(n2);
            if (session.accountID == n) {
                this.freeSession(session);
                break;
            }
            ++n2;
        }
    }

    @Override
    public void initSession(Session session) {
        ServiceManager.getInstance().createSession(session);
    }

    @Override
    public void freeSession(Session session) {
        if (session != null) {
            try {
                this.sessionList.remove((Object) session);
                this.uIdSessionMap.remove((Object) session.userID);
                this.sessionMap.remove((Object) session.ID);
                ResponseMessageQueue.getInstance().removeItem(session.index);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            ServiceManager.getInstance().dbUpdate(session.userID);
            ServiceManager.getInstance().freeSession(session);
        }
    }

    @Override
    public int getIndexBySessionID(int n) {
        Session session = (Session) this.sessionMap.get((Object) n);
        if (session == null) {
            return -1;
        }
        return session.index;
    }

    @Override
    public int getIndexByUserID(int n) {
        Session session = (Session) this.uIdSessionMap.get((Object) n);
        if (session == null) {
            return -1;
        }
        return session.index;
    }

    public Session getSessionByID(int n) {
        return (Session) this.sessionMap.get((Object) n);
    }

    public void fireSessionFree(int n) {
        Session session = (Session) this.sessionMap.get((Object) n);
        if (session != null) {
            try {
                this.sessionList.remove((Object) session);
                this.uIdSessionMap.remove((Object) session.userID);
                this.sessionMap.remove((Object) session.ID);
                ResponseMessageQueue.getInstance().removeItem(session.index);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            ServiceManager.getInstance().dbUpdate(session.userID);
            ServiceManager.getInstance().freeSession(session);
        }
    }

    private int createSessionID() {
        int n = 0;
        Calendar calendar = Calendar.getInstance();
        n |= (calendar.get(5) & 3) << 30;
        n |= (calendar.get(11) & 0x1F) << 25;
        n |= (calendar.get(12) & 0x3B) << 19;
        n |= (calendar.get(13) & 0x3B) << 13;
        n |= (this.count & 0x1F) << 8;
        n |= RANDOM.nextInt() & 0xFF;
        try {
            this.countLock.lock();
            ++this.count;
            if (this.count == Integer.MAX_VALUE) {
                this.count = 1;
            }
        } finally {
            this.countLock.unlock();
        }
        return n;
    }

    private class CheckTask
            extends TimerTask {

        private CheckTask() {
        }

        @Override
        public void run() {
            try {
                long l = System.currentTimeMillis();
                int n = 0;
                while (n < SessionServiceImpl.this.sessionList.size()) {
                    Session session = (Session) SessionServiceImpl.this.sessionList.get(n);
                    long l2 = l - session.refreshTime;
                    if (l2 > (long) ((SessionConfig) ((SessionServiceImpl) SessionServiceImpl.this).config).maxUnActiveTime) {
                        SessionServiceImpl.this.freeSession(session);
                        continue;
                    }
                    ++n;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    class MonitorInfo {

        int sessionID;
        long lastCleanTime;
        int maliceTimes;

        MonitorInfo() {
        }
    }
}
