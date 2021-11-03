// 
// Decompiled by Procyon v0.5.36
// 
package hero.map.message;

import java.io.IOException;
import java.util.Iterator;
import java.util.ArrayList;
import hero.npc.others.Animal;
import hero.map.Map;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseAnimalInfoList extends AbsResponseMessage {

    private Map map;

    public ResponseAnimalInfoList(final Map _map) {
        this.map = _map;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        ArrayList<Animal> animalList = this.map.getAnimalList();
        this.yos.writeByte(animalList.size());
        if (animalList.size() > 0) {
            for (final Animal animal : animalList) {
                this.yos.writeInt(animal.getID());
                this.yos.writeByte(animal.getCellX());
                this.yos.writeByte(animal.getCellY());
                this.yos.writeShort(animal.getImageID());
                this.yos.writeShort(animal.getAnimationID());
            }
        }
    }
}
