// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.dict;

import hero.share.service.LogWriter;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.HashMap;

public class AnimalImageDict {

    private HashMap<Short, byte[]> animalImageMap;
    private static AnimalImageDict instance;

    private AnimalImageDict() {
        this.animalImageMap = new HashMap<Short, byte[]>();
    }

    public static AnimalImageDict getInstance() {
        if (AnimalImageDict.instance == null) {
            AnimalImageDict.instance = new AnimalImageDict();
        }
        return AnimalImageDict.instance;
    }

    public void load(final String _imagePath) {
        File[] imageFiles = new File(_imagePath).listFiles();
        for (int i = 0; i < imageFiles.length; ++i) {
            short imageID = -1;
            if (imageFiles[i].getName().toLowerCase().endsWith(".png")) {
                String imageFileName = imageFiles[i].getName();
                imageID = Short.parseShort(imageFileName.substring(0, imageFileName.length() - 4));
                this.animalImageMap.put(imageID, this.getImageBytes(imageFiles[i]));
            }
        }
        imageFiles = null;
    }

    private byte[] getImageBytes(final File _imageFile) {
        byte[] rtnValue = null;
        try {
            DataInputStream dis = null;
            dis = new DataInputStream(new FileInputStream(_imageFile));
            int imgFileByteSize = dis.available();
            rtnValue = new byte[imgFileByteSize];
            int pos = 0;
            while ((pos = dis.read(rtnValue, pos, imgFileByteSize - pos)) != -1) {
            }
            dis.close();
        } catch (Exception e) {
            LogWriter.error(null, e);
        }
        return rtnValue;
    }

    public byte[] getAnimalImageBytes(final short _imageID) {
        return this.animalImageMap.get(_imageID);
    }
}
