// 
// Decompiled by Procyon v0.5.36
// 
package hero.log.service;

import java.sql.Timestamp;
import hero.pet.Pet;
import hero.item.detail.EGoodsTrait;
import hero.item.Goods;
import hero.player.HeroPlayer;
import hero.gm.service.GmServiceImpl;
import java.util.Date;
import yoyo.service.tools.log.SystemLogServiceImpl;
import org.apache.log4j.Logger;
import hero.map.service.IMapService;
import yoyo.service.base.AbsServiceAdaptor;

public class LogServiceImpl extends AbsServiceAdaptor<LogConfig> implements IMapService {

    private static LogServiceImpl instance;

    private LogServiceImpl() {
        this.config = new LogConfig();
    }

    @Override
    protected void start() {
    }

    private Logger getLogger(final String name) {
        if (name == null || name.equals("")) {
            return SystemLogServiceImpl.getInstance().getLoggerByName(name);
        }
        return ((LogConfig) this.config).getLogger(name);
    }

    public static LogServiceImpl getInstance() {
        if (LogServiceImpl.instance == null) {
            LogServiceImpl.instance = new LogServiceImpl();
        }
        return LogServiceImpl.instance;
    }

    public void createDelRoleLog(final String operate, final int _accountID, final short _serverID, final int _userID, final String _nickname, final String _clanName, final String _vocation, final String _sex, final short _clientType, final boolean success) {
        XmlFormatter formatter = new XmlFormatter("createDelRoleLog");
        formatter.append("date", new Date()).append("operate", operate).append("accountID", _accountID).append("game_id", GmServiceImpl.gameID).append("server_id", GmServiceImpl.serverID).append("userID", _userID).append("nickname", _nickname).append("clan", _clanName).append("vocation", _vocation).append("sex", _sex).append("time", DateTime.getTime(System.currentTimeMillis())).append("clientType", _clientType).append("result", success ? "\u6210\u529f" : "\u5931\u8d25");
        this.getLogger("createRole").info((Object) formatter.flush());
    }

    public void roleLoginLog(final int _accountID, final int _userID, final String _nickname, final String _mobile, final String _userAgent, final String _clientVersion, final short _clientType, final short _conType, final long _loginTime, final int _publisher, final short mapId, final String _loginMapName, final String ip) {
        XmlFormatter formatter = new XmlFormatter("loginRole");
        formatter.append("date", new Date()).append("userid", _accountID).append("role_id", _userID).append("game_id", GmServiceImpl.gameID).append("server_id", GmServiceImpl.serverID).append("nickname", _nickname).append("logintime", DateTime.getTime(_loginTime)).append("agent", _userAgent).append("msisdn", _mobile).append("client_version", _clientVersion).append("publisher", _publisher).append("companyID", 0).append("SystemType", "").append("login_where_id", mapId).append("login_where_name", _loginMapName).append("loginIP", ip).append("logoutIP", ip).append("conType", _conType);
        this.getLogger("roleLogin").info((Object) formatter.flush());
    }

    public void roleOnOffLog(final int _accountID, final int _userID, final String _nickname, final String _mobile, final long _loginTime, final String _cause, final String _userAgent, final String _clientVersion, final short _clientType, final short _conType, final String _logoutMapName, final long _logoutTime, final int _publisher, final short mapId) {
        XmlFormatter formatter = new XmlFormatter("offOlineRole");
        formatter.append("date", new Date()).append("userid", _accountID).append("role_id", _userID).append("game_id", GmServiceImpl.gameID).append("server_id", GmServiceImpl.serverID).append("nickname", _nickname).append("logintime", DateTime.getTime(_loginTime)).append("logouttime", DateTime.getTime(_logoutTime)).append("logoutReason", _cause).append("agent", _userAgent).append("msisdn", _mobile).append("client_version", _clientVersion).append("publisher", _publisher).append("companyID", 0).append("SystemType", "").append("logout_where_id", mapId).append("logout_where_name", _logoutMapName).append("logoutIP", "").append("conType", _conType);
        this.getLogger("roleOnOff").info((Object) formatter.flush());
    }

