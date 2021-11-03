// 
// Decompiled by Procyon v0.5.36
// 
package hero.micro.teach;

import org.apache.log4j.Logger;

public class MasterApprentice {

    private static Logger log;
    public int masterUserID;
    public String masterName;
    public boolean masterIsOnline;
    public ApprenticeInfo[] apprenticeList;
    public int apprenticeNumber;
    private int apprenticeOnlineNumber;
    public static final byte RELATION_TYPE_OF_MASTER = 1;
    public static final byte RELATION_TYPE_OF_APPRENTICE = 2;
    public static final byte MAX_APPRENTICER_NUMBER = 5;
    public static final byte RESULT_OF_TEACH_THAT_LEVEL_NOTENOUGH = -1;
    public static final byte RESULT_OF_TEACH_THAT_HAS_TEACHED = -2;
    public static final int MONEY_ADD_OF_MASTER = 1;
    public static final float EXP_MODULUS_WHEN_MASTER_ONLINE = 0.02f;
    public static final float EXP_MODULUS_WHEN_MASTER_IN_TEAM = 0.1f;

    static {
        MasterApprentice.log = Logger.getLogger((Class) MasterApprentice.class);
    }

    public void addApprenticeOnlineNumber(final boolean add) {
        if (add) {
            ++this.apprenticeOnlineNumber;
        } else {
            --this.apprenticeOnlineNumber;
        }
        if (this.apprenticeOnlineNumber > this.apprenticeNumber) {
            this.apprenticeOnlineNumber = this.apprenticeNumber;
        }
        if (this.apprenticeOnlineNumber < 0) {
            this.apprenticeOnlineNumber = 0;
        }
    }

    public int getApprenticeOnlineNumber() {
        return this.apprenticeOnlineNumber;
    }

    public void dismissAll() {
        this.apprenticeList = null;
        this.masterName = null;
        this.apprenticeNumber = 0;
        this.masterUserID = 0;
        this.apprenticeOnlineNumber = 0;
    }

    public void changeApprenticeStatus(final int _apprenticeUserID, final boolean _isOnline) {
        if (this.apprenticeList != null && this.apprenticeList.length > 0) {
            ApprenticeInfo[] apprenticeList;
            for (int length = (apprenticeList = this.apprenticeList).length, i = 0; i < length; ++i) {
                ApprenticeInfo apprenticeInfo = apprenticeList[i];
                if (apprenticeInfo == null) {
                    break;
                }
                if (apprenticeInfo.userID == _apprenticeUserID) {
                    apprenticeInfo.isOnline = _isOnline;
                    if (_isOnline) {
                        if (this.apprenticeOnlineNumber < 5) {
                            this.addApprenticeOnlineNumber(true);
                        }
                    } else {
                        this.addApprenticeOnlineNumber(false);
                    }
                    return;
                }
            }
        }
    }

    public void setMaster(final int _masterUserID, final String _name, final boolean _isOnline) {
        this.masterUserID = _masterUserID;
        this.masterName = _name;
        this.masterIsOnline = _isOnline;
    }

    public void leftMaster() {
        this.masterUserID = 0;
        this.masterName = null;
        this.masterIsOnline = false;
    }

    public synchronized boolean addNewApprenticer(final int _userID, final String _name) {
        if (this.apprenticeList == null) {
            (this.apprenticeList = new ApprenticeInfo[5])[0] = new ApprenticeInfo(_userID, _name);
            this.apprenticeList[0].isOnline = true;
            ++this.apprenticeNumber;
            if (this.apprenticeOnlineNumber < 5) {
                this.addApprenticeOnlineNumber(true);
            }
            return true;
        }
        for (int i = 0; i < 5; ++i) {
            if (this.apprenticeList[i] == null) {
                this.apprenticeList[i] = new ApprenticeInfo(_userID, _name);
                ++this.apprenticeNumber;
                this.apprenticeList[i].isOnline = true;
                if (this.apprenticeOnlineNumber < 5) {
                    this.addApprenticeOnlineNumber(true);
                }
                return true;
            }
            if (this.apprenticeList[i].userID == _userID) {
                return false;
            }
        }
        return false;
    }

