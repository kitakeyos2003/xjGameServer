// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.dict;

import hero.share.service.LogWriter;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.File;
import hero.npc.service.NotPlayerServiceImpl;
import hero.npc.service.NpcConfig;
import java.util.HashMap;

public class NpcImageDict {

    private HashMap<Short, byte[]> npcImageMap;
    private static NpcImageDict instance;

    private NpcImageDict() {
        this.npcImageMap = new HashMap<Short, byte[]>();
        this.load();
    }

    public static NpcImageDict getInstance() {
        if (NpcImageDict.instance == null) {
            NpcImageDict.instance = new NpcImageDict();
        }
        return NpcImageDict.instance;
    }

    private void load() {
        File[] imageFiles = new File(NotPlayerServiceImpl.getInstance().getConfig().NPCImagePath).listFiles();
        for (int i = 0; i < imageFiles.length; ++i) {
            short imageID = -1;
            if (imageFiles[i].getName().toLowerCase().endsWith(".png")) {
                String imageFileName = imageFiles[i].getName();
                imageID = Short.parseShort(imageFileName.substring(0, imageFileName.length() - 4));
                this.npcImageMap.put(new Short(imageID), this.getImageBytes(imageFiles[i]));
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

    public byte[] getImageBytes(final short _imageID) {
        return this.npcImageMap.get(_imageID);
    }
}