    public void talkLog(final int _accountID, final int _userID, final String _nickname, final String _mobile, final int _receiveID, final String _receiveNickname, final String _type, final String _mapName, final String _content) {
        XmlFormatter formatter = new XmlFormatter("talkLog");
        formatter.append("date", new Date()).append("accountID", _accountID).append("game_id", GmServiceImpl.gameID).append("server_id", GmServiceImpl.serverID).append("userID", _userID).append("nickname", _nickname).append("msisdn", _mobile).append("receiverID", _receiveID).append("receiveNickname", _receiveNickname).append("type", _type).append("mapName", _mapName).append("content", _content.replaceAll("\\[", "(").replaceAll("\\]", ")"));
        this.getLogger("talk").info((Object) formatter.flush());
    }

    public void upgradeLog(final int _accountID, final int _userID, final String _nickname, final String _mobile, final String _mapName, final int _lvl) {
        XmlFormatter formatter = new XmlFormatter("upgradeLog");
        formatter.append("date", new Date()).append("accountID", _accountID).append("game_id", GmServiceImpl.gameID).append("server_id", GmServiceImpl.serverID).append("userID", _userID).append("nickname", _nickname).append("msisdn", _mobile).append("mapName", _mapName).append("level", _lvl);
        this.getLogger("upgrade").info((Object) formatter.flush());
    }

    public void onlineNumLog(final int _onlineNum) {
        XmlFormatter formatter = new XmlFormatter("onlineNumInfo");
        formatter.append("date", new Date()).append("game_id", GmServiceImpl.gameID).append("server_id", GmServiceImpl.serverID).append("logtime", DateTime.getTime(System.currentTimeMillis())).append("onlineNum", _onlineNum);
        this.getLogger("onlineNum").info((Object) formatter.flush());
    }

    public void tradeLog(final int _accountID, final int _userID, final String _nickname, final String _mobile, final int _money, final String _items, final int _receiveAccountID, final int _receiveUserID, final String _receiveNickname, final String _receiveMobile, final int _receiveMoney, final String _receiveItems, final String _mapName) {
        XmlFormatter formatter = new XmlFormatter("tradeLog");
        formatter.append("date", new Date()).append("accountID", _accountID).append("userID", _userID).append("game_id", GmServiceImpl.gameID).append("server_id", GmServiceImpl.serverID).append("nickname", _nickname).append("msisdn", _mobile).append("money", _money).append("goodsName", _items).append("receiveAccountID", _receiveAccountID).append("receiveUserID", _receiveUserID).append("receiveNickname", _receiveNickname).append("receiveMobile", _receiveMobile).append("receiveMoney", _receiveMoney).append("receiveItems", _receiveItems).append("mapName", _mapName);
        this.getLogger("trade").info((Object) formatter.flush());
    }

    public void moneyChangeLog(final int _accountID, final int _userID, final String _nickname, final String _mobile, final String _cause, final int _number) {
        XmlFormatter formatter = new XmlFormatter("moneyChangeLog");
        formatter.append("date", new Date()).append("accountID", _accountID).append("userID", _userID).append("game_id", GmServiceImpl.gameID).append("server_id", GmServiceImpl.serverID).append("nickname", _nickname).append("msisdn", _mobile).append("cause", _cause).append("number", _number);
        this.getLogger("moneyChange").info((Object) formatter.flush());
    }

    public void depotChangeLog(final int _accountID, final int _userID, final String _nickname, final String _mobile, final int _itemID, final String _itemName, final int _number, final String _option, final String _mapName) {
        XmlFormatter formatter = new XmlFormatter("depotChangeLog");
        formatter.append("date", new Date()).append("accountID", _accountID).append("userID", _userID).append("game_id", GmServiceImpl.gameID).append("server_id", GmServiceImpl.serverID).append("nickname", _nickname).append("msisdn", _mobile).append("goodsID", _itemID).append("goodsName", _itemName).append("goodsNumber", _number).append("option", _option).append("mapName", _mapName);
        this.getLogger("depotChange").info((Object) formatter.flush());
    }

    public void guildChangeLog(final int _userID, final String _nickname, final int _guildID, final String _guildName, final int _guildNumber, final int _leaderID, final String _leaderNickname, final String _option) {
        XmlFormatter formatter = new XmlFormatter("guildMemberChangeLog");
        formatter.append("date", new Date()).append("userID", _userID).append("nickname", _nickname).append("game_id", GmServiceImpl.gameID).append("server_id", GmServiceImpl.serverID).append("guildID", _guildID).append("guildName", _guildName).append("guildNumber", _guildNumber).append("lederID", _leaderID).append("leaderNickname", _leaderNickname).append("option", _option);
        this.getLogger("guildChange").info((Object) formatter.flush());
    }

