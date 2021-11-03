// 
// Decompiled by Procyon v0.5.36
// 
package hero.fight.message;

import java.io.IOException;
import hero.item.EquipmentInstance;
import hero.fight.service.FightServiceImpl;
import hero.item.Weapon;
import hero.player.HeroPlayer;
import hero.share.ME2GameObject;
import yoyo.core.packet.AbsResponseMessage;

public class GenericAttackViewNotify extends AbsResponseMessage {

    private byte attackerObjectType;
    private int attackerObjectID;
    private byte targetObjectType;
    private int targetObjectID;
    private ME2GameObject attacker;

    public GenericAttackViewNotify(final byte _attackerObjectType, final int _attackerObjectID, final byte _targetObjectType, final int _targetObjectID) {
        this.attackerObjectType = _attackerObjectType;
        this.attackerObjectID = _attackerObjectID;
        this.targetObjectType = _targetObjectType;
        this.targetObjectID = _targetObjectID;
    }

    public GenericAttackViewNotify(final byte _attackerObjectType, final int _attackerObjectID, final byte _targetObjectType, final int _targetObjectID, final ME2GameObject _attacker) {
        this.attackerObjectType = _attackerObjectType;
        this.attackerObjectID = _attackerObjectID;
        this.targetObjectType = _targetObjectType;
        this.targetObjectID = _targetObjectID;
        this.attacker = _attacker;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.attackerObjectType);
        this.yos.writeInt(this.attackerObjectID);
        this.yos.writeByte(this.targetObjectType);
        this.yos.writeInt(this.targetObjectID);
        if (this.attacker != null && this.attacker instanceof HeroPlayer) {
            HeroPlayer player = (HeroPlayer) this.attacker;
            EquipmentInstance weapon = player.getBodyWear().getWeapon();
            if (weapon != null) {
                Weapon.EWeaponType wType = ((Weapon) weapon.getArchetype()).getWeaponType();
                this.yos.writeShort(FightServiceImpl.getInstance().getFightTarget(wType)[0]);
                this.yos.writeShort(FightServiceImpl.getInstance().getFightTarget(wType)[1]);
            } else {
                this.yos.writeShort(-1);
                this.yos.writeShort(-1);
            }
        } else {
            this.yos.writeShort(-1);
            this.yos.writeShort(-1);
        }
    }
}
