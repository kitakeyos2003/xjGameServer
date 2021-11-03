// 
// Decompiled by Procyon v0.5.36
// 
package hero.charge.message;

import java.io.IOException;
import yoyo.core.packet.AbsResponseMessage;

public class ExperienceBookTraceTime extends AbsResponseMessage {

    private long traceTimeOfOffline;
    private long traceExperienceBookTime;
    private long traceHuntExperienceBookTime;

    public ExperienceBookTraceTime(final long _traceTimeOfOffline, final long _traceExperienceBookTime, final long _traceHuntExperienceBookTime) {
        this.traceTimeOfOffline = _traceTimeOfOffline;
        this.traceExperienceBookTime = _traceExperienceBookTime;
        this.traceHuntExperienceBookTime = _traceHuntExperienceBookTime;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeLong(this.traceTimeOfOffline);
        this.yos.writeLong(this.traceExperienceBookTime);
        this.yos.writeLong(this.traceHuntExperienceBookTime);
    }
}
