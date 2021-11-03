// 
// Decompiled by Procyon v0.5.36
// 
package hero.dungeon.message;

import java.io.IOException;
import java.util.Iterator;
import hero.dungeon.service.DungeonHistoryManager;
import hero.dungeon.DungeonHistory;
import java.util.ArrayList;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseDungeonHistoryInfo extends AbsResponseMessage {

    private ArrayList<DungeonHistory> historyList;

    public ResponseDungeonHistoryInfo(final ArrayList<DungeonHistory> _historyList) {
        this.historyList = _historyList;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        if (this.historyList != null) {
            this.yos.writeShort(this.historyList.size());
            if (this.historyList.size() > 0) {
                String[] dungeonRefreshTimeInfo = DungeonHistoryManager.getInstance().getDungeonRefreshTimeInfo();
                for (final DungeonHistory history : this.historyList) {
                    if (1 == history.getPattern()) {
                        this.yos.writeUTF(String.valueOf(history.getDungeonName()) + "\uff08\u7b80\u5355\uff09");
                    } else {
                        this.yos.writeUTF(String.valueOf(history.getDungeonName()) + "\uff08\u56f0\u96be\uff09");
                    }
                    this.yos.writeInt(history.getID());
                    if (2 == history.getDungeonType()) {
                        this.yos.writeUTF(dungeonRefreshTimeInfo[0]);
                    } else {
                        this.yos.writeUTF(dungeonRefreshTimeInfo[1]);
                    }
                }
            }
        } else {
            this.yos.writeShort(0);
        }
    }
}
