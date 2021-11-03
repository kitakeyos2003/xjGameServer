// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.service;

import hero.item.detail.EGoodsTrait;
import hero.item.enhance.WeaponBloodyEnhance;
import hero.item.Weapon;

public class WeaponRankUnit {

    public Weapon weapon;
    public String ownerName;
    public double score;
    public byte[] genericEnhance;
    public boolean existSeal;
    public String genericEnhanceDesc;
    public WeaponBloodyEnhance bloodyEnhance;
    public String bloodyEnhanceDesc;

    public WeaponRankUnit(final Weapon _weapon, final String _ownerName, final String _genericEnhanceDesc, final String _bloodyEnhanceDesc, final boolean _existSeal) {
        this.genericEnhanceDesc = "";
        this.weapon = _weapon;
        this.ownerName = _ownerName;
        this.existSeal = _existSeal;
        this.genericEnhance = new byte[12];
        this.bloodyEnhance = new WeaponBloodyEnhance(_weapon.getPveEnhanceID(), _weapon.getPvpEnhanceID());
        if (!_genericEnhanceDesc.equals("")) {
            this.genericEnhanceDesc = _genericEnhanceDesc;
            String[] genericEnhanceDescData = _genericEnhanceDesc.split("#");
            for (int i = 0; i < genericEnhanceDescData.length; ++i) {
                this.genericEnhance[i] = Byte.parseByte(genericEnhanceDescData[i]);
            }
        }
        this.score = calculateRankScore(this.weapon, this.genericEnhance, this.bloodyEnhance);
    }

    private static double calculateRankScore(final Weapon weapon, final byte[] _genericEnhance, final WeaponBloodyEnhance _bloodyEnhance) {
        double magicAttack = 0.0;
        if (weapon.getMagicDamage() != null) {
            magicAttack = (weapon.getMagicDamage().maxDamageValue + weapon.getMagicDamage().minDamageValue) / 2.0;
        }
        double weaponDps = (weapon.getMinPhysicsAttack() + weapon.getMaxPhysicsAttack() + magicAttack) / (2.0f * weapon.getImmobilityTime());
        double genericEnhanceLevle = 0.0;
        for (final byte enhanceLevel : _genericEnhance) {
            genericEnhanceLevle += enhanceLevel;
        }
        genericEnhanceLevle /= 4.0;
        return Math.pow(genericEnhanceLevle + 1.0, 2.0) / (2.0 * (genericEnhanceLevle + 1.0)) * (0.15 * _bloodyEnhance.getPvpNumber() + _bloodyEnhance.getPveNumber()) * weapon.getNeedLevel() * ((weapon.getTrait() == EGoodsTrait.YU_ZHI) ? 2 : 3) * weaponDps / 1000.0;
    }
}
