// 
// Decompiled by Procyon v0.5.36
// 
package hero.login.rmi;

import javolution.util.FastList;
import hero.item.Goods;
import hero.item.dictionary.GoodsContents;
import hero.gm.service.GmServiceImpl;
import hero.chat.service.ChatServiceImpl;
import java.util.Iterator;
import java.util.ArrayList;
import hero.map.MapModelData;
import hero.map.service.MapModelDataDict;
import hero.player.LoginInfo;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import hero.login.LoginServiceImpl;
import java.rmi.RemoteException;
import javax.rmi.PortableRemoteObject;

public class YOYOLoginRMIImpl extends PortableRemoteObject implements IYOYOLoginRMI {

    public YOYOLoginRMIImpl() throws RemoteException {
    }

    public byte[] listRole(final int[] _userIDs) throws RemoteException {
        return LoginServiceImpl.getInstance().listRole(_userIDs);
    }

    public byte[] createRole(final int accountID, final short serverID, final int userID, final String[] paras) throws RemoteException {
        byte[] result = null;
        try {
            result = LoginServiceImpl.getInstance().createRole(accountID, serverID, userID, paras);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public int deleteRole(final int userId) throws RemoteException {
        return LoginServiceImpl.getInstance().deleteRole(userId);
    }

    public boolean resetPlayersStatus(final int _accountID) throws RemoteException {
        return LoginServiceImpl.getInstance().resetPlayersStatus(_accountID);
    }

    public boolean checkStatusOfRun() throws RemoteException {
        return true;
    }

    public int createSessionID(final int _userID, final int _accountID) throws RemoteException {
        return LoginServiceImpl.getInstance().login(_userID, _accountID);
    }

    public String getPlayerInfoByUserID(final int _userID) throws RemoteException {
        int online = 1;
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(_userID);
        if (player == null) {
            online = 0;
            player = PlayerServiceImpl.getInstance().getOffLinePlayerInfo(_userID);
        }
        if (player == null) {
            return "-1";
        }
        StringBuffer sb = new StringBuffer();
        String phone = "";
        String publisher = "";
        LoginInfo lInfo = player.getLoginInfo();
        if (lInfo != null) {
            phone = new StringBuilder(String.valueOf(lInfo.loginMsisdn)).toString();
            publisher = new StringBuilder(String.valueOf(lInfo.publisher)).toString();
        }
        sb.append(player.getUserID()).append(",").append(player.getName()).append(",").append(player.getLoginInfo().accountID).append(",").append(player.getSex().getDesc()).append(",").append(player.getVocation().getDesc()).append(",").append(player.getClan().getDesc()).append(",").append(player.getLevel()).append(",").append(player.getExp()).append(",").append(player.getMoney()).append(",").append(online).append(",").append(player.loginTime).append(",").append(player.lastLogoutTime).append(",").append((player.where() != null) ? player.where().getName() : "").append(",").append(phone).append(",").append(publisher);
        return sb.toString();
    }

    public String getPlayerInfoByNickname(final String nickname) throws RemoteException {
        int online = 1;
        HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByName(nickname);
        if (player == null) {
            online = 0;
            player = PlayerServiceImpl.getInstance().getOffLinePlayerInfoByName(nickname);
        }
        if (player == null) {
            return "-1";
        }
        StringBuffer sb = new StringBuffer();
        String phone = "";
        String publisher = "";
        LoginInfo lInfo = player.getLoginInfo();
        if (lInfo != null) {
            phone = new StringBuilder(String.valueOf(lInfo.loginMsisdn)).toString();
            publisher = new StringBuilder(String.valueOf(lInfo.publisher)).toString();
        }
        sb.append(player.getUserID()).append(",").append(player.getName()).append(",").append(player.getLoginInfo().accountID).append(",").append(player.getSex().getDesc()).append(",").append(player.getVocation().getDesc()).append(",").append(player.getClan().getDesc()).append(",").append(player.getLevel()).append(",").append(player.getExp()).append(",").append(player.getMoney()).append(",").append(online).append(",").append(player.loginTime).append(",").append(player.lastLogoutTime).append(",").append((player.where() != null) ? player.where().getName() : "").append(",").append(phone).append(",").append(publisher);
        return sb.toString();
    }

    public int getOnlinePlayerNumber() throws RemoteException {
        return LoginServiceImpl.getInstance().getOnlinePlayerNumber();
    }

    public byte[] listDefaultRole() throws RemoteException {
        return LoginServiceImpl.getInstance().listDefaultRole();
    }

    public String getGameMapList() throws RemoteException {
        ArrayList<MapModelData> mapList = MapModelDataDict.getInstance().getMapModelDataList();
        StringBuffer sb = new StringBuffer();
        for (final MapModelData map : mapList) {
            sb.append(map.id).append("-").append(map.name).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public void sendNoticeGM(final String gmName, final String content) throws RemoteException {
        ChatServiceImpl.getInstance().sendNoticeGM(gmName, content);
    }

    public void GMReplyLetter(final int gmLetterID) throws RemoteException {
        GmServiceImpl.GMReplyLetter(gmLetterID);
    }

    public int blink(final short mapID, final int userID) throws RemoteException {
        return GmServiceImpl.gmBlinkPlayer(mapID, userID);
    }

    public int szfFeeCallBack(final int userID, final String transID, final byte result, final String orderID, final int point) throws RemoteException {
        return GmServiceImpl.szfFeeCallBack(userID, transID, result, orderID, point);
    }

    public int addGoodsForPlayer(final int userID, final int goodsID, final int number) throws RemoteException {
        return PlayerServiceImpl.getInstance().GMAddGoodsForPlayer(userID, goodsID, number);
    }

    public int addPointForPlayer(final int userID, final int point) throws RemoteException {
        return PlayerServiceImpl.getInstance().GMAddPointForPlayer(userID, point);
    }

    public int modifyPlayerInfo(final int userID, final int money, final int loverValue, final int level, final int skillPoint) throws RemoteException {
        return PlayerServiceImpl.getInstance().GMModifyPlayerInfo(userID, money, loverValue, level, skillPoint);
    }

    public String getGoodsName(final int goodsID) throws RemoteException {
        Goods goods = GoodsContents.getGoods(goodsID);
        if (goods != null) {
            return goods.getName();
        }
        return "0";
    }

    public void smsCallBack(final String transID, final String result) throws RemoteException {
        GmServiceImpl.smsCallBack(transID, result);
    }

    public void resetPlayers() throws RemoteException {
        FastList<HeroPlayer> playerList = PlayerServiceImpl.getInstance().getPlayerList();
        for (int i = playerList.size() - 1; i >= 0; --i) {
            HeroPlayer hPlayer = (HeroPlayer) playerList.get(i);
            LoginServiceImpl.getInstance().resetPlayersStatus(hPlayer.getLoginInfo().accountID);
        }
    }
}
