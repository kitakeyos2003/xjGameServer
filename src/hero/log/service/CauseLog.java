// 
// Decompiled by Procyon v0.5.36
// 
package hero.log.service;

public enum CauseLog {
    SALE("SALE", 0, 0, "\u51fa\u552e"),
    BUY("BUY", 1, 1, "\u8d2d\u4e70"),
    EXCHANGE("EXCHANGE", 2, 2, "\u4ea4\u6613"),
    DROP("DROP", 3, 3, "\u6389\u843d"),
    AUCTION("AUCTION", 4, 4, "\u62cd\u5356"),
    MAIL("MAIL", 5, 5, "\u90ae\u7bb1"),
    MANUF("MANUF", 6, 6, "\u5236\u9020"),
    DEL("DEL", 7, 7, "\u4e22\u5f03"),
    TASKAWARD("TASKAWARD", 8, 8, "\u4efb\u52a1\u5956\u52b1"),
    STORAGE("STORAGE", 9, 9, "\u4ed3\u5e93"),
    STORE("STORE", 10, 10, "\u4e2a\u4eba\u5546\u5e97"),
    GATHER("GATHER", 11, 11, "\u91c7\u96c6"),
    REFINED("REFINED", 12, 12, "\u70bc\u5316"),
    RATTAN("RATTAN", 13, 13, "\u85e4\u6761"),
    TASKTOOL("TASKTOOL", 14, 14, "\u4efb\u52a1\u9053\u5177"),
    MALL("MALL", 15, 15, "\u5546\u57ce"),
    TEACH("TEACH", 16, 16, "\u77e5\u8bc6\u6388\u4e88"),
    CANCELTASK("CANCELTASK", 17, 17, "\u53d6\u6d88\u4efb\u52a1"),
    SUBMITTASK("SUBMITTASK", 18, 18, "\u5b8c\u6210\u4efb\u52a1"),
    WORLDCHAT("WORLDCHAT", 19, 19, "\u4e16\u754c\u804a\u5929"),
    CHANGEVOCATION("CHANGEVOCATION", 20, 20, "\u8f6c\u804c"),
    REMOVESEAL("REMOVESEAL", 21, 21, "\u89e3\u9664\u5c01\u5370"),
    CONVERT("CONVERT", 22, 22, "\u5151\u6362"),
    SHORTCUTKEY("SHORTCUTKEY", 23, 23, "\u5feb\u6377\u952e"),
    NOVICE("NOVICE", 24, 24, "\u65b0\u624b\u5956\u52b1"),
    UNLOAD("UNLOAD", 25, 25, "\u5378\u4e0b"),
    WEAR("WEAR", 26, 26, "\u7a7f\u6234"),
    DIVORCE("DIVORCE", 27, 27, "\u79bb\u5a5a"),
    TAKEOFF("TAKEOFF", 28, 28, "\u6446\u644a\u4e0b\u67b6"),
    ENHANCE("ENHANCE", 29, 29, "\u9576\u5d4c"),
    USE("USE", 30, 30, "\u8ba1\u8d39\u9053\u5177\u4f7f\u7528"),
    GUILDBUILD("GUILDBUILD", 31, 31, "\u521b\u5efa\u516c\u4f1a\u6d88\u8017\u9053\u5177"),
    PERFORATE("PERFORATE", 32, 32, "\u6253\u5b54"),
    PULLOUT("PULLOUT", 33, 33, "\u62d4\u9664\u5b9d\u77f3"),
    DICEEQUIP("DICEEQUIP", 34, 34, "\u6253\u5b54\u5931\u8d25\u88c5\u5907\u6467\u6bc1"),
    GMADD("GMADD", 35, 35, "GM\u6dfb\u52a0"),
    RINSE("RINSE", 36, 36, "\u6d17\u70b9"),
    CLANCHAT("CLANCHAT", 37, 37, "\u9635\u8425\u804a\u5929"),
    TASKPUSH("TASKPUSH", 38, 38, "\u4efb\u52a1\u63a8\u5e7f"),
    OPENGIFTBAG("OPENGIFTBAG", 39, 39, "\u6253\u5f00\u793c\u5305"),
    COUNTDOWNGIFTSEND("COUNTDOWNGIFTSEND", 40, 39, "\u5012\u8ba1\u65f6\u793c\u5305\u4e0b\u53d1"),
    ENHANCEFEE("ENHANCEFEE", 41, 40, "\u5f3a\u5316\u82b1\u8d39\u91d1\u94b1"),
    ANSWERQUESTION("ANSWERQUESTION", 42, 41, "\u56de\u7b54\u95ee\u9898\u6d3b\u52a8\u83b7\u5f97\u9053\u5177"),
    EVIDENVEGET("EVIDENVEGET", 43, 42, "\u51ed\u8f93\u5165\u9886\u53d6\u5956\u52b1");

    private int id;
    private String name;

    private CauseLog(final String name, final int ordinal, final int _id, final String _name) {
        this.id = _id;
        this.name = _name;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}
