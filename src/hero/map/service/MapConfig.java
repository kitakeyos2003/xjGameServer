// 
// Decompiled by Procyon v0.5.36
// 
package hero.map.service;

import yoyo.service.YOYOSystem;
import org.dom4j.Element;
import yoyo.service.base.AbsConfig;

public class MapConfig extends AbsConfig {

    private String areaImagePath;
    private String microMapImagePath;
    private String mapTileImagePath;
    private String transmitMapListPath;
    private String areaDataPath;
    private String mapRelationDataPath;
    private short bornMapIDOfLongShan;
    private short bornMapIDOfHeMuDu;
    private String mapModelFilePath;
    private String mapElementImagePath;
    public String map_music_config_path;
    public String pet_equip_data_path;
    public short break_lock_default_long_map;
    public short break_lock_default_mo_map;
    public boolean use_default_map;
    public String world_maps_shen_long_jie;
    public String world_maps_mo_long_jie;
    public String world_maps_xian_jie;
    public String world_maps;
    public String[] world_map_png_anu;
    public String[] world_names;

    @Override
    public void init(final Element _xmlNode) throws Exception {
        Element eMap = _xmlNode.element("map");
        Element para = _xmlNode.element("para");
        this.mapModelFilePath = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("map_model_path");
        this.areaImagePath = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("area_image_path");
        this.microMapImagePath = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("micro_map_image_path");
        this.mapTileImagePath = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("map_tile_image_path");
        this.transmitMapListPath = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("transmit_list_path");
        this.areaDataPath = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("area_data_path");
        this.mapRelationDataPath = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("map_relation_path");
        this.mapElementImagePath = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("map_element_image_path");
        this.map_music_config_path = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("map_music_config_path");
        this.pet_equip_data_path = String.valueOf(YOYOSystem.HOME) + eMap.elementTextTrim("pet_equipment_data_path");
        this.break_lock_default_long_map = Short.valueOf(para.elementTextTrim("break_lock_default_long_map"));
        this.break_lock_default_mo_map = Short.valueOf(para.elementTextTrim("break_lock_default_mo_map"));
        this.use_default_map = Boolean.valueOf(para.elementTextTrim("break_lock_default_mo_map"));
        this.world_maps = eMap.elementTextTrim("world_maps");
        this.world_maps_mo_long_jie = eMap.elementTextTrim("world_maps_mo_long");
        this.world_maps_shen_long_jie = eMap.elementTextTrim("world_maps_shen_long");
        this.world_maps_xian_jie = eMap.elementTextTrim("world_maps_xian_jie");
        this.world_map_png_anu = para.elementTextTrim("world_map_png_anu").split(",");
        this.world_names = para.elementTextTrim("world_names").split(",");
    }

    public String getMapModelFilePath() {
        return this.mapModelFilePath;
    }

    public String getAreaImagePath() {
        return this.areaImagePath;
    }

    public String getMicroMapImagePath() {
        return this.microMapImagePath;
    }

    public String getMapTileImagePath() {
        return this.mapTileImagePath;
    }

    public String getTransmitMapListPath() {
        return this.transmitMapListPath;
    }

    public String getAreaDataPath() {
        return this.areaDataPath;
    }

    public String getMapRelationDataPath() {
        return this.mapRelationDataPath;
    }

    public String getMapElementImagePath() {
        return this.mapElementImagePath;
    }
}
