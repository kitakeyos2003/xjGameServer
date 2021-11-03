// 
// Decompiled by Procyon v0.5.36
// 
package hero.login.rmi;

import java.rmi.UnexpectedException;
import java.rmi.RemoteException;
import java.rmi.Remote;
import java.rmi.server.RemoteRef;
import java.lang.reflect.Method;
import java.rmi.server.RemoteStub;

public final class YOYOLoginRMIImpl_Stub extends RemoteStub implements IYOYOLoginRMI {

    private static final long serialVersionUID = 2L;
    private static Method $method_GMReplyLetter_0;
    private static Method $method_addGoodsForPlayer_1;
    private static Method $method_addPointForPlayer_2;
    private static Method $method_blink_3;
    private static Method $method_checkStatusOfRun_4;
    private static Method $method_createRole_5;
    private static Method $method_createSessionID_6;
    private static Method $method_deleteRole_7;
    private static Method $method_getGameMapList_8;
    private static Method $method_getGoodsName_9;
    private static Method $method_getOnlinePlayerNumber_10;
    private static Method $method_getPlayerInfoByNickname_11;
    private static Method $method_getPlayerInfoByUserID_12;
    private static Method $method_listDefaultRole_13;
    private static Method $method_listRole_14;
    private static Method $method_modifyPlayerInfo_15;
    private static Method $method_resetPlayers_16;
    private static Method $method_resetPlayersStatus_17;
    private static Method $method_sendNoticeGM_18;
    private static Method $method_smsCallBack_19;
    private static Method $method_szfFeeCallBack_20;

    static {
        try {
            YOYOLoginRMIImpl_Stub.$method_GMReplyLetter_0 = IYOYOLoginRMI.class.getMethod("GMReplyLetter", Integer.TYPE);
            YOYOLoginRMIImpl_Stub.$method_addGoodsForPlayer_1 = IYOYOLoginRMI.class.getMethod("addGoodsForPlayer", Integer.TYPE, Integer.TYPE, Integer.TYPE);
            YOYOLoginRMIImpl_Stub.$method_addPointForPlayer_2 = IYOYOLoginRMI.class.getMethod("addPointForPlayer", Integer.TYPE, Integer.TYPE);
            YOYOLoginRMIImpl_Stub.$method_blink_3 = IYOYOLoginRMI.class.getMethod("blink", Short.TYPE, Integer.TYPE);
            YOYOLoginRMIImpl_Stub.$method_checkStatusOfRun_4 = IYOYOLoginRMI.class.getMethod("checkStatusOfRun", (Class<?>[]) new Class[0]);
            YOYOLoginRMIImpl_Stub.$method_createRole_5 = IYOYOLoginRMI.class.getMethod("createRole", Integer.TYPE, Short.TYPE, Integer.TYPE, String[].class);
            YOYOLoginRMIImpl_Stub.$method_createSessionID_6 = IYOYOLoginRMI.class.getMethod("createSessionID", Integer.TYPE, Integer.TYPE);
            YOYOLoginRMIImpl_Stub.$method_deleteRole_7 = IYOYOLoginRMI.class.getMethod("deleteRole", Integer.TYPE);
            YOYOLoginRMIImpl_Stub.$method_getGameMapList_8 = IYOYOLoginRMI.class.getMethod("getGameMapList", (Class<?>[]) new Class[0]);
            YOYOLoginRMIImpl_Stub.$method_getGoodsName_9 = IYOYOLoginRMI.class.getMethod("getGoodsName", Integer.TYPE);
            YOYOLoginRMIImpl_Stub.$method_getOnlinePlayerNumber_10 = IYOYOLoginRMI.class.getMethod("getOnlinePlayerNumber", (Class<?>[]) new Class[0]);
            YOYOLoginRMIImpl_Stub.$method_getPlayerInfoByNickname_11 = IYOYOLoginRMI.class.getMethod("getPlayerInfoByNickname", String.class);
            YOYOLoginRMIImpl_Stub.$method_getPlayerInfoByUserID_12 = IYOYOLoginRMI.class.getMethod("getPlayerInfoByUserID", Integer.TYPE);
            YOYOLoginRMIImpl_Stub.$method_listDefaultRole_13 = IYOYOLoginRMI.class.getMethod("listDefaultRole", (Class<?>[]) new Class[0]);
            YOYOLoginRMIImpl_Stub.$method_listRole_14 = IYOYOLoginRMI.class.getMethod("listRole", int[].class);
            YOYOLoginRMIImpl_Stub.$method_modifyPlayerInfo_15 = IYOYOLoginRMI.class.getMethod("modifyPlayerInfo", Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE);
            YOYOLoginRMIImpl_Stub.$method_resetPlayers_16 = IYOYOLoginRMI.class.getMethod("resetPlayers", (Class<?>[]) new Class[0]);
            YOYOLoginRMIImpl_Stub.$method_resetPlayersStatus_17 = IYOYOLoginRMI.class.getMethod("resetPlayersStatus", Integer.TYPE);
            YOYOLoginRMIImpl_Stub.$method_sendNoticeGM_18 = IYOYOLoginRMI.class.getMethod("sendNoticeGM", String.class, String.class);
            YOYOLoginRMIImpl_Stub.$method_smsCallBack_19 = IYOYOLoginRMI.class.getMethod("smsCallBack", String.class, String.class);
            YOYOLoginRMIImpl_Stub.$method_szfFeeCallBack_20 = IYOYOLoginRMI.class.getMethod("szfFeeCallBack", Integer.TYPE, String.class, Byte.TYPE, String.class, Integer.TYPE);
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodError("stub class initialization failed");
        }
    }

