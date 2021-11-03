// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.service.base.network.fuwu;

import yoyo.service.base.session.SessionServiceImpl;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.service.IoHandlerAdapter;

public class RegistryIoHandler extends IoHandlerAdapter {

    private static int id;

    static {
        RegistryIoHandler.id = 1;
    }

    public int getID() {
        return RegistryIoHandler.id++;
    }

    public void messageReceived(final IoSession session, final Object message) throws Exception {
        int id = (int) message;
        if (id == 2008) {
            int sid = SessionServiceImpl.getInstance().createSession(this.getID(), 2);
            session.write((Object) sid);
        }
        session.close();
    }

    public void exceptionCaught(final IoSession session, final Throwable e) throws Exception {
        e.printStackTrace();
        session.close();
    }

    public void sessionClosed(final IoSession session) throws Exception {
    }
}
