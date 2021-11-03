// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.service;

import hero.share.ME2GameObject;

public class GameObjectChecker {

    private GameObjectChecker() {
    }

    public static boolean checkValidity(final ME2GameObject _object) {
        return _object != null && _object.isEnable();
    }

    public static boolean checkValidity(final ME2GameObject _object, final boolean _isDead) {
        return _object != null && _object.isEnable() && _isDead == _object.isDead();
    }

    public static boolean checkActiveValidity(final ME2GameObject _object) {
        return _object != null && _object.isEnable() && !_object.isDead();
    }

    public static boolean checkActiveValidity(final ME2GameObject _one, final ME2GameObject _another) {
        return _one != null && _one.isEnable() && !_one.isDead() && _another != null && _another.isEnable() && !_another.isDead();
    }
}