    public YOYOLoginRMIImpl_Stub(final RemoteRef ref) {
        super(ref);
    }

    @Override
    public void GMReplyLetter(final int $param_int_1) throws RemoteException {
        try {
            this.ref.invoke(this, YOYOLoginRMIImpl_Stub.$method_GMReplyLetter_0, new Object[]{new Integer($param_int_1)}, 3362286105506325484L);
        } catch (RuntimeException e) {
            throw e;
        } catch (RemoteException e2) {
            throw e2;
        } catch (Exception e3) {
            throw new UnexpectedException("undeclared checked exception", e3);
        }
    }

    @Override
    public int addGoodsForPlayer(final int $param_int_1, final int $param_int_2, final int $param_int_3) throws RemoteException {
        try {
            Object $result = this.ref.invoke(this, YOYOLoginRMIImpl_Stub.$method_addGoodsForPlayer_1, new Object[]{new Integer($param_int_1), new Integer($param_int_2), new Integer($param_int_3)}, -2125371492887870376L);
            return (int) $result;
        } catch (RuntimeException e) {
            throw e;
        } catch (RemoteException e2) {
            throw e2;
        } catch (Exception e3) {
            throw new UnexpectedException("undeclared checked exception", e3);
        }
    }

    @Override
    public int addPointForPlayer(final int $param_int_1, final int $param_int_2) throws RemoteException {
        try {
            Object $result = this.ref.invoke(this, YOYOLoginRMIImpl_Stub.$method_addPointForPlayer_2, new Object[]{new Integer($param_int_1), new Integer($param_int_2)}, 4134609681590273210L);
            return (int) $result;
        } catch (RuntimeException e) {
            throw e;
        } catch (RemoteException e2) {
            throw e2;
        } catch (Exception e3) {
            throw new UnexpectedException("undeclared checked exception", e3);
        }
    }

    @Override
    public int blink(final short $param_short_1, final int $param_int_2) throws RemoteException {
        try {
            Object $result = this.ref.invoke(this, YOYOLoginRMIImpl_Stub.$method_blink_3, new Object[]{new Short($param_short_1), new Integer($param_int_2)}, 6344898611983036648L);
            return (int) $result;
        } catch (RuntimeException e) {
            throw e;
        } catch (RemoteException e2) {
            throw e2;
        } catch (Exception e3) {
            throw new UnexpectedException("undeclared checked exception", e3);
        }
    }

    @Override
    public boolean checkStatusOfRun() throws RemoteException {
        try {
            Object $result = this.ref.invoke(this, YOYOLoginRMIImpl_Stub.$method_checkStatusOfRun_4, null, -819198521117889461L);
            return (boolean) $result;
        } catch (RuntimeException e) {
            throw e;
        } catch (RemoteException e2) {
            throw e2;
        } catch (Exception e3) {
            throw new UnexpectedException("undeclared checked exception", e3);
        }
    }

