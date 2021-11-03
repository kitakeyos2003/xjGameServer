// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill.message;

import java.io.IOException;
import java.util.Iterator;
import hero.skill.unit.ActiveSkillUnit;
import hero.player.HeroPlayer;
import hero.skill.ActiveSkill;
import java.util.ArrayList;
import yoyo.core.packet.AbsResponseMessage;

public class RefreshSkillTime extends AbsResponseMessage {

    private ArrayList<ActiveSkill> activeSkillList;

    public RefreshSkillTime(final HeroPlayer _player) {
        this.activeSkillList = _player.activeSkillList;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        ActiveSkillUnit skillUnit = null;
        this.yos.writeShort(this.activeSkillList.size());
        for (final ActiveSkill skill : this.activeSkillList) {
            this.yos.writeInt(skill.id);
            skillUnit = (ActiveSkillUnit) skill.skillUnit;
            this.yos.writeShort(skillUnit.releaseTime * 1000.0f);
            if (skillUnit.releaseTime > 0.0f) {
                System.out.println("\u72c2\u66b4\u540e\u901f\u5ea6\u5217\u8868:" + skillUnit.releaseTime);
            }
        }
    }
}
