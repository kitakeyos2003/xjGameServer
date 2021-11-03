// 
// Decompiled by Procyon v0.5.36
// 
package hero.duel;

public class Duel {

    public int player1UserID;
    public int player2UserID;
    public boolean isConfirming;
    public long startTime;
    public int duleMapID;

    public Duel(final int _player1UserID, final int _player2UserID, final int _mapID) {
        this.player1UserID = _player1UserID;
        this.player2UserID = _player2UserID;
        this.duleMapID = _mapID;
        this.isConfirming = true;
        this.startTime = System.currentTimeMillis();
    }
}