    @Override
    public byte[] createRole(final int $param_int_1, final short $param_short_2, final int $param_int_3, final String[] $param_arrayOf_String_4) throws RemoteException {
        try {
            Object $result = this.ref.invoke(this, YOYOLoginRMIImpl_Stub.$method_createRole_5, new Object[]{new Integer($param_int_1), new Short($param_short_2), new Integer($param_int_3), $param_arrayOf_String_4}, 7821257337317999579L);
            return (byte[]) $result;
        } catch (RuntimeException e) {
            throw e;
        } catch (RemoteException e2) {
            throw e2;
        } catch (Exception e3) {
            throw new UnexpectedException("undeclared checked exception", e3);
        }
    }

    @Override
    public int createSessionID(final int $param_int_1, final int $param_int_2) throws RemoteException {
        try {
            Object $result = this.ref.invoke(this, YOYOLoginRMIImpl_Stub.$method_createSessionID_6, new Object[]{new Integer($param_int_1), new Integer($param_int_2)}, -5866673518348984378L);
            return (int) $result;
        } catch (RuntimeException e) {
            throw e;
        } catch (RemoteException e2) {
            throw e2;
        } catch (Exception e3) {
            throw new UnexpectedException("undeclared checked exception", e3);
        }
    }

    @Override
    public int deleteRole(final int $param_int_1) throws RemoteException {
        try {
            Object $result = this.ref.invoke(this, YOYOLoginRMIImpl_Stub.$method_deleteRole_7, new Object[]{new Integer($param_int_1)}, 5582361264620256667L);
            return (int) $result;
        } catch (RuntimeException e) {
            throw e;
        } catch (RemoteException e2) {
            throw e2;
        } catch (Exception e3) {
            throw new UnexpectedException("undeclared checked exception", e3);
        }
    }

    @Override
    public String getGameMapList() throws RemoteException {
        try {
            Object $result = this.ref.invoke(this, YOYOLoginRMIImpl_Stub.$method_getGameMapList_8, null, 722486599017532250L);
            return (String) $result;
        } catch (RuntimeException e) {
            throw e;
        } catch (RemoteException e2) {
            throw e2;
        } catch (Exception e3) {
            throw new UnexpectedException("undeclared checked exception", e3);
        }
    }

    @Override
    public String getGoodsName(final int $param_int_1) throws RemoteException {
        try {
            Object $result = this.ref.invoke(this, YOYOLoginRMIImpl_Stub.$method_getGoodsName_9, new Object[]{new Integer($param_int_1)}, 6483968136812330620L);
            return (String) $result;
        } catch (RuntimeException e) {
            throw e;
        } catch (RemoteException e2) {
            throw e2;
        } catch (Exception e3) {
            throw new UnexpectedException("undeclared checked exception", e3);
        }
    }

    @Override
    public int getOnlinePlayerNumber() throws RemoteException {
        try {
            Object $result = this.ref.invoke(this, YOYOLoginRMIImpl_Stub.$method_getOnlinePlayerNumber_10, null, 7993396340870161215L);
            return (int) $result;
        } catch (RuntimeException e) {
            throw e;
        } catch (RemoteException e2) {
            throw e2;
        } catch (Exception e3) {
            throw new UnexpectedException("undeclared checked exception", e3);
        }
    }

    @Override
    public String getPlayerInfoByNickname(final String $param_String_1) throws RemoteException {
        try {
            Object $result = this.ref.invoke(this, YOYOLoginRMIImpl_Stub.$method_getPlayerInfoByNickname_11, new Object[]{$param_String_1}, -3178326323566395682L);
            return (String) $result;
        } catch (RuntimeException e) {
            throw e;
        } catch (RemoteException e2) {
            throw e2;
        } catch (Exception e3) {
            throw new UnexpectedException("undeclared checked exception", e3);
        }
    }

    @Override
    public String getPlayerInfoByUserID(final int $param_int_1) throws RemoteException {
        try {
            Object $result = this.ref.invoke(this, YOYOLoginRMIImpl_Stub.$method_getPlayerInfoByUserID_12, new Object[]{new Integer($param_int_1)}, 3986221029998072520L);
            return (String) $result;
        } catch (RuntimeException e) {
            throw e;
        } catch (RemoteException e2) {
            throw e2;
        } catch (Exception e3) {
            throw new UnexpectedException("undeclared checked exception", e3);
        }
    }

