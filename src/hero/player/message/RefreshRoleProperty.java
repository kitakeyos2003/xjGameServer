// 
// Decompiled by Procyon v0.5.36
// 
package hero.player.message;

import java.io.IOException;
import hero.micro.service.MicroServiceImpl;
import hero.guild.service.GuildServiceImpl;
import hero.player.service.PlayerDAO;
import hero.share.EMagic;
import hero.player.HeroPlayer;
import org.apache.log4j.Logger;
import yoyo.core.packet.AbsResponseMessage;

public class RefreshRoleProperty extends AbsResponseMessage {

    private static Logger log;
    private static final String PERCENT_CHARACTOR = "%";
    private HeroPlayer player;

    static {
        RefreshRoleProperty.log = Logger.getLogger((Class) RefreshRoleProperty.class);
    }

    public RefreshRoleProperty(final HeroPlayer _player) {
        this.player = _player;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeInt(this.player.getExp());
        this.yos.writeInt(this.player.getUpgradeNeedExp());
        this.yos.writeInt(this.player.getExpShow());
        this.yos.writeInt(this.player.getUpgradeNeedExpShow());
        this.yos.writeInt(this.player.getHp());
        this.yos.writeInt(this.player.getActualProperty().getHpMax());
        this.yos.writeInt(this.player.getMp());
        this.yos.writeInt(this.player.getActualProperty().getMpMax());
        this.yos.writeShort(this.player.getActualProperty().getStrength());
        this.yos.writeShort(this.player.getActualProperty().getAgility());
        this.yos.writeShort(this.player.getActualProperty().getStamina());
        this.yos.writeShort(this.player.getActualProperty().getInte());
        this.yos.writeShort(this.player.getActualProperty().getSpirit());
        this.yos.writeShort(this.player.getActualProperty().getLucky());
        this.yos.writeInt(this.player.getActualProperty().getMaxPhysicsAttack());
        this.yos.writeInt(this.player.getActualProperty().getMinPhysicsAttack());
        int magic = (int) this.player.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(EMagic.FIRE);
        magic += (int) this.player.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(EMagic.SANCTITY);
        magic += (int) this.player.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(EMagic.SOIL);
        magic += (int) this.player.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(EMagic.UMBRA);
        magic += (int) this.player.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(EMagic.WATER);
        magic /= 5;
        magic += (int) this.player.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(EMagic.ALL);
        this.yos.writeInt(magic);
        this.yos.writeInt(PlayerDAO.getPlayerFailerNumber(this.player.getUserID()));
        this.yos.writeInt(PlayerDAO.getPlayerWinnerNumber(this.player.getUserID()));
        this.yos.writeInt(0);
        this.yos.writeInt(0);
        this.yos.writeInt(this.player.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(EMagic.UMBRA));
        this.yos.writeInt(this.player.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(EMagic.SANCTITY));
        this.yos.writeInt(this.player.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(EMagic.FIRE));
        this.yos.writeInt(this.player.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(EMagic.WATER));
        this.yos.writeInt(this.player.getActualProperty().getBaseMagicHarmList().getEMagicHarmValue(EMagic.SOIL));
        this.yos.writeUTF(String.valueOf(String.valueOf(this.player.getActualProperty().getPhysicsDeathblowOdds())) + "%");
        this.yos.writeUTF(String.valueOf(String.valueOf(this.player.getActualProperty().getMagicDeathblowOdds())) + "%");
        this.yos.writeUTF(String.valueOf(String.valueOf(this.player.getActualProperty().getPhysicsHitOdds())) + "%");
        this.yos.writeUTF(String.valueOf(String.valueOf(this.player.getActualProperty().getMagicHitOdds())) + "%");
        this.yos.writeInt(this.player.getActualProperty().getDefense());
        this.yos.writeInt(this.player.getActualProperty().getMagicFastnessList().getEMagicFastnessValue(EMagic.UMBRA));
        this.yos.writeInt(this.player.getActualProperty().getMagicFastnessList().getEMagicFastnessValue(EMagic.SANCTITY));
        this.yos.writeInt(this.player.getActualProperty().getMagicFastnessList().getEMagicFastnessValue(EMagic.FIRE));
        this.yos.writeInt(this.player.getActualProperty().getMagicFastnessList().getEMagicFastnessValue(EMagic.WATER));
        this.yos.writeInt(this.player.getActualProperty().getMagicFastnessList().getEMagicFastnessValue(EMagic.SOIL));
        this.yos.writeUTF(String.valueOf(String.valueOf(this.player.getActualProperty().getPhysicsDuckOdds())) + "%");
        this.yos.writeShort(this.player.getActualAttackImmobilityTime());
        String immobilityTime = String.valueOf(this.player.getActualAttackImmobilityTime() / 1000.0f);
        this.yos.writeUTF(immobilityTime);
        this.yos.writeByte(this.player.getAttackRange());
        String guild = GuildServiceImpl.getInstance().getGuildName(this.player);
        String memberRank = GuildServiceImpl.getInstance().getMemberRank(this.player);
        String master = MicroServiceImpl.getInstance().getMasterName(this.player);
        String app = MicroServiceImpl.getInstance().getApprenticeNameList(this.player);
        this.yos.writeUTF(guild);
        this.yos.writeUTF(memberRank);
        this.yos.writeUTF(master);
        this.yos.writeUTF(app);
        this.yos.writeShort(this.player.surplusSkillPoint);
        RefreshRoleProperty.log.info((Object) ("output size = " + String.valueOf(this.yos.size()) + "player id = " + String.valueOf(this.player.getUserID())));
    }
}
