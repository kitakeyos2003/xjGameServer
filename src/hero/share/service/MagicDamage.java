// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.service;

import java.util.Random;
import hero.share.EMagic;

public class MagicDamage {

    public EMagic magic;
    public int minDamageValue;
    public int maxDamageValue;
    private static final Random random;

    static {
        random = new Random();
    }

    public int getRandomDamageValue() {
        return this.minDamageValue + MagicDamage.random.nextInt(this.maxDamageValue - this.minDamageValue + 1);
    }
}
