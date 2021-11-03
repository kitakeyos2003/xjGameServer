// 
// Decompiled by Procyon v0.5.36
// 
package hero.charge.net;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolCodecFactory;

public class ChargeCodeFactory implements ProtocolCodecFactory {

    private ProtocolEncoder encoder;
    private ProtocolDecoder decoder;

    public ChargeCodeFactory() throws Exception {
        this.decoder = (ProtocolDecoder) new ChargeDecoder();
        this.encoder = (ProtocolEncoder) new ChargeEncoder();
    }

    public ProtocolDecoder getDecoder(final IoSession session) throws Exception {
        return this.decoder;
    }

    public ProtocolEncoder getEncoder(final IoSession session) throws Exception {
        return this.encoder;
    }
}
