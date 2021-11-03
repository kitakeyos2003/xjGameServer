// 
// Decompiled by Procyon v0.5.36
// 
package hero.dcnbbs.message;

import java.io.IOException;
import java.util.Iterator;
import hero.dcnbbs.Topic;
import yoyo.core.packet.AbsResponseMessage;

public class TopicResult extends AbsResponseMessage {

    private Topic topic;

    public TopicResult(final Topic topic) {
        this.topic = new Topic();
        this.topic = topic;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeUTF(this.topic.getPostId());
        this.yos.writeUTF(this.topic.getCreatedByInfo());
        this.yos.writeUTF(this.topic.getTitle());
        this.yos.writeUTF(this.topic.getDateTime());
        this.yos.writeUTF((this.topic.getContent() != null) ? this.topic.getContent().replaceAll("\\[url=.*?\\].*?\\[/url\\]", "") : this.topic.getContent());
        this.yos.writeShort(this.topic.getReplyTopicList().size());
        for (final Topic topic2 : this.topic.getReplyTopicList()) {
            this.yos.writeUTF(topic2.getCreatedByInfo());
            this.yos.writeUTF(topic2.getContent());
            this.yos.writeUTF(topic2.getDateTime());
        }
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
