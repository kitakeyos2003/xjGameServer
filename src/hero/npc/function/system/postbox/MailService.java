// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.function.system.postbox;

import java.sql.Timestamp;
import java.sql.PreparedStatement;
import hero.item.enhance.EnhanceService;
import hero.item.service.EquipmentFactory;
import hero.item.EquipmentInstance;
import java.util.Date;
import java.util.Iterator;
import hero.guild.service.GuildServiceImpl;
import hero.social.service.SocialServiceImpl;
import java.util.List;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import yoyo.service.tools.database.DBServiceImpl;
import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayList;
import java.util.HashMap;

public class MailService {

    private int mailID;
    private static MailService instance;
    public static final int MAX_MAIL_SIZE = 30;
    private HashMap<Integer, ArrayList<Mail>> mailDict;
    private static final String SEL_MAXID_SQL = "SELECT MAX(mail_id) FROM mail";
    private static final String SELECT_MAIL_NUMBER_SQL = "SELECT COUNT(*) AS number FROM mail WHERE receiver_uid = ?";
    private static final String INSERT_NEW_MAIL = "INSERT INTO mail VALUE (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String DEL_MAIL = "DELETE FROM mail WHERE mail_id = ? LIMIT 1";
    private static final String UPDATE_MAIL = "update mail set type=4,money=0,freight_point=0,goods_id=0,number=0,read_finish=1 WHERE mail_id = ? LIMIT 1";
    private static final String UPDATE_MAIL_READFINISH = "update mail set read_finish=1 WHERE mail_id = ? LIMIT 1";
    private static final String DELETE_ALL_MAIL = "DELETE FROM mail WHERE receiver_uid = ?";
    private static final String SELECT_MAILS = "SELECT * FROM mail left join equipment_instance ON mail.goods_id=equipment_instance.instance_id WHERE mail.receiver_uid = ?";
    private static final int PAGE_NUM = 10;
    private ReentrantLock lock;

    private MailService() {
        this.lock = new ReentrantLock();
        this.mailDict = new HashMap<Integer, ArrayList<Mail>>();
        this.loadMaxMailID();
    }

    public static MailService getInstance() {
        if (MailService.instance == null) {
            MailService.instance = new MailService();
        }
        return MailService.instance;
    }

