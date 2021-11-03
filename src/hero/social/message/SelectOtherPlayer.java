// 
// Decompiled by Procyon v0.5.36
// 
package hero.social.message;

import java.io.IOException;
import hero.player.service.PlayerServiceImpl;
import javolution.util.FastList;
import java.util.Random;
import hero.player.HeroPlayer;
import yoyo.core.packet.AbsResponseMessage;

public class SelectOtherPlayer extends AbsResponseMessage {

    private byte type;
    private String name;
    private byte sex;
    private byte vocation;
    private short level;
    private HeroPlayer who;
    private static final Random RANDOM;

    static {
        RANDOM = new Random();
    }

    public SelectOtherPlayer(final byte _type, final byte _sex, final byte _vocation, final short _level, final HeroPlayer _who) {
        this.type = _type;
        this.sex = _sex;
        this.vocation = _vocation;
        this.level = _level;
        this.who = _who;
    }

    public SelectOtherPlayer(final byte _type, final String _name, final HeroPlayer _who) {
        this.type = _type;
        this.name = _name;
        this.who = _who;
    }

    public SelectOtherPlayer(final byte _type, final HeroPlayer _who) {
        this.type = _type;
        this.who = _who;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        FastList<HeroPlayer> list = (FastList<HeroPlayer>) new FastList();
        FastList<HeroPlayer> returnList = (FastList<HeroPlayer>) new FastList();
        list = PlayerServiceImpl.getInstance().getPlayerListByClan(this.who.getClan());
        int x = SelectOtherPlayer.RANDOM.nextInt(list.size());
        if (x + 5 > list.size()) {
            x = list.size() - 5;
            if (x < 0) {
                x = 0;
            }
        }
        if (this.type == 5) {
            for (int i = x; i < list.size(); ++i) {
                HeroPlayer player = (HeroPlayer) list.get(i);
                returnList.add(player);
                if (returnList.size() >= 5) {
                    break;
                }
            }
            this.yos.writeByte(this.type);
        } else if (this.type == 4) {
            for (int i = x; i < list.size(); ++i) {
                HeroPlayer player = (HeroPlayer) list.get(i);
                if (player.getSex().value() == this.sex && player.getClan().getID() == this.vocation && player.getLevel() == this.level) {
                    returnList.add(player);
                }
                if (returnList.size() >= 5) {
                    break;
                }
            }
            this.yos.writeByte(this.type);
        } else {
            for (int i = x; i < list.size(); ++i) {
                HeroPlayer player = (HeroPlayer) list.get(i);
                if (player.getName().indexOf(this.name) > -1) {
                    returnList.add(player);
                }
                if (returnList.size() >= 5) {
                    break;
                }
            }
            this.yos.writeByte(this.type);
        }
        this.yos.writeByte(returnList.size());
        for (int i = 0; i < returnList.size(); ++i) {
            HeroPlayer player = (HeroPlayer) returnList.get(i);
            this.yos.writeUTF(player.getName());
            this.yos.writeShort(player.getLevel());
            this.yos.writeInt(player.getID());
        }
    }
}
