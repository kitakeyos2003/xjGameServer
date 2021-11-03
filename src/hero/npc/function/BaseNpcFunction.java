// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.function;

import yoyo.tools.YOYOInputStream;
import hero.player.HeroPlayer;
import hero.npc.dict.NpcFunIconDict;
import hero.npc.detail.NpcHandshakeOptionData;
import java.util.ArrayList;

public abstract class BaseNpcFunction {

    public static final int FUNCTION_EXPEND_MODULUS = 100000;
    private int hostNpcID;
    protected ArrayList<NpcHandshakeOptionData> optionList;

    public BaseNpcFunction(final int _hostNpcID) {
        this.hostNpcID = _hostNpcID;
        this.optionList = new ArrayList<NpcHandshakeOptionData>();
        this.initTopLayerOptionList();
    }

    public int getHostNpcID() {
        return this.hostNpcID;
    }

    public short getMinMarkIconID() {
        return NpcFunIconDict.getInstance().getNpcFunIcon(this.getFunctionType().value())[0];
    }

    public short getMinMarkIconID2() {
        return NpcFunIconDict.getInstance().getNpcFunIcon(this.getFunctionType().value())[1];
    }

    public abstract void process(final HeroPlayer p0, final byte p1, final int p2, final YOYOInputStream p3) throws Exception;

    public abstract ENpcFunctionType getFunctionType();

    public abstract void initTopLayerOptionList();

    public abstract ArrayList<NpcHandshakeOptionData> getTopLayerOptionList(final HeroPlayer p0);
}
