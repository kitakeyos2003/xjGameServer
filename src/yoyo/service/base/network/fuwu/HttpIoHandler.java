// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.service.base.network.fuwu;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.future.CloseFuture;
import yoyo.core.process.AbsClientProcess;
import yoyo.core.packet.ResponseData;
import yoyo.service.base.session.Session;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import yoyo.core.queue.ResponseMessageQueue;
import yoyo.core.threadpool.TaskThreadPool;
import yoyo.service.Priority;
import java.net.InetSocketAddress;
import yoyo.service.ServiceManager;
import yoyo.service.PriorityManager;
import yoyo.service.base.session.SessionServiceImpl;
import yoyo.core.packet.ContextData;
import org.apache.mina.core.session.IoSession;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;

public class HttpIoHandler extends IoHandlerAdapter {

    private final Logger logger;

    public HttpIoHandler() {
        this.logger = LoggerFactory.getLogger((Class) this.getClass());
    }

    public void exceptionCaught(final IoSession session, final Throwable ex) throws Exception {
        session.close();
    }

    public void messageReceived(final IoSession ioSession, final Object message) throws Exception {
        ContextData[] cds = (ContextData[]) message;
        int sessionID = cds[0].sessionID;
        Session session = SessionServiceImpl.getInstance().getSession(sessionID);
        ResponseData rd = null;
        long receivedTime = System.currentTimeMillis();
        short key = 0;
        if (session != null) {
            session.refreshTime = System.currentTimeMillis();
            ContextData[] array;
            for (int length = (array = cds).length, i = 0; i < length; ++i) {
                ContextData cd = array[i];
                receivedTime = cd.recvTime;
                key = cd.key;
                Priority priority = PriorityManager.getInstance().getPriorityByMsgId(cd.messageID);
                AbsClientProcess ch = ServiceManager.getInstance().getClientProcess(cd);
                try {
                    InetSocketAddress address = (InetSocketAddress) ioSession.getRemoteAddress();
                    if (address != null) {
                        String ip = address.getAddress().getHostAddress();
                        ch.setIp(ip);
                    }
                } catch (Exception e) {
                    this.logger.error("get IP error,accountID=" + session.accountID + ",nickname=" + session.nickName + ",userID=" + session.userID);
                }
                if (priority == Priority.REAL_TIME) {
                    ch.run();
                } else {
                    TaskThreadPool.getInstance().addTask(ch);
                }
            }
            rd = ResponseMessageQueue.getInstance().get(session.index);
            if (rd.isErrorMessage()) {
                this.logger.warn("sessionID = " + sessionID + "; nickname=" + session.nickName + "\u7684\u7528\u6237\u88ab\u901a\u77e5\u6389\u7ebf,\u4f46\u4ed6\u7684session\u5e76\u4e0d\u4e3aNULL");
            }
        } else {
            this.logger.warn("!!!to yoyo-->\u901a\u8fc7sessionID\u83b7\u5f97session\u4e3anull. sessionID=" + sessionID);
            rd = ResponseMessageQueue.getInstance().getErrorData();
        }
        rd.setSessionID(sessionID);
        rd.setRecvTime(receivedTime);
        rd.setKey(key);
        ioSession.write((Object) rd);
        CloseFuture future = ioSession.closeOnFlush();
        future.addListener((IoFutureListener) new IoFutureListener() {
            public void operationComplete(final IoFuture future) {
                future.getSession().close();
            }
        });
    }

    public void sessionCreated(final IoSession session) throws Exception {
        session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 60);
    }

    public void sessionIdle(final IoSession session, final IdleStatus arg1) throws Exception {
        session.close();
    }

    public void messageSent(final IoSession session, final Object message) throws Exception {
        ResponseData rd = (ResponseData) message;
        super.messageSent(session, message);
    }
}
