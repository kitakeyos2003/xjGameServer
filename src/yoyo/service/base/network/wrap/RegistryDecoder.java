// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.service.base.network.wrap;

import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;

public class RegistryDecoder extends CumulativeProtocolDecoder {

    protected boolean doDecode(final IoSession session, final IoBuffer in, final ProtocolDecoderOutput out) throws Exception {
        if (in.remaining() >= 4) {
            int tag = in.getInt();
            out.write((Object) tag);
            return true;
        }
        return false;
    }
}
