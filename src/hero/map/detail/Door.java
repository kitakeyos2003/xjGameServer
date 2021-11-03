// 
// Decompiled by Procyon v0.5.36
// 
package hero.map.detail;

public class Door implements Cloneable {

    public boolean visible;
    public short x;
    public short y;
    public byte direction;
    public short targetMapID;
    public String targetMapName;
    public short targetMapX;
    public short targetMapY;
    public String monsterIDAbout;

    public Door clone() throws CloneNotSupportedException {
        return (Door) super.clone();
    }
}
