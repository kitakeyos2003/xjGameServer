// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.letter;

import hero.player.HeroPlayer;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.MailStatusChanges;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import java.util.Iterator;
import hero.log.service.LogServiceImpl;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import hero.share.service.LogWriter;
import yoyo.service.tools.database.DBServiceImpl;
import java.util.TimerTask;
import java.util.Timer;
import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayList;
import java.util.HashMap;

public class LetterService {

    private HashMap<Integer, ArrayList<Letter>> letterDict;
    private static LetterService instance;
    private ReentrantLock lock;
    public static final int MAX_SIZE = 30;
    private Timer mCheckTimer;
    private int maxLetterID;
    public static final long SAVE_TIME = 604800000L;
    public static final long CHECK_TIME = 14400000L;
    private static final String SQL_OF_DELETE_ONE_LETTER = "DELETE FROM letter WHERE letter_id = ? LIMIT 1";
    private static final String SQL_OF_UPDATE_SAVE_STATUS = "UPDATE letter SET issave = 1,isread = 1 WHERE letter_id = ? LIMIT 1";
    private static final String SQL_OF_UPDATE_READ_STATUS = "UPDATE letter SET isread = 1 WHERE letter_id = ? LIMIT 1";
    private static final String SQL_OF_ADD_LETTER = "INSERT INTO letter (letter_id,title,sender,receiver_uid,receiver,content,send_time,type) VALUES (?,?,?,?,?,?,?,?)";
    private static final String SQL_OF_SELECT_ALL_LETTER = "SELECT * FROM letter order by type asc,send_time desc";
    private static final String SQL_OF_DELELE_PLAYER_LETTER = "DELETE FROM letter WHERE receiver_uid = ?";

    private LetterService() {
        this.lock = new ReentrantLock();
        this.maxLetterID = 0;
        this.letterDict = new HashMap<Integer, ArrayList<Letter>>();
        this.load();
        this.mCheckTimer = new Timer();
        CheckTask checkTask = new CheckTask();
        this.mCheckTimer.schedule(checkTask, 14400000L, 14400000L);
    }

