// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.core.queue;

import yoyo.core.packet.AbsResponseMessage;
import yoyo.service.YOYOSystem;
import java.io.IOException;
import yoyo.tools.Convertor;
import yoyo.tools.YOYOOutputStream;
import org.slf4j.LoggerFactory;
import yoyo.core.packet.ResponseData;
import org.slf4j.Logger;

public class ResponseMessageQueue {

    private final Logger logger;
    private static ResponseMessageQueue instance;
    private int maxItem;
    private int usableQueueIndex;
    private int itemCount;
    private int packetSize;
    private int packetOffSize;
    private int maxPacketSize;
    private ResponseData rd_heart;
    private ResponseData rderror;
    private UserEvent[] userMsg;

    public static ResponseMessageQueue getInstance() {
        if (ResponseMessageQueue.instance == null) {
            ResponseMessageQueue.instance = new ResponseMessageQueue();
        }
        return ResponseMessageQueue.instance;
    }

    public static void destroy() {
        if (ResponseMessageQueue.instance != null) {
            ResponseMessageQueue.instance.clear();
            ResponseMessageQueue.instance = null;
        }
    }

    ResponseMessageQueue() {
        this.logger = LoggerFactory.getLogger((Class) this.getClass());
        this.maxItem = 2000;
        this.usableQueueIndex = 0;
        this.itemCount = 0;
        this.packetSize = 8000;
        this.packetOffSize = 500;
        this.maxPacketSize = 500;
        this.userMsg = new UserEvent[this.maxItem];
        this.rd_heart = new ResponseData(new byte[]{1, 0, 2, 8, 0});
        YOYOOutputStream output = null;
        Label_0208:
        {
            try {
                output = new YOYOOutputStream();
                output.writeByte(1);
                output.writeShort(0);
                output.writeShort(1030);
                byte[] data = output.getBytes();
                Convertor.short2Bytes((short) (data.length - 3), data, 1);
                (this.rderror = new ResponseData(data)).setErrorMessage();
            } catch (IOException e1) {
                e1.printStackTrace();
                try {
                    output.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
                break Label_0208;
            } finally {
                try {
                    output.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
            try {
                output.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        UserEvent.msgLevel = 3;
        this.clear();
        try {
            ResponseQueueConfig config = new ResponseQueueConfig(String.valueOf(YOYOSystem.HOME) + "conf/server.xml");
            this.maxItem = config.maxItem;
            UserEvent.msgLevel = config.levelNum;
            this.packetSize = config.packetSize;
            this.packetOffSize = config.packetOffSize;
            this.maxPacketSize = config.maxPacketSize;
        } catch (Exception e3) {
            e3.printStackTrace();
        }
    }

    public synchronized int createItem() {
        int size = 0;
        int msgQIndex = -1;
        if (this.usableQueueIndex >= this.maxItem) {
            this.usableQueueIndex = 0;
        }
        while (size < this.maxItem) {
            if (this.userMsg[this.usableQueueIndex++] == null) {
                msgQIndex = this.usableQueueIndex - 1;
                this.userMsg[msgQIndex] = new UserEvent();
                ++this.itemCount;
                break;
            }
            ++size;
        }
        return msgQIndex;
    }

    public synchronized void removeItem(final int index) {
        if (index >= 0 && index < this.maxItem && this.userMsg[index] != null) {
            this.userMsg[index].clear();
            this.userMsg[index] = null;
            this.usableQueueIndex = index;
            --this.itemCount;
        }
    }

    public ResponseData getErrorData() {
        return this.rderror;
    }

    public boolean put(final int index, final AbsResponseMessage event) {
        if (index < 0 || index >= this.maxItem) {
            return false;
        }
        if (this.userMsg[index] == null) {
            return false;
        }
        try {
            if (event.getSize() >= 20480) {
                this.logger.warn("\u8b66\u544a:\u5355\u4e2a\u6d88\u606f\u8d85\u8fc720k;size=" + String.valueOf(event.getSize()));
            }
            synchronized (this.userMsg[index]) {
                if (this.userMsg[index].getSize() < this.maxPacketSize) {
                    this.userMsg[index].addEvent(event.getPriority(), event.getBytes());
                    // monitorexit(this.userMsg[index])
                    return true;
                }
                this.logger.warn("\u4e25\u91cd\u8b66\u544a:\u73a9\u5bb6\u961f\u5217\u8d85\u8fc7\u6700\u5927BYTE,\u961f\u5217ID=" + String.valueOf(index) + "  size:" + String.valueOf(this.userMsg[index].getSize()));
                // monitorexit(this.userMsg[index])
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public ResponseData get(final int index) {
        if (index < 0 || index >= this.maxItem) {
            this.logger.warn("!!!to yoyo-->\u83b7\u5f97\u6d88\u606fID\u975e\u6cd5,\u901a\u77e5\u5ba2\u6237\u7aef\u65ad\u5f00 msgQueueIndex=" + index);
            return this.rderror;
        }
        if (this.userMsg[index] == null) {
            this.logger.warn("!!!to yoyo-->\u901a\u8fc7\u6d88\u606fID\u83b7\u5f97\u73a9\u5bb6\u961f\u5217\u4e3a\u7a7a,\u901a\u77e5\u5ba2\u6237\u7aef\u65ad\u5f00 msgQueueIndex=" + index);
            return this.rderror;
        }
        synchronized (this.userMsg[index]) {
            int queueNum = this.userMsg[index].checkSize(this.packetSize, this.packetOffSize);
            YOYOOutputStream output = new YOYOOutputStream();
            if (queueNum > 0) {
                Label_0228:
                {
                    try {
                        output.writeByte(queueNum);
                        for (int i = 0; i < queueNum; ++i) {
                            byte[] temp = this.userMsg[index].getEvent();
                            output.writeBytes(temp);
                        }
                        output.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                        try {
                            output.close();
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
                        break Label_0228;
                    } finally {
                        try {
                            output.close();
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                    try {
                        output.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
                // monitorexit(this.userMsg[index])
                return new ResponseData(output.getBytes());
            }
            // monitorexit(this.userMsg[index])
            return this.rd_heart;
        }
    }

    public int[] getEventNum(final int sid) {
        if (this.userMsg[sid] != null) {
            synchronized (this.userMsg[sid]) {
                // monitorexit(this.userMsg[sid])
                return this.userMsg[sid].getNum();
            }
        }
        return null;
    }

    private void clear() {
        for (int i = this.maxItem - 1; i >= 0; --i) {
            if (this.userMsg[i] != null) {
                this.userMsg[i].clear();
                this.userMsg[i] = null;
            }
        }
        this.itemCount = 0;
    }
}
