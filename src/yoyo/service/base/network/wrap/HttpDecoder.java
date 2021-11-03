// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.service.base.network.wrap;

import yoyo.core.packet.ContextData;
import yoyo.tools.Convertor;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;

public class HttpDecoder extends CumulativeProtocolDecoder {

    private static final CharsetDecoder decoder;
    private static String START;
    private static String End;

    static {
        decoder = Charset.forName("iso8859-1").newDecoder();
        HttpDecoder.START = "datastart:";
        HttpDecoder.End = "dataend";
    }

    protected boolean doDecode(final IoSession session, final IoBuffer buffer, final ProtocolDecoderOutput output) throws Exception {
        String request = buffer.getString(HttpDecoder.decoder);
        int dataBegin = request.indexOf(HttpDecoder.START);
        int dataEnd = request.indexOf(HttpDecoder.End);
        if (dataBegin != -1 && dataEnd != -1) {
            request = request.substring(dataBegin + HttpDecoder.START.length(), dataEnd).trim();
            if (request != null) {
                byte[] data = Decoder.decode(request.getBytes("ISO8859-1"));
                if (data.length >= 12) {
                    short packageLen = Convertor.bytes2Short(data, 0);
                    byte gameId = data[2];
                    int id = Convertor.bytes2Int(data, 3);
                    byte mesgNum = data[7];
                    ContextData[] cds = new ContextData[mesgNum];
                    int pos = 8;
                    for (int i = 0; i < mesgNum; ++i) {
                        short msgLen = Convertor.bytes2Short(data, pos);
                        short msgId = Convertor.bytes2Short(data, pos + 2);
                        byte[] body = new byte[msgLen - 2];
                        System.arraycopy(data, pos + 4, body, 0, body.length);
                        cds[i] = new ContextData((byte) 2, id, gameId, msgId, body);
                        pos += msgLen + 2;
                    }
                    output.write((Object) cds);
                    return true;
                }
            }
        }
        return false;
    }
}