    public void guildMemberChangeLog(final int _userID, final String _nickname, final int _guildID, final String _guildName, final int _receiveUserID, final String _receiveNickname, final String _option) {
        XmlFormatter formatter = new XmlFormatter("guildMemberChangeLog");
        formatter.append("date", new Date()).append("userID", _userID).append("nickname", _nickname).append("game_id", GmServiceImpl.gameID).append("server_id", GmServiceImpl.serverID).append("guildID", _guildID).append("guildName", _guildName).append("receiverUserID", _receiveUserID).append("receiverNickname", _receiveNickname).append("option", _option);
        this.getLogger("guildMemberChange").info((Object) formatter.flush());
    }

    public void goodsChangeLog(final HeroPlayer _player, final Goods _goods, final int _itemNum, final LoctionLog _loction, final FlowLog _flow, final CauseLog _cause) {
        if (_goods.getTrait().value() >= EGoodsTrait.YU_ZHI.value()) {
            XmlFormatter formatter = new XmlFormatter("goodsChangeLog");
            formatter.append("date", new Date()).append("game_id", GmServiceImpl.gameID).append("server_id", GmServiceImpl.serverID).append("accountID", _player.getLoginInfo().accountID).append("userID", _player.getUserID()).append("nickname", _player.getName()).append("loginMsisdn", _player.getLoginInfo().loginMsisdn).append("goodsID", _goods.getID()).append("goodsName", _goods.getName()).append("itemNum", _itemNum).append("mapName", _player.where().getName()).append("loction", _loction.getName()).append("flow", _flow.getName()).append("cause", _cause.getName());
            this.getLogger("goodsChange").info((Object) formatter.flush());
        }
    }

    public void petChangeLog(final HeroPlayer _player, final Pet _pet, final int _itemNum, final LoctionLog _loction, final FlowLog _flow, final CauseLog _cause) {
        Formatter formatter = new Formatter();
        formatter.append(new Date()).append(_player.getLoginInfo().accountID).append(_player.getUserID()).append(_player.getName()).append(_player.getLoginInfo().loginMsisdn).append(_pet.id).append(_pet.name).append(_itemNum).append(_player.where().getName()).append(_loction.getName()).append(_flow.getName()).append(_cause.getName());
        this.getLogger("petChange").info((Object) formatter.flush());
    }

    public void letterLog(final String _nickname, final int _letterID, final String _receiveNickname, final String _letterTitle, final String _letterContent) {
        XmlFormatter formatter = new XmlFormatter("letterLog");
        formatter.append("date", new Date()).append("nickname", _nickname).append("letterID", _letterID).append("game_id", GmServiceImpl.gameID).append("server_id", GmServiceImpl.serverID).append("receiveNickname", _receiveNickname).append("letterTitle", _letterTitle).append("letterContent", _letterContent);
        this.getLogger("letter").info((Object) formatter.flush());
    }

    public void mailLog(final int _accountID, final int _userID, final String _nickname, final String _mobile, final int _mailID, final int _receiveUserID, final String _receiveNickname, final int _money, final int _point, final String _items) {
        XmlFormatter formatter = new XmlFormatter("mailLog");
        formatter.append("date", new Date()).append("accountID", _accountID).append("userID", _userID).append("game_id", GmServiceImpl.gameID).append("server_id", GmServiceImpl.serverID).append("nickname", _nickname).append("msisdn", _mobile).append("mailID", _mailID).append("receiverUserID", _receiveUserID).append("receiveNickname", _receiveNickname).append("money", _money).append("point", _point).append("items", _items);
        this.getLogger("mail").info((Object) formatter.flush());
    }

    public void chargeLog(final String _content) {
        XmlFormatter formatter = new XmlFormatter("charge");
        formatter.append("date", new Date()).append("content", _content);
        this.getLogger("charge").info((Object) formatter.flush());
    }

    public void chargeGenLog(final int _accountID, final String _account, final int _userID, final String _nickname, final String _msisdn, final String _tranID, final String _fpcode, final byte _forother, final String _otherNickname, final int _otherAccountID, final String _rechargeType, final String _syncresult, final String _status_code, final String _status_desc) {
        XmlFormatter formatter = new XmlFormatter("chargeGenLog");
        formatter.append("date", new Date()).append("accountID", _accountID).append("username", _account).append("userID", _userID).append("nickname", _nickname).append("msisdn", _msisdn).append("transID", _tranID).append("fpcode", _fpcode).append("forother", _forother).append("otherNickname", _otherNickname).append("otherAccountID", _otherAccountID).append("rechargeType", _rechargeType).append("syncresult", _syncresult).append("statusCode", _status_code).append("statusDesc", _status_desc);
        this.getLogger("chargeGen").info((Object) formatter.flush());
    }

