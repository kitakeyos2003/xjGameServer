// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.service;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import yoyo.service.YOYOSystem;
import org.dom4j.Element;
import java.util.HashMap;
import hero.share.RankMenuField;
import java.util.Map;
import org.apache.log4j.Logger;
import yoyo.service.base.AbsConfig;

public class ShareConfig extends AbsConfig {

    private int rmb_fee_point_convert;
    private String picture_data_path;
    private String high_path;
    private String middle_path;
    private String low_path;
    private String monetary_unit;
    private String sign_line_break;
    private String resource_DB_name;
    private String resource_DB_url;
    private String resource_DB_username;
    private String resource_DB_password;
    private static Logger log;
    public Map<Byte, RankMenuField> rankTypeMap;
    public int hookExpGoodsID;
    public int hookHours;

    static {
        ShareConfig.log = Logger.getLogger((Class) ShareConfig.class);
    }

    public ShareConfig() {
        this.rankTypeMap = new HashMap<Byte, RankMenuField>();
    }

    @Override
    public void init(final Element _xmlNode) throws Exception {
        Element ePicture = _xmlNode.element("data");
        Element params = _xmlNode.element("para");
        Element resDBConfig = _xmlNode.element("resource_DB_config");
        this.resource_DB_name = resDBConfig.elementTextTrim("resource_DB_name");
        this.resource_DB_url = resDBConfig.elementTextTrim("resource_DB_url");
        this.resource_DB_username = resDBConfig.elementTextTrim("resource_DB_username");
        this.resource_DB_password = resDBConfig.elementTextTrim("resource_DB_password");
        this.rmb_fee_point_convert = Integer.valueOf(ePicture.elementTextTrim("rmb_fee_point_convert"));
        this.high_path = String.valueOf(YOYOSystem.HOME) + ePicture.elementTextTrim("high_path");
        this.middle_path = String.valueOf(YOYOSystem.HOME) + ePicture.elementTextTrim("middle_path");
        this.low_path = String.valueOf(YOYOSystem.HOME) + ePicture.elementTextTrim("low_path");
        this.picture_data_path = String.valueOf(YOYOSystem.HOME) + ePicture.elementTextTrim("picture_data_path");
        this.monetary_unit = ePicture.elementTextTrim("monetary_unit");
        this.sign_line_break = ePicture.elementTextTrim("sign_line_break");
        this.hookExpGoodsID = Integer.parseInt(params.elementTextTrim("hook_exp_id"));
        this.hookHours = Integer.parseInt(params.elementTextTrim("hook_hours"));
        Element rankE = params.element("rank_types");
        Iterator<Element> rankTypes = (Iterator<Element>) rankE.elementIterator();
        while (rankTypes.hasNext()) {
            Element rankType = rankTypes.next();
            ShareConfig.log.debug((Object) ("rank type name = " + rankType.getName()));
            byte id = Byte.parseByte(rankType.elementTextTrim("id"));
            String name = rankType.elementTextTrim("name");
            String fields = rankType.elementTextTrim("fields");
            byte menuLevel = Byte.parseByte(rankType.elementTextTrim("menu_level"));
            ShareConfig.log.debug((Object) ("rank type id=" + id + ",name=" + name + ",fields=" + fields));
            Element secondMenusE = rankType.element("show_type_menus");
            List<RankMenuField> secondMenuList = new ArrayList<RankMenuField>();
            if (secondMenusE != null) {
                Iterator<Element> menuIt = (Iterator<Element>) secondMenusE.elementIterator();
                while (menuIt.hasNext()) {
                    Element menu = menuIt.next();
                    byte sid = Byte.parseByte(menu.elementTextTrim("id"));
                    String sname = menu.elementTextTrim("name");
                    byte smenuLevel = Byte.parseByte(menu.elementTextTrim("menu_level"));
                    Element thirdMneusE = menu.element("child_menus");
                    List<RankMenuField> thirdMenuList = new ArrayList<RankMenuField>();
                    if (thirdMneusE != null) {
                        Iterator<Element> childMenuIt = (Iterator<Element>) thirdMneusE.elementIterator();
                        while (childMenuIt.hasNext()) {
                            Element childE = childMenuIt.next();
                            ShareConfig.log.debug((Object) ("child e name =" + childE.getName()));
                            byte cid = Byte.parseByte(childE.elementTextTrim("id"));
                            String cname = childE.elementTextTrim("name");
                            byte cmenuLevel = Byte.parseByte(childE.elementTextTrim("menu_level"));
                            ShareConfig.log.debug((Object) ("child cid=" + cid + ",cname=" + cname + ",cmenulevel=" + cmenuLevel));
                            String vocations = childE.elementTextTrim("vocation");
                            RankMenuField rmf = new RankMenuField();
                            rmf.id = cid;
                            rmf.name = cname;
                            rmf.menuLevel = cmenuLevel;
                            rmf.vocation = vocations;
                            thirdMenuList.add(rmf);
                        }
                    }
                    ShareConfig.log.debug((Object) ("menu id=" + id + ",name=" + name));
                    RankMenuField srmf = new RankMenuField();
                    srmf.id = sid;
                    srmf.name = sname;
                    srmf.menuLevel = smenuLevel;
                    srmf.childMenuList = thirdMenuList;
                    secondMenuList.add(srmf);
                }
            }
            List<String> fieldList = new ArrayList<String>();
            String[] fieldArray = fields.split(",");
            String[] array;
            for (int length = (array = fieldArray).length, i = 0; i < length; ++i) {
                String s = array[i];
                fieldList.add(s);
            }
            RankMenuField rmf2 = new RankMenuField();
            rmf2.id = id;
            rmf2.name = name;
            rmf2.fieldList = fieldList;
            rmf2.menuLevel = menuLevel;
            rmf2.childMenuList = secondMenuList;
            this.rankTypeMap.put(id, rmf2);
        }
        ShareConfig.log.debug((Object) ("rank type map size = " + this.rankTypeMap.size()));
    }

    public String getHighPath() {
        return this.high_path;
    }

    public String getMiddlePath() {
        return this.middle_path;
    }

    public String getLowPath() {
        return this.low_path;
    }

    public String getPicture() {
        return this.picture_data_path;
    }

    public String getResourceDBname() {
        return this.resource_DB_name;
    }

    public String getResourceDBurl() {
        return this.resource_DB_url;
    }

    public String getResourceDBusername() {
        return this.resource_DB_username;
    }

    public String getResourceDBpassword() {
        return this.resource_DB_password;
    }

    public String getMonetaryUnit() {
        return this.monetary_unit;
    }

    public String getSignLineBreak() {
        return this.sign_line_break;
    }

    public int getFeePointConvert() {
        return this.rmb_fee_point_convert;
    }
}
