// 
// Decompiled by Procyon v0.5.36
// 
package hero.effect.service;

import hero.share.ME2GameObject;

public class ObjectCheckor {

    public static boolean isValidate(final ME2GameObject _one, final ME2GameObject _other) {
        return _other != null && _other.isEnable() && !_other.isDead() && _one != null && _one.isEnable() && !_one.isDead();
    }

    public static boolean isValidate(final ME2GameObject _object) {
        return _object != null && _object.isEnable() && !_object.isDead();
    }
}
