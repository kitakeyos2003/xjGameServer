// 
// Decompiled by Procyon v0.5.36
// 
package hero.map.service;

import java.io.InputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import hero.share.service.LogWriter;
import java.io.File;
import java.util.HashMap;

public class MiniMapImageDict {

    private HashMap<Integer, byte[]> dictionary;
    private static MiniMapImageDict instance;

    private MiniMapImageDict() {
        this.dictionary = new HashMap<Integer, byte[]>();
    }

    public static MiniMapImageDict getInstance() {
        if (MiniMapImageDict.instance == null) {
            MiniMapImageDict.instance = new MiniMapImageDict();
        }
        return MiniMapImageDict.instance;
    }

    protected void init(final String _imagePath) {
        File filePath;
        try {
            filePath = new File(_imagePath);
        } catch (Exception e) {
            LogWriter.println("\u672a\u627e\u5230\u6307\u5b9a\u7684\u76ee\u5f55\uff1a" + _imagePath);
            return;
        }
        try {
            File[] fileList = filePath.listFiles();
            if (fileList.length > 0) {
                this.dictionary.clear();
            }
            for (int i = 0; i < fileList.length; ++i) {
                int imageID = -1;
                if (fileList[i].getName().toLowerCase().endsWith(".png")) {
                    String imageFileName = fileList[i].getName();
                    imageID = Integer.parseInt(imageFileName.substring(0, imageFileName.length() - 4));
                    this.dictionary.put(imageID, this.getImageBytes(fileList[i]));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] getImageBytes(final File _imageFile) {
        byte[] rtnValue = null;
        try {
            DataInputStream dis = new DataInputStream(new FileInputStream(_imageFile));
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

    public byte[] getImageBytes(final int _imageID) {
        return this.dictionary.get(_imageID);
    }
}
