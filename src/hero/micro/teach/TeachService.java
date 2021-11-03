// 
// Decompiled by Procyon v0.5.36
// 
package hero.micro.teach;

import java.util.Timer;
import hero.group.Group;
import hero.player.service.PlayerDAO;
import hero.log.service.CauseLog;
import hero.item.service.GoodsServiceImpl;
import hero.share.message.FullScreenTip;
import java.sql.Timestamp;
import hero.share.message.MailStatusChanges;
import hero.share.letter.Letter;
import hero.share.letter.LetterService;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import hero.player.HeroPlayer;
import javolution.util.FastMap;
import org.apache.log4j.Logger;

public class TeachService {

    private static Logger log;
    private static FastMap<Integer, MasterApprentice> masterApprenticeTable;
    private static FastMap<String, MasterApprentice> masterApprenticeOffLineList;
    private static final int waintingtime = 20;
    private static final int FINISHE_STUDY_LEVEL = 40;
    private static final int CAN_LEVEL_NOTENOUGH = 30;
    private static final long LAST_LEFT_MASTER_DISTANCE = 259200L;
    private static final String DEFAULT_LEFT_MASTER_TIME = "2011-01-01 00:00:00";
    private static final int LEVEL_DIFFERENCE_NOTENOUGH = 10;
    private static final int[] TEACH_EXP_DATA;
    private static final int[] TEACH_MONEY_DATA;
    private static final int[][] TEACH_GOODS_DATA;
    private static final String[] TEACH_TALK_CONTENT;
    private static final String[] TEACH_AWARDS;