    public synchronized boolean addNewApprenticer(final int _userID, final String _name, final byte _teachTimes, final short _levelOfLastTeach) {
        if (this.apprenticeList == null) {
            (this.apprenticeList = new ApprenticeInfo[5])[0] = new ApprenticeInfo(_userID, _name, _teachTimes, _levelOfLastTeach);
            ++this.apprenticeNumber;
            return true;
        }
        for (int i = 0; i < 5; ++i) {
            if (this.apprenticeList[i] == null) {
                this.apprenticeList[i] = new ApprenticeInfo(_userID, _name, _teachTimes, _levelOfLastTeach);
                ++this.apprenticeNumber;
                return true;
            }
        }
        return false;
    }

    public synchronized String removeApprenticer(final int _apprenticeUserID) {
        if (this.apprenticeList != null) {
            for (int i = 0; i < this.apprenticeNumber; ++i) {
                MasterApprentice.log.debug((Object) ("apprenticeList[" + i + "].userID = " + this.apprenticeList[i].userID));
                if (this.apprenticeList[i] != null && this.apprenticeList[i].userID == _apprenticeUserID) {
                    String apprenticeName = this.apprenticeList[i].name;
                    if (this.apprenticeList[i].isOnline) {
                        this.addApprenticeOnlineNumber(false);
                    }
                    MasterApprentice.log.debug((Object) ("apprenticeList[" + i + "].name = " + this.apprenticeList[i].name));
                    if (this.apprenticeNumber > 1) {
                        System.arraycopy(this.apprenticeList, i + 1, this.apprenticeList, i, this.apprenticeNumber - i - 1);
                    }
                    this.apprenticeList[--this.apprenticeNumber] = null;
                    return apprenticeName;
                }
            }
        }
        return null;
    }

    public boolean isValidate() {
        return this.apprenticeNumber != 0 || this.masterUserID != 0;
    }

    public synchronized byte teachKnowledge(final int _apprenticeUserID, final short _apprenticeLevel) {
        if (this.apprenticeList != null) {
            int i = 0;
            while (i < this.apprenticeNumber) {
                if (this.apprenticeList[i] != null && this.apprenticeList[i].userID == _apprenticeUserID) {
                    if (_apprenticeLevel < 10) {
                        return -1;
                    }
                    if (_apprenticeLevel / 10 != this.apprenticeList[i].levelOfLastAccepted / 10) {
                        ApprenticeInfo apprenticeInfo = this.apprenticeList[i];
                        ++apprenticeInfo.acceptedTimesThatTeach;
                        this.apprenticeList[i].levelOfLastAccepted = _apprenticeLevel;
                        return this.apprenticeList[i].acceptedTimesThatTeach;
                    }
                    return -2;
                } else {
                    ++i;
                }
            }
        }
        return -3;
    }

    public byte authenTeachCondition(final int _apprenticeUserID, final short _apprenticeLevel) {
        if (this.apprenticeList != null) {
            int i = 0;
            while (i < this.apprenticeNumber) {
                if (this.apprenticeList[i] != null && this.apprenticeList[i].userID == _apprenticeUserID) {
                    if (_apprenticeLevel < 10) {
                        return -1;
                    }
                    if (_apprenticeLevel / 10 == this.apprenticeList[i].levelOfLastAccepted / 10) {
                        return -2;
                    }
                    return this.apprenticeList[i].acceptedTimesThatTeach;
                } else {
                    ++i;
                }
            }
        }
        return -3;
    }

    public class ApprenticeInfo {

        public int userID;
        public String name;
        public boolean isOnline;
        public byte acceptedTimesThatTeach;
        public short levelOfLastAccepted;

        public ApprenticeInfo(final int _userID, final String _name) {
            this.userID = _userID;
            this.name = _name;
        }

        public ApprenticeInfo(final int _userID, final String _name, final byte _acceptedTimesThatTeach, final short _levelOfLastAccepted) {
            this.userID = _userID;
            this.name = _name;
            this.acceptedTimesThatTeach = _acceptedTimesThatTeach;
            this.levelOfLastAccepted = _levelOfLastAccepted;
        }
    }
}
