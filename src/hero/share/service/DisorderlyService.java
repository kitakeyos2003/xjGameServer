// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.service;

import hero.item.service.WeaponRankManager;

public class DisorderlyService {

    private static DisorderlyService instance;

    private DisorderlyService() {
    }

    public static DisorderlyService getInstance() {
        if (DisorderlyService.instance == null) {
            DisorderlyService.instance = new DisorderlyService();
        }
        return DisorderlyService.instance;
    }

    public void start() {
        WeaponRankManager.getInstance();
    }
}
