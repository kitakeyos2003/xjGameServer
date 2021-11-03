// 
// Decompiled by Procyon v0.5.36
// 
package hero.chat.service;

import hero.player.HeroPlayer;
import hero.player.service.PlayerServiceImpl;
import java.util.Timer;
import javolution.util.FastList;
import java.util.TimerTask;

public class WorldHornService extends TimerTask {

    private FastList<HornContent> list;
    private Timer timer;
    private static WorldHornService instance;
    private static final long START_DELAY_TIME = 30000L;
    private static final long EXCUTE_INTERVAL = 1000L;
    private static final long SEND_INTERVAL = 5000L;
    private static final byte MAX_TIMES = 3;

    private WorldHornService() {
        this.list = (FastList<HornContent>) new FastList();
    }

    public static WorldHornService getInstance() {
        if (WorldHornService.instance == null) {
            WorldHornService.instance = new WorldHornService();
        }
        return WorldHornService.instance;
    }

    public void start() {
        if (this.timer == null) {
            (this.timer = new Timer()).schedule(this, 30000L, 1000L);
        }
    }

    public void put(final String _speakerName, final String _content, final int _type) {
        synchronized (this.list) {
            this.list.add(new HornContent(_speakerName, _content, _type));
        }
        // monitorexit(this.list)
    }

    @Override
    public void run() {
        synchronized (this.list) {
            long now = System.currentTimeMillis();
            int i = 0;
            while (i < this.list.size()) {
                HornContent horn = (HornContent) this.list.get(i);
                if (horn.lastTimesSendTime == 0L || now - horn.lastTimesSendTime >= 5000L) {
                    HeroPlayer speaker = PlayerServiceImpl.getInstance().getPlayerByName(horn.speakerName);
                    if (speaker == null || !speaker.isEnable()) {
                        this.list.remove(i);
                        continue;
                    }
                    if (horn.type == 1) {
                        ChatServiceImpl.getInstance().sendWorldPlayer(speaker, horn.content);
                    } else if (horn.type == 2) {
                        ChatServiceImpl.getInstance().sendWorldPlayerUseMassHorn(speaker, horn.content);
                    }
                    HornContent hornContent = horn;
                    ++hornContent.whichTimes;
                    horn.lastTimesSendTime = now;
                    if (horn.whichTimes == 3) {
                        this.list.remove(i);
                        continue;
                    }
                }
                ++i;
            }
        }
        // monitorexit(this.list)
    }

    public class HornContent {

        public long lastTimesSendTime;
        public int whichTimes;
        public String speakerName;
        public String content;
        public int type;

        public HornContent(final String _speakerName, final String _content, final int _type) {
            this.speakerName = _speakerName;
            this.content = _content;
            this.type = _type;
        }
    }
}
