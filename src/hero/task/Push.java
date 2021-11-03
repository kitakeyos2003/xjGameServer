// 
// Decompiled by Procyon v0.5.36
// 
package hero.task;

public class Push {

    public static final int PUSH_TYPE_COMM = 0;
    public static final int PUSH_TYPE_SMS = 1;
    public static final int PUSH_TYPE_MOBILE_PROXY = 2;
    public static final int PROXY_JTCQ_ID = 0;
    public static final int PROXY_HERO_ID = 1;
    public static final int PROXY_SZF_ID = 2;
    public int id;
    public int time;
    public int point;
    public int goodsID;
    public String commPushContent;
    public String commConfirmContent;
    public int pushNum;
    public int[] pushType;
    public String[] pushContent;
    public String[] limitContent;
}
