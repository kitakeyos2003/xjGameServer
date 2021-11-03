// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.function.system.auction;

import hero.player.HeroPlayer;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import hero.log.service.LogServiceImpl;
import hero.npc.function.system.postbox.Mail;
import java.util.Date;
import hero.npc.function.system.postbox.MailService;
import hero.item.Goods;
import hero.item.dictionary.GoodsContents;
import java.util.Iterator;
import hero.item.EquipmentInstance;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import hero.item.enhance.EnhanceService;
import hero.item.service.EquipmentFactory;
import yoyo.service.tools.database.DBServiceImpl;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Timer;
import java.util.HashMap;
import java.util.ArrayList;

public class AuctionDict {

    private static AuctionDict instance;
    private int auctionID;
    private ArrayList<AuctionGoods> weaponGoods;
    private ArrayList<AuctionGoods> bjGoods;
    private ArrayList<AuctionGoods> qjGoods;
    private ArrayList<AuctionGoods> zjGoods;
    private ArrayList<AuctionGoods> psGoods;
    private ArrayList<AuctionGoods> xhdjGoods;
    private ArrayList<AuctionGoods> clGoods;
    private ArrayList<AuctionGoods> tsdjGoods;
    private HashMap<AuctionType, ArrayList<AuctionGoods>> auctionGoods;
    public static final int AUCTION_PRICE = 10;
    public static final long AUCTION_TIME = 86400000L;
    public static final long AUCTION_CHECK_TIME = 3600000L;
    private Timer mCheckTimer;
    private static final byte PAGE_NUM = 10;
    private static final String INSERT_SQL = "INSERT INTO auction(auction_id,goods_id,user_id,nickname,enhance_level,num,price,type,begin_time) VALUES(?,?,?,?,?,?,?,?,?)";
    private static final String DEL_SQL = "DELETE FROM auction WHERE auction_id = ? LIMIT 1";
    private static final String SEL_EQUIPMENT_SQL = "SELECT a.*,e.equipment_id,e.creator_user_id,e.owner_user_id,e.current_durability,e.generic_enhance_desc,e.bloody_enhance_desc,e.be_sealed,e.bind FROM auction a,equipment_instance e WHERE a.goods_id = e.instance_id AND a.type<=?";
    private static final String SEL_GOODS_SQL = "SELECT * FROM auction WHERE type>=?";
    private static final String AUCTION_TITLE = "\u62cd\u5356\u884c";
    private ReentrantLock lock;

    private AuctionDict() {
        this.lock = new ReentrantLock();
        this.weaponGoods = new ArrayList<AuctionGoods>();
        this.bjGoods = new ArrayList<AuctionGoods>();
        this.qjGoods = new ArrayList<AuctionGoods>();
        this.zjGoods = new ArrayList<AuctionGoods>();
        this.psGoods = new ArrayList<AuctionGoods>();
        this.xhdjGoods = new ArrayList<AuctionGoods>();
        this.clGoods = new ArrayList<AuctionGoods>();
        this.tsdjGoods = new ArrayList<AuctionGoods>();
        (this.auctionGoods = new HashMap<AuctionType, ArrayList<AuctionGoods>>()).put(AuctionType.WEAPON, this.weaponGoods);
        this.auctionGoods.put(AuctionType.BU_JIA, this.bjGoods);
        this.auctionGoods.put(AuctionType.QING_JIA, this.qjGoods);
        this.auctionGoods.put(AuctionType.ZHONG_JIA, this.zjGoods);
        this.auctionGoods.put(AuctionType.PEI_SHI, this.psGoods);
        this.auctionGoods.put(AuctionType.MEDICAMENT, this.xhdjGoods);
        this.auctionGoods.put(AuctionType.MATERIAL, this.clGoods);
        this.auctionGoods.put(AuctionType.SPECIAL, this.tsdjGoods);
        this.load();
        this.mCheckTimer = new Timer();
        CheckTask checkTask = new CheckTask();
        this.mCheckTimer.schedule(checkTask, 3600000L, 3600000L);
    }

