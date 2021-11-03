// 
// Decompiled by Procyon v0.5.36
// 
package hero.manufacture.dict;

import hero.manufacture.Odd;
import java.util.Comparator;

class OddCompare implements Comparator {

    @Override
    public int compare(final Object o1, final Object o2) {
        Odd odd1 = (Odd) o1;
        Odd odd2 = (Odd) o2;
        if (odd1.odd > odd2.odd) {
            return 1;
        }
        if (odd1.odd == odd2.odd) {
            return 0;
        }
        return -1;
    }
}