    public void chargeCardCallBackLog(final String _resultType, final String _resultCode, final String _account, final int _userID, final String _msisdn, final String _nickname, final Timestamp rechargetime, final int _accountID, final int _rechargetype, final int _publisher, final int _paytype, final String _paytransid, final String _orderid, final String memo, final int _price) {
        XmlFormatter formatter = new XmlFormatter("chargeCardFeedBack");
        formatter.append("date", new Date()).append("resultType", _resultType).append("accountName", _account).append("user_id", _accountID).append("role_id", _userID).append("role_name", _nickname).append("rechargetype", _rechargetype).append("game_id", GmServiceImpl.gameID).append("server_id", GmServiceImpl.serverID).append("rechargetime", rechargetime).append("logtime", DateTime.getTime(System.currentTimeMillis())).append("msisdn", _msisdn).append("publisher", _publisher).append("paytype", _paytype).append("result", _resultCode).append("paytransid", _paytransid).append("orderid", _orderid).append("price", _price).append("remark", memo);
        this.getLogger("chargeCardFeedBack").info((Object) formatter.flush());
    }

    public void pointLog(final String _tranID, final int _accountID, final String _account, final int _userID, final String _nickname, final String _operateType, final int _point, final String _des, final int _publisher, final String _goodslist) {
        XmlFormatter formatter = new XmlFormatter("pointLog");
        formatter.append("date", new Date()).append("paytransid", _tranID).append("user_id", _accountID).append("role_id", _userID).append("game_id", GmServiceImpl.gameID).append("server_id", GmServiceImpl.serverID).append("deductiontime", DateTime.getTime(System.currentTimeMillis())).append("logtime", DateTime.getTime(System.currentTimeMillis())).append("publisher", _publisher).append("points", _point).append("goodslist", _goodslist).append("result", "\u6210\u529f").append("desc", _des).append("accountName", _account).append("nickname", _nickname).append("operateType", _operateType);
        this.getLogger("chargePoint").info((Object) formatter.flush());
    }

    public void switchMapLog(final int _accountID, final String _accountName, final int _userID, final String nickname, final short mapID, final String mapName, final short targetMapID, final String targetMapName, final String mapType, final String targetMapType) {
        XmlFormatter formatter = new XmlFormatter("switchMap");
        formatter.append("date", new Date()).append("accountID", _accountID).append("accountName", _accountName).append("game_id", GmServiceImpl.gameID).append("server_id", GmServiceImpl.serverID).append("roleID", _userID).append("roleName", nickname).append("mapID", mapID).append("mapName", mapName).append("targetMapID", targetMapID).append("targetMapName", targetMapName).append("currType", mapType).append("targetMapType", targetMapType);
        this.getLogger("switchMap").info((Object) formatter.flush());
    }

    public void goodsUsedLog(final int accountID, final String accountName, final int userID, final String nickname, final int goodsID, final String goodsName, final String trait, final String type) {
        XmlFormatter formatter = new XmlFormatter("goodsUsed");
        formatter.append("date", new Date()).append("game_id", GmServiceImpl.gameID).append("server_id", GmServiceImpl.serverID).append("accountID", accountID).append("accountName", accountName).append("roleID", userID).append("roleName", nickname).append("goodsID", goodsID).append("goodsName", goodsName).append("trait", trait).append("type", type);
        this.getLogger("goodsUsed").info((Object) formatter.flush());
    }

    public void taskPushOption(final int accountID, final String accountName, final int userID, final String nickname, final int pushID, final int step, final String option, final int price) {
        XmlFormatter formatter = new XmlFormatter("taskPushOption");
        formatter.append("date", new Date()).append("game_id", GmServiceImpl.gameID).append("server_id", GmServiceImpl.serverID).append("accountID", accountID).append("accountName", accountName).append("roleID", userID).append("roleName", nickname).append("pushID", pushID).append("step", step).append("option", option).append("price", price);
        this.getLogger("taskPushOption").info((Object) formatter.flush());
    }