    static {
        TeachService.log = Logger.getLogger((Class) TeachService.class);
        TeachService.masterApprenticeTable = (FastMap<Integer, MasterApprentice>) new FastMap();
        TeachService.masterApprenticeOffLineList = (FastMap<String, MasterApprentice>) new FastMap();
        TEACH_EXP_DATA = new int[]{0, 2000, 4000, 8000};
        TEACH_MONEY_DATA = new int[]{0, 20000, 40000, 80000};
        TEACH_GOODS_DATA = new int[][]{{0, 1}, new int[2], {551, 2}, {551, 3}};
        TEACH_TALK_CONTENT = new String[]{"\u7b2c\u4e00\u6b21\u88ab\u73b0\u5728\u7684\u5e08\u5085\u6388\u4e88\u77e5\u8bc6\u3002\n\n\u867d\u7136\u8eab\u5728\u4e71\u4e16\uff0c\u4f46\u6211\u4eec\u4e0d\u80fd\u53ea\u61c2\u5f97\u6253\u6253\u6740\u6740\uff0c\u751f\u6d3b\u6280\u5de7\u7684\u638c\u63e1\u5bf9\u6211\u4eec\u540c\u6837\u91cd\u8981\uff0c\u5728\u5404\u4e2a\u57ce\u5e02\u4e2d\u90fd\u6709\u91c7\u96c6\u548c\u5236\u4f5c\u8bad\u7ec3\u5e08\uff0c\u4ed6\u4eec\u4f1a\u8ba9\u6211\u4eec\u4e60\u5f97\u4e30\u5bcc\u7684\u751f\u6d3b\u6280\u5de7\u3002\n\n\u5982\u679c\u88ab\u5e08\u5085\u591a\u6b21\u6388\u4e88\u77e5\u8bc6\uff0c\u53ef\u4ee5\u5f97\u5230\u989d\u5916\u7684\u5956\u52b1\uff0c\u6bcf\u5341\u7ea7\u53ef\u4ee5\u88ab\u5e08\u5085\u6388\u4e88\u4e00\u6b21\u77e5\u8bc6\u3002", "\u7b2c\u4e8c\u6b21\u88ab\u73b0\u5728\u7684\u5e08\u5085\u6388\u4e88\u77e5\u8bc6\u3002\n\n\u5728\u90a3\u4e9b\u9b54\u7269\u51f6\u731b\u7684\u533a\u57df\uff0c\u4f60\u4f1a\u4f53\u4f1a\u5230\u56e2\u7ed3\u7684\u529b\u91cf\uff0c\u5206\u5de5\u660e\u786e\u7684\u5c0f\u961f\u53ef\u4ee5\u8ba9\u6211\u4eec\u6218\u65e0\u4e0d\u80dc\uff0c\u8fd9\u4e2a\u961f\u4f0d\u4e2d\u9700\u8981\u4e00\u4e2a\u9632\u5fa1\u8005\uff0c\u4e00\u4e2a\u6cbb\u6108\u8005\u548c\u4e09\u4e2a\u5584\u4e8e\u653b\u51fb\u7684\u5bb6\u4f19\u3002\n\n\u83b7\u5f97\u4e86\u7ecf\u9a8c\uff1a2000", "\u7b2c\u4e09\u6b21\u88ab\u73b0\u5728\u7684\u5e08\u5085\u6388\u4e88\u77e5\u8bc6\u3002\n\n\u5f92\u5f1f\uff0c\u770b\u7740\u4f60\u9010\u6e10\u6210\u957f\u8d77\u6765\u4e3a\u5e08\u975e\u5e38\u9ad8\u5174\uff0c\u53ef\u80fd\u4f60\u6b63\u5728\u5f77\u5fa8\u89c9\u5f97\u7ecf\u9a8c\u7684\u63d0\u5347\u8d8a\u53d1\u7684\u56f0\u96be\uff0c\u4f46\u4f60\u8981\u8bb0\u4f4f\u575a\u6301\u624d\u80fd\u80dc\u5229\uff0c\u4e0d\u8981\u653e\u8fc7\u6bcf\u4e00\u4e2a\u4efb\u52a1\uff0c\u5b83\u4eec\u80fd\u4fc3\u8fdb\u4f60\u7684\u6210\u957f\u3002\n\n\u83b7\u5f97\u4e86\u7ecf\u9a8c\uff1a4000", "\u7b2c\u56db\u6b21\u88ab\u73b0\u5728\u7684\u5e08\u5085\u6388\u4e88\u77e5\u8bc6\u3002\n\n\u53ef\u80fd\u4f60\u5df2\u7ecf\u6ce8\u610f\u5230\u4e86\uff0c\u5728\u8fdb\u5165\u90a3\u4e9b\u51f6\u731b\u602a\u533a\u7684\u65f6\u5019\u6709\u4e00\u4e2a\u201c\u56f0\u96be\u201d\u7684\u9009\u62e9\uff0c\u90a3\u662f\u56e0\u4e3a\u91cc\u9762\u7684\u602a\u7269\u5f02\u5e38\u5f3a\u5927\uff0c\u6211\u4eec\u9700\u8981\u5728\u4e94\u5341\u7ea7\u7684\u65f6\u5019\u624d\u80fd\u4e0e\u4e4b\u6297\u8861\uff0c\u800c\u4f5c\u4e3a\u5956\u52b1\u6211\u4eec\u53ef\u4ee5\u4ece\u5b83\u4eec\u8eab\u4e0a\u5f97\u5230\u5723\u5668\uff0c\u5bf9\uff0c\u5c31\u662f\u90a3\u4e9b\u5929\u795e\u4f7f\u7528\u8fc7\u7684\u5175\u5668\u3002\n\n\u83b7\u5f97\u4e86\u7ecf\u9a8c\uff1a8000"};
        TEACH_AWARDS = new String[]{"\u7b2c\u4e00\u6b21\u6388\u4e88\u8be5\u5f92\u5f1f\u77e5\u8bc6\u3002\n\n\u7ef4\u6301\u4e0e\u5f92\u5f1f\u7684\u5173\u7cfb\uff0c\u591a\u6b21\u8fdb\u884c\u77e5\u8bc6\u6388\u4e88\u4f1a\u5f97\u5230\u989d\u5916\u7684\u5956\u52b1\u3002\n\n\u5f92\u5f1f\u6bcf\u5341\u7ea7\u53ef\u4ee5\u63a5\u53d7\u4e00\u6b21\u77e5\u8bc6\u6388\u4e88\u3002\n\n\u83b7\u53d6\u7269\u54c1\uff1a\u4e0a\u53e4\u5370\u8bb0 x 1", "\u7b2c\u4e8c\u6b21\u6388\u4e88\u8be5\u5f92\u5f1f\u77e5\u8bc6\u3002\n\n\u83b7\u5f97\u91d1\u94b1\uff1a20000", "\u7b2c\u4e09\u6b21\u6388\u4e88\u8be5\u5f92\u5f1f\u77e5\u8bc6\u3002\n\n\u83b7\u5f97\u91d1\u94b1\uff1a40000\n\u83b7\u5f97\u7269\u54c1\uff1a\u4e0a\u53e4\u5370\u8bb0 x 2", "\u7b2c\u56db\u6b21\u6388\u4e88\u8be5\u5f92\u5f1f\u77e5\u8bc6\u3002\n\n\u83b7\u5f97\u91d1\u94b1\uff1a80000\n\u83b7\u5f97\u7269\u54c1\uff1a\u4e0a\u53e4\u5370\u8bb0 x 3"};
    }

    private TeachService() {
    }

