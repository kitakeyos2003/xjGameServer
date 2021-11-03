// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.dict;

import jxl.Sheet;
import java.io.InputStream;
import hero.share.service.LogWriter;
import jxl.Workbook;
import java.io.FileInputStream;
import java.io.File;
import hero.npc.service.NotPlayerServiceImpl;
import java.util.HashMap;
import org.apache.log4j.Logger;

public class NpcImageConfDict {

    private static Logger log;
    private static final String CONFIG_FILE_NAME = "NpcPictConf.xls";
    private static HashMap<Short, Config> configMap;
    private static boolean hasInited;

    static {
        NpcImageConfDict.log = Logger.getLogger((Class) NpcImageConfDict.class);
        NpcImageConfDict.configMap = new HashMap<Short, Config>();
    }

    public static void init() {
        if (!NpcImageConfDict.hasInited) {
            load();
            NpcImageConfDict.hasInited = true;
        }
    }

    private static void load() {
        Workbook rwb = null;
        InputStream is = null;
        try {
            File file = new File(String.valueOf(NotPlayerServiceImpl.getInstance().getConfig().NPCImageCfgPath) + File.separator + "NpcPictConf.xls");
            if (file.exists()) {
                is = new FileInputStream(file);
                rwb = Workbook.getWorkbook(is);
                Sheet rs = rwb.getSheet(0);
                int packetNums = rs.getRows();
                if (packetNums > 1) {
                    int rowIndex = 1;
                    try {
                        short ID = 0;
                        short shadowY = 0;
                        short height = 0;
                        short animation = 0;
                        byte headExcursionX = 0;
                        byte headExcursionY = 0;
                        byte shadowType = 0;
                        byte shadowX = 0;
                        byte grid = 0;
                        byte shadowSize = 0;
                        while (rowIndex < packetNums) {
                            ID = Short.parseShort(rs.getCell(0, rowIndex).getContents().trim());
                            grid = Byte.parseByte(rs.getCell(1, rowIndex).getContents().trim());
                            height = Short.parseShort(rs.getCell(2, rowIndex).getContents().trim());
                            animation = Short.parseShort(rs.getCell(8, rowIndex).getContents().trim());
                            shadowSize = Byte.parseByte(rs.getCell(9, rowIndex).getContents().trim());
                            NpcImageConfDict.configMap.put(ID, new Config(ID, headExcursionX, headExcursionY, shadowType, shadowX, shadowY, height, grid, animation, shadowSize));
                            ++rowIndex;
                        }
                    } catch (Exception e) {
                        NpcImageConfDict.log.info(("\u8b66\u544a:NpcPictConf.xls->\u7b2c" + (rowIndex + 1) + "\u884c\u6570\u636e\u4e0d\u89c4\u8303"));
                        e.printStackTrace();
                    }
                } else {
                    NpcImageConfDict.log.info("NPC\u56fe\u7247\u914d\u7f6e\u6587\u4ef6\u6ca1\u6709\u5185\u5bb9");
                }
            } else {
                NpcImageConfDict.log.info("\u6ca1\u6709\u627e\u5230\u602a\u7269\u56fe\u7247\u914d\u7f6e\u6587\u4ef6");
            }
        } catch (Exception e2) {
            LogWriter.error(null, e2);
        } finally {
            try {
                if (rwb != null) {
                    rwb.close();
                    rwb = null;
                }
                if (is != null) {
                    is.close();
                    is = null;
                }
            } catch (Exception ex) {
            }
        }
        try {
            if (rwb != null) {
                rwb.close();
                rwb = null;
            }
            if (is != null) {
                is.close();
                is = null;
            }
        } catch (Exception ex2) {
        }
    }

    public static Config get(final short _ID) {
        return NpcImageConfDict.configMap.get(_ID);
    }

    public static class Config {

        public short ID;
        public byte headExcursionX;
        public byte headExcursionY;
        public byte shadowType;
        public byte shadowX;
        public short shadowY;
        public short npcHeight;
        public byte npcGrid;
        public short animationID;
        public byte shadowSize;

        private Config(final short _ID, final byte _headExcursionX, final byte _headExcursionY, final byte _shadowType, final byte _shadowX, final short _shadowY, final short _npcHeight, final byte _grid, final short _animationID, final byte _shadowSize) {
            this.ID = _ID;
            this.headExcursionX = _headExcursionX;
            this.headExcursionY = _headExcursionY;
            this.shadowType = _shadowType;
            this.shadowX = _shadowX;
            this.shadowY = _shadowY;
            this.npcHeight = _npcHeight;
            this.npcGrid = _grid;
            this.animationID = _animationID;
            this.shadowSize = _shadowSize;
        }
    }
}
