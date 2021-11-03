// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.service.base.network.wrap;

import yoyo.core.packet.ContextData;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;

public class SocketDecoder extends CumulativeProtocolDecoder {

    private final Logger logger;
    private static final int HEAD_LENGTH = 2;

    public SocketDecoder() {
        this.logger = LoggerFactory.getLogger((Class) this.getClass());
    }

    protected boolean doDecode(final IoSession session, final IoBuffer input, final ProtocolDecoderOutput output) throws Exception {
        if (input.prefixedDataAvailable(2)) {
            try {
                short packageLen = input.getShort();
                if (input.remaining() < packageLen) {
                    return false;
                }
                byte gameId = input.get();
                int id = input.getInt();
                byte msgcount = input.get();
                ContextData[] cds = new ContextData[msgcount];
                for (int i = 0; i < msgcount; ++i) {
                    short msgLen = input.getShort();
                    short msgId = input.getShort();
                    byte[] body = new byte[msgLen - 2];
                    input.get(body);
                    cds[i] = new ContextData((byte) 1, id, gameId, msgId, body);
                }
                output.write((Object) cds);
            } catch (Exception e) {
                this.logger.error("decode\u51fa\u9519", (Throwable) e);
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
}
