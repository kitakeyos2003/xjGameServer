// 
// Decompiled by Procyon v0.5.36
// 
package hero.dcnbbs;

import java.util.List;

public class Topic {

    private String forumId;
    private String postId;
    private String mid;
    private String title;
    private String content;
    private String dateTime;
    private String createdByInfo;
    private List<Topic> replyTopicList;

    public String getContent() {
        return this.content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public String getCreatedByInfo() {
        return this.createdByInfo;
    }

    public void setCreatedByInfo(final String createdByInfo) {
        this.createdByInfo = createdByInfo;
    }

    public String getDateTime() {
        return this.dateTime;
    }

    public void setDateTime(final String dateTime) {
        this.dateTime = dateTime;
    }

    public String getForumId() {
        return this.forumId;
    }

    public void setForumId(final String forumId) {
        this.forumId = forumId;
    }

    public String getMid() {
        return this.mid;
    }

    public void setMid(final String mid) {
        this.mid = mid;
    }

    public String getPostId() {
        return this.postId;
    }

    public void setPostId(final String postId) {
        this.postId = postId;
    }

    public List<Topic> getReplyTopicList() {
        return this.replyTopicList;
    }

    public void setReplyTopicList(final List<Topic> replyTopicList) {
        this.replyTopicList = replyTopicList;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }
}
