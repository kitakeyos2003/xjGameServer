// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.service.base.network.wrap;

import yoyo.service.base.network.NetworkServiceImpl;
import yoyo.service.base.network.NetworkConfig;
import org.apache.mina.core.buffer.IoBuffer;
import yoyo.core.packet.ResponseData;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;

public class SocketEncoder extends ProtocolEncoderAdapter {

    public void encode(final IoSession session, final Object message, final ProtocolEncoderOutput output) throws Exception {
        ResponseData rd = (ResponseData) message;
        int capacity = rd.getSize() + 2;
        IoBuffer buffer = IoBuffer.allocate(capacity, false);
        buffer.putShort((short) rd.getSize());
        buffer.put(NetworkServiceImpl.getInstance().getConfig().getGameId());
        buffer.putInt(rd.getSessionId());
        buffer.put(rd.getContext());
        buffer.flip();
        output.write((Object) buffer);
    }
}
