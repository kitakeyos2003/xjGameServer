// 
// Decompiled by Procyon v0.5.36
// 
package hero.micro.sports;

import hero.map.Map;

public class SportsUnit {

    public SportsTeam team1;
    public SportsTeam team2;
    public Map site;
    public byte status;
    public long fightKeepTime;
    public long readyKeepTime;

    public SportsUnit(final SportsTeam _team1, final SportsTeam _team2, final Map _site) {
        this.team1 = _team1;
        this.team2 = _team2;
        this.site = _site;
        this.status = 1;
    }

    public void start() {
        this.status = 2;
    }
}
