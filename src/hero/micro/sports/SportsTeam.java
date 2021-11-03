// 
// Decompiled by Procyon v0.5.36
// 
package hero.micro.sports;

import java.util.ArrayList;

public class SportsTeam {

    public byte queueType;
    public short levelZoon;
    public int teamPointTotal;
    public long executeTime;
    public int teamID;
    public ESportsClan sportsClan;
    public ArrayList<Integer> memberInfoList;
    public byte liveMemberNumber;
    public static final byte TYPE_OF_TWO = 2;
    public static final byte TYPE_OF_THREE = 3;
    public static final byte TYPE_OF_FIVE = 5;

    public SportsTeam(final int _teamID, final byte _queueType, final int _teamLeaderUserID) {
        this.teamID = _teamID;
        this.queueType = _queueType;
        this.memberInfoList = new ArrayList<Integer>();
    }

    public void addMemberInfo(final int _memberUserID, final int _memberPoint) {
        this.memberInfoList.add(_memberUserID);
        this.memberInfoList.add(_memberPoint);
    }

    public void enterSpace(final int _memberUserID) {
        ++this.liveMemberNumber;
    }

    public void memberDie(final int _memberUserID) {
        --this.liveMemberNumber;
    }

    public byte getLiveMemberNumber() {
        return this.liveMemberNumber;
    }
}
