// 
// Decompiled by Procyon v0.5.36
// 
package hero.charge.service;

import java.util.Iterator;
import yoyo.service.YOYOSystem;
import org.dom4j.Element;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import yoyo.service.base.AbsConfig;

public class ChargeConfig extends AbsConfig {

    private static Logger log;
    public String point_amount_db_url;
    public String point_amount_db_username;
    public String point_amount_db_pwd;
    public String mall_goods_data_path;
    public String url_charge_type_path;
    public String url_charge_info_path;
    public int port_callback;
    public String fee_ini_url;
    public String szf_rechange_url;
    public String ng_rechange_url;
    public String add_point_url;
    public String sub_point_url;
    public String query_point_url;
    public String query_deduct_list_url;
    public String query_rechage_list_url;
    public Map<String, String> feeIdsMap;
    public String[] type_string;
    public String notice_string;
    public short now_version;
    public String[][] bag_upgrade_data;

    static {
        ChargeConfig.log = Logger.getLogger((Class) ChargeConfig.class);
    }

    public ChargeConfig() {
        this.feeIdsMap = new HashMap<String, String>();
    }

    @Override
    public void init(final Element node) throws Exception {
        try {
            Element paraElement = node.element("config");
            this.mall_goods_data_path = String.valueOf(YOYOSystem.HOME) + paraElement.elementTextTrim("mall_goods_data_path");
            this.url_charge_type_path = String.valueOf(YOYOSystem.HOME) + paraElement.elementTextTrim("url_charge_type_path");
            this.url_charge_info_path = String.valueOf(YOYOSystem.HOME) + paraElement.elementTextTrim("url_charge_info_path");
            this.port_callback = Integer.parseInt(paraElement.elementTextTrim("port_callback"));
            this.fee_ini_url = paraElement.elementTextTrim("fee_ini_url");
            this.szf_rechange_url = paraElement.elementTextTrim("szf_rechange_url");
            this.ng_rechange_url = paraElement.elementTextTrim("ng_rechange_url");
            this.add_point_url = paraElement.elementTextTrim("add_point_url");
            this.sub_point_url = paraElement.elementTextTrim("sub_point_url");
            this.query_point_url = paraElement.elementTextTrim("query_point_url");
            this.query_deduct_list_url = paraElement.elementTextTrim("query_deduct_list_url");
            this.query_rechage_list_url = paraElement.elementTextTrim("query_rechage_list_url");
            Element subE = paraElement.element("fee_ids");
            Iterator<Element> feeidsIt = (Iterator<Element>) subE.elementIterator();
            while (feeidsIt.hasNext()) {
                Element eit = feeidsIt.next();
                String id = eit.elementTextTrim("id");
                String url = eit.elementTextTrim("url");
                this.feeIdsMap.put(id, url);
            }
            this.type_string = paraElement.elementTextTrim("type_string").split(",");
            this.notice_string = paraElement.elementTextTrim("notice_string");
            this.now_version = Short.valueOf(paraElement.elementTextTrim("now_version"));
            String[] temp = paraElement.elementTextTrim("bag_upgrade_data").split(";");
            this.bag_upgrade_data = new String[temp.length][2];
            for (int i = 0; i < temp.length; ++i) {
                this.bag_upgrade_data[i] = temp[i].split(",");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getTypeDesc(final byte _type) {
        String desc = "";
        try {
            desc = this.type_string[_type];
        } catch (Exception e) {
            ChargeConfig.log.info((Object) ("warn:\u65e0\u6cd5\u901a\u8fc7type\u83b7\u5f97type\u7684\u4e2d\u6587\u63cf\u8ff0type=" + _type));
            e.printStackTrace();
        }
        return desc;
    }
}
