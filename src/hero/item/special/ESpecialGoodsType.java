// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.special;

public enum ESpecialGoodsType {
    GOURD("GOURD", 0, "\u846b\u82a6"),
    RATTAN("RATTAN", 1, "\u85e4\u6761"),
    CRYSTAL("CRYSTAL", 2, "\u6c34\u6676"),
    DRAWINGS("DRAWINGS", 3, "\u56fe\u7eb8"),
    SEAL_PRAY("SEAL_PRAY", 4, "\u5c01\u5370\u795d\u798f"),
    WORLD_HORN("WORLD_HORN", 5, "\u4e16\u754c\u53f7\u89d2"),
    MASS_HORN("MASS_HORN", 6, "\u96c6\u7ed3\u53f7\u89d2"),
    EXPERIENCE_BOOK("EXPERIENCE_BOOK", 7, "\u5728\u7ebf\u7ecf\u9a8c\u4e66"),
    EXP_BOOK_OFFLINE("EXP_BOOK_OFFLINE", 8, "\u79bb\u7ebf\u7ecf\u9a8c\u4e66"),
    HUNT_EXP_BOOK("HUNT_EXP_BOOK", 9, "\u72e9\u730e\u7ecf\u9a8c\u4e66"),
    SOUL_MARK("SOUL_MARK", 10, "\u7075\u9b42\u5370\u8bb0"),
    SOUL_CHANNEL("SOUL_CHANNEL", 11, "\u7075\u9b42\u7b26\u6587"),
    EQUIPMENT_REPAIR("EQUIPMENT_REPAIR", 12, "\u88c5\u5907\u4fee\u8865\u5242"),
    PET_ARCHETYPE("PET_ARCHETYPE", 13, "\u5ba0\u7269\u539f\u578b"),
    SHOP_LICENCE("SHOP_LICENCE", 14, "\u4e2a\u4eba\u5546\u5e97\u8bb8\u53ef\u8bc1"),
    SKILL_BOOK("SKILL_BOOK", 15, "\u6280\u80fd\u4e66"),
    PET_FEED("PET_FEED", 16, "\u5ba0\u7269\u9972\u6599"),
    PET_REVIVE("PET_REVIVE", 17, "\u5ba0\u7269\u590d\u6d3b\u5377\u8f74"),
    PET_DICARD("PET_DICARD", 18, "\u5ba0\u7269\u80fd\u529b\u69fd\u6d17\u70b9\u9053\u5177"),
    PET_SKILL_BOOK("PET_SKILL_BOOK", 19, "\u5ba0\u7269\u6280\u80fd\u4e66"),
    MARRY_RING("MARRY_RING", 20, "\u7ed3\u5a5a\u6212\u6307"),
    DIVORCE("DIVORCE", 21, "\u79bb\u5a5a\u8bc1\u660e"),
    HEAVEN_BOOK("HEAVEN_BOOK", 22, "\u5929\u4e66"),
    TASK_TRANSPORT("TASK_TRANSPORT", 23, "\u4f20\u9001\u6c34\u6676"),
    BIG_TONIC("BIG_TONIC", 24, "\u5927\u8865\u4e38"),
    RINSE_SKILL("RINSE_SKILL", 25, "\u6d17\u70b9\u7b26"),
    REVIVE_STONE("REVIVE_STONE", 26, "\u590d\u6d3b\u77f3"),
    GUILD_BUILD("GUILD_BUILD", 27, "\u5de5\u4f1a\u6210\u7acb\u5361"),
    PET_FOREVER("PET_FOREVER", 28, "\u6c38\u4e45\u5750\u9a91\u5361"),
    PET_PER("PET_PER", 29, "\u6309\u6b21\u5750\u9a91\u5361"),
    PET_TIME("PET_TIME", 30, "\u8ba1\u65f6\u5750\u9a91\u5361"),
    FLOWER("FLOWER", 31, "\u9c9c\u82b1"),
    CHOCOLATE("CHOCOLATE", 32, "\u5de7\u514b\u529b"),
    SPOUSE_TRANSPORT("SPOUSE_TRANSPORT", 33, "\u592b\u59bb\u4f20\u9001\u7b26"),
    BAG_EXPAN("BAG_EXPAN", 34, "\u80cc\u5305\u6269\u5c55"),
    GIFT_BAG("GIFT_BAG", 35, "\u793c\u5305"),
    HOOK_EXP("HOOK_EXP", 36, "\u79bb\u7ebf\u6302\u673a\u7ecf\u9a8c"),
    REPEATE_TASK_EXPAN("REPEATE_TASK_EXPAN", 37, "\u5faa\u73af\u4efb\u52a1\u6269\u5c55");

    private String description;

    private ESpecialGoodsType(final String name, final int ordinal, final String _desc) {
        this.description = _desc;
    }

    public String getDescription() {
        return this.description;
    }

    public static ESpecialGoodsType get(final String _desc) {
        ESpecialGoodsType[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            ESpecialGoodsType type = values[i];
            if (type.getDescription().equals(_desc)) {
                return type;
            }
        }
        return null;
    }
}
