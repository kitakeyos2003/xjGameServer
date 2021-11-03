// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.service.base.network;

import java.util.List;
import org.dom4j.Element;
import yoyo.service.base.AbsConfig;

public class NetworkConfig extends AbsConfig {

    private byte gameId;
    private int serverCount;
    ConfigInfo[] configs;

    public NetworkConfig() {
        this.gameId = 0;
        this.serverCount = 0;
    }

    public byte getGameId() {
        return this.gameId;
    }

    public int getServerCount() {
        return this.serverCount;
    }

    @Override
    public void init(final Element element) throws Exception {
        List list = element.selectNodes("//networkservice/servers/*");
        if (list.size() <= 0) {
            throw new RuntimeException("not define server");
        }
        this.configs = new ConfigInfo[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            Element eServer = (Element) list.get(i);
            Element ePort = eServer.element("port");
            if (ePort == null) {
                throw new NullPointerException("can not find <port> tag");
            }
            String sPort = ePort.getTextTrim();
            if (sPort == null || sPort.equals("")) {
                throw new RuntimeException(String.valueOf(eServer.getName()) + " not define port");
            }
            Element eHandler = eServer.element("handler");
            if (eHandler == null) {
                throw new NullPointerException("can not find <handler> tag");
            }
            String sHandler = eHandler.getTextTrim();
            if (sHandler == null || sHandler.equals("")) {
                throw new RuntimeException(String.valueOf(eServer.getName()) + " not define handler");
            }
            Element eyoyo = eServer.element("yoyoserver");
            if (eyoyo == null) {
                throw new NullPointerException("can not find <yoyoserver> tag");
            }
            String sServer = eyoyo.getTextTrim();
            if (sServer == null || sServer.equals("")) {
                throw new RuntimeException(String.valueOf(eServer.getName()) + " not define yoyoserver");
            }
            Element eDecoder = eServer.element("decoder");
            String decoder = null;
            if (eDecoder != null) {
                decoder = eDecoder.getTextTrim();
            }
            Element eEncoder = eServer.element("encoder");
            String encoder = null;
            if (eEncoder != null) {
                encoder = eEncoder.getTextTrim();
            }
            this.configs[i] = new ConfigInfo();
            this.configs[i].port = Integer.parseInt(sPort);
            this.configs[i].process = sHandler;
            this.configs[i].server = sServer;
            this.configs[i].decoder = decoder;
            this.configs[i].encoder = encoder;
        }
        this.serverCount = this.configs.length;
    }

    public class ConfigInfo {

        private int port;
        private String process;
        private String server;
        private String decoder;
        private String encoder;

        public String getDecoder() {
            return this.decoder;
        }

        public String getEncoder() {
            return this.encoder;
        }

        public String getProcess() {
            return this.process;
        }

        public int getPort() {
            return this.port;
        }

        public String getServer() {
            return this.server;
        }

        @Override
        public String toString() {
            StringBuffer buf = new StringBuffer();
            buf.append("port=" + this.port + "\n");
            buf.append("server=" + this.server + "\n");
            buf.append("process=" + this.process + "\n");
            buf.append("encoder=" + this.encoder + "\n");
            buf.append("decoder=" + this.decoder + "\n");
            return buf.toString();
        }
    }
}
