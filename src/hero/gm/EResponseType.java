// 
// Decompiled by Procyon v0.5.36
// 
package hero.gm;

public enum EResponseType {
    SEND_CHAT_CONTENT("SEND_CHAT_CONTENT", 0),
    SEND_ROLE_ONLINE("SEND_ROLE_ONLINE", 1),
    SEND_ROLE_OUTLINE("SEND_ROLE_OUTLINE", 2),
    SEND_ROLE_LVL_UPDATE("SEND_ROLE_LVL_UPDATE", 3),
    SEND_NEW_QUEATION("SEND_NEW_QUEATION", 4),
    SEND_QUEATION_EACH("SEND_QUEATION_EACH", 5),
    SEND_QUESTION_APPRAISE("SEND_QUESTION_APPRAISE", 6);

    private EResponseType(final String name, final int ordinal) {
    }
}
