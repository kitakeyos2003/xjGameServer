// 
// Decompiled by Procyon v0.5.36
// 
package hero.login.rmi;

import java.rmi.RemoteException;
import java.rmi.Remote;

public interface IYOYOLoginRMI extends Remote {

    byte[] listRole(final int[] p0) throws RemoteException;

    byte[] createRole(final int p0, final short p1, final int p2, final String[] p3) throws RemoteException;

    byte[] listDefaultRole() throws RemoteException;

    int deleteRole(final int p0) throws RemoteException;

    boolean resetPlayersStatus(final int p0) throws RemoteException;

    boolean checkStatusOfRun() throws RemoteException;

    int getOnlinePlayerNumber() throws RemoteException;

    int createSessionID(final int p0, final int p1) throws RemoteException;

    String getPlayerInfoByUserID(final int p0) throws RemoteException;

    String getPlayerInfoByNickname(final String p0) throws RemoteException;

    String getGameMapList() throws RemoteException;

    void sendNoticeGM(final String p0, final String p1) throws RemoteException;

    void GMReplyLetter(final int p0) throws RemoteException;

    int blink(final short p0, final int p1) throws RemoteException;

    int szfFeeCallBack(final int p0, final String p1, final byte p2, final String p3, final int p4) throws RemoteException;

    int addGoodsForPlayer(final int p0, final int p1, final int p2) throws RemoteException;

    int addPointForPlayer(final int p0, final int p1) throws RemoteException;

    int modifyPlayerInfo(final int p0, final int p1, final int p2, final int p3, final int p4) throws RemoteException;

    String getGoodsName(final int p0) throws RemoteException;

    void smsCallBack(final String p0, final String p1) throws RemoteException;

    void resetPlayers() throws RemoteException;
}
