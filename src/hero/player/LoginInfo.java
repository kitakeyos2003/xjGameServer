// 
// Decompiled by Procyon v0.5.36
// 
package hero.player;

public class LoginInfo {

    public short clientType;
    public String clientVersion;
    public short communicatePipe;
    public int accountID;
    public String username;
    public String password;
    public String loginMsisdn;
    public String boundMsisdn;
    public String userAgent;
    public short msisdnZoon;
    public String logoutCause;
    public int publisher;

    public LoginInfo() {
        this.loginMsisdn = "";
        this.userAgent = "";
        this.msisdnZoon = -1;
        this.logoutCause = "\u8d85\u65f6";
    }
}
