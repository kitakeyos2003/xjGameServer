// 
// Decompiled by Procyon v0.5.36
// 
package hero.lover.service;

import java.util.TimerTask;

public class LoverTask extends TimerTask {

    @Override
    public void run() {
        LoverDAO.deleteTimeOut();
        LoverServiceImpl.getInstance().getTimer().schedule(new LoverTask(), LoverServiceImpl.getInstance().getTomorrow());
    }
}
