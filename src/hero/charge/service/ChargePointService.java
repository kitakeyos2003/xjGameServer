// 
// Decompiled by Procyon v0.5.36
// 
package hero.charge.service;

import hero.log.service.LogServiceImpl;
import hero.share.service.LogWriter;
import java.text.MessageFormat;
import org.dom4j.Element;
import org.dom4j.Document;
import java.io.UnsupportedEncodingException;
import org.dom4j.DocumentException;
import java.io.ByteArrayInputStream;
import org.dom4j.io.SAXReader;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.FileReader;
import yoyo.service.YOYOSystem;
import java.util.Properties;

public class ChargePointService {

    private static ChargePointService instance;
    public String GAME_ID;
    public String SENDER;
    public String SERVICE_URL;
    private static String ACTION_SUFIX_QUERY;
    private static String ACTION_SUFIX_ADD;
    private static String ACTION_SUFIX_ZERO;
    private String CONFIG_FILE_PATH;
    private Properties properties;

    static {
        ChargePointService.instance = null;
        ChargePointService.ACTION_SUFIX_QUERY = "action=query&gameid={0}&account={1}&Sender={2}";
        ChargePointService.ACTION_SUFIX_ADD = "action=add&gameid={0}&account={1}&point={2}&Sender={3}&trid={4}";
        ChargePointService.ACTION_SUFIX_ZERO = "action=setzero&gameid={0}&account={1}&Sender={2}";
    }

    private ChargePointService() {
        this.GAME_ID = "";
        this.SENDER = "";
        this.SERVICE_URL = "";
        this.CONFIG_FILE_PATH = String.valueOf(YOYOSystem.HOME) + "res/config/charge/gamepoint.config";
        this.properties = new Properties();
        try {
            this.properties.load(new FileReader(this.CONFIG_FILE_PATH));
            this.GAME_ID = this.properties.getProperty("gameid");
            this.SENDER = this.properties.getProperty("Sender");
            this.SERVICE_URL = this.properties.getProperty("GamePointURL");
        } catch (FileNotFoundException ex) {
        } catch (IOException ex2) {
        }
    }

    public static ChargePointService getInstance() {
        if (ChargePointService.instance == null) {
            ChargePointService.instance = new ChargePointService();
        }
        return ChargePointService.instance;
    }

    private Response request(final String _actionSufix) throws IOException {
        URL Url = new URL(String.valueOf(this.SERVICE_URL) + _actionSufix);
        HttpURLConnection connection = (HttpURLConnection) Url.openConnection();
        connection.setConnectTimeout(3000);
        connection.setDoInput(true);
        connection.connect();
        InputStream in = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuffer resp = new StringBuffer();
        for (String temp = reader.readLine(); temp != null; temp = reader.readLine()) {
            resp.append(temp);
        }
        reader.close();
        in.close();
        connection.disconnect();
        return this.parse(resp.toString());
    }

    private Response parse(final String _xmlResp) {
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read((InputStream) new ByteArrayInputStream(_xmlResp.getBytes("UTF-8")));
            Element root = document.getRootElement();
            Element eleResultCode = root.element("ResultCode");
            String resultCode = eleResultCode.getText();
            if (resultCode.equals("0")) {
                Element eAccount = root.element("Account");
                String account = eAccount.getTextTrim();
                Element elePoint = root.element("Results");
                String point = elePoint.getTextTrim();
                Element eleState = root.element("State");
                String state = eleState.getTextTrim();
                Element eleTransID = root.element("TransID");
                String transID = eleTransID.getTextTrim();
                Response resp = new Response(resultCode, account);
                Response.access$0(resp, _xmlResp);
                resp.setPoint(Integer.parseInt(point));
                resp.setState(state);
                resp.setTransID(transID);
                return resp;
            }
            Response resp2 = new Response(resultCode, null);
            return resp2;
        } catch (DocumentException ex) {
        } catch (UnsupportedEncodingException ex2) {
        }
        return null;
    }

    public Response query(final String _account) {
        String actionSufix = MessageFormat.format(ChargePointService.ACTION_SUFIX_QUERY, this.GAME_ID, _account, this.SENDER);
        try {
            Response resp = this.request(actionSufix);
            return resp;
        } catch (Exception e) {
            LogWriter.error("ChargePointService.query exception..._account:" + _account, e);
            return null;
        }
    }

    public Response modify(final String _tranID, final String _account, final int _point) {
        String sPoint = String.valueOf(_point);
        String actionSufix = MessageFormat.format(ChargePointService.ACTION_SUFIX_ADD, this.GAME_ID, _account, sPoint, this.SENDER, _tranID);
        try {
            Response resp = this.request(actionSufix);
            return resp;
        } catch (Exception e) {
            e.printStackTrace();
            LogWriter.error("ChargePointService.modify exception..._account:" + _account + " _point:" + sPoint, e);
            return null;
        }
    }

    public boolean reset(final String _account) {
        String actionSufix = MessageFormat.format(ChargePointService.ACTION_SUFIX_ZERO, this.GAME_ID, _account, this.SENDER);
        try {
            Response resp = this.request(actionSufix);
            if (resp.getResultCode().equals("0")) {
                return true;
            }
            LogServiceImpl.getInstance().chargeLog("ChargePointService.reset error result code..._account:" + _account);
            LogServiceImpl.getInstance().chargeLog(">>>>> " + resp.getResultCode());
            return false;
        } catch (Exception e) {
            LogWriter.error("ChargePointService.reset exception..._msisdn:" + _account, e);
            return false;
        }
    }

    public static void main(final String[] args) {
        String username = "00000000001";
        Response response = getInstance().query(username);
        if (response == null) {
            System.out.println("\u8fd4\u56de\u4e3a\u7a7a");
        } else {
            String resultCode = response.getResultCode();
            if (resultCode.equals("0")) {
                System.out.println("\u6210\u529f");
                System.out.println(response.account);
                System.out.println(response.point);
            } else {
                System.out.println(">>>>> resultCode:" + resultCode);
            }
        }
    }

    public static class Response {

        private String resultCode;
        private String account;
        private int point;
        private String state;
        private String transID;
        private String rawRespXML;
        private String feeResult;

        public Response(final String _resultCode, final String _account) {
            this.transID = null;
            this.resultCode = _resultCode;
            this.account = _account;
        }

        public String getResultCode() {
            return this.resultCode;
        }

        public String getMSISDN() {
            return this.account;
        }

        void setPoint(final int _point) {
            this.point = _point;
        }

        public int getPoint() {
            return this.point;
        }

        void setState(final String _state) {
            this.state = _state;
        }

        public String getState() {
            return this.state;
        }

        public void setTransID(final String _transID) {
            this.transID = _transID;
        }

        public String getTransID() {
            return this.transID;
        }

        public String getRawRespXML() {
            return this.rawRespXML;
        }

        public void setRawRespXML(final String _rawRespXML) {
            this.rawRespXML = _rawRespXML;
        }

        public void setFeeResult(final String _feeResult) {
            this.feeResult = _feeResult;
        }

        public String getFeeResult() {
            return this.feeResult;
        }

        static /* synthetic */ void access$0(final Response response, final String rawRespXML) {
            response.rawRespXML = rawRespXML;
        }
    }
}
