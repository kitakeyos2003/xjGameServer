// 
// Decompiled by Procyon v0.5.36
// 
package hero.share;

import java.util.Iterator;
import java.util.List;

public class RankMenuField {

    public byte id;
    public String name;
    public byte menuLevel;
    public String vocation;
    public List<String> fieldList;
    public List<RankMenuField> childMenuList;

    public RankMenuField getChildRankMenuFieldByID(final byte id) {
        for (final RankMenuField rmf : this.childMenuList) {
            if (rmf.id == id) {
                return rmf;
            }
        }
        return null;
    }
}
