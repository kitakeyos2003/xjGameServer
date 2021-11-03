// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.service.base.network.wrap;

import org.apache.mina.core.buffer.IoBuffer;
import yoyo.service.base.network.NetworkServiceImpl;
import yoyo.service.base.network.NetworkConfig;
import yoyo.tools.Convertor;
import yoyo.core.packet.ResponseData;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;

public class HttpEncoder extends ProtocolEncoderAdapter {

    private static final int OFFSET = 4;
    private static final String NEWLINE = "\r\n";
    private static final String RESPONSE_HTTP_HEAD = "HTTP/1.0 200 OK\r\nServer: YOYO\r\nContent-Type: application/octet-stream\r\nConnection: close\r\nCache-Control: no-cache\r\nContent-Length: ";

    public void encode(final IoSession session, final Object message, final ProtocolEncoderOutput output) throws Exception {
        ResponseData rd = (ResponseData) message;
        byte[] data = new byte[rd.getSize() + 2];
        Convertor.short2Bytes((short) rd.getSize(), data, 0);
        data[2] = NetworkServiceImpl.getInstance().getConfig().getGameId();
        Convertor.int2Bytes(rd.getSessionId(), data, 3);
        System.arraycopy(rd.getContext(), 0, data, 7, rd.getContext().length);
        data = addHttpHead(data);
        if (data == null || data.length == 0) {
            session.close();
        }
        int capacity = data.length;
        IoBuffer buffer = IoBuffer.allocate(capacity, false);
        buffer.put(data);
        buffer.flip();
        output.write((Object) buffer);
    }

    private static byte[] addHttpHead(final byte[] data) {
        byte[] ret = null;
        try {
            byte[] httpHead = new StringBuffer().append("HTTP/1.0 200 OK\r\nServer: YOYO\r\nContent-Type: application/octet-stream\r\nConnection: close\r\nCache-Control: no-cache\r\nContent-Length: ").append(data.length + 4).append("\r\n").append("\r\n").toString().getBytes();
            ret = new byte[httpHead.length + data.length];
            System.arraycopy(httpHead, 0, ret, 0, httpHead.length);
            System.arraycopy(data, 0, ret, httpHead.length, data.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
}