    private void loadMaxMailID() {
        this.mailID = 0;
        Connection conn = null;
        Statement stm = null;
        ResultSet rs = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            stm = conn.createStatement();
            rs = stm.executeQuery("SELECT MAX(mail_id) FROM mail");
            if (rs.next()) {
                this.mailID = rs.getInt(1);
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

    public void loadMail(final int _userID) {
        if (this.mailDict.get(_userID) == null) {
            this.mailDict.put(_userID, this.loadMailFromDB(_userID));
        }
    }

    public void clear(final int _userID) {
        this.mailDict.remove(_userID);
    }

    public int getUseableMailID() {
        try {
            this.lock.lock();
            return ++this.mailID;
        } finally {
            this.lock.unlock();
        }
    }

    public boolean addMail(final Mail _mail, final boolean _isSystemMail) {
        ArrayList<Mail> mailList = this.mailDict.get(_mail.getReceiverUserID());
        if (mailList != null && mailList.size() >= 30) {
            return false;
        }
        if (this.insertMailToDB(_mail, _isSystemMail)) {
            if (mailList != null) {
                mailList.add(_mail);
            }
            return true;
        }
        return false;
    }

    public List<Mail> getMailList(final int _userID, final short _page) {
        try {
            this.lock.lock();
            ArrayList<Mail> mailList = this.mailDict.get(_userID);
            try {
                mailList = this.getSortMails(mailList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (mailList == null || mailList.size() <= _page * 10) {
                return null;
            }
            int begin = _page * 10;
            int end = ((_page + 1) * 10 <= mailList.size()) ? ((_page + 1) * 10) : mailList.size();
            return mailList.subList(begin, end);
        } finally {
            this.lock.unlock();
        }
    }

    private ArrayList<Mail> getSortMails(final ArrayList<Mail> list) {
        ArrayList<Mail> mailList = list;
        ArrayList<Mail> oldList = new ArrayList<Mail>();
        ArrayList<Mail> newList = new ArrayList<Mail>();
        Mail temp = null;
        for (int i = 0; i < mailList.size(); ++i) {
            temp = mailList.get(i);
            if (temp.getReadFinish()) {
                oldList.add(temp);
            } else {
                newList.add(temp);
            }
        }
        Mail[] oldMails = new Mail[oldList.size()];
        Mail[] newMails = new Mail[newList.size()];
        for (int j = 0; j < oldList.size(); ++j) {
            oldMails[j] = oldList.get(j);
        }
        for (int j = 0; j < newList.size(); ++j) {
            newMails[j] = newList.get(j);
        }
        Mail k = null;
        for (int l = 0; l < oldMails.length; ++l) {
            for (int m = l + 1; m < oldMails.length; ++m) {
                if (oldMails[l].getDate().getTime() > oldMails[m].getDate().getTime()) {
                    k = oldMails[l];
                    oldMails[l] = oldMails[m];
                    oldMails[m] = k;
                }
            }
        }
        for (int l = 0; l < newMails.length; ++l) {
            for (int m = l + 1; m < newMails.length; ++m) {
                if (newMails[l].getDate().getTime() > newMails[m].getDate().getTime()) {
                    k = newMails[l];
                    newMails[l] = newMails[m];
                    newMails[m] = k;
                }
            }
        }
        mailList = new ArrayList<Mail>();
        for (int l = newMails.length - 1; l > -1; --l) {
            mailList.add(newMails[l]);
        }
        for (int l = oldMails.length - 1; l > -1; --l) {
            mailList.add(oldMails[l]);
        }
        return mailList;
    }

    public byte getSocial(final String _sender, final String _receiver) {
        byte result = 3;
        if (SocialServiceImpl.getInstance().beFriend(_sender, _receiver, true)) {
            result = 0;
        } else if (GuildServiceImpl.getInstance().isAssociate(_sender, _receiver)) {
            result = 1;
        }
        return result;
    }

    public int getMailNumber(final int _userID) {
        ArrayList<Mail> goodsList = this.mailDict.get(_userID);
        if (goodsList == null) {
            return 0;
        }
        return goodsList.size();
    }

    public int getUnreadMailNumber(final int _userID) {
        ArrayList<Mail> list = this.mailDict.get(_userID);
        int result = 0;
        if (list != null) {
            for (final Mail mail : list) {
                if (!mail.getReadFinish()) {
                    ++result;
                }
            }
        }
        return result;
    }

    public Mail getMail(final int _userID, final int _mailID) {
        try {
            this.lock.lock();
            ArrayList<Mail> mails = this.mailDict.get(_userID);
            if (mails != null) {
                for (final Mail mail : mails) {
                    if (mail.getID() == _mailID) {
                        return mail;
                    }
                }
            }
            return null;
        } finally {
            this.lock.unlock();
        }
    }

    public boolean removeAttachment(final int _userID, final int _mailID) {
        try {
            this.lock.lock();
            ArrayList<Mail> list = this.mailDict.get(_userID);
            if (list != null) {
                for (final Mail mail : list) {
                    if (mail.getID() == _mailID) {
                        mail.removeAttachment();
                        this.removeAttachmentFromDB(_mailID);
                        return true;
                    }
                }
            }
            return false;
        } finally {
            this.lock.unlock();
        }
    }

    public boolean readMail(final int _userID, final int _mailID) {
        try {
            this.lock.lock();
            ArrayList<Mail> list = this.mailDict.get(_userID);
            if (list != null) {
                for (final Mail mail : list) {
                    if (mail.getID() == _mailID) {
                        mail.readMail();
                        this.readFinishFromDB(_mailID);
                        return true;
                    }
                }
            }
            return false;
        } finally {
            this.lock.unlock();
        }
    }

    public boolean removeMail(final int _userID, final int _mailID) {
        try {
            this.lock.lock();
            ArrayList<Mail> list = this.mailDict.get(_userID);
            if (list != null) {
                for (int i = 0; i < list.size(); ++i) {
                    if (list.get(i).getID() == _mailID) {
                        list.remove(i);
                        this.deleteMailFromDB(_mailID);
                        return true;
                    }
                }
            }
            return false;
        } finally {
            this.lock.unlock();
        }
    }

    private ArrayList<Mail> loadMailFromDB(final int _userID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        ArrayList<Mail> mailList = new ArrayList<Mail>();
        Date date = new Date(System.currentTimeMillis());
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("SELECT * FROM mail left join equipment_instance ON mail.goods_id=equipment_instance.instance_id WHERE mail.receiver_uid = ?");
            pstm.setInt(1, _userID);
            rs = pstm.executeQuery();
            Mail mail = null;
            while (rs.next()) {
                int mailID = rs.getInt(1);
                int recevier_uid = rs.getInt(2);
                String receiver = rs.getString(3);
                String sender = rs.getString(4);
                byte mailType = rs.getByte(5);
                int money = rs.getInt(6);
                int gamePoint = rs.getInt(7);
                int goodsID = rs.getInt(8);
                short number = rs.getShort(9);
                String content = rs.getString(10);
                String title = rs.getString(11);
                int read = rs.getInt(12);
                Timestamp lastLogoutTime = rs.getTimestamp(13);
                date = new Date(lastLogoutTime.getTime());
                int social = rs.getInt(14);
                if (mailType == 0) {
                    mail = new Mail(mailID, recevier_uid, receiver, sender, mailType, money, content, title, date, (byte) social);
                } else if (1 == mailType) {
                    mail = new Mail(mailID, recevier_uid, receiver, sender, mailType, gamePoint, content, title, date, (byte) social);
                } else if (2 == mailType) {
                    mail = new Mail(mailID, recevier_uid, receiver, sender, mailType, goodsID, number, null, content, title, date, (byte) social);
                } else if (3 == mailType) {
                    int equipmentID = rs.getInt(14);
                    int creatorUserID = rs.getInt(15);
                    int ownerUserID = rs.getInt(16);
                    int currentDurabilityPoint = rs.getInt(17);
                    String genericEnhanceDesc = rs.getString(18);
                    String bloodyEnhanceDesc = rs.getString(19);
                    byte existSeal = rs.getByte(20);
                    byte isBind = rs.getByte(21);
                    EquipmentInstance instance = EquipmentFactory.getInstance().buildFromDB(creatorUserID, ownerUserID, goodsID, equipmentID, currentDurabilityPoint, existSeal, isBind);
                    EnhanceService.getInstance().parseEnhanceDesc(instance, genericEnhanceDesc, bloodyEnhanceDesc);
                    mail = new Mail(mailID, recevier_uid, receiver, sender, mailType, goodsID, number, instance, content, title, date, (byte) social);
                } else if (4 == mailType) {
                    mail = new Mail(mailID, recevier_uid, receiver, sender, mailType, 0, content, title, date, (byte) social);
                }
                if (read == 1) {
                    mail.readMail();
                }
                mailList.add(mail);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
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
            if (rs != null) {
                rs.close();
                rs = null;
            }
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
        return mailList;
    }

    private boolean insertMailToDB(final Mail _mail, final boolean _isSystemMail) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            if (!_isSystemMail) {
                pstm = conn.prepareStatement("SELECT COUNT(*) AS number FROM mail WHERE receiver_uid = ?");
                pstm.setInt(1, _mail.getReceiverUserID());
                rs = pstm.executeQuery();
                if (rs.next()) {
                    int mailNumber = rs.getInt(1);
                    if (mailNumber >= 30) {
                        return false;
                    }
                }
                pstm.close();
                pstm = null;
            }
            pstm = conn.prepareStatement("INSERT INTO mail VALUE (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            pstm.setInt(1, _mail.getID());
            pstm.setInt(2, _mail.getReceiverUserID());
            pstm.setString(3, _mail.getReceiverName());
            pstm.setString(4, _mail.getSender());
            pstm.setByte(5, _mail.getType());
            pstm.setInt(6, _mail.getMoney());
            pstm.setInt(7, _mail.getGamePoint());
            if (2 == _mail.getType()) {
                pstm.setInt(8, _mail.getSingleGoods().getID());
                pstm.setShort(9, _mail.getSingleGoodsNumber());
            } else if (3 == _mail.getType()) {
                pstm.setInt(8, _mail.getEquipment().getInstanceID());
                pstm.setShort(9, (short) 1);
            } else {
                pstm.setInt(8, 0);
                pstm.setShort(9, (short) 0);
            }
            pstm.setString(10, _mail.getContent());
            pstm.setString(11, _mail.getTitle());
            if (_mail.getReadFinish()) {
                pstm.setShort(12, (short) 1);
            } else {
                pstm.setShort(12, (short) 0);
            }
            Timestamp timestamp = new Timestamp(_mail.getDate().getTime());
            pstm.setTimestamp(13, timestamp);
            pstm.setInt(14, _mail.getSocial());
            if (pstm.executeUpdate() == 1) {
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
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
            if (rs != null) {
                rs.close();
                rs = null;
            }
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
        return false;
    }

    private void deleteMailFromDB(final int _mailID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("DELETE FROM mail WHERE mail_id = ? LIMIT 1");
            pstm.setInt(1, _mailID);
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

    private void readFinishFromDB(final int _mailID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("update mail set read_finish=1 WHERE mail_id = ? LIMIT 1");
            pstm.setInt(1, _mailID);
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

    private void removeAttachmentFromDB(final int _mailID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("update mail set type=4,money=0,freight_point=0,goods_id=0,number=0,read_finish=1 WHERE mail_id = ? LIMIT 1");
            pstm.setInt(1, _mailID);
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

    public void deleteRole(final int _userID) {
        ArrayList<Mail> mailList = this.mailDict.remove(_userID);
        if (mailList != null && mailList.size() > 0) {
            Connection conn = null;
            PreparedStatement pstm = null;
            try {
                conn = DBServiceImpl.getInstance().getConnection();
                pstm = conn.prepareStatement("DELETE FROM mail WHERE receiver_uid = ?");
                pstm.setInt(1, _userID);
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
    }
}
