// 
// Decompiled by Procyon v0.5.36
// 
package hero.dcnbbs.message;

import java.io.IOException;
import java.util.Iterator;
import java.util.ArrayList;
import hero.dcnbbs.Topic;
import java.util.List;
import yoyo.core.packet.AbsResponseMessage;

public class TopicListResult extends AbsResponseMessage {

    private List<Topic> topicList;
    private short pageno;

    public TopicListResult(final List<Topic> topicList, final short pageno) {
        this.topicList = new ArrayList<Topic>();
        this.pageno = 1;
        this.topicList = topicList;
        this.pageno = pageno;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeShort(this.pageno);
        this.yos.writeByte(this.topicList.size());
        for (final Topic topic : this.topicList) {
            this.yos.writeUTF(topic.getPostId());
            this.yos.writeUTF(topic.getCreatedByInfo());
            this.yos.writeUTF(topic.getTitle());
            this.yos.writeUTF(topic.getDateTime());
        }
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
