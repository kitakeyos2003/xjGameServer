// 
// Decompiled by Procyon v0.5.36
// 
package hero.chat.service;

import java.sql.Timestamp;

public class GmNotice {

    private int id;
    private int severID;
    private String title;
    private String content;
    private Timestamp create_time;
    private Timestamp update_time;
    private Timestamp startTime;
    private Timestamp endTime;
    private int intervalTime;
    private int times;

    public int getSeverID() {
        return this.severID;
    }

    public void setSeverID(final int severID) {
        this.severID = severID;
    }

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public Timestamp getCreate_time() {
        return this.create_time;
    }

    public void setCreate_time(final Timestamp createTime) {
        this.create_time = createTime;
    }

    public Timestamp getUpdate_time() {
        return this.update_time;
    }

    public void setUpdate_time(final Timestamp updateTime) {
        this.update_time = updateTime;
    }

    public Timestamp getStartTime() {
        return this.startTime;
    }

    public void setStartTime(final Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return this.endTime;
    }

    public void setEndTime(final Timestamp endTime) {
        this.endTime = endTime;
    }

    public int getIntervalTime() {
        return this.intervalTime;
    }

    public void setIntervalTime(final int intervalTime) {
        this.intervalTime = intervalTime;
    }

    public int getTimes() {
        return this.times;
    }

    public void setTimes(final int times) {
        this.times = times;
    }
}
