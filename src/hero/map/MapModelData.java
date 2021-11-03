// 
// Decompiled by Procyon v0.5.36
// 
package hero.map;

import hero.map.detail.Cartoon;
import hero.map.detail.PopMessage;
import hero.map.detail.Door;
import java.util.ArrayList;
import hero.map.detail.OtherObjectData;

public class MapModelData {

    public short id;
    public String name;
    public short tileImageID;
    public int mapTypeValue;
    public String monsterModelIDAbout;
    public int mapWeatherValue;
    public short width;
    public short height;
    public short bornX;
    public short bornY;
    public boolean modifiable;
    public byte pkMark;
    public OtherObjectData[] notPlayerObjectList;
    public OtherObjectData[] decorateObjectList;
    public byte[] bottomCanvasData;
    public byte[] transformData1;
    public byte[] transformData2;
    public byte[] resourceTransformData;
    public byte[] resourceCanvasMap;
    public short[] elementCanvasData;
    public ArrayList<Short> elementImageIDList;
    public ArrayList<Short> fixedNpcImageIDList;
    public ArrayList<Short> fixedMonsterImageIDList;
    public ArrayList<Short> groundTaskGoodsImageIDList;
    public ArrayList<Short> taskGearImageIDList;
    public byte[][] unpassMarkArray;
    public byte[] unpassData;
    public byte[][] internalPorts;
    public Door[] externalPortList;
    public PopMessage[] popMessageList;
    public Cartoon[] cartoonList;
    public Cartoon[] cartoonList2;
    public int animNum;
}
