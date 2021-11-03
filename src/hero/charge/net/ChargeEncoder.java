// 
// Decompiled by Procyon v0.5.36
// 
package hero.charge.net;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;

public class ChargeEncoder extends ProtocolEncoderAdapter {

    private static final String CRLN = "\r\n";
    private static final String HTTP_HEAD = "HTTP/1.1 200 OK \r\nServer: WangZheYingXiong Digifun\r\nKeep-Alive: timeout=15, max=100\r\nConnection: Keep-Alive\r\nContent-Type: application/octet-stream\r\nContent-Length: ";

    public void encode(final IoSession _session, final Object _message, final ProtocolEncoderOutput _out) throws Exception {
        String rd = (String) _message;
        byte[] temp = rd.getBytes();
        temp = addHttpHead(temp);
        IoBuffer buffer = IoBuffer.allocate(temp.length, false);
        buffer.put(temp);
        buffer.flip();
        _out.write((Object) buffer);
    }

    private static byte[] addHttpHead(final byte[] _data) {
        try {
            byte[] httpHead = new StringBuffer().append("HTTP/1.1 200 OK \r\nServer: WangZheYingXiong Digifun\r\nKeep-Alive: timeout=15, max=100\r\nConnection: Keep-Alive\r\nContent-Type: application/octet-stream\r\nContent-Length: ").append(_data.length).append("\r\n").append("\r\n").toString().getBytes();
            byte[] returnValue = new byte[httpHead.length + _data.length];
            System.arraycopy(httpHead, 0, returnValue, 0, httpHead.length);
            System.arraycopy(_data, 0, returnValue, httpHead.length, _data.length);
            return returnValue;
        } catch (Exception e) {
            return null;
        }
    }
}