    public static void login(final HeroPlayer _player) {
        MasterApprentice masterApprentice = (MasterApprentice) TeachService.masterApprenticeTable.get(_player.getUserID());
        if (masterApprentice == null) {
            masterApprentice = new MasterApprentice();
            TeachDAO.loadMasterApprenticeRelation(_player.getUserID(), masterApprentice);
            TeachService.masterApprenticeTable.put(_player.getUserID(), masterApprentice);
        }
        HeroPlayer master = PlayerServiceImpl.getInstance().getPlayerByUserID(masterApprentice.masterUserID);
        TeachService.log.debug(("teach login _player=" + _player.getUserID() + ", master id=" + masterApprentice.masterUserID));
        if (master != null) {
            TeachService.log.debug(("_player is apprentice , master is online.. " + _player.getName()));
            _player.changeExperienceModulus(0.02f);
            masterApprentice.masterIsOnline = true;
        }
        MasterApprentice relationOfMaster = (MasterApprentice) TeachService.masterApprenticeTable.get(masterApprentice.masterUserID);
        if (relationOfMaster != null) {
            TeachService.log.debug(("relationOfMaster login = " + relationOfMaster.getApprenticeOnlineNumber()));
            relationOfMaster.changeApprenticeStatus(_player.getUserID(), true);
            TeachService.log.debug(("relationOfMaster login after = " + relationOfMaster.getApprenticeOnlineNumber()));
        }
        if (masterApprentice.apprenticeList != null && masterApprentice.apprenticeNumber > 0) {
            TeachService.log.debug(("master login apprenticeNumber = " + masterApprentice.apprenticeNumber));
            TeachService.log.debug(("master login apprenticeOnLineNumber = " + masterApprentice.getApprenticeOnlineNumber()));
            MasterApprentice.ApprenticeInfo[] apprenticeList;
            for (int length = (apprenticeList = masterApprentice.apprenticeList).length, i = 0; i < length; ++i) {
                MasterApprentice.ApprenticeInfo apprenticeInfo = apprenticeList[i];
                TeachService.log.debug(("apprenticeInfo = " + apprenticeInfo));
                if (apprenticeInfo == null) {
                    break;
                }
                MasterApprentice relationOfApprentice = (MasterApprentice) TeachService.masterApprenticeTable.get(apprenticeInfo.userID);
                if (relationOfApprentice != null) {
                    relationOfApprentice.masterIsOnline = true;
                    HeroPlayer apprentice = PlayerServiceImpl.getInstance().getPlayerByUserID(apprenticeInfo.userID);
                    if (apprentice != null) {
                        if (masterApprentice.getApprenticeOnlineNumber() < 5) {
                            masterApprentice.addApprenticeOnlineNumber(true);
                        }
                        apprentice.changeExperienceModulus(0.02f);
                        apprenticeInfo.isOnline = true;
                    }
                }
            }
        }
    }

    public static void logout(final int _userID) {
        MasterApprentice masterApprentice = (MasterApprentice) TeachService.masterApprenticeTable.get(_userID);
        if (masterApprentice != null) {
            HeroPlayer master = PlayerServiceImpl.getInstance().getPlayerByUserID(masterApprentice.masterUserID);
            MasterApprentice relationOfMaster = (MasterApprentice) TeachService.masterApprenticeTable.get(masterApprentice.masterUserID);
            if (relationOfMaster != null) {
                TeachService.log.debug(("user logout master online apprent number = " + relationOfMaster.getApprenticeOnlineNumber()));
                relationOfMaster.changeApprenticeStatus(_userID, false);
                TeachService.log.debug(("user logout master online apprent number after= " + relationOfMaster.getApprenticeOnlineNumber()));
            }
            if (masterApprentice.apprenticeList != null && masterApprentice.apprenticeNumber > 0) {
                MasterApprentice.ApprenticeInfo[] apprenticeList;
                for (int length = (apprenticeList = masterApprentice.apprenticeList).length, i = 0; i < length; ++i) {
                    MasterApprentice.ApprenticeInfo apprenticeInfo = apprenticeList[i];
                    if (apprenticeInfo == null) {
                        break;
                    }
                    MasterApprentice relationOfApprentice = (MasterApprentice) TeachService.masterApprenticeTable.get(apprenticeInfo.userID);
                    if (relationOfApprentice != null) {
                        relationOfApprentice.masterIsOnline = false;
                        HeroPlayer apprentice = PlayerServiceImpl.getInstance().getPlayerByUserID(apprenticeInfo.userID);
                        if (apprentice != null) {
                            apprentice.changeExperienceModulus(-0.02f);
                        }
                    }
                }
            }
        }
    }

    public static void clear(final int _userID) {
        TeachService.masterApprenticeTable.remove(_userID);
    }

