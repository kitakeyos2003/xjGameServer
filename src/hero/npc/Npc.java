// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc;

import hero.npc.service.NotPlayerServiceImpl;
import hero.npc.message.NpcRefreshNotify;
import hero.map.Map;
import hero.share.service.ThreadPoolFactory;
import hero.map.message.DisappearNotify;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.npc.message.NpcInteractiveResponse;
import hero.npc.detail.NpcHandshakeOptionData;
import java.util.ArrayList;
import hero.ui.UI_NpcHandshake;
import yoyo.core.queue.ResponseMessageQueue;
import yoyo.tools.YOYOInputStream;
import hero.player.HeroPlayer;
import hero.share.ME2GameObject;
import hero.share.EObjectType;
import hero.npc.ai.NPCFollowAI;
import hero.npc.function.BaseNpcFunction;
import javolution.util.FastList;
import org.apache.log4j.Logger;

public class Npc extends ME2NotPlayer {

    private static Logger log;
    public static final byte NOT_MARK = 0;
    public static final byte RECEIVE_TASK_MARK = 1;
    public static final byte SUBMIT_TASK_MARK = 2;
    public static final byte RECEIVE_TASK_NOT_MARK = 4;
    public static final byte SUBMIT_TASK_NOT_MARK = 3;
    private String hello;
    private String screamContent;
    private String title;
    private byte functionType;
    private FastList<BaseNpcFunction> functionList;
    private byte imageType;
    private boolean canInteract;
    private NPCFollowAI followAi;
    private short animationID;

    static {
        Npc.log = Logger.getLogger((Class) Npc.class);
    }

    public Npc() {
        this.canInteract = true;
        this.objectType = EObjectType.NPC;
        this.functionList = (FastList<BaseNpcFunction>) new FastList();
    }

    public Npc(final String _hello) {
        this();
        this.hello = _hello;
        this.setMoveSpeed((byte) 4);
    }

    public String getHello() {
        return this.hello;
    }

    public void setScreamContent(final String _screamHello) {
        this.screamContent = _screamHello;
    }

    public String getScreamContent() {
        return this.screamContent;
    }

    public void setTitle(final String _title) {
        this.title = _title;
    }

    public void setFunctionType(final byte _functionType) {
        this.functionType = _functionType;
    }

    public byte getFunctionType() {
        return this.functionType;
    }

    public String getTitle() {
        if (this.title == null) {
            return "";
        }
        return this.title;
    }

    public void addFunction(final BaseNpcFunction _function) {
        this.functionList.add(_function);
    }

    @Override
    public void active() {
        super.active();
        this.setDirection((byte) 2);
    }

    @Override
    public boolean canBeAttackBy(final ME2GameObject _object) {
        return false;
    }

    public void listen(final HeroPlayer _speaker, final YOYOInputStream _content) {
        try {
            if (this.canInteract()) {
                byte stepID = _content.readByte();
                Npc.log.debug(("NPC listen stepID = " + stepID));
                if (stepID == 0) {
                    this.handshake(_speaker);
                } else {
                    int functionMark = _content.readInt();
                    Npc.log.debug(("NPC functionMark = " + functionMark));
                    BaseNpcFunction function = this.getFunction(functionMark);
                    if (function != null) {
                        Npc.log.debug(("NPC optionIndex = " + this.parseFunctionOptionIndex(functionMark)));
                        function.process(_speaker, stepID, this.parseFunctionOptionIndex(functionMark), _content);
                    }
                }
            } else {
                ResponseMessageQueue.getInstance().put(_speaker.getMsgQueueIndex(), new NpcInteractiveResponse(this.getID(), 0, (byte) 1, UI_NpcHandshake.getBytes(null)));
            }
        } catch (Exception e) {
            Npc.log.error("\u4e0eNPC\u4ea4\u4e92\u51fa\u9519:", (Throwable) e);
        }
    }

    public void handshake(final HeroPlayer _player) {
        ArrayList<NpcHandshakeOptionData> options = null;
        if (this.functionList.size() > 0) {
            options = new ArrayList<NpcHandshakeOptionData>();
            for (final BaseNpcFunction function : this.functionList) {
                ArrayList<NpcHandshakeOptionData> functionOption = function.getTopLayerOptionList(_player);
                if (functionOption != null) {
                    for (final NpcHandshakeOptionData nhod : functionOption) {
                        options.add(nhod);
                    }
                }
            }
        }
        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NpcInteractiveResponse(this.getID(), 0, (byte) 1, UI_NpcHandshake.getBytes(options)));
    }

    private int parseFunctionType(final int _functionMark) {
        return _functionMark / 100000;
    }

    private int parseFunctionOptionIndex(final int _functionMark) {
        return _functionMark % 100000;
    }

    public BaseNpcFunction getFunction(final int _functionMark) {
        int functionType = this.parseFunctionType(_functionMark);
        for (final BaseNpcFunction function : this.functionList) {
            if (function.getFunctionType().value() == functionType) {
                return function;
            }
        }
        return null;
    }

    public void setImageType(final byte _imageType) {
        this.imageType = _imageType;
    }

    public byte getImageType() {
        return this.imageType;
    }

    public boolean canInteract() {
        return this.canInteract;
    }

    @Override
    public void die(final ME2GameObject _killer) {
    }

    public void heartBeat() {
        if (this.isCalled() && System.currentTimeMillis() - this.getRefreshTime() >= this.getExistsTime()) {
            this.destroy();
            this.where().getMonsterList().remove(this);
            MapSynchronousInfoBroadcast.getInstance().put(this.where(), new DisappearNotify(this.getObjectType().value(), this.getID()), false, 0);
        }
    }

    public void beginFollow(final HeroPlayer _player) {
        (this.followAi = new NPCFollowAI(this)).startFollow(_player);
        this.canInteract = false;
    }

    public void stopFollowTask() {
        if (this.followAi != null) {
            this.followAi.stopFollow();
            ThreadPoolFactory.getInstance().removeAI(this.followAi);
            this.followAi = null;
            this.canInteract = true;
        }
    }

    public void gotoMap(final Map _map) {
        if (this.where() != null) {
            this.where().getNpcList().remove(this);
            if (this.where() != _map) {
                MapSynchronousInfoBroadcast.getInstance().put(this.where(), new DisappearNotify(this.getObjectType().value(), this.getID()), false, 0);
            }
        }
        this.live(_map);
        if (_map != null) {
            this.where().getNpcList().add(this);
            MapSynchronousInfoBroadcast.getInstance().put((short) 3, this.where(), new NpcRefreshNotify((short) 3, this), false, 0);
        }
    }

    @Override
    public void destroy() {
        this.invalid();
        this.stopFollowTask();
        NotPlayerServiceImpl.getInstance().removeNpc(this);
    }

    @Override
    public void happenFight() {
    }

    @Override
    public byte getDefaultSpeed() {
        return 4;
    }

    @Override
    public short getAnimationID() {
        return this.animationID;
    }

    @Override
    public void setAnimationID(final short animationID) {
        this.animationID = animationID;
    }
}
