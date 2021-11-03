// Decompiled with: Procyon 0.5.36
// Class Version: 8
package hero.charge.service;

import java.text.DecimalFormat;
import hero.charge.message.PointAmountNotify;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.log.service.LogServiceImpl;
import java.io.IOException;
import java.net.MalformedURLException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.HttpURLConnection;
import hero.log.service.ServiceType;
import hero.charge.FeeIni;
import hero.gm.service.GmDAO;
import java.util.Iterator;
import hero.charge.RechargeTypeCard;
import hero.charge.FPType;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import java.io.File;
import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import yoyo.service.base.session.Session;
import java.util.Date;
import hero.gm.service.GmServiceImpl;
import java.util.ArrayList;
import java.text.NumberFormat;
import hero.charge.FeePointInfo;
import hero.charge.FeeType;
import hero.charge.RequestInfo;
import java.util.List;
import java.text.SimpleDateFormat;
import org.apache.log4j.Logger;
import yoyo.service.base.AbsServiceAdaptor;

public class ChargeServiceImpl extends AbsServiceAdaptor<ChargeConfig> {

    private static Logger log;
    private static ChargeServiceImpl instance;
    private static SimpleDateFormat format;
    private List<RequestInfo> infoList;
    private List<FeeType> feeTypeList;
    private List<FeePointInfo> fpList;
    private static final byte chargepid = 1;
    public static final byte PAYTYPE_SZF = 1;
    public static final byte PAYTYPE_NG = 2;
    public static final byte RECHARGE_TYPE_ME = 1;
    public static final byte RECHARGE_TYPE_OTHER = 2;
    private static final String REQUEST_METHOD_POST = "POST";
    private static final String REQUEST_METHOD_GET = "GET";
    public static final String CK = "ck";
    public static final String CA = "ca";
    public static final String CHARGE_TYPE = "szf";
    static volatile int transid;
    private static NumberFormat numberFormat;