    public static void dismissAll(final int _userID) {
        MasterApprentice master = get(_userID);
        TeachService.log.debug(("dissmissAll master = " + master));
        if (master != null) {
            HeroPlayer masterPlayer = PlayerServiceImpl.getInstance().getPlayerByUserID(_userID);
            TeachService.log.debug(("dismiss all apprent , master = " + masterPlayer.getName()));
            MasterApprentice.ApprenticeInfo[] apprenticeList = master.apprenticeList;
            TeachService.log.debug(("apprenticeList size = " + apprenticeList.length));
            if (TeachDAO.deleteAllMasterApprenticeRelation(_userID)) {
                MasterApprentice.ApprenticeInfo[] array;
                for (int length = (array = apprenticeList).length, i = 0; i < length; ++i) {
                    MasterApprentice.ApprenticeInfo apprentice = array[i];
                    if (apprentice != null) {
                        TeachService.log.debug("deleted all master apprentice relation...");
                        MasterApprentice apprenticeRelation = (MasterApprentice) TeachService.masterApprenticeTable.get(apprentice.userID);
                        if (apprenticeRelation != null && apprenticeRelation.masterUserID == _userID) {
                            apprenticeRelation.leftMaster();
                            if (!apprenticeRelation.isValidate()) {
                                TeachService.masterApprenticeTable.remove(apprentice.userID);
                            }
                        }
                        HeroPlayer apprenticePlayer = PlayerServiceImpl.getInstance().getPlayerByUserID(apprentice.userID);
                        if (apprentice.isOnline) {
                            ResponseMessageQueue.getInstance().put(apprenticePlayer.getMsgQueueIndex(), new Warning(String.valueOf(masterPlayer.getName()) + "\u5df2\u4e0e\u60a8\u89e3\u9664\u5e08\u5f92\u5173\u7cfb"));
                        }
                        Letter letter = new Letter((byte) 0, LetterService.getInstance().getUseableLetterID(), "\u7cfb\u7edf", "\u7cfb\u7edf", apprenticePlayer.getUserID(), apprenticePlayer.getName(), String.valueOf(masterPlayer.getName()) + "\u5df2\u4e0e\u60a8\u89e3\u9664\u5e08\u5f92\u5173\u7cfb");
                        LetterService.getInstance().addNewLetter(letter);
                        if (apprentice.isOnline) {
                            ResponseMessageQueue.getInstance().put(apprenticePlayer.getMsgQueueIndex(), new MailStatusChanges(MailStatusChanges.TYPE_OF_LETTER, true));
                        }
                    }
                }
                ResponseMessageQueue.getInstance().put(masterPlayer.getMsgQueueIndex(), new Warning("\u60a8\u5df2\u9063\u6563\u6240\u6709\u5f92\u5f1f"));
                TeachService.masterApprenticeTable.remove(_userID);
                master.dismissAll();
                Letter letter2 = new Letter((byte) 0, LetterService.getInstance().getUseableLetterID(), "\u7cfb\u7edf", "\u7cfb\u7edf", master.masterUserID, masterPlayer.getName(), "\u60a8\u5df2\u9063\u6563\u6240\u6709\u5f92\u5f1f");
                LetterService.getInstance().addNewLetter(letter2);
                ResponseMessageQueue.getInstance().put(masterPlayer.getMsgQueueIndex(), new MailStatusChanges(MailStatusChanges.TYPE_OF_LETTER, true));
            }
        }
        TeachService.log.debug("dismissAll end ...");
    }

    public static MasterApprentice get(final int _userID) {
        return (MasterApprentice) TeachService.masterApprenticeTable.get(_userID);
    }

    public static MasterApprentice getOffLineMasterApprentice(final String _userName) {
        MasterApprentice ma = (MasterApprentice) TeachService.masterApprenticeOffLineList.get(_userName);
        if (ma == null) {
            TeachService.log.debug(("\u4ece\u6570\u636e\u5e93\u91cc\u52a0\u8f7d\u4e0d\u5728\u7ebf\u73a9\u5bb6\u7684\u5e08\u5f92\u5173\u7cfb _username= " + _userName));
            ma = new MasterApprentice();
            ma = TeachDAO.loadMasterApprenticeRelationByName(_userName, ma);
            if (ma != null) {
                TeachService.masterApprenticeOffLineList.put(_userName, ma);
            }
        }
        return ma;
    }

