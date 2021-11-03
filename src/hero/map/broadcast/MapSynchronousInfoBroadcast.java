// 
// Decompiled by Procyon v0.5.36
// 
package hero.map.broadcast;

import java.util.ArrayList;
import hero.share.service.ME2ObjectList;
import java.util.Iterator;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.HeroPlayer;
import yoyo.core.packet.AbsResponseMessage;
import hero.map.Map;
import javolution.util.FastList;

public class MapSynchronousInfoBroadcast implements Runnable {

    private FastList<SynchronousMessage> infoList;
    private static MapSynchronousInfoBroadcast instance;

    public MapSynchronousInfoBroadcast() {
        this.infoList = (FastList<SynchronousMessage>) new FastList();
    }

    public static MapSynchronousInfoBroadcast getInstance() {
        if (MapSynchronousInfoBroadcast.instance == null) {
            MapSynchronousInfoBroadcast.instance = new MapSynchronousInfoBroadcast();
        }
        return MapSynchronousInfoBroadcast.instance;
    }

    public void put(final Map _map, final AbsResponseMessage _msg, final boolean _needExcludeTrigger, final int _playerObjectID) {
        if (_map.getPlayerList().size() > 0) {
            synchronized (this.infoList) {
                this.infoList.add(new SynchronousMessage(_map, _msg, _needExcludeTrigger, _playerObjectID));
            }
            // monitorexit(this.infoList)
        }
    }

    public void put(final short _clientType, final Map _map, final AbsResponseMessage _msg, final boolean _needExcludeTrigger, final int _playerObjectID) {
        if (_map.getPlayerList().size() > 0) {
            synchronized (this.infoList) {
                this.infoList.add(new SynchronousMessage(_clientType, _map, _msg, _needExcludeTrigger, _playerObjectID));
            }
            // monitorexit(this.infoList)
        }
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException ex) {
        }
        while (true) {
            try {
                this.broadcast();
            } catch (Exception ex2) {
            }
            try {
                Thread.sleep(20L);
            } catch (InterruptedException ex3) {
            }
        }
    }

    private void broadcast() {
        try {
            synchronized (this.infoList) {
                for (final SynchronousMessage info : this.infoList) {
                    ME2ObjectList mapPlayerList = info.map.getPlayerList();
                    if (mapPlayerList.size() > 0) {
                        HeroPlayer player = null;
                        for (int i = 0; i < mapPlayerList.size(); ++i) {
                            player = (HeroPlayer) mapPlayerList.get(i);
                            if (player.isEnable()) {
                                if (!info.needExcludeTrigger || player.getID() != info.objectID) {
                                    if (info.clientType == 0) {
                                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), info.msg);
                                    } else if (3 == info.clientType) {
                                        if (player.getLoginInfo().clientType == 3) {
                                            ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), info.msg);
                                        }
                                    } else if (player.getLoginInfo().clientType != 3) {
                                        ResponseMessageQueue.getInstance().put(player.getMsgQueueIndex(), info.msg);
                                    }
                                }
                            }
                        }
                    }
                }
                this.infoList.clear();
            }
            // monitorexit(this.infoList)
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
