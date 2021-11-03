// 
// Decompiled by Procyon v0.5.36
// 
package hero.gm.service;

import javolution.util.FastMap;

public class GmQuestionManager {

    private FastMap<Integer, Integer> container;
    private static GmQuestionManager instance;

    private GmQuestionManager() {
        this.container = (FastMap<Integer, Integer>) new FastMap();
    }

    public static GmQuestionManager getInstance() {
        if (GmQuestionManager.instance == null) {
            GmQuestionManager.instance = new GmQuestionManager();
        }
        return GmQuestionManager.instance;
    }

    public int getQuestionID(final int _userID) {
        Integer questionID = (Integer) this.container.get(_userID);
        if (questionID == null) {
            return 0;
        }
        return questionID;
    }

    public void putQuestion(final int _userID, final int _questionID) {
        this.container.put(_userID, _questionID);
    }

    public void delQuestion(final int _userID) {
        this.container.remove(_userID);
    }
}
