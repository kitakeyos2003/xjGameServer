// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.core.packet;

import java.io.IOException;
import yoyo.tools.Convertor;
import yoyo.service.ServiceManager;
import yoyo.tools.YOYOOutputStream;

public abstract class AbsResponseMessage {

    protected YOYOOutputStream yos;
    private boolean isCached;

    public AbsResponseMessage() {
        this.yos = new YOYOOutputStream();
    }

    public int getSize() {
        return this.yos.size();
    }

    public int getID() throws Exception {
        int id = ServiceManager.getInstance().getMsgIdByName(this.getName());
        return id;
    }

    protected String getName() {
        return this.getClass().getName();
    }

    public byte[] getBytes() {
        try {
            if (!this.isCached) {
                this.flush();
            }
            byte[] data = this.yos.getBytes();
            Convertor.short2Bytes((short) (data.length - 2), data, 0);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                this.yos.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    public void flush() {
        try {
            this.yos.writeShort(0);
            this.yos.writeShort(this.getID());
            this.write();
            this.yos.flush();
            this.isCached = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract void write() throws IOException;

    public abstract int getPriority();
}