    private void load() {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("SELECT * FROM letter order by type asc,send_time desc");
            rs = pstm.executeQuery();
            while (rs.next()) {
                int letterID = rs.getInt("letter_id");
                byte type = rs.getByte("type");
                String title = rs.getString("title");
                String senderName = rs.getString("sender");
                int receiverUserID = rs.getInt("receiver_uid");
                String receiverName = rs.getString("receiver");
                String content = rs.getString("content");
                long time = rs.getLong("send_time");
                byte isRead = rs.getByte("isread");
                byte isSave = rs.getByte("issave");
                Letter letter = new Letter(type, letterID, title, senderName, receiverUserID, receiverName, content);
                letter.sendTime = time;
                letter.isRead = (isRead == 1);
                letter.isSave = (isSave == 1);
                this.addToList(letter);
                if (this.maxLetterID < letterID) {
                    this.maxLetterID = letterID;
                }
            }
        } catch (Exception ex) {
            LogWriter.println(ex);
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
            } catch (SQLException e) {
                LogWriter.println(e);
            }
            return;
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
            } catch (SQLException e) {
                LogWriter.println(e);
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
        } catch (SQLException e) {
            LogWriter.println(e);
        }
    }

    public int getUseableLetterID() {
        try {
            this.lock.lock();
            return ++this.maxLetterID;
        } finally {
            this.lock.unlock();
        }
    }

    public static LetterService getInstance() {
        if (LetterService.instance == null) {
            LetterService.instance = new LetterService();
        }
        return LetterService.instance;
    }

    public ArrayList<Letter> getLetterList(final int _userID) {
        return this.letterDict.get(_userID);
    }

    public int getLetterNumber(final int _userID) {
        ArrayList<Letter> letterList = this.letterDict.get(_userID);
        if (letterList != null) {
            return letterList.size();
        }
        return 0;
    }

    private void addToList(final Letter _letter) {
        ArrayList<Letter> letterList = this.letterDict.get(_letter.receiverUserID);
        if (letterList == null) {
            letterList = new ArrayList<Letter>();
            this.letterDict.put(_letter.receiverUserID, letterList);
        }
        letterList.add(_letter);
    }

    public void addNewLetter(final Letter _letter) {
        ArrayList<Letter> letterList = this.letterDict.get(_letter.receiverUserID);
        if (letterList == null) {
            letterList = new ArrayList<Letter>();
            this.letterDict.put(_letter.receiverUserID, letterList);
        }
        letterList.add(_letter);
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("INSERT INTO letter (letter_id,title,sender,receiver_uid,receiver,content,send_time,type) VALUES (?,?,?,?,?,?,?,?)");
            pstm.setInt(1, _letter.letterID);
            pstm.setString(2, _letter.title);
            pstm.setString(3, _letter.senderName);
            pstm.setInt(4, _letter.receiverUserID);
            pstm.setString(5, _letter.receiverName);
            pstm.setString(6, _letter.content);
            pstm.setLong(7, _letter.sendTime);
            pstm.setInt(8, _letter.type);
            pstm.executeUpdate();
        } catch (Exception ex) {
            LogWriter.println(ex);
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
        LogServiceImpl.getInstance().letterLog(_letter.senderName, _letter.letterID, _letter.receiverName, _letter.title, _letter.content);
    }

    public boolean existsUnreadedLetter(final int _userID) {
        ArrayList<Letter> letterList = this.getLetterList(_userID);
        try {
            this.lock.lock();
            if (letterList == null || letterList.size() == 0) {
                return false;
            }
            for (final Letter l : letterList) {
                if (!l.isRead) {
                    return true;
                }
            }
            return false;
        } finally {
            this.lock.unlock();
        }
    }

    public void settingToRead(final int _userID, final int _letterID) {
        ArrayList<Letter> letterList = this.getLetterList(_userID);
        try {
            this.lock.lock();
            if (letterList != null && letterList.size() > 0) {
                for (final Letter l : letterList) {
                    if (l.letterID == _letterID) {
                        l.isRead = true;
                        Connection conn = null;
                        PreparedStatement pstm = null;
                        try {
                            conn = DBServiceImpl.getInstance().getConnection();
                            pstm = conn.prepareStatement("UPDATE letter SET isread = 1 WHERE letter_id = ? LIMIT 1");
                            pstm.setInt(1, _letterID);
                            pstm.executeUpdate();
                        } catch (Exception ex) {
                            LogWriter.println(ex);
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
                        return;
                    }
                }
            }
        } finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }

    public void settingToSaved(final int _userID, final int _letterID) {
        ArrayList<Letter> letterList = this.getLetterList(_userID);
        try {
            this.lock.lock();
            if (letterList != null && letterList.size() > 0) {
                for (final Letter l : letterList) {
                    if (l.letterID == _letterID) {
                        l.isSave = true;
                        l.isRead = true;
                        Connection conn = null;
                        PreparedStatement pstm = null;
                        try {
                            conn = DBServiceImpl.getInstance().getConnection();
                            pstm = conn.prepareStatement("UPDATE letter SET issave = 1,isread = 1 WHERE letter_id = ? LIMIT 1");
                            pstm.setInt(1, _letterID);
                            pstm.executeUpdate();
                        } catch (Exception ex) {
                            LogWriter.println(ex);
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
                        return;
                    }
                }
            }
        } finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }

    public void removeLetter(final int _userID, final int _letterID) {
        ArrayList<Letter> letterList = this.getLetterList(_userID);
        try {
            this.lock.lock();
            if (letterList != null && letterList.size() > 0) {
                for (int i = 0; i < letterList.size(); ++i) {
                    if (letterList.get(i).letterID == _letterID) {
                        letterList.remove(i);
                        Connection conn = null;
                        PreparedStatement pstm = null;
                        try {
                            conn = DBServiceImpl.getInstance().getConnection();
                            pstm = conn.prepareStatement("DELETE FROM letter WHERE letter_id = ? LIMIT 1");
                            pstm.setInt(1, _letterID);
                            pstm.executeUpdate();
                        } catch (Exception ex) {
                            LogWriter.println(ex);
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
                        return;
                    }
                }
            }
        } finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }

    public void deleteRole(final int _userID) {
        this.letterDict.remove(_userID);
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("DELETE FROM letter WHERE receiver_uid = ?");
            pstm.setInt(1, _userID);
            pstm.executeUpdate();
        } catch (Exception ex) {
            LogWriter.println(ex);
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

    private void checkTimeOut() {
        ArrayList<Integer> timeoutList = new ArrayList<Integer>();
        long nowtime = System.currentTimeMillis();
        Iterator<ArrayList<Letter>> letterListIt = this.letterDict.values().iterator();
        boolean existsUnreadedLetter = false;
        ArrayList<Integer> invalidateLetterIDList = new ArrayList<Integer>();
        while (letterListIt.hasNext()) {
            ArrayList<Letter> list = letterListIt.next();
            if (list != null) {
                int receiverUserID = list.get(0).receiverUserID;
                for (int i = 0; i < list.size(); ++i) {
                    try {
                        Letter letter = list.get(i);
                        if (!letter.isSave && nowtime - letter.sendTime >= 604800000L) {
                            list.remove(i);
                            invalidateLetterIDList.add(letter.letterID);
                            --i;
                            timeoutList.add(letter.letterID);
                        } else if (!letter.isRead) {
                            existsUnreadedLetter = true;
                        }
                    } catch (Exception e) {
                        return;
                    }
                }
                if (existsUnreadedLetter) {
                    continue;
                }
                HeroPlayer receiver = PlayerServiceImpl.getInstance().getPlayerByUserID(receiverUserID);
                if (receiver == null || !receiver.isEnable()) {
                    continue;
                }
                ResponseMessageQueue.getInstance().put(receiver.getMsgQueueIndex(), new MailStatusChanges(MailStatusChanges.TYPE_OF_LETTER, false));
            }
        }
        this.delete(invalidateLetterIDList);
    }

    private void delete(final ArrayList<Integer> _letterIDList) {
        if (_letterIDList != null && _letterIDList.size() > 0) {
            Connection conn = null;
            PreparedStatement pstm = null;
            try {
                conn = DBServiceImpl.getInstance().getConnection();
                conn.setAutoCommit(false);
                pstm = conn.prepareStatement("DELETE FROM letter WHERE letter_id = ? LIMIT 1");
                for (final int letterID : _letterIDList) {
                    pstm.setInt(1, letterID);
                    pstm.addBatch();
                }
                pstm.executeBatch();
                conn.commit();
                conn.setAutoCommit(true);
            } catch (Exception ex) {
                LogWriter.println(ex);
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

    class CheckTask extends TimerTask {

        @Override
        public void run() {
            LetterService.this.checkTimeOut();
        }
    }
}