    private ChargeServiceImpl() {
        this.feeTypeList = new ArrayList<FeeType>();
        this.fpList = new ArrayList<FeePointInfo>();
        this.config = new ChargeConfig();
        try {
            this.infoList = new ArrayList<RequestInfo>();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static ChargeServiceImpl getInstance() {
        if (ChargeServiceImpl.instance == null) {
            ChargeServiceImpl.instance = new ChargeServiceImpl();
        }
        return ChargeServiceImpl.instance;
    }

    public synchronized String getTransIDGen() {
        String string = String.valueOf(GmServiceImpl.gameID) + "-" + GmServiceImpl.serverID + "-" + ChargeServiceImpl.format.format(new Date());
        ChargeServiceImpl.log.debug("transID = " + string);
        return string;
    }

    @Override
    public void createSession(final Session session) {
        HeroPlayer playerByUserID = PlayerServiceImpl.getInstance().getPlayerByUserID(session.userID);
        if (playerByUserID != null) {
            ChargeDAO.loadTimeInfo(playerByUserID);
        }
    }

    public void start() {
        try {
            this.readChargeList();
            MallGoodsDict.getInstance().load(((ChargeConfig) this.config).mall_goods_data_path);
            ExperienceBookService.getInstance().start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void readChargeList() {
        try {
            File[] listFiles;
            for (int length = (listFiles = new File(((ChargeConfig) this.config).url_charge_info_path).listFiles()).length, i = 0; i < length; ++i) {
                File file = listFiles[i];
                if (file.getName().endsWith(".xml")) {
                    Iterator elementIterator = new SAXReader().read(file).getRootElement().elementIterator();
                    while (elementIterator.hasNext()) {
                        FeePointInfo feePointInfo = new FeePointInfo();
                        Element element = (Element) elementIterator.next();
                        feePointInfo.id = Byte.parseByte(element.elementTextTrim("id"));
                        feePointInfo.fpcode = element.elementTextTrim("fpcode");
                        feePointInfo.name = element.elementTextTrim("name");
                        feePointInfo.price = Integer.parseInt(element.elementTextTrim("price"));
                        feePointInfo.presentPoint = Integer.parseInt(element.elementTextTrim("present_point"));
                        feePointInfo.typeID = Byte.parseByte(element.elementTextTrim("typeID"));
                        feePointInfo.type = FPType.getFPTypeByName(element.elementTextTrim("feetype"));
                        feePointInfo.desc = element.elementTextTrim("desc");
                        ChargeServiceImpl.log.debug("fp name=" + feePointInfo.name + ",price=" + feePointInfo.price + ",type=" + feePointInfo.type);
                        this.fpList.add(feePointInfo);
                    }
                }
            }
            File file2 = new File(((ChargeConfig) this.config).url_charge_type_path);
            ChargeServiceImpl.log.debug("datapath2 = " + file2);
            File[] listFiles2 = file2.listFiles();
            ChargeServiceImpl.log.debug("dataFileList2 size = " + listFiles2.length);
            File[] array;
            for (int length2 = (array = listFiles2).length, j = 0; j < length2; ++j) {
                File file3 = array[j];
                if (file3.getName().endsWith(".xml")) {
                    Iterator elementIterator2 = new SAXReader().read(file3).getRootElement().elementIterator();
                    while (elementIterator2.hasNext()) {
                        FeeType feeType = new FeeType();
                        Element element2 = (Element) elementIterator2.next();
                        feeType.id = Byte.parseByte(element2.elementTextTrim("id"));
                        feeType.name = element2.elementTextTrim("name");
                        String elementTextTrim = element2.elementTextTrim("card_type");
                        if (elementTextTrim != null) {
                            feeType.cardType = RechargeTypeCard.getCardTypeByName(elementTextTrim);
                        }
                        feeType.desc = element2.elementTextTrim("desc");
                        feeType.type = FPType.getFPTypeByName(element2.elementTextTrim("feetype"));
                        ChargeServiceImpl.log.debug("feetype name=" + feeType.name + ",cardtype=" + feeType.cardType + ",type=" + feeType.type);
                        this.feeTypeList.add(feeType);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        ChargeServiceImpl.log.info("fpList size=" + this.fpList.size());
        ChargeServiceImpl.log.info("feeTypeList size =" + this.feeTypeList.size());
    }

    public FeePointInfo getFpcodeByTypeAndPrice(final FPType fpType, final int n) {
        for (final FeePointInfo feePointInfo : this.fpList) {
            if (feePointInfo.type == fpType && feePointInfo.price == n) {
                return feePointInfo;
            }
        }
        return null;
    }

    public FeePointInfo getFpcodeByTypeAndPrice(final byte b) {
        for (final FeePointInfo feePointInfo : this.fpList) {
            if (feePointInfo.id == b) {
                return feePointInfo;
            }
        }
        return null;
    }

    public FeePointInfo getFpInfoByFpcodeAndPrice(final String s, final int n, final FPType fpType) {
        for (final FeePointInfo feePointInfo : this.fpList) {
            if (feePointInfo.fpcode.equals(s) && feePointInfo.price == n && feePointInfo.type == fpType) {
                return feePointInfo;
            }
        }
        return null;
    }

    public List<FeeType> getFeeTypeListForRecharge() {
        ArrayList<FeeType> list = new ArrayList<FeeType>();
        for (final FeeType feeType : this.feeTypeList) {
            if (feeType.type == FPType.CHARGE) {
                list.add(feeType);
            }
        }
        ChargeServiceImpl.log.info("rechargeFeeList size=" + list.size());
        return list;
    }

    public List<FeePointInfo> getFpListForRecharge() {
        ArrayList<FeePointInfo> list = new ArrayList<FeePointInfo>();
        Iterator<FeePointInfo> iterator = this.fpList.iterator();
        while (iterator.hasNext()) {
            FeePointInfo clone = iterator.next().clone();
            if (clone.type == FPType.CHARGE) {
                int presentPoint = GmDAO.getPresentPoint(clone.price);
                if (presentPoint > 0) {
                    FeePointInfo feePointInfo = clone;
                    feePointInfo.name = String.valueOf(feePointInfo.name) + "（活动赠送 " + presentPoint + " 点）";
                }
                list.add(clone);
            }
        }
        ChargeServiceImpl.log.info("rechargeFeePointInfoList size=" + list.size());
        return list;
    }

    public FeePointInfo getFeePointInfoById(final byte b) {
        for (final FeePointInfo feePointInfo : this.fpList) {
            if (feePointInfo.id == b) {
                return feePointInfo;
            }
        }
        return null;
    }

    public FeeType getFeeTypeById(final byte b) {
        for (final FeeType feeType : this.feeTypeList) {
            if (feeType.id == b) {
                return feeType;
            }
        }
        return null;
    }

    public FeeIni getFeeIniForTask(final int n, final int n2, final String s, final String s2, final String s3, final int n3, final int sumPrice) {
        FeeIni feeIni = new FeeIni();
        String transIDGen = this.getTransIDGen();
        ChargeServiceImpl.log.debug("给任务计费的接口,sumprice=" + sumPrice);
        FeePointInfo fpcodeByTypeAndPrice = this.getFpcodeByTypeAndPrice(FPType.FEE, sumPrice);
        ChargeServiceImpl.log.debug("feepointInfo = " + fpcodeByTypeAndPrice);
        if (fpcodeByTypeAndPrice == null) {
            feeIni.status = 1;
            return feeIni;
        }
        String fpcode = fpcodeByTypeAndPrice.fpcode;
        ChargeServiceImpl.log.debug("给任务计费的接口,根据价格获取的计费伪码:fpcode=" + fpcode);
        FeeIni feeIni2 = this.getFeeIni(n, n2, s, fpcode, transIDGen, s2, s3, n3, "ck");
        feeIni2.transID = transIDGen;
        feeIni2.sumPrice = sumPrice;
        if (feeIni2.status == 0 && feeIni2.feeType.equals("sms")) {
            this.saveFeeIniInfo(n, n2, s3, sumPrice, transIDGen);
        }
        return feeIni2;
    }

    public void saveFeeIniInfo(final int n, final int n2, final String s, final int n3, final String s2) {
        ChargeDAO.saveSmsFeeIni(n, n2, s2, s, n3, GmServiceImpl.serverID);
    }

    public FeeIni getFeeIni(final int n, final int n2, final String s, final String s2, final String s3, final String s4, final String s5, final int n3, final String s6) {
        FeeIni feeIni = new FeeIni();
        String fee_ini_url = getInstance().getConfig().fee_ini_url;
        StringBuffer sb = new StringBuffer();
        sb.append("mid=").append(n).append("&").append("userid=").append(n).append("&").append("roleid=").append(n2).append("&").append("swcode=").append(s).append("&").append("fpcode=").append(s2).append("&").append("paytransid=").append(s3).append("&").append("msisdn=").append(s4).append("&").append("user_id=").append(s5).append("&").append("ditchid=").append(n3).append("&").append("gameid=").append(GmServiceImpl.gameID).append("&").append("serverid=").append(GmServiceImpl.serverID);
        ChargeServiceImpl.log.info("get fee ini parsm=" + (Object) sb);
        try {
            String requestUrl = this.requestUrl(String.valueOf(fee_ini_url) + "?" + sb.toString(), null, false, "GET");
            if (requestUrl.trim().length() > 0) {
                if (requestUrl.substring(0, 1).equals("0")) {
                    String[] split = requestUrl.split("#");
                    feeIni.status = Integer.parseInt(split[0]);
                    feeIni.feeType = split[1];
                    feeIni.feeCode = split[2];
                    feeIni.feeUrlID = split[3];
                    feeIni.price = Integer.parseInt(split[4]);
                } else {
                    feeIni.status = Integer.parseInt(requestUrl);
                }
            } else {
                feeIni.status = 1;
            }
        } catch (Exception ex) {
            feeIni.status = 2;
            ChargeServiceImpl.log.error("获取计费配置信息 error: ", ex);
        }
        return feeIni;
    }

    public String chargeUpSZF(final String s, final int n, final int n2, final int n3, final String s2, final String s3, final int n4, final byte b, final String s4, int n5, final ServiceType serviceType, final int n6, String s5, final String s6) {
        String szf_rechange_url = ((ChargeConfig) this.config).szf_rechange_url;
        if (s5 == null || s5.trim().length() == 0) {
            s5 = "127.0.0.1";
        }
        String s7 = "0";
        if (s4.startsWith("189")) {
            s7 = "1";
        }
        n5 = n4 * 100;
        StringBuffer sb = new StringBuffer();
        sb.append("utp=").append(s7).append("&").append("uip=").append(s5).append("&").append("feetype=").append("szf").append("&").append("paymoney=").append(n5).append("&").append("cardsum=").append(n4).append("&").append("cardnum=").append(s2).append("&").append("cardpass=").append(s3).append("&").append("cardtypecombine=").append(b).append("&").append("paytransid=").append(s).append("&").append("mid=").append(n3).append("&").append("userid=").append(n3).append("&").append("roleid=").append(n2).append("&").append("gameid=").append(GmServiceImpl.gameID).append("&").append("serverid=").append(GmServiceImpl.serverID).append("&").append("bindmobile=").append(s6).append("&").append("mobile=").append(s4).append("&").append("pid=").append(n6).append("&").append("servicetype=").append(serviceType.getId()).append("&").append("channel_id=").append(n6).append("&").append("dopoint=").append("ca");
        ChargeServiceImpl.log.info("szf recharge parmas=" + sb.toString());
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(szf_rechange_url).openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpURLConnection.getOutputStream());
            outputStreamWriter.write(sb.toString());
            outputStreamWriter.flush();
            ChargeServiceImpl.log.info("responsecode = " + httpURLConnection.getResponseCode());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String replaceAll = bufferedReader.readLine().replaceAll("\r\n", "");
            bufferedReader.close();
            httpURLConnection.disconnect();
            ChargeServiceImpl.log.info("神州付充值同步返回的结果: " + replaceAll);
            return replaceAll;
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex2) {
            ex2.printStackTrace();
        }
        return null;
    }

    public String chargeUpNg(final String s, final String s2, final int n, final int n2, final int n3, final String s3, final String s4, final ServiceType serviceType, final int n4, final String s5) {
        String s6 = ((ChargeConfig) this.config).feeIdsMap.get(s);
        StringBuffer sb = new StringBuffer();
        sb.append("paytransid=").append(s2).append("&").append("mid=").append(n2).append("&").append("userid=").append(n2).append("&").append("roleid=").append(n3).append("&").append("gameid=").append(GmServiceImpl.gameID).append("&").append("serverid=").append(GmServiceImpl.serverID).append("&").append("mobile=").append(s3).append("&").append("bindmobile=").append(s4).append("&").append("servicetype=").append(serviceType.getId()).append("&").append("channel_id=").append(n4).append("&").append("pid=").append(1).append("&").append("dopoint=").append(s5);
        return this.requestUrl(s6, sb.toString(), false, "GET");
    }

    private String requestUrl(final String s, final String s2, final boolean b) {
        return this.requestUrl(s, s2, b, "POST");
    }

    private String requestUrl(final String s, final String s2, final boolean b, final String requestMethod) {
        StringBuffer sb = new StringBuffer("");
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(s).openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod(requestMethod);
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setDefaultUseCaches(false);
            if (requestMethod.equals("POST")) {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpURLConnection.getOutputStream());
                outputStreamWriter.write(s2);
                outputStreamWriter.flush();
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
            ChargeServiceImpl.log.info("request url = " + s);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
                if (b) {
                    sb.append("$$");
                }
                ChargeServiceImpl.log.info("response str = " + line);
            }
            bufferedReader.close();
            httpURLConnection.disconnect();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex2) {
            ex2.printStackTrace();
        }
        return sb.toString().replaceAll("\r\n", "");
    }

    public String queryBalancePoint(final int n) {
        String query_point_url = ((ChargeConfig) this.config).query_point_url;
        StringBuffer sb = new StringBuffer();
        sb.append("userid=").append(n).append("&").append("gameid=").append(GmServiceImpl.gameID);
        return this.requestUrl(query_point_url, sb.toString(), false);
    }

    public int getBalancePoint(final int n) {
        int int1 = 0;
        String[] split = this.queryBalancePoint(n).split("#");
        if (split[0].equals("0")) {
            int1 = Integer.parseInt(split[1]);
        }
        ChargeServiceImpl.log.debug(String.valueOf(n) + " -- getBalancePoint = " + int1);
        return int1;
    }

    public String queryConsumeDetail(final int n, final int n2, final int n3, final String s, final String s2) {
        String query_deduct_list_url = ((ChargeConfig) this.config).query_deduct_list_url;
        StringBuffer sb = new StringBuffer();
        sb.append("gameid=").append(GmServiceImpl.gameID).append("&").append("userid=").append(n).append("&").append("roleid=").append(n2).append("&").append("serverid=").append(GmServiceImpl.serverID).append("&").append("querytype=").append(n3);
        sb.append("&").append("stime=").append(s).append("&").append("etime=").append(s2);
        String requestUrl = this.requestUrl(query_deduct_list_url, sb.toString(), true);
        if (requestUrl.indexOf("#") > 0) {
            String[] split = requestUrl.split("\\$\\$");
            StringBuffer sb2 = new StringBuffer();
            String[] array;
            for (int length = (array = split).length, i = 0; i < length; ++i) {
                String[] split2 = array[i].split("#");
                sb2.append(split2[0]).append(".").append(split2[1]).append(".");
                sb2.append(MallGoodsDict.getInstance().getMallGoods(Integer.parseInt(split2[2])).name).append(".").append(split2[3]).append("点");
                sb2.append("#HH");
            }
            return sb2.toString();
        }
        return requestUrl.replaceAll("\\$\\$", "#HH");
    }

    public String queryChargeUpDetail(final int n, final int n2, final int n3, final String s, final String s2) {
        String query_rechage_list_url = ((ChargeConfig) this.config).query_rechage_list_url;
        StringBuffer sb = new StringBuffer();
        sb.append("gameid=").append(GmServiceImpl.gameID).append("&").append("userid=").append(n).append("&").append("roleid=").append(n2).append("&").append("serverid=").append(GmServiceImpl.serverID).append("&").append("querytype=").append(n3);
        sb.append("&").append("stime=").append(s).append("&").append("etime=").append(s2);
        ChargeServiceImpl.log.debug("query charge up params: " + sb.toString());
        String requestUrl = this.requestUrl(query_rechage_list_url, sb.toString(), true);
        if (requestUrl.indexOf("#") > 0) {
            String[] split = requestUrl.split("\\$\\$");
            StringBuffer sb2 = new StringBuffer();
            String[] array;
            for (int length = (array = split).length, i = 0; i < length; ++i) {
                String[] split2 = array[i].split("#");
                sb2.append(split2[0]).append(".").append(split2[1]).append(".").append(split2[2]).append("点");
                sb2.append("#HH");
            }
            return sb2.toString();
        }
        return requestUrl.replaceAll("\\$\\$", "#HH");
    }

    public boolean addPoint(final HeroPlayer heroPlayer, final String s, final int n, final byte b, final int n2, final ServiceType serviceType) {
        boolean updatePointAmount = false;
        String add_point_url = ((ChargeConfig) this.config).add_point_url;
        StringBuffer sb = new StringBuffer("");
        sb.append("gameid=").append(GmServiceImpl.gameID).append("&").append("serverid=").append(GmServiceImpl.serverID).append("&").append("mid=").append(heroPlayer.getLoginInfo().accountID).append("&").append("userid=").append(heroPlayer.getLoginInfo().accountID).append("&").append("roleid=").append(heroPlayer.getUserID()).append("&").append("paytransid=").append(s).append("&").append("msisdn=").append(heroPlayer.getLoginInfo().loginMsisdn).append("&").append("point=").append(n).append("&").append("pid=").append(1).append("&").append("rechargetype=").append(b).append("&").append("channel_id=").append(n2).append("&").append("servicetype=").append(serviceType.getId());
        ChargeServiceImpl.log.info(String.valueOf(add_point_url) + "?" + sb.toString());
        String requestUrl = this.requestUrl(add_point_url, sb.toString(), false);
        if (requestUrl.trim().length() > 0 && requestUrl.split("#")[0].equals("0") && heroPlayer != null) {
            updatePointAmount = this.updatePointAmount(heroPlayer, n);
            LogServiceImpl.getInstance().pointLog(s, heroPlayer.getLoginInfo().accountID, heroPlayer.getLoginInfo().username, heroPlayer.getUserID(), heroPlayer.getName(), "增加", n, serviceType.getName(), heroPlayer.getLoginInfo().publisher, "");
        }
        return updatePointAmount;
    }

    public boolean reducePoint(final HeroPlayer heroPlayer, final int n, final int n2, final String s, final int n3, final ServiceType serviceType) {
        boolean updatePointAmount = false;
        String sub_point_url = ((ChargeConfig) this.config).sub_point_url;
        StringBuffer sb = new StringBuffer();
        sb.append("gameid=").append(GmServiceImpl.gameID).append("&").append("serverid=").append(GmServiceImpl.serverID).append("&").append("mid=").append(heroPlayer.getLoginInfo().accountID).append("&").append("userid=").append(heroPlayer.getLoginInfo().accountID).append("&").append("roleid=").append(heroPlayer.getUserID()).append("&").append("point=").append(n).append("&").append("toolid=").append(n2).append("&").append("servicetype=").append(serviceType.getId()).append("&").append("channel_id=").append(heroPlayer.getLoginInfo().publisher);
        ChargeServiceImpl.log.info(sb.toString());
        String requestUrl = this.requestUrl(sub_point_url, sb.toString(), false);
        if (requestUrl.trim().length() > 0) {
            String[] split = requestUrl.split("#");
            if (split[0].equals("0")) {
                updatePointAmount = this.updatePointAmount(heroPlayer, -n);
                LogServiceImpl.getInstance().pointLog(split[2], heroPlayer.getLoginInfo().accountID, heroPlayer.getLoginInfo().username, heroPlayer.getUserID(), heroPlayer.getName(), "扣点", n, "购买'" + s + "', 数量:" + n3 + " ", heroPlayer.getLoginInfo().publisher, s);
                LogServiceImpl.getInstance().chargeLog(String.valueOf(heroPlayer.getName()) + " 购买'" + s + "', 数量:" + n3 + " 扣点成功," + split[1]);
            } else {
                ResponseMessageQueue.getInstance().put(heroPlayer.getMsgQueueIndex(), new Warning(split[1], (byte) 1));
                LogServiceImpl.getInstance().chargeLog(String.valueOf(heroPlayer.getName()) + " 购买'" + s + "', 数量:" + n3 + " 扣点失败");
            }
        }
        return updatePointAmount;
    }

    public boolean updatePointAmount(final HeroPlayer heroPlayer, final int n) {
        return this.updatePointAmount(heroPlayer, n, null);
    }

    public boolean updatePointAmount(final HeroPlayer heroPlayer, final int n, final ServiceType serviceType) {
        if (heroPlayer.isEnable()) {
            ChargeServiceImpl.log.debug("update point amoutn .... point=" + n);
            if (n > 0) {
                if (-1 == heroPlayer.getChargeInfo().addPointAmount(n)) {
                    return false;
                }
                ChargeServiceImpl.log.debug("add point amount ...");
                String s = "获得游戏点数：";
                if (serviceType != null && (serviceType == ServiceType.ACTIVE_PRESENT || serviceType == ServiceType.PRESENT)) {
                    s = "获得额外游戏点数：";
                }
                ResponseMessageQueue.getInstance().put(heroPlayer.getMsgQueueIndex(), new Warning(String.valueOf(s) + n, (byte) 0));
            }
            if (n < 0) {
                if (-1 == heroPlayer.getChargeInfo().reducePointAmount(-n)) {
                    return false;
                }
                ChargeServiceImpl.log.debug("reducePointAmount point amount ...");
                ResponseMessageQueue.getInstance().put(heroPlayer.getMsgQueueIndex(), new Warning("消耗游戏点数：" + -n, (byte) 0));
            }
            ResponseMessageQueue.getInstance().put(heroPlayer.getMsgQueueIndex(), new PointAmountNotify(heroPlayer.getChargeInfo().pointAmount));
        }
        return true;
    }

    public boolean[] ngBuyMallTools(final String s, final int n, final String s2, final String s3, final int n2, final int n3, final ServiceType serviceType, final int n4, final int n5) {
        ChargeServiceImpl.log.info("网游计费 sumprice=" + n5 + ",price=" + n4);
        int n6 = n5 / n4;
        ChargeServiceImpl.log.info("网游计费请求次数：count=" + n6);
        boolean[] array = new boolean[n6];
        for (int i = 0; i < n6; ++i) {
            array[i] = this.ngSingleBuyMallTools(s, n, s2, s3, n2, n3, serviceType, n4, n5);
        }
        return array;
    }

    private boolean ngSingleBuyMallTools(final String s, final int n, final String s2, final String s3, final int n2, final int n3, final ServiceType serviceType, final int n4, final int n5) {
        ChargeServiceImpl.log.info("ngBuyMallTool ngurlid=" + s + ",accountID=" + n + ",toolsid=" + s2);
        StringBuffer sb = new StringBuffer();
        sb.append("mid=").append(n).append("&").append("userid=").append(n).append("&").append("roleid=").append(n2).append("&").append("gameid=").append(GmServiceImpl.gameID).append("&").append("serverid=").append(GmServiceImpl.serverID).append("&").append("mobile=").append(s3).append("&").append("servicetype=").append(serviceType.getId()).append("&").append("channel_id=").append(n3).append("&").append("pid=").append(1).append("&").append("dopoint=").append("ck");
        String s4 = ((ChargeConfig) this.config).feeIdsMap.get(s);
        StringBuffer sb2 = new StringBuffer();
        ++ChargeServiceImpl.transid;
        String s5 = "";
        if (s.equals("jiutian")) {
            s5 = "C00227" + ChargeServiceImpl.format.format(new Date()) + ChargeServiceImpl.numberFormat.format(ChargeServiceImpl.transid);
            s4 = String.valueOf(s4) + "?paytransid=" + s5 + "&" + (Object) sb;
            ChargeServiceImpl.log.debug("jiutian url=" + s4);
            sb2.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append("<request>").append("<msgType>BuyGameToolReq</msgType>").append("<sender>593</sender>").append("<userId>").append(s3).append("</userId>").append("<cpId>C00227</cpId>").append("<cpServiceId>120122078000</cpServiceId>").append("<consumeCode>").append(s2).append("</consumeCode>").append("<fid>1000</fid>").append("<transIDO>").append(s5).append("</transIDO>").append("<versionId>2_0_0</versionId>").append("</request>");
        }
        if (s.equals("hero")) {
            s5 = "C00216" + ChargeServiceImpl.format.format(new Date()) + ChargeServiceImpl.numberFormat.format(ChargeServiceImpl.transid);
            s4 = String.valueOf(s4) + "?paytransid=" + s5 + "&" + (Object) sb;
            ChargeServiceImpl.log.info("hero url=" + s4);
            sb2.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append("<request>").append("<msgType>BuyGameToolReq</msgType>").append("<sender>592</sender>").append("<userId>").append(s3).append("</userId>").append("<cpId>C00216</cpId>").append("<cpServiceId>120122080000</cpServiceId>").append("<consumeCode>").append(s2).append("</consumeCode>").append("<fid>1000</fid>").append("<transIDO>").append(s5).append("</transIDO>").append("<versionId>2_0_0</versionId>").append("</request>");
        }
        ChargeServiceImpl.log.info("ng buy mall param=" + (Object) sb2);
        String requestUrl = this.requestUrl(s4, sb2.toString(), false);
        ChargeServiceImpl.log.info("ng buy mall tools result=" + requestUrl);
        String[] split = requestUrl.split("#");
        boolean b = false;
        if (split[0].equals("0")) {
            b = true;
        }
        LogServiceImpl.getInstance().feeLog(s, n, s2, s3, n2, n3, serviceType, n4, n5, s5, split[1], b ? "成功" : "失败");
        return b;
    }

    public static void main(final String[] array) {
        String string = String.valueOf(GmServiceImpl.gameID) + "-" + GmServiceImpl.serverID + "-" + ChargeServiceImpl.format.format(new Date());
        ChargeServiceImpl.format.format(new Date());
        ++ChargeServiceImpl.transid;
        FeeIni feeIni = getInstance().getFeeIni(12000, 19362, "1.9.9", "10019", string, "15936599392", "", 0, "ck");
        ChargeServiceImpl.log.info("feeini status=" + feeIni.status);
        ChargeServiceImpl.log.info("feeini feeid=" + feeIni.feeUrlID + ",type=" + feeIni.feeType + ",code=" + feeIni.feeCode + ",price=" + feeIni.price);
    }

    static {
        ChargeServiceImpl.log = Logger.getLogger(ChargeServiceImpl.class);
        ChargeServiceImpl.format = new SimpleDateFormat("yyyyMMddHHmmss");
        ChargeServiceImpl.transid = 0;
        ChargeServiceImpl.numberFormat = new DecimalFormat("000000");
    }
}