    public void taskFinished(final int accountID, final String accountName, final int userID, final String nickname, final int taskID, final String taskName) {
        XmlFormatter formatter = new XmlFormatter("taskFinished");
        formatter.append("date", new Date()).append("game_id", GmServiceImpl.gameID).append("server_id", GmServiceImpl.serverID).append("accountID", accountID).append("accountName", accountName).append("roleID", userID).append("roleName", nickname).append("taskID", taskID).append("taskName", taskName);
        this.getLogger("taskFinishedLogs").info((Object) formatter.flush());
    }

    public void monsterLegacy(final String monsterID, final String monsterName, final int killerNum, final boolean isGroup, final int money, final int kind, final int[] goodsIDS, final String[] goodsNames, final int[] goodsNums, final String[] goodsTypes) {
        XmlFormatter formatter = new XmlFormatter("monsterLegacy");
        formatter.append("date", new Date()).append("monsterID", monsterID).append("monsterName", monsterName).append("killerNum", killerNum).append("game_id", GmServiceImpl.gameID).append("server_id", GmServiceImpl.serverID).append("isGroup", isGroup ? "\u7ec4\u961f" : "\u5355\u4eba").append("money", money).append("kind", kind);
        StringBuffer sf = new StringBuffer();
        for (int i = 0; i < goodsIDS.length; ++i) {
            sf.append(goodsIDS[i]).append(",");
        }
        sf.deleteCharAt(sf.length() - 1);
        formatter.append("goodsIDS", sf.toString());
        sf = new StringBuffer("");
        for (int i = 0; i < goodsNames.length; ++i) {
            sf.append(goodsNames[i]).append(",");
        }
        sf.deleteCharAt(sf.length() - 1);
        formatter.append("goodsNames", sf.toString());
        sf = new StringBuffer("");
        for (int i = 0; i < goodsNums.length; ++i) {
            sf.append(goodsNums[i]).append(",");
        }
        sf.deleteCharAt(sf.length() - 1);
        formatter.append("goodsNums", sf.toString());
        sf = new StringBuffer("");
        for (int i = 0; i < goodsTypes.length; ++i) {
            sf.append(goodsTypes[i]).append(",");
        }
        sf.deleteCharAt(sf.length() - 1);
        formatter.append("goodsTypes", sf.toString());
        this.getLogger("monsterLegacy").info((Object) formatter.flush());
    }

    public void getMonsterLegacyGoodsLog(final int accountID, final String accountName, final int userID, final String nickname, final int goodsID, final String goodsName, final int goodsNum, final String trait, final String goodsType) {
        XmlFormatter formatter = new XmlFormatter("getMonsterLegacy");
        formatter.append("date", new Date()).append("game_id", GmServiceImpl.gameID).append("server_id", GmServiceImpl.serverID).append("accountID", accountID).append("accountName", accountName).append("roleID", userID).append("roleName", nickname).append("goodsID", goodsID).append("goodsName", goodsName).append("goodsNum", goodsNum).append("trait", trait).append("goodsType", goodsType);
        this.getLogger("getMonsterLegacy").info((Object) formatter.flush());
    }

    public void feeLog(final String ngUrlID, final int _accountID, final String _toolsID, final String mobileUserID, final int userID, final int publisher, final ServiceType serviceType, final int price, final int sumPrice, final String transID, final String resCode, final String result) {
        XmlFormatter formatter = new XmlFormatter("fee");
        formatter.append("date", new Date()).append("game_id", GmServiceImpl.gameID).append("server_id", GmServiceImpl.serverID).append("accountID", _accountID).append("roleID", userID).append("publisher", publisher).append("servicetype", serviceType.getName()).append("toolsID", _toolsID).append("mobileUserID", mobileUserID).append("ngID", ngUrlID).append("price", price).append("sumPrice", sumPrice).append("transID", transID).append("resCode", resCode).append("result", result);
        this.getLogger("feeLogs").info((Object) formatter.flush());
    }

    public void numberErrorLog(final int accountID, final String accountName, final int userID, final String nickname, final int number, final String desc) {
        XmlFormatter formatter = new XmlFormatter("numberError");
        formatter.append("data", new Date()).append("game_id", GmServiceImpl.gameID).append("server_id", GmServiceImpl.serverID).append("accountID", accountID).append("accountName", accountName).append("roleID", userID).append("nickname", nickname).append("number", number).append("desc", desc);
        this.getLogger("numberError").info((Object) formatter.flush());
    }
}