    public static boolean authenRecruitAppr(final HeroPlayer _master, final HeroPlayer _apprentice) {
        if (_master == null || _apprentice == null) {
            return false;
        }
        if (_master.getLevel() < 30) {
            ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(), new Warning("\u62d2\u7edd\u6536\u60a8\u4e3a\u5f92"));
            return false;
        }
        if (_master.getLevel() - _apprentice.getLevel() < 10) {
            ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(), new Warning("\u76f8\u5dee10\u7ea7\u624d\u53ef\u4ee5\u6210\u4e3a\u5e08\u5f92"));
            return false;
        }
        if (_master.getClan() != _apprentice.getClan()) {
            ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(), new Warning("\u540c\u79cd\u65cf\u624d\u53ef\u4ee5\u6210\u4e3a\u5e08\u5f92"));
            return false;
        }
        MasterApprentice apprenticeRelation = (MasterApprentice) TeachService.masterApprenticeTable.get(_apprentice.getUserID());
        if (apprenticeRelation != null && apprenticeRelation.masterUserID > 0) {
            ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u5df2\u6709\u5e08\u5085"));
            return false;
        }
        MasterApprentice masterRelation = (MasterApprentice) TeachService.masterApprenticeTable.get(_master.getUserID());
        if (masterRelation != null && 5 <= masterRelation.apprenticeNumber) {
            ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(), new Warning("\u5f92\u5f1f\u6570\u91cf\u5df2\u6ee1"));
            return false;
        }
        return true;
    }

    public static boolean authenFollowMaster(final HeroPlayer _apprentice, final HeroPlayer _master) {
        if (_master == null || _apprentice == null) {
            return false;
        }
        if (_apprentice.leftMasterTime > Timestamp.valueOf("2011-01-01 00:00:00").getTime()) {
            long distance = System.currentTimeMillis() - _apprentice.leftMasterTime;
            if (distance - 259200L > 0L) {
                ResponseMessageQueue.getInstance().put(_apprentice.getMsgQueueIndex(), new Warning("\u8ddd\u79bb\u60a8\u4e0a\u6b21\u89e3\u9664\u5e08\u5f92\u5173\u7cfb\u672a\u6ee1 3 \u5929,\u4e0d\u80fd\u62dc\u5e08"));
                return false;
            }
        }
        if (_master.getLevel() < 30) {
            ResponseMessageQueue.getInstance().put(_apprentice.getMsgQueueIndex(), new Warning("\u62d2\u7edd\u6536\u60a8\u4e3a\u5f92"));
            return false;
        }
        if (_master.getLevel() - _apprentice.getLevel() < 10) {
            ResponseMessageQueue.getInstance().put(_apprentice.getMsgQueueIndex(), new Warning("\u76f8\u5dee10\u7ea7\u624d\u53ef\u4ee5\u6210\u4e3a\u5e08\u5f92"));
            return false;
        }
        if (_master.getClan() != _apprentice.getClan()) {
            ResponseMessageQueue.getInstance().put(_apprentice.getMsgQueueIndex(), new Warning("\u540c\u79cd\u65cf\u624d\u53ef\u4ee5\u6210\u4e3a\u5e08\u5f92"));
            return false;
        }
        MasterApprentice masterRelation = (MasterApprentice) TeachService.masterApprenticeTable.get(_master.getUserID());
        if (masterRelation != null && 5 <= masterRelation.apprenticeNumber) {
            ResponseMessageQueue.getInstance().put(_apprentice.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u5f92\u5f1f\u5df2\u6ee1"));
            return false;
        }
        MasterApprentice apprenticeRelation = (MasterApprentice) TeachService.masterApprenticeTable.get(_apprentice.getUserID());
        if (apprenticeRelation != null && apprenticeRelation.masterUserID > 0) {
            ResponseMessageQueue.getInstance().put(_apprentice.getMsgQueueIndex(), new Warning("\u60a8\u5df2\u6709\u5e08\u5085"));
            return false;
        }
        return true;
    }

    public static void recruitApprentice(final HeroPlayer _master, final HeroPlayer _apprentice) {
        if (_master != null && _apprentice != null) {
            if (_master.getLevel() < 30) {
                ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(), new Warning("\u62d2\u7edd\u6536\u60a8\u4e3a\u5f92"));
                return;
            }
            if (_master.getLevel() - _apprentice.getLevel() < 10) {
                ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(), new Warning("\u76f8\u5dee10\u7ea7\u624d\u53ef\u4ee5\u6210\u4e3a\u5e08\u5f92"));
                return;
            }
            if (_master.getClan() != _apprentice.getClan()) {
                ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(), new Warning("\u540c\u79cd\u65cf\u624d\u53ef\u4ee5\u6210\u4e3a\u5e08\u5f92"));
                return;
            }
            MasterApprentice apprenticeRelation = (MasterApprentice) TeachService.masterApprenticeTable.get(_apprentice.getUserID());
            if (apprenticeRelation != null && apprenticeRelation.masterUserID > 0) {
                ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u5df2\u6709\u5e08\u5085"));
                return;
            }
            MasterApprentice masterRelation = (MasterApprentice) TeachService.masterApprenticeTable.get(_master.getUserID());
            if (masterRelation != null && 5 <= masterRelation.apprenticeNumber) {
                ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(), new Warning("\u5f92\u5f1f\u6570\u91cf\u5df2\u6ee1"));
                ResponseMessageQueue.getInstance().put(_apprentice.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u5f92\u5f1f\u5df2\u6ee1"));
                return;
            }
            if (TeachDAO.insertMasterApprentice(_apprentice.getUserID(), _apprentice.getName(), _master.getUserID(), _master.getName())) {
                if (masterRelation == null) {
                    masterRelation = new MasterApprentice();
                    TeachService.masterApprenticeTable.put(_master.getUserID(), masterRelation);
                }
                masterRelation.addNewApprenticer(_apprentice.getUserID(), _apprentice.getName());
                if (apprenticeRelation == null) {
                    apprenticeRelation = new MasterApprentice();
                    TeachService.masterApprenticeTable.put(_apprentice.getUserID(), apprenticeRelation);
                }
                apprenticeRelation.setMaster(_master.getUserID(), _master.getName(), true);
                ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(), new Warning(String.valueOf(_apprentice.getName()) + "\u6210\u4e3a\u60a8\u7684\u5f92\u5f1f"));
                ResponseMessageQueue.getInstance().put(_apprentice.getMsgQueueIndex(), new Warning(String.valueOf(_master.getName()) + "\u5df2\u6210\u4e3a\u60a8\u7684\u5e08\u5085"));
            }
        }
    }

    public static void followMaster(final HeroPlayer _apprentice, final HeroPlayer _master) {
        if (_apprentice != null && _master != null) {
            if (_master.getLevel() < 30) {
                ResponseMessageQueue.getInstance().put(_apprentice.getMsgQueueIndex(), new Warning("\u62d2\u7edd\u6536\u60a8\u4e3a\u5f92"));
                return;
            }
            if (_master.getLevel() - _apprentice.getLevel() < 10) {
                ResponseMessageQueue.getInstance().put(_apprentice.getMsgQueueIndex(), new Warning("\u76f8\u5dee10\u7ea7\u624d\u53ef\u4ee5\u6210\u4e3a\u5e08\u5f92"));
                return;
            }
            if (_master.getClan() != _apprentice.getClan()) {
                ResponseMessageQueue.getInstance().put(_apprentice.getMsgQueueIndex(), new Warning("\u540c\u79cd\u65cf\u624d\u53ef\u4ee5\u6210\u4e3a\u5e08\u5f92"));
                return;
            }
            MasterApprentice masterRelation = (MasterApprentice) TeachService.masterApprenticeTable.get(_master.getUserID());
            if (masterRelation != null && 5 <= masterRelation.apprenticeNumber) {
                ResponseMessageQueue.getInstance().put(_apprentice.getMsgQueueIndex(), new Warning("\u5bf9\u65b9\u5f92\u5f1f\u5df2\u6ee1"));
                ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(), new Warning("\u5f92\u5f1f\u6570\u91cf\u5df2\u6ee1"));
                return;
            }
            MasterApprentice apprenticeRelation = (MasterApprentice) TeachService.masterApprenticeTable.get(_apprentice.getUserID());
            if (apprenticeRelation != null && apprenticeRelation.masterUserID > 0) {
                ResponseMessageQueue.getInstance().put(_apprentice.getMsgQueueIndex(), new Warning("\u60a8\u5df2\u6709\u5e08\u5085"));
                return;
            }
            if (TeachDAO.insertMasterApprentice(_apprentice.getUserID(), _apprentice.getName(), _master.getUserID(), _master.getName())) {
                if (masterRelation == null) {
                    masterRelation = new MasterApprentice();
                    TeachService.masterApprenticeTable.put(_master.getUserID(), masterRelation);
                }
                masterRelation.addNewApprenticer(_apprentice.getUserID(), _apprentice.getName());
                if (apprenticeRelation == null) {
                    apprenticeRelation = new MasterApprentice();
                    TeachService.masterApprenticeTable.put(_apprentice.getUserID(), apprenticeRelation);
                }
                apprenticeRelation.setMaster(_master.getUserID(), _master.getName(), true);
                ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(), new Warning(String.valueOf(_apprentice.getName()) + "\u6210\u4e3a\u60a8\u7684\u5f92\u5f1f"));
                ResponseMessageQueue.getInstance().put(_apprentice.getMsgQueueIndex(), new Warning(String.valueOf(_master.getName()) + "\u5df2\u6210\u4e3a\u60a8\u7684\u5e08\u5085"));
            }
        }
    }

    public static int getMasterAddMoney(final HeroPlayer player) {
        int money = 0;
        MasterApprentice masterApprentice = (MasterApprentice) TeachService.masterApprenticeTable.get(player.getUserID());
        if (masterApprentice != null && masterApprentice.getApprenticeOnlineNumber() > 0) {
            for (int i = 0; i < masterApprentice.getApprenticeOnlineNumber(); ++i) {
                ++money;
            }
        }
        return money;
    }

    public static void teachKnowledge(final HeroPlayer _master, final HeroPlayer _apprentice) {
        TeachService.log.debug("\u6388\u77e5\u8bc6 teachKonwledge ...");
        if (_apprentice == null) {
            ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(), new Warning("\u8be5\u5f92\u5f1f\u4e0d\u5728\u7ebf"));
            return;
        }
        MasterApprentice masterRelation = (MasterApprentice) TeachService.masterApprenticeTable.get(_master.getUserID());
        if (masterRelation != null) {
            if (_master.where() != _apprentice.where()) {
                ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(), new Warning("\u60a8\u4eec\u76f8\u8ddd\u592a\u8fdc"));
                return;
            }
            byte authenResult = masterRelation.authenTeachCondition(_apprentice.getUserID(), _apprentice.getLevel());
            if (-2 == authenResult) {
                ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(), new Warning("\u5f53\u524d\u7b49\u7ea7\u6bb5\u5df2\u6388\u4e88\u8fc7\u77e5\u8bc6\u4e86"));
            } else if (-1 == authenResult) {
                ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(), new Warning("\u5f92\u5f1f\u672a\u8fbe\u523010\u7ea7"));
            } else if (authenResult >= 0) {
                if (TeachService.TEACH_GOODS_DATA[authenResult][0] != 0 && _master.getInventory().getMaterialBag().getEmptyGridNumber() == 0) {
                    ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(), new Warning("\u6750\u6599\u80cc\u5305\u6ca1\u5730\u513f\u4e86\u5440"));
                    return;
                }
                byte timesOfTeach = masterRelation.teachKnowledge(_apprentice.getUserID(), _apprentice.getLevel());
                if (TeachDAO.changeMasterApprentice(_apprentice.getUserID(), timesOfTeach, _apprentice.getLevel())) {
                    ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(), new FullScreenTip("\u6388\u4e88\u5f92\u5f1f\u77e5\u8bc6", TeachService.TEACH_AWARDS[authenResult]));
                    GoodsServiceImpl.getInstance().addGoods2Package(_master, TeachService.TEACH_GOODS_DATA[authenResult][0], TeachService.TEACH_GOODS_DATA[authenResult][1], CauseLog.TEACH);
                    PlayerServiceImpl.getInstance().addMoney(_master, TeachService.TEACH_MONEY_DATA[authenResult], 1.0f, 0, "\u77e5\u8bc6\u6388\u4e88");
                    ResponseMessageQueue.getInstance().put(_apprentice.getMsgQueueIndex(), new FullScreenTip("\u5e08\u5085\u6388\u4e88\u60a8\u77e5\u8bc6", TeachService.TEACH_TALK_CONTENT[authenResult]));
                } else {
                    ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(), new Warning("\u77e5\u8bc6\u6388\u4e88\u5931\u8d25"));
                }
            } else {
                ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(), new Warning("\u77e5\u8bc6\u6388\u4e88\u5931\u8d25"));
            }
        }
    }

    public static boolean leftMaster(final HeroPlayer _apprentice) {
        TeachService.log.debug(("left master _apprentice=" + _apprentice + " , ma table size = " + TeachService.masterApprenticeTable.size()));
        try {
            MasterApprentice apprenticeRelation = (MasterApprentice) TeachService.masterApprenticeTable.get(_apprentice.getUserID());
            TeachService.log.debug(("left master apprenticeRelation = " + apprenticeRelation));
            if (apprenticeRelation != null && apprenticeRelation.masterUserID != 0) {
                String masterName = apprenticeRelation.masterName;
                int masterUserID = apprenticeRelation.masterUserID;
                TeachService.log.debug(("left master name = " + apprenticeRelation.masterName));
                MasterApprentice masterRelation = (MasterApprentice) TeachService.masterApprenticeTable.get(apprenticeRelation.masterUserID);
                apprenticeRelation.leftMaster();
                if (!apprenticeRelation.isValidate()) {
                    TeachService.log.debug("masterApprenticeTable remove apprentice ...");
                    TeachService.masterApprenticeTable.remove(_apprentice.getUserID());
                }
                TeachDAO.deleteMasterApprentice(_apprentice.getUserID());
                PlayerServiceImpl.getInstance().updateLeftMasterTime(_apprentice);
                ResponseMessageQueue.getInstance().put(_apprentice.getMsgQueueIndex(), new Warning("\u60a8\u5df2\u4e0e" + masterName + "\u89e3\u9664\u5e08\u5f92\u5173\u7cfb"));
                HeroPlayer master = PlayerServiceImpl.getInstance().getPlayerByUserID(masterUserID);
                if (masterRelation != null && masterRelation.removeApprenticer(_apprentice.getUserID()) != null) {
                    if (!apprenticeRelation.isValidate()) {
                        TeachService.log.debug("masterApprenticeTable remove master ...");
                        TeachService.masterApprenticeTable.remove(apprenticeRelation.masterUserID);
                    }
                    if (master != null) {
                        masterRelation.addApprenticeOnlineNumber(false);
                    }
                }
                Letter letter = new Letter((byte) 0, LetterService.getInstance().getUseableLetterID(), "\u7cfb\u7edf", "\u7cfb\u7edf", masterUserID, masterName, String.valueOf(_apprentice.getName()) + "\u5df2\u4e0e\u60a8\u89e3\u9664\u5e08\u5f92\u5173\u7cfb");
                LetterService.getInstance().addNewLetter(letter);
                if (master != null && master.isEnable()) {
                    ResponseMessageQueue.getInstance().put(master.getMsgQueueIndex(), new Warning(String.valueOf(_apprentice.getName()) + "\u5df2\u4e0e\u60a8\u89e3\u9664\u5e08\u5f92\u5173\u7cfb"));
                    ResponseMessageQueue.getInstance().put(master.getMsgQueueIndex(), new MailStatusChanges(MailStatusChanges.TYPE_OF_LETTER, true));
                }
                return true;
            }
        } catch (Exception e) {
            TeachService.log.error("left master error  : ", (Throwable) e);
        }
        return false;
    }

    public static boolean reduceApprentice(final HeroPlayer _master, final int _apprenticeUserID) {
        MasterApprentice masterRelation = (MasterApprentice) TeachService.masterApprenticeTable.get(_master.getUserID());
        if (masterRelation != null) {
            String apprenticeName = masterRelation.removeApprenticer(_apprenticeUserID);
            TeachService.log.debug(("remove apprenticer = " + apprenticeName));
            if (apprenticeName != null) {
                if (!masterRelation.isValidate()) {
                    TeachService.masterApprenticeTable.remove(_master.getUserID());
                }
                TeachDAO.deleteMasterApprentice(_apprenticeUserID);
                ResponseMessageQueue.getInstance().put(_master.getMsgQueueIndex(), new Warning("\u60a8\u5df2\u4e0e" + apprenticeName + "\u89e3\u9664\u5e08\u5f92\u5173\u7cfb"));
                MasterApprentice apprenticeRelation = (MasterApprentice) TeachService.masterApprenticeTable.get(_apprenticeUserID);
                if (apprenticeRelation != null && apprenticeRelation.masterUserID == _master.getUserID()) {
                    apprenticeRelation.leftMaster();
                    HeroPlayer apprentor = PlayerServiceImpl.getInstance().getPlayerByUserID(_apprenticeUserID);
                    if (apprentor != null) {
                        masterRelation.changeApprenticeStatus(_apprenticeUserID, false);
                    }
                    if (!apprenticeRelation.isValidate()) {
                        TeachService.masterApprenticeTable.remove(_apprenticeUserID);
                    }
                }
                Letter letter = new Letter((byte) 0, LetterService.getInstance().getUseableLetterID(), "\u7cfb\u7edf", "\u7cfb\u7edf", _apprenticeUserID, apprenticeName, String.valueOf(_master.getName()) + "\u5df2\u4e0e\u60a8\u89e3\u9664\u5e08\u5f92\u5173\u7cfb");
                LetterService.getInstance().addNewLetter(letter);
                HeroPlayer apprentice = PlayerServiceImpl.getInstance().getPlayerByUserID(_apprenticeUserID);
                if (apprentice != null && apprentice.isEnable()) {
                    ResponseMessageQueue.getInstance().put(apprentice.getMsgQueueIndex(), new Warning(String.valueOf(_master.getName()) + "\u5df2\u4e0e\u60a8\u89e3\u9664\u5e08\u5f92\u5173\u7cfb"));
                    ResponseMessageQueue.getInstance().put(apprentice.getMsgQueueIndex(), new MailStatusChanges(MailStatusChanges.TYPE_OF_LETTER, true));
                }
                return true;
            }
        }
        return false;
    }

    public static void finishedStudy(final HeroPlayer _apprentice) {
        MasterApprentice apprenticeRelation = (MasterApprentice) TeachService.masterApprenticeTable.get(_apprentice.getUserID());
        if (apprenticeRelation != null && apprenticeRelation.masterUserID != 0) {
            HeroPlayer master = PlayerServiceImpl.getInstance().getPlayerByUserID(apprenticeRelation.masterUserID);
            if (master != null) {
                int masterLevel = master.getLevel();
            } else {
                int masterLevel = PlayerDAO.getRoleLevel(apprenticeRelation.masterUserID);
            }
            if (_apprentice.getLevel() >= 40) {
                String masterName = apprenticeRelation.masterName;
                apprenticeRelation.leftMaster();
                if (!apprenticeRelation.isValidate()) {
                    TeachService.masterApprenticeTable.remove(_apprentice.getUserID());
                }
                MasterApprentice masterRelation = (MasterApprentice) TeachService.masterApprenticeTable.get(apprenticeRelation.masterUserID);
                if (masterRelation != null && masterRelation.removeApprenticer(_apprentice.getUserID()) != null && !apprenticeRelation.isValidate()) {
                    TeachService.masterApprenticeTable.remove(apprenticeRelation.masterUserID);
                }
                TeachDAO.deleteMasterApprentice(_apprentice.getUserID());
                ResponseMessageQueue.getInstance().put(_apprentice.getMsgQueueIndex(), new Warning("\u5df2\u7ecf\u5706\u6ee1\u51fa\u5e08"));
                Letter letter = new Letter((byte) 0, LetterService.getInstance().getUseableLetterID(), "\u7cfb\u7edf", "\u7cfb\u7edf", apprenticeRelation.masterUserID, masterName, String.valueOf(_apprentice.getName()) + "\u5df2\u7ecf\u5706\u6ee1\u51fa\u5e08");
                LetterService.getInstance().addNewLetter(letter);
                if (master != null && master.isEnable()) {
                    ResponseMessageQueue.getInstance().put(master.getMsgQueueIndex(), new Warning(String.valueOf(_apprentice.getName()) + "\u5df2\u7ecf\u5706\u6ee1\u51fa\u5e08"));
                    ResponseMessageQueue.getInstance().put(master.getMsgQueueIndex(), new MailStatusChanges(MailStatusChanges.TYPE_OF_LETTER, true));
                }
            }
        }
    }

    public static void enterTeam(final HeroPlayer _apprentice, final Group _group) {
        MasterApprentice apprenticeRelation = (MasterApprentice) TeachService.masterApprenticeTable.get(_apprentice.getUserID());
        if (apprenticeRelation != null && apprenticeRelation.masterUserID != 0 && apprenticeRelation.masterIsOnline && _apprentice.getGroupID() == PlayerServiceImpl.getInstance().getPlayerByUserID(apprenticeRelation.masterUserID).getGroupID()) {
            _apprentice.changeExperienceModulus(-0.02f);
            _apprentice.changeExperienceModulus(0.1f);
        }
    }

    public static void delteRole(final int _userID) {
        TeachDAO.deleteAll(_userID);
        MasterApprentice masterApprentice = (MasterApprentice) TeachService.masterApprenticeTable.remove(_userID);
        if (masterApprentice != null) {
            if (masterApprentice.apprenticeNumber > 0) {
                for (int i = 0; i < masterApprentice.apprenticeNumber; ++i) {
                    MasterApprentice relation = (MasterApprentice) TeachService.masterApprenticeTable.get(masterApprentice.apprenticeList[i].userID);
                    if (relation != null) {
                        relation.leftMaster();
                    }
                }
            }
            if (masterApprentice.masterUserID > 0) {
                MasterApprentice relation = (MasterApprentice) TeachService.masterApprenticeTable.get(masterApprentice.masterUserID);
                if (relation != null) {
                    relation.removeApprenticer(_userID);
                    relation.addApprenticeOnlineNumber(false);
                    PlayerServiceImpl.getInstance().getPlayerByUserID(masterApprentice.masterUserID);
                }
            }
        }
    }

    public static void waitingReply(final HeroPlayer askeder) {
        (askeder.waitingTimer = new Timer()).schedule(new WaitingResponse(askeder, 20, true), 0L, 1000L);
        askeder.waitingTimerRunning = true;
    }

    public static void cancelWaitingTimer(final HeroPlayer replyer) {
        if (replyer.waitingTimerRunning) {
            replyer.waitingTimer.cancel();
            replyer.waitingTimerRunning = false;
            replyer.waitingTimer = null;
        }
    }
}
