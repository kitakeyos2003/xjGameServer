// 
// Decompiled by Procyon v0.5.36
// 
package hero.map.broadcast;

import yoyo.core.packet.AbsResponseMessage;
import hero.map.Map;

public class SynchronousMessage {

    public short clientType;
    public Map map;
    public AbsResponseMessage msg;
    public boolean needExcludeTrigger;
    public int objectID;

    public SynchronousMessage(final Map _map, final AbsResponseMessage _msg, final boolean _needExcludeTrigger, final int _objectID) {
        this.map = _map;
        this.msg = _msg;
        this.needExcludeTrigger = _needExcludeTrigger;
        this.objectID = _objectID;
    }

    public SynchronousMessage(final short _clientType, final Map _map, final AbsResponseMessage _msg, final boolean _needExcludeTrigger, final int _objectID) {
        this.clientType = _clientType;
        this.map = _map;
        this.msg = _msg;
        this.needExcludeTrigger = _needExcludeTrigger;
        this.objectID = _objectID;
    }
}
