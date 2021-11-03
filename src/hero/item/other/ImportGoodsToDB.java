// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.other;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import java.util.Iterator;
import java.sql.CallableStatement;
import java.sql.Connection;
import org.dom4j.DocumentException;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.io.File;
import java.util.List;
import org.apache.log4j.Logger;

public class ImportGoodsToDB {

    private static Logger log;
    private static final String DIR_PATH = "\\res\\data\\goods\\";
    private static final String PET_PATH = "\\res\\data\\pet\\pet.xml";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/pss?connectTimeout=0&autoReconnect=true&characterEncoding=utf8";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "yoyo1705";
    private static List<File> filelist;
    private static final int GAME_ID = 1;

    static {
        ImportGoodsToDB.log = Logger.getLogger((Class) ImportGoodsToDB.class);
        ImportGoodsToDB.filelist = new ArrayList<File>();
    }

    public static void main(final String[] args) {
        importToDB();
    }

    private static void importToDB() {
        Connection conn = null;
        CallableStatement cs = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pss?connectTimeout=0&autoReconnect=true&characterEncoding=utf8", "root", "yoyo1705");
            conn.setAutoCommit(false);
            cs = conn.prepareCall("call sync_goods_proc(?,?,?,?,?)");
            List<Goodsx> goodslist = getGoodsList();
            for (final Goodsx goods : goodslist) {
                cs.setInt(1, 1);
                cs.setInt(2, goods.getGoodsid());
                cs.setString(3, goods.getGoodsname());
                cs.setString(4, goods.getType());
                cs.setString(5, goods.getSmallType());
                cs.addBatch();
            }
            cs.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
            cs.close();
            conn.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e2) {
            e2.printStackTrace();
        } catch (DocumentException e3) {
            e3.printStackTrace();
        } finally {
            try {
                if (cs != null) {
                    cs.close();
                    cs = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e4) {
                e4.printStackTrace();
            }
        }
        try {
            if (cs != null) {
                cs.close();
                cs = null;
            }
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e4) {
            e4.printStackTrace();
        }
    }

    private static List<Goodsx> getGoodsList() throws DocumentException {
        List<Goodsx> goodsList = new ArrayList<Goodsx>();
        File dir = new File(String.valueOf(System.getProperty("user.dir")) + "\\res\\data\\goods\\");
        getFileList(dir);
        getFileList(new File(String.valueOf(System.getProperty("user.dir")) + "\\res\\data\\pet\\pet.xml"));
        for (final File f : ImportGoodsToDB.filelist) {
            ImportGoodsToDB.log.info((Object) f.getName());
            String type = "";
            boolean ispet = false;
            if (f.getName().equals("suite.xml") || f.getName().equals("clothes.xml") || f.getName().equals("glove.xml") || f.getName().equals("hat.xml") || f.getName().equals("necklace.xml") || f.getName().equals("shoes.xml")) {
                type = "\u9632\u5177";
            } else if (f.getName().equals("feed.xml")) {
                type = "\u5ba0\u7269\u9972\u6599";
            } else if (f.getName().equals("mall_goods.xml")) {
                type = "\u5546\u57ce\u7269\u54c1";
            } else if (f.getName().equals("material.xml")) {
                type = "\u6750\u6599";
            } else if (f.getName().equals("medicament.xml")) {
                type = "\u836f\u6c34";
            } else if (f.getName().equals("pet_quip.xml")) {
                type = "\u5ba0\u7269\u88c5\u5907";
            } else if (f.getName().equals("special.xml")) {
                type = "\u7279\u6b8a\u7269\u54c1";
            } else if (f.getName().equals("weapon.xml")) {
                type = "\u6b66\u5668";
            } else {
                if (!f.getName().equals("pet.xml")) {
                    continue;
                }
                type = "\u5ba0\u7269";
                ispet = true;
            }
            SAXReader reader = new SAXReader();
            Document document = reader.read(f);
            Element root = document.getRootElement();
            Iterator<Element> rootIt = (Iterator<Element>) root.elementIterator();
            while (rootIt.hasNext()) {
                Goodsx goods = new Goodsx();
                Element subE = rootIt.next();
                if (subE != null) {
                    String id = subE.elementTextTrim("id");
                    String name = subE.elementTextTrim("name");
                    String smallType = subE.elementTextTrim("type");
                    if (ispet) {
                        int stage = Integer.parseInt(subE.elementTextTrim("stage"));
                        type = "\u5ba0\u7269" + name;
                        if (stage == 0) {
                            name = String.valueOf(name) + "\u86cb";
                            smallType = "\u5ba0\u7269\u86cb";
                        }
                        if (stage == 1) {
                            name = "\u5e7c\u5e74" + name;
                            smallType = "\u5e7c\u5e74\u5ba0\u7269";
                        }
                        if (stage == 2) {
                            if (Integer.parseInt(smallType) == 1) {
                                name = "\u5750\u9a91" + name;
                            } else if (Integer.parseInt(smallType) == 2) {
                                name = "\u6218\u6597" + name;
                            }
                            smallType = "\u6210\u5e74\u5ba0\u7269";
                        }
                    }
                    goods.setGoodsid(Integer.parseInt(id));
                    goods.setGoodsname(name);
                    goods.setType(type);
                    if (smallType != null) {
                        goods.setSmallType(smallType);
                    }
                    goodsList.add(goods);
                }
            }
        }
        return goodsList;
    }

    private static void getFileList(final File file) {
        if (file.isDirectory()) {
            File[] fs = file.listFiles();
            File[] array;
            for (int length = (array = fs).length, i = 0; i < length; ++i) {
                File f = array[i];
                getFileList(f);
            }
        } else if (file.getName().endsWith(".xml")) {
            ImportGoodsToDB.filelist.add(file);
        }
    }
}
