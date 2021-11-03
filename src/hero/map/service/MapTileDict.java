// 
// Decompiled by Procyon v0.5.36
// 
package hero.map.service;

import hero.share.service.DataConvertor;
import java.io.FileInputStream;
import hero.share.service.LogWriter;
import java.io.File;
import java.util.HashMap;

public class MapTileDict {

    private HashMap<Integer, char[]> dictionary;
    private static MapTileDict instance;

    public static MapTileDict getInstance() {
        if (MapTileDict.instance == null) {
            MapTileDict.instance = new MapTileDict();
        }
        return MapTileDict.instance;
    }

    private MapTileDict() {
        this.dictionary = new HashMap<Integer, char[]>();
    }

    public void init(final String _tilePath) {
        File filePath;
        try {
            filePath = new File(_tilePath);
        } catch (Exception e) {
            LogWriter.println("\u672a\u627e\u5230\u6307\u5b9a\u7684\u76ee\u5f55\uff1a" + _tilePath);
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
                    this.dictionary.put(imageID, this.getTileChars(fileList[i]));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private char[] getTileChars(final File _tileFile) throws Exception {
        char[] rtnValue = null;
        try {
            int tilesFileSize = (int) _tileFile.length();
            if (1 == tilesFileSize % 2) {
                ++tilesFileSize;
            }
            byte[] content = new byte[tilesFileSize];
            rtnValue = new char[tilesFileSize / 2];
            FileInputStream fis = new FileInputStream(_tileFile);
            fis.read(content, 0, (int) _tileFile.length());
            DataConvertor.bytes2Chars((short) content.length, content, rtnValue);
            fis.close();
        } catch (Exception e) {
            LogWriter.error(null, e);
        }
        return rtnValue;
    }

    public char[] getMapTileChars(final int _mapTileID) {
        return this.dictionary.get(_mapTileID);
    }
}
