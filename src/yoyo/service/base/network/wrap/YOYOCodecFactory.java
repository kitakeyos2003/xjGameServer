// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.service.base.network.wrap;

import yoyo.service.base.network.NetworkConfig;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolCodecFactory;

public class YOYOCodecFactory implements ProtocolCodecFactory {

    private ProtocolEncoder encoder;
    private ProtocolDecoder decoder;

    public ProtocolDecoder getDecoder(final IoSession session) throws Exception {
        return this.decoder;
    }

    public ProtocolEncoder getEncoder(final IoSession session) throws Exception {
        return this.encoder;
    }

    public YOYOCodecFactory(final NetworkConfig.ConfigInfo config) throws Exception {
        this.decoder = (ProtocolDecoder) Class.forName(config.getDecoder()).newInstance();
        this.encoder = (ProtocolEncoder) Class.forName(config.getEncoder()).newInstance();
    }
}
