// 
// Decompiled by Procyon v0.5.36
// 
package hero.charge.net.handler;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import hero.charge.net.parse.detail.ChargeListRefreshParse;
import hero.charge.net.parse.detail.RechargeFeedbackParse;
import hero.log.service.LogServiceImpl;
import java.util.HashMap;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.service.IoHandlerAdapter;

public class ChargeHttpHandler extends IoHandlerAdapter {

    public void exceptionCaught(final IoSession _session, final Throwable ex) throws Exception {
        _session.close();
    }

    public void messageReceived(final IoSession _ioSession, final Object _message) throws Exception {
        try {
            HashMap<String, String> param = (HashMap<String, String>) _message;
            String reqType = param.get("REQ_TYPE");
            String resp = "";
            if (reqType == null || reqType.equals("")) {
                LogServiceImpl.getInstance().chargeLog("\u7a7a\u7684handleType!");
            } else if (reqType.toLowerCase().equals("shenyu")) {
                String typeValue = param.get("type");
                if (typeValue != null && !typeValue.isEmpty()) {
                    if (typeValue.equals("1")) {
                        LogServiceImpl.getInstance().chargeLog("\u5145\u503c\u7ed3\u679c\u56de\u8c03");
                        new RechargeFeedbackParse(param);
                    } else if (typeValue.equals("2")) {
                        String result = param.get("result");
                        if (!result.isEmpty()) {
                            LogServiceImpl.getInstance().chargeLog("\u8ba1\u8d39\u5217\u8868\u5237\u65b0\u56de\u8c03");
                            new ChargeListRefreshParse(result);
                        }
                    }
                    resp = "OK";
                }
            } else {
                resp = "FAIL";
            }
            _ioSession.write((Object) resp);
            CloseFuture future = _ioSession.closeOnFlush();
            future.addListener((IoFutureListener) new IoFutureListener() {
                public void operationComplete(final IoFuture _future) {
                    _future.getSession().close();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void sessionCreated(final IoSession session) throws Exception {
        session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 60);
    }

    public void sessionIdle(final IoSession session, final IdleStatus arg1) throws Exception {
        session.close();
    }
}
