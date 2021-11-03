// 
// Decompiled by Procyon v0.5.36
// 
package hero.ui.service;

import yoyo.service.base.AbsServiceAdaptor;

public class UIServiceImpl extends AbsServiceAdaptor<UIConfig> {

    private static UIServiceImpl instance;

    static {
        UIServiceImpl.instance = null;
    }

    private UIServiceImpl() {
        this.config = new UIConfig();
    }

    public static UIServiceImpl getInstance() {
        if (UIServiceImpl.instance == null) {
            UIServiceImpl.instance = new UIServiceImpl();
        }
        return UIServiceImpl.instance;
    }
}
