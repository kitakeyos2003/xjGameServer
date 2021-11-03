// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.message;

import java.io.IOException;
import java.util.Iterator;
import hero.share.Inotice;
import java.util.List;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseIndexNoticeList extends AbsResponseMessage {

    private List<Inotice> inoticeList;

    public ResponseIndexNoticeList(final List<Inotice> inoticeList) {
        this.inoticeList = inoticeList;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.inoticeList.size());
        for (final Inotice inotice : this.inoticeList) {
            this.yos.writeInt(inotice.id);
            String title = inotice.title;
            this.yos.writeUTF(title);
            this.yos.writeInt(inotice.color);
        }
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