    @Override
    public byte[] listDefaultRole() throws RemoteException {
        try {
            Object $result = this.ref.invoke(this, YOYOLoginRMIImpl_Stub.$method_listDefaultRole_13, null, 4314811098416132512L);
            return (byte[]) $result;
        } catch (RuntimeException e) {
            throw e;
        } catch (RemoteException e2) {
            throw e2;
        } catch (Exception e3) {
            throw new UnexpectedException("undeclared checked exception", e3);
        }
    }

    @Override
    public byte[] listRole(final int[] $param_arrayOf_int_1) throws RemoteException {
        try {
            Object $result = this.ref.invoke(this, YOYOLoginRMIImpl_Stub.$method_listRole_14, new Object[]{$param_arrayOf_int_1}, 5271732377633397755L);
            return (byte[]) $result;
        } catch (RuntimeException e) {
            throw e;
        } catch (RemoteException e2) {
            throw e2;
        } catch (Exception e3) {
            throw new UnexpectedException("undeclared checked exception", e3);
        }
    }

    @Override
    public int modifyPlayerInfo(final int $param_int_1, final int $param_int_2, final int $param_int_3, final int $param_int_4, final int $param_int_5) throws RemoteException {
        try {
            Object $result = this.ref.invoke(this, YOYOLoginRMIImpl_Stub.$method_modifyPlayerInfo_15, new Object[]{new Integer($param_int_1), new Integer($param_int_2), new Integer($param_int_3), new Integer($param_int_4), new Integer($param_int_5)}, -5065521471865725353L);
            return (int) $result;
        } catch (RuntimeException e) {
            throw e;
        } catch (RemoteException e2) {
            throw e2;
        } catch (Exception e3) {
            throw new UnexpectedException("undeclared checked exception", e3);
        }
    }

    @Override
    public void resetPlayers() throws RemoteException {
        try {
            this.ref.invoke(this, YOYOLoginRMIImpl_Stub.$method_resetPlayers_16, null, 2615789245185974199L);
        } catch (RuntimeException e) {
            throw e;
        } catch (RemoteException e2) {
            throw e2;
        } catch (Exception e3) {
            throw new UnexpectedException("undeclared checked exception", e3);
        }
    }

    @Override
    public boolean resetPlayersStatus(final int $param_int_1) throws RemoteException {
        try {
            Object $result = this.ref.invoke(this, YOYOLoginRMIImpl_Stub.$method_resetPlayersStatus_17, new Object[]{new Integer($param_int_1)}, 1329813045848175969L);
            return (boolean) $result;
        } catch (RuntimeException e) {
            throw e;
        } catch (RemoteException e2) {
            throw e2;
        } catch (Exception e3) {
            throw new UnexpectedException("undeclared checked exception", e3);
        }
    }

    @Override
    public void sendNoticeGM(final String $param_String_1, final String $param_String_2) throws RemoteException {
        try {
            this.ref.invoke(this, YOYOLoginRMIImpl_Stub.$method_sendNoticeGM_18, new Object[]{$param_String_1, $param_String_2}, 3199868298211475341L);
        } catch (RuntimeException e) {
            throw e;
        } catch (RemoteException e2) {
            throw e2;
        } catch (Exception e3) {
            throw new UnexpectedException("undeclared checked exception", e3);
        }
    }

    @Override
    public void smsCallBack(final String $param_String_1, final String $param_String_2) throws RemoteException {
        try {
            this.ref.invoke(this, YOYOLoginRMIImpl_Stub.$method_smsCallBack_19, new Object[]{$param_String_1, $param_String_2}, -4697181263741900115L);
        } catch (RuntimeException e) {
            throw e;
        } catch (RemoteException e2) {
            throw e2;
        } catch (Exception e3) {
            throw new UnexpectedException("undeclared checked exception", e3);
        }
    }

    @Override
    public int szfFeeCallBack(final int $param_int_1, final String $param_String_2, final byte $param_byte_3, final String $param_String_4, final int $param_int_5) throws RemoteException {
        try {
            Object $result = this.ref.invoke(this, YOYOLoginRMIImpl_Stub.$method_szfFeeCallBack_20, new Object[]{new Integer($param_int_1), $param_String_2, new Byte($param_byte_3), $param_String_4, new Integer($param_int_5)}, 2978294565660189059L);
            return (int) $result;
        } catch (RuntimeException e) {
            throw e;
        } catch (RemoteException e2) {
            throw e2;
        } catch (Exception e3) {
            throw new UnexpectedException("undeclared checked exception", e3);
        }
    }
}
