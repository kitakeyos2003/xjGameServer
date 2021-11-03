// 
// Decompiled by Procyon v0.5.36
// 
package hero.dcnbbs.service;

import java.io.IOException;
import java.util.Iterator;
import java.util.ArrayList;
import hero.dcnbbs.Topic;
import java.util.List;
import java.io.UnsupportedEncodingException;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.Document;
import java.io.InputStream;
import org.dom4j.io.SAXReader;
import java.net.URLEncoder;
import org.apache.log4j.Logger;

public class DCNService {

    private static Logger log;
    public static final String ROOT_URL = "http://202.142.19.66:8877/connect/";
    public static final String LOGIN_URL = "http://202.142.19.66:8877/connect/member/login";
    public static final String SYNC_URL = "http://202.142.19.66:8877/connect/member/autosync";
    public static final String BBS_TOPIC_LIST_URL = "http://202.142.19.66:8877/connect/forum/list";
    public static final String TOPIC_URL = "http://202.142.19.66:8877/connect/forum/show-";
    public static final String NEW_TOPIC_URL = "http://202.142.19.66:8877/connect/forum/new-topic";
    public static final String REPLY_TOPIC_URL = "http://202.142.19.66:8877/connect/forum/reply-";
    public static final String UPLOAD_GAMEIMG_URL = "http://202.142.19.66:8877/connect/forum/game-img";
    public static final String NEW_NOTE = "http://202.142.19.66:8877/connect/notice/new-note";
    public static final String API_KEY = "40";
    public static final String CHARSET = "utf-8";
    private static final String KEY = "sdf5432c";
    public static final String CHANNEL_ID = "1001";
    public static final int PAGE_NUM = 10;

    static {
        DCNService.log = Logger.getLogger((Class) DCNService.class);
    }

    public static String getLoginUrl(final String mid, final String username, final String pwd) {
        StringBuffer sbu = new StringBuffer();
        try {
            long time = System.currentTimeMillis();
            String verString = new StringBuffer().append("api_key=").append("40").append("&call_id=").append(time).append("&mid=").append(mid).append("&username=").append(URLEncoder.encode(username, "utf-8")).toString();
            String sig = getSign(new StringBuffer().append(verString).append("&sha256_pwd=").append(M.sha256(pwd, "utf-8").toUpperCase()).append("&secret_key=").append("sdf5432c").toString()).toUpperCase();
            String vc = getSign(new StringBuffer().append(verString).append("&sig=").append(sig).toString()).toUpperCase();
            sbu.append("http://202.142.19.66:8877/connect/member/login").append("?").append(verString).append("&vc=").append(vc).append("&sig=").append(sig);
            DCNService.log.info(("login_url" + sbu.toString()));
        } catch (Exception ex) {
            DCNService.log.error("\u751f\u6210\u767b\u5f55\u5f97url", (Throwable) ex);
        }
        return sbu.toString();
    }

    public static Result login(final String mid, final String username, final String pwd) {
        InputStream reStr = M.httpRequest(getLoginUrl(mid, username, pwd), "\u5f53\u4e50\u7528\u6237" + mid + username + "\u767b\u5f55");
        Result result = new Result();
        result.setResult(false);
        result.setReList("");
        if (reStr != null) {
            try {
                SAXReader reader = new SAXReader();
                Document doc = reader.read(reStr);
                Element root = doc.getRootElement();
                if (root != null) {
                    Attribute attribute = root.attribute("status");
                    if (attribute != null) {
                        String status = attribute.getValue();
                        if (status.equals("0")) {
                            Element user = root.element("user");
                            String mid2 = user.element("mid").getText();
                            result.setResult(true);
                            result.setReList(mid2);
                            result.setDjtk(user.element("djtk").getText());
                            DCNService.log.info(("mid:" + mid2));
                        } else {
                            result.setReList(status);
                        }
                    }
                }
            } catch (Exception e) {
                DCNService.log.error("\u5f53\u4e50\u767b\u9646\u63a5\u53e3", (Throwable) e);
            }
        } else {
            DCNService.log.error(("\u5f53\u4e50\u767b\u5f55\u8bf7\u6c42\u8fd4\u56de" + reStr));
        }
        return result;
    }

