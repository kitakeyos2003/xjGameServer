// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.service;

import java.util.Calendar;
import java.io.FileOutputStream;
import hero.item.Weapon;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import hero.item.dictionary.WeaponDict;
import yoyo.service.tools.database.DBServiceImpl;
import java.util.Date;
import hero.share.service.LogWriter;
import java.util.TimerTask;
import java.util.Properties;
import java.io.FileInputStream;
import java.util.GregorianCalendar;
import java.text.DateFormat;
import java.io.File;
import yoyo.service.YOYOSystem;
import java.text.SimpleDateFormat;
import java.util.Timer;
import javolution.util.FastList;

public class WeaponRankManager {

    private FastList<WeaponRankUnit> rankList;
    private Timer rankTimer;
    private static WeaponRankManager instance;
    private static final String CONFIG_FILE;
    private static final String INSERT_RANK_SQL = "INSERT INTO weapon_rank(equipment_id,owner_name,generic_enhance_desc,bloody_enhance_desc,exist_seal) VALUES (?,?,?,?,?)";
    private static final String SELECT_RANK_SQL = "SELECT equipment_id,exist_seal,owner_name,generic_enhance_desc,bloody_enhance_desc FROM weapon_rank WHERE overdue = 0 ORDER BY id ASC LIMIT ?";
    private static final String UPDATE_OVERDUE_RANK_SQL = "UPDATE weapon_rank SET overdue = 1;";
    private static final String SELECT_WEAPON_SQL = "SELECT ei.equipment_id,ei.be_sealed,ei.generic_enhance_desc,ei.bloody_enhance_desc,p.nickname FROM equipment_instance ei,player p WHERE ei.bloody_enhance_desc NOT LIKE '' AND p.user_id = ei.owner_user_id";
    private static final int MAX_RANK_NUMBER = 50;
    private static SimpleDateFormat DATE_FORMATTER;

    static {
        CONFIG_FILE = String.valueOf(YOYOSystem.HOME) + File.separator + "res" + File.separator + "config" + File.separator + "WeaponRankTime.txt";
        (WeaponRankManager.DATE_FORMATTER = (SimpleDateFormat) DateFormat.getDateTimeInstance()).applyPattern("yy-MM-dd HH:mm:ss");
    }

    private WeaponRankManager() {
    }

    public static WeaponRankManager getInstance() {
        if (WeaponRankManager.instance == null) {
            (WeaponRankManager.instance = new WeaponRankManager()).init();
        }
        return WeaponRankManager.instance;
    }

