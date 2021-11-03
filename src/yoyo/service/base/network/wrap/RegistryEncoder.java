// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.service.base.network.wrap;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;

public class RegistryEncoder extends ProtocolEncoderAdapter {

    public void encode(final IoSession session, final Object message, final ProtocolEncoderOutput output) throws Exception {
        int id = (int) message;
        IoBuffer buffer = IoBuffer.allocate(4, false);
        buffer.putInt(id);
        buffer.flip();
        output.write((Object) buffer);
    }
}
