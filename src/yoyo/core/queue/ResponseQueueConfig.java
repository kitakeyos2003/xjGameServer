// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.core.queue;

import org.dom4j.Document;
import org.apache.commons.lang.StringUtils;
import java.io.File;
import org.dom4j.io.SAXReader;

public class ResponseQueueConfig {

    int maxItem;
    byte levelNum;
    int packetSize;
    int packetOffSize;
    int maxPacketSize;

    public ResponseQueueConfig(final String _xml) throws Exception {
        this.maxItem = 2000;
        this.levelNum = 3;
        this.packetSize = 90000;
        this.packetOffSize = 50;
        this.maxPacketSize = 500;
        SAXReader reader = new SAXReader();
        Document document = reader.read(new File(_xml));
        String sMaxItem = document.valueOf("//ResponseMessageQueue/maxItem");
        if (!StringUtils.isBlank(sMaxItem)) {
            this.maxItem = Integer.parseInt(sMaxItem);
        }
        String sLevelNum = document.valueOf("//ResponseMessageQueue/levelNum");
        if (!StringUtils.isBlank(sLevelNum)) {
            this.levelNum = Byte.parseByte(sLevelNum);
        }
        String sPacketSize = document.valueOf("//ResponseMessageQueue/packetSize");
        if (!StringUtils.isBlank(sPacketSize)) {
            this.packetSize = Integer.parseInt(sPacketSize);
        }
        String sPacketOffSize = document.valueOf("//ResponseMessageQueue/packetOffSize");
        if (!StringUtils.isBlank(sPacketOffSize)) {
            this.packetOffSize = Integer.parseInt(sPacketOffSize);
        }
        String sMaxPacketSize = document.valueOf("//ResponseMessageQueue/maxPacketSize");
        if (!StringUtils.isBlank(sMaxPacketSize)) {
            this.maxPacketSize = Integer.parseInt(sMaxPacketSize);
        }
    }
}