    private void load() {
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        this.auctionID = 0;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            stm = conn.prepareStatement("SELECT a.*,e.equipment_id,e.creator_user_id,e.owner_user_id,e.current_durability,e.generic_enhance_desc,e.bloody_enhance_desc,e.be_sealed,e.bind FROM auction a,equipment_instance e WHERE a.goods_id = e.instance_id AND a.type<=?");
            stm.setShort(1, AuctionType.PEI_SHI.getID());
            rs = stm.executeQuery();
            while (rs.next()) {
                int _auctionID = rs.getInt(1);
                int _goodsID = rs.getInt(2);
                int _ownerUid = rs.getInt(3);
                String _ownerNickname = rs.getString(4);
                short _enhanceLevel = rs.getShort(5);
                short _num = rs.getShort(6);
                int _price = rs.getInt(7);
                byte _typeID = rs.getByte(8);
                long _time = rs.getLong(9);
                AuctionType _type = AuctionType.getType(_typeID);
                int _equipmentID = rs.getInt(10);
                int _creatorUserID = rs.getInt(11);
                int _ownerUserID = rs.getInt(12);
                int _currentDurabilityPoint = rs.getInt(13);
                String genericEnhanceDesc = rs.getString(14);
                String bloodyEnhanceDesc = rs.getString(15);
                byte existSeal = rs.getByte(16);
                byte isBind = rs.getByte(17);
                EquipmentInstance instance = EquipmentFactory.getInstance().buildFromDB(_creatorUserID, _ownerUserID, _goodsID, _equipmentID, _currentDurabilityPoint, existSeal, isBind);
                EnhanceService.getInstance().parseEnhanceDesc(instance, genericEnhanceDesc, bloodyEnhanceDesc);
                AuctionGoods _goods = new AuctionGoods(_auctionID, _goodsID, _ownerUid, _ownerNickname, _enhanceLevel, _num, _price, _type, instance, _time);
                this.addAuctionGoods(_goods, false);
                if (_auctionID > this.auctionID) {
                    this.auctionID = _auctionID;
                }
            }
            rs.close();
            rs = null;
            stm.close();
            stm = conn.prepareStatement("SELECT * FROM auction WHERE type>=?");
            stm.setShort(1, AuctionType.MEDICAMENT.getID());
            rs = stm.executeQuery();
            while (rs.next()) {
                int _auctionID = rs.getInt(1);
                int _goodsID = rs.getInt(2);
                int _ownerUid = rs.getInt(3);
                String _ownerNickname = rs.getString(4);
                short _enhanceLevel = rs.getShort(5);
                short _num = rs.getShort(6);
                int _price = rs.getInt(7);
                byte _typeID = rs.getByte(8);
                long _time = rs.getLong(9);
                AuctionType _type = AuctionType.getType(_typeID);
                EquipmentInstance instance2 = null;
                AuctionGoods _goods2 = new AuctionGoods(_auctionID, _goodsID, _ownerUid, _ownerNickname, _enhanceLevel, _num, _price, _type, instance2, _time);
                this.addAuctionGoods(_goods2, false);
                if (_auctionID > this.auctionID) {
                    this.auctionID = _auctionID;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (stm != null) {
                    stm.close();
                    stm = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException ex2) {
            }
        }
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (stm != null) {
                stm.close();
                stm = null;
            }
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException ex3) {
        }
    }

    public static AuctionDict getInstance() {
        if (AuctionDict.instance == null) {
            AuctionDict.instance = new AuctionDict();
        }
        return AuctionDict.instance;
    }

    public int getAuctionID() {
        try {
            this.lock.lock();
            return ++this.auctionID;
        } finally {
            this.lock.unlock();
        }
    }

