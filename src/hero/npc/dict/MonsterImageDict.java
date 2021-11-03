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

public class MonsterImageDict {

    private HashMap<Short, byte[]> imageDictionary;
    private static MonsterImageDict instance;

    private MonsterImageDict() {
        this.imageDictionary = new HashMap<Short, byte[]>();
    }

    public static MonsterImageDict getInstance() {
        if (MonsterImageDict.instance == null) {
            MonsterImageDict.instance = new MonsterImageDict();
        }
        return MonsterImageDict.instance;
    }

    public void load(final String _imageHPath, final String _imageLPath) {
        this.load(_imageHPath);
    }

    private void load(final String _imagePath) {
        File[] imageFiles = new File(_imagePath).listFiles();
        for (int i = 0; i < imageFiles.length; ++i) {
            short imageID = -1;
            if (imageFiles[i].getName().toLowerCase().endsWith(".png")) {
                String imageFileName = imageFiles[i].getName();
                imageID = Short.parseShort(imageFileName.substring(0, imageFileName.length() - 4));
                this.imageDictionary.put(imageID, this.getImageBytes(imageFiles[i]));
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

    public byte[] getMonsterImageBytes(final short _imageID) {
        return this.imageDictionary.get(_imageID);
    }
}