    public static String getSyncUrl(final String unique_id, final String playerName, final String pwd) {
        long time = System.currentTimeMillis();
        StringBuffer ver = new StringBuffer();
        try {
            ver.append("api_key=").append("40").append("&call_id=").append(time).append("&unique_id=").append(unique_id).append("&show_name=").append(URLEncoder.encode(playerName, "utf-8")).append("&sha256_pwd=").append(M.sha256(pwd, "utf-8").toUpperCase());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String sig = getSign(new StringBuffer().append(ver).append("&secret_key=").append("sdf5432c").toString()).toUpperCase();
        StringBuffer sbu = new StringBuffer();
        sbu.append("http://202.142.19.66:8877/connect/member/autosync").append("?").append(ver.toString()).append("&sig=").append(sig);
        DCNService.log.info(sbu.toString());
        return sbu.toString();
    }

    public static Result sys(final String unique_id, final String playerName, final String pwd) {
        InputStream reStr = M.httpRequest(getSyncUrl(unique_id, playerName, pwd), "\u5f53\u4e50\u540c\u6b65\u7528\u6237unique_id" + unique_id);
        Result result = new Result();
        result.setResult(false);
        result.setReList("");
        if (reStr != null) {
            try {
                SAXReader reader = new SAXReader();
                Document doc = reader.read(reStr);
                Element root = doc.getRootElement();
                if (root != null) {
                    Attribute attribute = root.attribute("status");
                    if (attribute != null) {
                        String status = attribute.getValue();
                        if (status.equals("0")) {
                            Element user = root.element("user");
                            result.setResult(true);
                            result.setReList(user.element("mid").getText());
                            result.setDjtk(user.element("djtk").getText());
                        } else {
                            result.setReList(status);
                        }
                    }
                }
            } catch (Exception e) {
                DCNService.log.error("\u5f53\u4e50\u7528\u6237\u540c\u6b65\u63a5\u53e3:", (Throwable) e);
            }
        } else {
            DCNService.log.info(("\u5f53\u4e50\u767b\u5f55\u8bf7\u6c42\u8fd4\u56de" + reStr));
        }
        return result;
    }

    public static String getNewNoteUrl() {
        long time = System.currentTimeMillis();
        StringBuffer ver = new StringBuffer();
        ver.append("api_key=").append("40").append("&call_id=").append(time);
        String sig = getSign(new StringBuffer().append(ver).append("&secret_key=").append("sdf5432c").toString()).toUpperCase();
        StringBuffer sbu = new StringBuffer();
        sbu.append("http://202.142.19.66:8877/connect/notice/new-note").append("?").append(ver.toString()).append("&sig=").append(sig);
        DCNService.log.info(sbu.toString());
        return sbu.toString();
    }

    public static Result newNote(final String content) {
        InputStream instr = null;
        try {
            instr = M.httpPostRequest("content=" + URLEncoder.encode(content, "utf-8"), getNewNoteUrl(), "");
        } catch (UnsupportedEncodingException e1) {
            DCNService.log.error("\u5f53\u4e50\u53d1\u516c\u544a\u63a5\u53e3", (Throwable) e1);
        }
        Result result = new Result();
        result.setResult(false);
        result.setReList("");
        if (instr != null) {
            try {
                SAXReader reader = new SAXReader();
                Document doc = reader.read(instr);
                Element root = doc.getRootElement();
                if (root != null) {
                    Attribute attribute = root.attribute("status");
                    if (attribute != null) {
                        String status = attribute.getValue();
                        if (status.equals("0")) {
                            result.setResult(true);
                            result.setReList("");
                        } else {
                            result.setReList(status);
                        }
                    }
                }
            } catch (Exception e2) {
                DCNService.log.error("\u5f53\u4e50\u53d1\u516c\u544a\u63a5\u53e3", (Throwable) e2);
            }
        } else {
            DCNService.log.error("\u5f53\u4e50\u53d1\u516c\u544a\u63a5\u53e3\u53d1\u9001\u516c\u544a\u540e\u65e0\u6570\u636e\u8fd4\u56de");
        }
        return result;
    }

    public static String getForumListUrl(final String pageno, final String pagesize, final String djtk) {
        long time = System.currentTimeMillis();
        StringBuffer ver = new StringBuffer();
        ver.append("api_key=").append("40").append("&call_id=").append(time).append("&page_no=").append(pageno).append("&page_size=").append(pagesize).append("&djtk=").append(djtk);
        String sig = getSign(new StringBuffer().append(ver).append("&secret_key=").append("sdf5432c").toString()).toUpperCase();
        StringBuffer sbu = new StringBuffer();
        sbu.append("http://202.142.19.66:8877/connect/forum/list").append("?").append(ver.toString()).append("&sig=").append(sig);
        DCNService.log.info(sbu.toString());
        return sbu.toString();
    }

    public static List<Topic> getForumList(int pageno, final String djtk) {
        List<Topic> topicList = new ArrayList<Topic>();
        if (pageno <= 0) {
            pageno = 1;
        }
        long time3 = System.currentTimeMillis();
        InputStream reStr = M.httpRequest(getForumListUrl(new StringBuilder(String.valueOf(pageno)).toString(), "10", djtk), "\u83b7\u5f97\u8bba\u575b\u5e16\u5b50\u5217\u8868");
        Label_0354:
        {
            if (reStr != null) {
                try {
                    SAXReader reader = new SAXReader();
                    Document doc = reader.read(reStr);
                    Element root = doc.getRootElement();
                    if (root == null) {
                        break Label_0354;
                    }
                    Attribute attribute = root.attribute("STATUS");
                    if (attribute == null) {
                        break Label_0354;
                    }
                    String status = attribute.getValue();
                    if (!status.equals("0")) {
                        DCNService.log.info(("\u83b7\u53d6\u8bba\u575b\u5e16\u5b50\u5217\u8868\u5931\u8d25 status=" + status));
                        return topicList;
                    }
                    Element data = root.element("DATA");
                    List list = data.elements();
                    if (list.size() > 0) {
                        for (Object obj : list) {
                            Element elem = (Element) obj;
                            Topic topic = new Topic();
                            topic.setPostId(elem.element("POSTID").getText());
                            topic.setTitle(elem.element("TITLE").getText());
                            topic.setCreatedByInfo(elem.element("CREATED_BY_INFO").getText());
                            topic.setDateTime(M.getTimeDes(elem.element("EDITDATE").getText()));
                            topicList.add(topic);
                        }
                    }
                    break Label_0354;
                } catch (Exception e) {
                    DCNService.log.error("\u5f53\u4e50\u7528\u6237\u540c\u6b65\u63a5\u53e3:", (Throwable) e);
                    break Label_0354;
                }
            }
            DCNService.log.error(("\u5f53\u4e50\u767b\u5f55\u8bf7\u6c42\u8fd4\u56de" + reStr));
        }
        System.out.println("\u83b7\u53d6\u8bba\u575b\u5e16\u5b50\u5217\u8868\u65f6\u957f\uff1a\uff08ms\uff09" + (System.currentTimeMillis() - time3));
        return topicList;
    }

    public static String getShowForumUrl(final String topic_id, final String djtk) {
        long time = System.currentTimeMillis();
        StringBuffer ver = new StringBuffer();
        ver.append("api_key=").append("40").append("&call_id=").append(time).append("&djtk=").append(djtk);
        String sig = getSign(new StringBuffer().append(ver).append("&secret_key=").append("sdf5432c").toString()).toUpperCase();
        StringBuffer sbu = new StringBuffer();
        sbu.append("http://202.142.19.66:8877/connect/forum/show-").append(topic_id).append("?").append(ver.toString()).append("&sig=").append(sig);
        DCNService.log.info(sbu.toString());
        return sbu.toString();
    }

    public static Topic getForum(final String topic_id, final String djtk) {
        Topic topic = null;
        long time3 = System.currentTimeMillis();
        InputStream reStr = M.httpRequest(getShowForumUrl(topic_id, djtk), "\u83b7\u5f97\u8bba\u575b\u5e16\u5b50\u4fe1\u606f");
        Label_0524:
        {
            if (reStr != null) {
                try {
                    long time4 = System.currentTimeMillis();
                    SAXReader reader = new SAXReader();
                    Document doc = reader.read(reStr);
                    Element root = doc.getRootElement();
                    if (root == null) {
                        break Label_0524;
                    }
                    Attribute attribute = root.attribute("STATUS");
                    if (attribute == null) {
                        break Label_0524;
                    }
                    String status = attribute.getValue();
                    if (status.equals("0")) {
                        topic = new Topic();
                        Element data = root.element("DATA");
                        Element dataRow = data.element("ROW");
                        topic.setPostId(dataRow.element("POSTID").getText());
                        topic.setForumId(dataRow.element("FORUMID").getText());
                        topic.setTitle(dataRow.element("TITLE").getText());
                        topic.setContent(dataRow.element("MESSAGE").getText());
                        String time5 = dataRow.element("EDITDATE").getText();
                        topic.setDateTime(M.getTimeDes(time5));
                        topic.setCreatedByInfo(dataRow.element("CREATED_BY_INFO").getText());
                        Element newbanch = root.element("NEWBRANCH");
                        Element bdata = newbanch.element("DATA");
                        List<Element> list = (List<Element>) bdata.elements();
                        List<Topic> topicList = new ArrayList<Topic>();
                        for (final Element el : list) {
                            Topic topic2 = new Topic();
                            topic2.setContent(el.element("MESSAGE").getText());
                            topic2.setCreatedByInfo(el.element("CREATED_BY_INFO").getText());
                            String time6 = el.element("DATETIME").getText();
                            topic2.setDateTime(M.getTimeDes(time6));
                            topicList.add(topic2);
                        }
                        topic.setReplyTopicList(topicList);
                        System.out.println("\u89e3\u6790xml\u5355\u4e2a\u5e16\u65f6\u957f\uff1a\uff08ms\uff09" + (System.currentTimeMillis() - time4));
                        break Label_0524;
                    }
                    DCNService.log.error(("\u83b7\u53d6\u8bba\u575b\u5e16\u5b50\u8be6\u7ec6\u4fe1\u606f\u5931\u8d25topicid=" + topic_id + "status=" + status));
                    return null;
                } catch (Exception e) {
                    DCNService.log.error("\u5f53\u4e50\u7528\u6237\u540c\u6b65\u63a5\u53e3:", (Throwable) e);
                    break Label_0524;
                }
            }
            DCNService.log.error(("\u5f53\u4e50\u767b\u5f55\u8bf7\u6c42\u8fd4\u56de" + reStr));
        }
        System.out.println("\u89e3\u6790\u5355\u4e2a\u5e16\u65f6\u957f\uff1a\uff08ms\uff09" + (System.currentTimeMillis() - time3));
        return topic;
    }

    public static String getNewTopicUrl(final String mid, final String djtk, final String pwd, final String imgs) {
        long time = System.currentTimeMillis();
        StringBuffer ver = new StringBuffer();
        ver.append("api_key=").append("40").append("&call_id=").append(time).append("&mid=").append(mid).append("&djtk=").append(djtk);
        String s1 = new StringBuffer().append(ver).append("&sha256_pwd=").append((pwd != null && pwd.length() > 0) ? M.sha256(pwd, "utf-8").toUpperCase() : "").append("&secret_key=").append("sdf5432c").append((imgs != null && imgs.length() > 0) ? ("&imgs=" + imgs) : "").toString();
        String sig = getSign(s1).toUpperCase();
        StringBuffer sbu = new StringBuffer();
        sbu.append("http://202.142.19.66:8877/connect/forum/new-topic").append("?").append(ver.toString()).append("&sig=").append(sig);
        DCNService.log.info(sbu.toString());
        return sbu.toString();
    }

    public static Result newTopic(final String mid, final String djtk, final String pwd, final String title, final String content, final String ip, final String showName, final String imgs, final String imgsdes) {
        StringBuffer message = null;
        try {
            message = new StringBuffer().append("title=").append(URLEncoder.encode(title, "utf-8")).append("&content=").append(URLEncoder.encode(content, "utf-8")).append("&ip=").append(ip).append("&show_name=").append(URLEncoder.encode(showName, "utf-8")).append("&imgs=").append((imgs != null && imgs.length() > 0) ? URLEncoder.encode(imgs, "utf-8") : "").append("&imgs_desc=").append((imgsdes != null && imgsdes.length() > 0) ? URLEncoder.encode(imgsdes, "utf-8") : "");
        } catch (UnsupportedEncodingException e) {
            DCNService.log.error("\u5f53\u4e50\u63a5\u53e3\u53d1\u65b0\u5e16\u5931\u8d25", (Throwable) e);
        }
        InputStream instr = M.httpPostRequest(message.toString(), getNewTopicUrl(mid, djtk, pwd, imgs), "");
        Result result = new Result();
        result.setResult(false);
        result.setReList("");
        if (instr != null) {
            try {
                SAXReader reader = new SAXReader();
                Document doc = reader.read(instr);
                Element root = doc.getRootElement();
                if (root != null) {
                    Attribute attribute = root.attribute("STATUS");
                    if (attribute != null) {
                        String status = attribute.getValue();
                        if (status.equals("0")) {
                            result.setResult(true);
                            result.setReList("\u53d1\u5e16\u6210\u529f");
                        } else {
                            result.setReList(status);
                        }
                    }
                }
            } catch (Exception e2) {
                DCNService.log.error("\u5f53\u4e50\u53d1\u5e16\u63a5\u53e3\u5f02\u5e38", (Throwable) e2);
            }
        } else {
            DCNService.log.error("\u53d1\u5e16\u540e\u65e0\u6570\u636e\u8fd4\u56de");
        }
        return result;
    }

    public static String getReplyTopicUrl(final String topic_id, final String mid, final String djtk, final String pwd) {
        long time = System.currentTimeMillis();
        StringBuffer ver = new StringBuffer();
        ver.append("api_key=").append("40").append("&call_id=").append(time).append("&mid=").append(mid).append("&djtk=").append(djtk);
        String sig = getSign(new StringBuffer().append(ver).append("&sha256_pwd=").append((pwd != null && pwd.length() > 0) ? M.sha256(pwd, "utf-8").toUpperCase() : "").append("&secret_key=").append("sdf5432c").toString()).toUpperCase();
        StringBuffer sbu = new StringBuffer();
        sbu.append("http://202.142.19.66:8877/connect/forum/reply-").append(topic_id).append("?").append(ver.toString()).append("&sig=").append(sig);
        return sbu.toString();
    }

    public static Result replyTopic(final String topic_id, final String mid, final String djtk, final String pwd, final String title, final String content, final String ip, final String showName) {
        StringBuffer message = null;
        try {
            message = new StringBuffer().append("title=").append(URLEncoder.encode(title, "utf-8")).append("&content=").append(URLEncoder.encode(content, "utf-8")).append("&ip=").append(ip).append("&show_name=").append(URLEncoder.encode(showName, "utf-8"));
        } catch (UnsupportedEncodingException e1) {
            DCNService.log.error("", (Throwable) e1);
        }
        InputStream instr = M.httpPostRequest(message.toString(), getReplyTopicUrl(topic_id, mid, djtk, pwd), "");
        Result result = new Result();
        result.setResult(false);
        result.setReList("");
        if (instr != null) {
            try {
                SAXReader reader = new SAXReader();
                Document doc = reader.read(instr);
                Element root = doc.getRootElement();
                if (root != null) {
                    Attribute attribute = root.attribute("STATUS");
                    if (attribute != null) {
                        String status = attribute.getValue();
                        if (status.equals("0")) {
                            result.setResult(true);
                            result.setReList("\u56de\u5e16\u6210\u529f");
                        } else {
                            result.setReList(status);
                        }
                    }
                }
            } catch (Exception e2) {
                DCNService.log.error("\u5f53\u4e50\u56de\u5e16\u63a5\u53e3\u5f02\u5e38", (Throwable) e2);
            }
        } else {
            DCNService.log.error("\u5f53\u4e50\u63a5\u53e3\u56de\u5e16\u540e\u65e0\u6570\u636e\u8fd4\u56de");
        }
        return result;
    }

    public static String getUploadImgUrl(final String mid, final String djtk, final String pwd) {
        long time = System.currentTimeMillis();
        StringBuffer ver = new StringBuffer();
        ver.append("api_key=").append("40").append("&call_id=").append(time).append("&mid=").append(mid).append("&djtk=").append(djtk);
        String str = new StringBuffer().append(ver).append("&sha256_pwd=").append((pwd != null && pwd.length() > 0) ? M.sha256(pwd, "utf-8").toUpperCase() : "").append("&secret_key=").append("sdf5432c").toString();
        String sig = getSign(str).toUpperCase();
        StringBuffer sbu = new StringBuffer();
        sbu.append("http://202.142.19.66:8877/connect/forum/game-img").append("?").append(ver.toString()).append("&sig=").append(sig);
        DCNService.log.info(sbu.toString());
        return sbu.toString();
    }

    public static Result uploadImg(final String mid, final String djtk, final String pwd, final String name, final String bytes, final String ip, final String type) {
        StringBuffer message = null;
        try {
            message = new StringBuffer().append("name=").append(name).append("&bytes=").append(URLEncoder.encode(bytes, "utf-8")).append("&ip=").append(ip).append("&type=").append(type);
        } catch (UnsupportedEncodingException e1) {
            DCNService.log.error("", (Throwable) e1);
        }
        InputStream instr = M.httpPostRequest(message.toString(), getUploadImgUrl(mid, djtk, pwd), "");
        Result result = new Result();
        result.setResult(false);
        result.setReList("");
        if (instr != null) {
            try {
                SAXReader reader = new SAXReader();
                Document doc = reader.read(instr);
                Element root = doc.getRootElement();
                if (root != null) {
                    Attribute attribute = root.attribute("STATUS");
                    if (attribute != null) {
                        String status = attribute.getValue();
                        if (status.equals("0")) {
                            Element eml = root.element("IMG_URL");
                            result.setResult(true);
                            result.setReList(eml.getText());
                        } else {
                            result.setReList(status);
                        }
                    }
                }
            } catch (Exception e2) {
                DCNService.log.error("\u5f53\u4e50\u4e0a\u4f20\u56fe\u7247\u63a5\u53e3\u5f02\u5e38", (Throwable) e2);
            }
        } else {
            DCNService.log.error("\u4e0a\u4f20\u56fe\u7247\u540e\u65e0\u6570\u636e\u8fd4\u56de");
        }
        return result;
    }

    public static String getSign(final String params) {
        String dString = M.md5(params, "utf-8");
        return dString;
    }

    public static void main(final String[] arg) throws IOException {
    }
}
