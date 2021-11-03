// 
// Decompiled by Procyon v0.5.36
// 
package hero.map;

import javolution.util.FastList;
import javolution.util.FastMap;

public class Area {

    private int id;
    private String name;
    private byte[] imageBytes;
    private FastMap<Map, int[]> innerVisibleMapTable;
    private FastList<Map> innerVisibleList;
    private FastList<Map> innerUnvisibleList;

    public Area(final int _id, final String _name, final byte[] _imageBytes) {
        this.id = _id;
        this.name = _name;
        this.imageBytes = _imageBytes;
        this.innerVisibleMapTable = (FastMap<Map, int[]>) new FastMap();
        this.innerVisibleList = (FastList<Map>) new FastList();
        this.innerUnvisibleList = (FastList<Map>) new FastList();
    }

    public void add(final Map _map, final boolean _visible, final int _locationX, final int _locationY) {
        if (_visible) {
            int insertIndex;
            for (insertIndex = 0; insertIndex < this.innerVisibleList.size(); ++insertIndex) {
                int[] location = (int[]) this.innerVisibleMapTable.get(this.innerVisibleList.get(insertIndex));
                if (_locationY < location[1]) {
                    break;
                }
                if (_locationY == location[1] && _locationX < location[0]) {
                    break;
                }
            }
            this.innerVisibleList.add(insertIndex, _map);
            this.innerVisibleMapTable.put(_map, new int[]{_locationX, _locationY});
        } else {
            this.innerUnvisibleList.add(_map);
        }
        _map.setArea(this);
    }

    public FastMap<Map, int[]> getVisibleMapTable() {
        return this.innerVisibleMapTable;
    }

    public FastList<Map> getVisibleMapList() {
        return this.innerVisibleList;
    }

    public int getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public byte[] getImageBytes() {
        return this.imageBytes;
    }
}