    public void addAuctionGoods(final AuctionGoods _goods, final boolean _insertFlag) {
        try {
            this.lock.lock();
            ArrayList<AuctionGoods> goodsList = this.auctionGoods.get(_goods.getAuctionType());
            if (goodsList != null) {
                goodsList.add(_goods);
            }
        } finally {
            this.lock.unlock();
        }
        this.lock.unlock();
        if (_insertFlag) {
            this.insertDB(_goods);
        }
    }

    private void insertDB(final AuctionGoods _goods) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("INSERT INTO auction(auction_id,goods_id,user_id,nickname,enhance_level,num,price,type,begin_time) VALUES(?,?,?,?,?,?,?,?,?)");
            pstm.setInt(1, _goods.getAuctionID());
            pstm.setInt(2, _goods.getGoodsID());
            pstm.setInt(3, _goods.getOwnerUserID());
            pstm.setString(4, _goods.getOwnerNickname());
            pstm.setShort(5, _goods.getEnhanceLevel());
            pstm.setShort(6, _goods.getNum());
            pstm.setInt(7, _goods.getPrice());
            pstm.setShort(8, _goods.getAuctionType().getID());
            pstm.setLong(9, _goods.getAuctionTime());
            pstm.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                    pstm = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException ex2) {
            }
        }
        try {
            if (pstm != null) {
                pstm.close();
                pstm = null;
            }
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException ex3) {
        }
    }

    private void delDB(final int _auctionID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("DELETE FROM auction WHERE auction_id = ? LIMIT 1");
            pstm.setInt(1, _auctionID);
            pstm.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                    pstm = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException ex2) {
            }
        }
        try {
            if (pstm != null) {
                pstm.close();
                pstm = null;
            }
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException ex3) {
        }
    }

    public AuctionGoods removeAuctionGoods(final int _auctionID, final AuctionType _type) {
        try {
            this.lock.lock();
            ArrayList<AuctionGoods> goodsList = this.auctionGoods.get(_type);
            if (goodsList == null) {
                return null;
            }
            for (int i = 0; i < goodsList.size(); ++i) {
                if (goodsList.get(i).getAuctionID() == _auctionID) {
                    this.delDB(_auctionID);
                    return goodsList.remove(i);
                }
            }
            return null;
        } finally {
            this.lock.unlock();
        }
    }

    public AuctionGoods getAuctionGoods(final int _auctionID, final AuctionType _type) {
        try {
            this.lock.lock();
            ArrayList<AuctionGoods> goodsList = this.auctionGoods.get(_type);
            if (goodsList == null) {
                return null;
            }
            for (final AuctionGoods goods : goodsList) {
                if (goods.getAuctionID() == _auctionID) {
                    return goods;
                }
            }
            return null;
        } finally {
            this.lock.unlock();
        }
    }

    public int[] getAuctionGoods(final int _page, final ArrayList<AuctionGoods> _goods, final AuctionType _type) {
        try {
            this.lock.lock();
            int[] re = new int[2];
            ArrayList<AuctionGoods> goodsList = this.auctionGoods.get(_type);
            if (goodsList == null || goodsList.size() <= _page * 10) {
                return re;
            }
            int begin = _page * 10;
            for (int end = ((_page + 1) * 10 <= goodsList.size()) ? ((_page + 1) * 10) : goodsList.size(), i = begin; i < end; ++i) {
                _goods.add(goodsList.get(i));
            }
            return re;
        } finally {
            this.lock.unlock();
        }
    }

    public ArrayList<AuctionGoods> sreachAuctionGoods(final AuctionType _type, final String _name) {
        ArrayList<AuctionGoods> tempList = new ArrayList<AuctionGoods>();
        ArrayList<AuctionGoods> goodsList = this.auctionGoods.get(_type);
        for (final AuctionGoods g : goodsList) {
            if (g.getInstance() == null) {
                Goods goods = GoodsContents.getGoods(g.getGoodsID());
                if (goods.getName().indexOf(_name) < 0) {
                    continue;
                }
                this.addSreachAuctionGoods(tempList, g);
            } else {
                if (g.getInstance().getArchetype().getName().indexOf(_name) < 0) {
                    continue;
                }
                this.addSreachAuctionGoods(tempList, g);
            }
        }
        if (tempList.size() < 20) {
            return tempList;
        }
        ArrayList<AuctionGoods> list = new ArrayList<AuctionGoods>();
        for (int i = 0; i < 20; ++i) {
            list.add(tempList.get(i));
        }
        return list;
    }

    private void addSreachAuctionGoods(final ArrayList<AuctionGoods> _goods, final AuctionGoods _auctionGoods) {
        for (int i = 0; i < _goods.size(); ++i) {
            if (_goods.get(i).getPrice() > _auctionGoods.getPrice()) {
                _goods.add(i, _auctionGoods);
            }
        }
        _goods.add(_auctionGoods);
    }

    private void checkTimeOut() {
        ArrayList<AuctionGoods> goodsList = new ArrayList<AuctionGoods>();
        try {
            this.lock.lock();
            long nowTime = System.currentTimeMillis();
            for (int i = 0; i < this.weaponGoods.size(); ++i) {
                AuctionGoods agoods = this.weaponGoods.get(i);
                if (nowTime - agoods.getAuctionTime() > 86400000L) {
                    goodsList.add(this.weaponGoods.remove(i));
                    --i;
                }
            }
            for (int i = 0; i < this.bjGoods.size(); ++i) {
                AuctionGoods agoods = this.bjGoods.get(i);
                if (nowTime - agoods.getAuctionTime() > 86400000L) {
                    goodsList.add(this.bjGoods.remove(i));
                    --i;
                }
            }
            for (int i = 0; i < this.qjGoods.size(); ++i) {
                AuctionGoods agoods = this.qjGoods.get(i);
                if (nowTime - agoods.getAuctionTime() > 86400000L) {
                    goodsList.add(this.qjGoods.remove(i));
                    --i;
                }
            }
            for (int i = 0; i < this.zjGoods.size(); ++i) {
                AuctionGoods agoods = this.zjGoods.get(i);
                if (nowTime - agoods.getAuctionTime() > 86400000L) {
                    goodsList.add(this.zjGoods.remove(i));
                    --i;
                }
            }
            for (int i = 0; i < this.psGoods.size(); ++i) {
                AuctionGoods agoods = this.psGoods.get(i);
                if (nowTime - agoods.getAuctionTime() > 86400000L) {
                    goodsList.add(this.psGoods.remove(i));
                    --i;
                }
            }
            for (int i = 0; i < this.xhdjGoods.size(); ++i) {
                AuctionGoods agoods = this.xhdjGoods.get(i);
                if (nowTime - agoods.getAuctionTime() > 86400000L) {
                    goodsList.add(this.xhdjGoods.remove(i));
                    --i;
                }
            }
            for (int i = 0; i < this.clGoods.size(); ++i) {
                AuctionGoods agoods = this.clGoods.get(i);
                if (nowTime - agoods.getAuctionTime() > 86400000L) {
                    goodsList.add(this.clGoods.remove(i));
                    --i;
                }
            }
            for (int i = 0; i < this.tsdjGoods.size(); ++i) {
                AuctionGoods agoods = this.tsdjGoods.get(i);
                if (nowTime - agoods.getAuctionTime() > 86400000L) {
                    goodsList.add(this.tsdjGoods.remove(i));
                    --i;
                }
            }
        } finally {
            this.lock.unlock();
            for (final AuctionGoods goods : goodsList) {
                AuctionType type = goods.getAuctionType();
                if (type == AuctionType.MATERIAL || type == AuctionType.MEDICAMENT || type == AuctionType.SPECIAL) {
                    Mail mail = new Mail(MailService.getInstance().getUseableMailID(), goods.getOwnerUserID(), goods.getOwnerNickname(), "\u62cd\u5356\u884c", (byte) 2, goods.getGoodsID(), goods.getNum(), null, "", "\u62cd\u5356\u83b7\u5f97:" + goods.getName(), new Date(System.currentTimeMillis()), (byte) 2);
                    MailService.getInstance().addMail(mail, true);
                    Goods _goods = GoodsContents.getGoods(goods.getGoodsID());
                    LogServiceImpl.getInstance().mailLog(0, 0, "\u62cd\u5356\u884c", "", mail.getID(), 0, goods.getOwnerNickname(), 0, 0, String.valueOf(goods.getGoodsID()) + "," + _goods.getName() + "," + goods.getNum());
                } else {
                    Mail mail = new Mail(MailService.getInstance().getUseableMailID(), goods.getOwnerUserID(), goods.getOwnerNickname(), "\u62cd\u5356\u884c", (byte) 3, 0, (short) 0, goods.getInstance(), "", "\u62cd\u5356\u83b7\u5f97:" + goods.getName(), new Date(System.currentTimeMillis()), (byte) 2);
                    MailService.getInstance().addMail(mail, true);
                    LogServiceImpl.getInstance().mailLog(0, 0, "\u62cd\u5356\u884c", "", mail.getID(), 0, goods.getOwnerNickname(), 0, 0, String.valueOf(goods.getInstance().getInstanceID()) + "," + goods.getInstance().getArchetype().getName() + ",1");
                }
                HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByName(goods.getOwnerNickname());
                if (player != null && player.isEnable()) {
                    ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u60a8\u6709\u4e00\u5c01\u65b0\u7684\u90ae\u4ef6\uff0c\u5feb\u53bb\u90ae\u7bb1\u67e5\u6536\u5427\uff01"));
                }
                this.delDB(goods.getAuctionID());
            }
            goodsList.clear();
        }
        this.lock.unlock();
        for (final AuctionGoods goods : goodsList) {
            AuctionType type = goods.getAuctionType();
            if (type == AuctionType.MATERIAL || type == AuctionType.MEDICAMENT || type == AuctionType.SPECIAL) {
                Mail mail = new Mail(MailService.getInstance().getUseableMailID(), goods.getOwnerUserID(), goods.getOwnerNickname(), "\u62cd\u5356\u884c", (byte) 2, goods.getGoodsID(), goods.getNum(), null, "", "\u62cd\u5356\u83b7\u5f97:" + goods.getName(), new Date(System.currentTimeMillis()), (byte) 2);
                MailService.getInstance().addMail(mail, true);
                Goods _goods = GoodsContents.getGoods(goods.getGoodsID());
                LogServiceImpl.getInstance().mailLog(0, 0, "\u62cd\u5356\u884c", "", mail.getID(), 0, goods.getOwnerNickname(), 0, 0, String.valueOf(goods.getGoodsID()) + "," + _goods.getName() + "," + goods.getNum());
            } else {
                Mail mail = new Mail(MailService.getInstance().getUseableMailID(), goods.getOwnerUserID(), goods.getOwnerNickname(), "\u62cd\u5356\u884c", (byte) 3, 0, (short) 0, goods.getInstance(), "", "\u62cd\u5356\u83b7\u5f97:" + goods.getName(), new Date(System.currentTimeMillis()), (byte) 2);
                MailService.getInstance().addMail(mail, true);
                LogServiceImpl.getInstance().mailLog(0, 0, "\u62cd\u5356\u884c", "", mail.getID(), 0, goods.getOwnerNickname(), 0, 0, String.valueOf(goods.getInstance().getInstanceID()) + "," + goods.getInstance().getArchetype().getName() + ",1");
            }
            HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByName(goods.getOwnerNickname());
            if (player != null && player.isEnable()) {
                ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), new Warning("\u60a8\u6709\u4e00\u5c01\u65b0\u7684\u90ae\u4ef6\uff0c\u5feb\u53bb\u90ae\u7bb1\u67e5\u6536\u5427\uff01"));
            }
            this.delDB(goods.getAuctionID());
        }
        goodsList.clear();
    }

    class CheckTask extends TimerTask {

        @Override
        public void run() {
            AuctionDict.this.checkTimeOut();
        }
    }
}
