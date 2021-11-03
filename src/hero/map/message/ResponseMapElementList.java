// 
// Decompiled by Procyon v0.5.36
// 
package hero.map.message;

import java.io.IOException;
import java.util.Iterator;
import java.util.ArrayList;
import hero.npc.others.GroundTaskGoods;
import hero.npc.others.TaskGear;
import hero.npc.dict.NpcImageConfDict;
import hero.npc.dict.NpcImageDict;
import hero.npc.others.DoorPlate;
import hero.npc.others.RoadInstructPlate;
import hero.map.Map;
import org.apache.log4j.Logger;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseMapElementList extends AbsResponseMessage {

    private static Logger log;
    private Map map;
    private short clientType;

    static {
        ResponseMapElementList.log = Logger.getLogger((Class) ResponseMapElementList.class);
    }

    public ResponseMapElementList(final short _clientType, final Map _map) {
        this.clientType = _clientType;
        this.map = _map;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        if (this.map.getRoadPlateList() != null) {
            ArrayList<RoadInstructPlate> roadPlateList = this.map.getRoadPlateList();
            this.yos.writeByte(roadPlateList.size());
            if (roadPlateList.size() > 0) {
                for (final RoadInstructPlate roadPlate : roadPlateList) {
                    this.yos.writeInt(roadPlate.getID());
                    this.yos.writeByte(roadPlate.getCellX());
                    this.yos.writeByte(roadPlate.getCellY());
                    this.yos.writeUTF(roadPlate.getContent());
                }
            }
        } else {
            this.yos.writeByte(0);
        }
        if (this.map.getDoorPlateList() != null) {
            ArrayList<DoorPlate> doorPlateList = this.map.getDoorPlateList();
            this.yos.writeByte(doorPlateList.size());
            if (doorPlateList.size() > 0) {
                for (final DoorPlate doorPlate : doorPlateList) {
                    this.yos.writeInt(doorPlate.getID());
                    this.yos.writeByte(doorPlate.getCellX());
                    this.yos.writeByte(doorPlate.getCellY());
                    this.yos.writeUTF(doorPlate.getTip());
                }
            }
        } else {
            this.yos.writeByte(0);
        }
        if (this.map.taskGearImageIDList != null) {
            this.yos.writeByte(this.map.taskGearImageIDList.size());
            ResponseMapElementList.log.info((Object) ("map.taskGearImageIDList.size()---->" + this.map.taskGearImageIDList.size()));
            for (final short imageID : this.map.taskGearImageIDList) {
                byte[] imageBytes = NpcImageDict.getInstance().getImageBytes(imageID);
                NpcImageConfDict.Config npcConfig = NpcImageConfDict.get(imageID);
                this.yos.writeShort(imageID);
                this.yos.writeShort(npcConfig.animationID);
                this.yos.writeByte(npcConfig.npcGrid);
                this.yos.writeShort(npcConfig.npcHeight);
                this.yos.writeByte(npcConfig.shadowSize);
                if (3 != this.clientType) {
                    this.yos.writeShort(imageBytes.length);
                    this.yos.writeBytes(imageBytes);
                }
            }
            this.yos.writeByte(this.map.getTaskGearList().size());
            for (final TaskGear gear : this.map.getTaskGearList()) {
                this.yos.writeInt(gear.getID());
                this.yos.writeUTF(gear.getName());
                this.yos.writeUTF(gear.getDesc());
                this.yos.writeUTF(gear.getOptionDesc());
                this.yos.writeByte(gear.getCellX());
                this.yos.writeByte(gear.getCellY());
                this.yos.writeShort(gear.getImageID());
            }
        } else {
            this.yos.writeByte(0);
        }
        if (this.map.groundTaskGoodsImageIDList != null) {
            this.yos.writeByte(this.map.groundTaskGoodsImageIDList.size());
            for (final short imageID : this.map.groundTaskGoodsImageIDList) {
                byte[] imageBytes = NpcImageDict.getInstance().getImageBytes(imageID);
                NpcImageConfDict.Config npcConfig = NpcImageConfDict.get(imageID);
                this.yos.writeShort(imageID);
                this.yos.writeShort(npcConfig.animationID);
                this.yos.writeByte(npcConfig.npcGrid);
                this.yos.writeShort(npcConfig.npcHeight);
                this.yos.writeByte(npcConfig.shadowSize);
                if (3 != this.clientType) {
                    this.yos.writeShort(imageBytes.length);
                    this.yos.writeBytes(imageBytes);
                }
            }
            this.yos.writeByte(this.map.getGroundTaskGoodsList().size());
            for (final GroundTaskGoods taskGoods : this.map.getGroundTaskGoodsList()) {
                this.yos.writeInt(taskGoods.getID());
                this.yos.writeUTF(taskGoods.getName());
                this.yos.writeByte(taskGoods.getCellX());
                this.yos.writeByte(taskGoods.getCellY());
                this.yos.writeShort(taskGoods.getImageID());
            }
        } else {
            this.yos.writeByte(0);
        }
    }
}
