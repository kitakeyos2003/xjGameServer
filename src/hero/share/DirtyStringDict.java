// 
// Decompiled by Procyon v0.5.36
// 
package hero.share;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.File;
import java.util.ArrayList;

public class DirtyStringDict {

    private static DirtyStringDict instance;
    private static ArrayList<String> dirtyStringList;
    private static final String RECORD_FILE_PATH;

    static {
        RECORD_FILE_PATH = String.valueOf(System.getProperty("user.dir")) + File.separator + "res" + File.separator + "data" + File.separator + "dirty" + File.separator + "TalkDirtyString.txt";
        BufferedReader br = null;
        try {
            if (DirtyStringDict.dirtyStringList == null) {
                DirtyStringDict.dirtyStringList = new ArrayList<String>();
                File file = new File(DirtyStringDict.RECORD_FILE_PATH);
                if (file.exists()) {
                    br = new BufferedReader(new InputStreamReader(new FileInputStream(file.getAbsolutePath()), "GB2312"));
                    for (String record = br.readLine(); record != null; record = br.readLine()) {
                        if (!record.trim().equals("")) {
                            DirtyStringDict.dirtyStringList.add(record);
                        }
                    }
                } else {
                    file.createNewFile();
                }
            }
        } catch (Exception ex) {
        } finally {
            try {
                if (br != null) {
                    br.close();
                    br = null;
                }
            } catch (Exception ex2) {
            }
        }
        try {
            if (br != null) {
                br.close();
                br = null;
            }
        } catch (Exception ex3) {
        }
    }

    private DirtyStringDict() {
    }

    public static DirtyStringDict getInstance() {
        if (DirtyStringDict.instance == null) {
            DirtyStringDict.instance = new DirtyStringDict();
        }
        return DirtyStringDict.instance;
    }

    public boolean isCleanString(final String _content) {
        if (_content.indexOf(" ") != -1) {
            return false;
        }
        if (_content.indexOf("\u3000") != -1) {
            return false;
        }
        if ((_content.indexOf("g") != -1 || _content.indexOf("G") != -1 || _content.indexOf("\uff27") != -1) && (_content.indexOf("m") != -1 || _content.indexOf("M") != -1 || _content.indexOf("\uff2d") != -1)) {
            return false;
        }
        if (_content.indexOf("\u5ba2") != -1 && _content.indexOf("\u670d") != -1) {
            return false;
        }
        if (_content.indexOf("\u7ba1") != -1 && _content.indexOf("\u7406") != -1) {
            return false;
        }
        if (_content.indexOf("\u7cfb") != -1 && _content.indexOf("\u7edf") != -1) {
            return false;
        }
        for (int i = 0; i < DirtyStringDict.dirtyStringList.size(); ++i) {
            if (_content.indexOf(DirtyStringDict.dirtyStringList.get(i)) != -1) {
                return false;
            }
        }
        return true;
    }

    public String clearDirtyChar(String _content) {
        StringBuffer sb = new StringBuffer();
        int goodsnum = Integer.parseInt(_content.substring(_content.indexOf("<num>") + 5, _content.indexOf("</num>")));
        _content = _content.substring(0, _content.indexOf("<num>"));
        if (goodsnum > 0) {
            for (int i = 0; i < goodsnum; ++i) {
                String content_tmp = _content.substring(0, _content.indexOf("<goodsname_" + i + ">"));
                String goodsname_tmp = _content.substring(_content.indexOf("<goodsname_" + i + ">") + 13, _content.indexOf("</goodsname_" + i + ">"));
                for (int j = 0; j < DirtyStringDict.dirtyStringList.size(); ++j) {
                    content_tmp = content_tmp.replaceAll(DirtyStringDict.dirtyStringList.get(j), "**");
                }
                sb.append(content_tmp).append(goodsname_tmp);
                _content = _content.substring(_content.indexOf("</goodsname_" + i + ">") + 14);
            }
            for (int i = 0; i < DirtyStringDict.dirtyStringList.size(); ++i) {
                _content = _content.replaceAll(DirtyStringDict.dirtyStringList.get(i), "**");
            }
            return sb.append(_content).toString();
        }
        for (int k = 0; k < DirtyStringDict.dirtyStringList.size(); ++k) {
            _content = _content.replaceAll(DirtyStringDict.dirtyStringList.get(k), "**");
        }
        return _content;
    }
}
