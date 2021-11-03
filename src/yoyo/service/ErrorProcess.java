// 
// Decompiled by Procyon v0.5.36
// 
package yoyo.service;

import yoyo.core.process.AbsClientProcess;

public class ErrorProcess extends AbsClientProcess {

    private String context;

    public ErrorProcess(final String context) {
        this.context = context;
    }

    @Override
    public void read() throws Exception {
    }
}