    private void init() {
        this.rankList = (FastList<WeaponRankUnit>) new FastList();
        FileInputStream fis = null;
        try {
            GregorianCalendar fixedRefreshTime = new GregorianCalendar();
            fixedRefreshTime.set(7, 2);
            fixedRefreshTime.set(11, 3);
            fixedRefreshTime.set(12, 0);
            fixedRefreshTime.set(13, 0);
            File configFile = new File(WeaponRankManager.CONFIG_FILE);
            if (!configFile.exists()) {
                configFile.createNewFile();
                this.reRank();
                return;
            }
            fis = new FileInputStream(configFile);
            Properties property = new Properties();
            property.load(fis);
            String lastRefreshDate = property.getProperty("Refresh_Time");
            if (lastRefreshDate != null) {
                Date refreshDate = WeaponRankManager.DATE_FORMATTER.parse(lastRefreshDate);
                property.clear();
                GregorianCalendar lastRefreshTime = new GregorianCalendar();
                lastRefreshTime.setTime(refreshDate);
                if (fixedRefreshTime.before(lastRefreshTime)) {
                    this.loadRankFromDB();
                } else {
                    this.reRank();
                }
            } else {
                this.reRank();
            }
            this.rankTimer = new Timer();
            fixedRefreshTime.add(5, 7);
            this.rankTimer.schedule(new WeaponRerankTask(), fixedRefreshTime.getTimeInMillis() - new GregorianCalendar().getTimeInMillis(), 604800000L);
        } catch (Exception e) {
            LogWriter.error(this, e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                    fis = null;
                } catch (Exception ex) {
                }
            }
        }
        if (fis != null) {
            try {
                fis.close();
                fis = null;
            } catch (Exception ex2) {
            }
        }
    }

    private void loadRankFromDB() {
        Connection conn = null;
        PreparedStatement stam = null;
        ResultSet rs = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            stam = conn.prepareStatement("SELECT equipment_id,exist_seal,owner_name,generic_enhance_desc,bloody_enhance_desc FROM weapon_rank WHERE overdue = 0 ORDER BY id ASC LIMIT ?");
            stam.setInt(1, 50);
            rs = stam.executeQuery();
            while (rs.next()) {
                int equipmentID = rs.getInt("equipment_id");
                boolean existSeal = rs.getBoolean("exist_seal");
                String ownerName = rs.getString("owner_name");
                String genericEnhanceDesc = rs.getString("generic_enhance_desc");
                String bloodyEnhanceDesc = rs.getString("bloody_enhance_desc");
                Weapon weapon = WeaponDict.getInstance().getWeapon(equipmentID);
                if (weapon != null) {
                    this.rankList.add(new WeaponRankUnit(weapon, ownerName, genericEnhanceDesc, bloodyEnhanceDesc, existSeal));
                }
            }
        } catch (Exception e) {
            LogWriter.error(this, e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (stam != null) {
                    stam.close();
                    stam = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (Exception ex) {
            }
        }
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (stam != null) {
                stam.close();
                stam = null;
            }
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (Exception ex2) {
        }
    }

    private void reRank() {
        Connection conn = null;
        PreparedStatement stam = null;
        ResultSet rs = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            stam = conn.prepareStatement("UPDATE weapon_rank SET overdue = 1;");
            stam.executeUpdate();
            this.rankList.clear();
            rs = stam.executeQuery("SELECT ei.equipment_id,ei.be_sealed,ei.generic_enhance_desc,ei.bloody_enhance_desc,p.nickname FROM equipment_instance ei,player p WHERE ei.bloody_enhance_desc NOT LIKE '' AND p.user_id = ei.owner_user_id");
            while (rs.next()) {
                int equipmentID = rs.getInt("equipment_id");
                byte existSeal = rs.getByte("be_sealed");
                String genericEnhanceDesc = rs.getString("generic_enhance_desc");
                String bloodyEnhanceDesc = rs.getString("bloody_enhance_desc");
                String ownerName = rs.getString("nickname");
                Weapon weapon = WeaponDict.getInstance().getWeapon(equipmentID);
                if (weapon != null) {
                    this.sort(new WeaponRankUnit(weapon, ownerName, genericEnhanceDesc, bloodyEnhanceDesc, existSeal == 1));
                }
            }
            if (this.rankList.size() > 0) {
                stam.close();
                stam = null;
                conn.setAutoCommit(false);
                stam = conn.prepareStatement("INSERT INTO weapon_rank(equipment_id,owner_name,generic_enhance_desc,bloody_enhance_desc,exist_seal) VALUES (?,?,?,?,?)");
                for (final WeaponRankUnit rank : this.rankList) {
                    stam.setInt(1, rank.weapon.getID());
                    stam.setString(2, rank.ownerName);
                    stam.setString(3, rank.genericEnhanceDesc);
                    stam.setString(4, rank.bloodyEnhanceDesc);
                    stam.setBoolean(5, rank.existSeal);
                    stam.addBatch();
                }
                stam.executeBatch();
                conn.setAutoCommit(true);
            }
            FileOutputStream fw = new FileOutputStream(WeaponRankManager.CONFIG_FILE);
            Properties property = new Properties();
            property.setProperty("Refresh_Time", WeaponRankManager.DATE_FORMATTER.format(new Date()));
            property.store(fw, null);
            property.clear();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stam != null) {
                    stam.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ex) {
            }
        }
        try {
            if (stam != null) {
                stam.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (Exception ex2) {
        }
    }

    private void sort(final WeaponRankUnit _rank) {
        if (_rank.bloodyEnhance.getPveLevel() >= 1 || _rank.bloodyEnhance.getPvpLevel() >= 1) {
            for (int i = 0; i < this.rankList.size(); ++i) {
                if (((WeaponRankUnit) this.rankList.get(i)).score < _rank.score) {
                    this.rankList.add(i, _rank);
                    return;
                }
            }
            this.rankList.add(_rank);
        }
    }

    public FastList<WeaponRankUnit> getRankList() {
        return this.rankList;
    }

    class WeaponRerankTask extends TimerTask {

        @Override
        public void run() {
            Calendar now = Calendar.getInstance();
            if (now.get(7) == 2 && now.get(11) == 3) {
                WeaponRankManager.this.reRank();
            }
        }
    }
}
