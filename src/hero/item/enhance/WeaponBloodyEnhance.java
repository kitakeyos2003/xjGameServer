// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.enhance;

import hero.effect.detail.TouchEffect;

public class WeaponBloodyEnhance {

    private int hostPveDataID;
    private int hostPvpDataID;
    private int pveNumber;
    private int pveUpgradeNumber;
    private byte pveLevel;
    private TouchEffect pveBuff;
    private int pvpNumber;
    private int pvpUpgradeNumber;
    private byte pvpLevel;
    private TouchEffect pvpBuff;
    private static final short BASE_NUMBER_OF_PVE = 10;
    private static final short BASE_NUMBER_OF_PVP = 100;
    private static final byte MAX_LEVEL = 12;

    public WeaponBloodyEnhance(final int _hostPveDataID, final int _hostPvpDataID) {
        this.hostPveDataID = _hostPveDataID;
        this.hostPvpDataID = _hostPvpDataID;
        this.pveUpgradeNumber = 10;
        this.pvpUpgradeNumber = 100;
    }

    public boolean addPveNumber() {
        ++this.pveNumber;
        if (this.pveLevel < 12) {
            int nextLevelNeedNumber = 0;
            byte oldPveLevel = 0;
            for (byte level = 1; level <= 12; ++level) {
                nextLevelNeedNumber += level * level * 10;
                if (this.pveNumber < nextLevelNeedNumber) {
                    this.pveUpgradeNumber = nextLevelNeedNumber;
                    break;
                }
                this.pveLevel = level;
            }
            if (this.pveLevel != oldPveLevel) {
                this.pveBuff = EnhanceService.getInstance().getEffect(this.hostPveDataID, this.pveLevel);
                return true;
            }
        }
        return false;
    }

    public int getPveNumber() {
        return this.pveNumber;
    }

    public int getPveUpgradeNumber() {
        return this.pveUpgradeNumber;
    }

    public void setPveNumber(final int _pveNumber) {
        this.pveNumber = _pveNumber;
        if (this.pveNumber > 0) {
            int nextLevelNeedNumber = 0;
            for (byte level = 1; level <= 12; ++level) {
                nextLevelNeedNumber += level * level * 10;
                if (this.pveNumber < nextLevelNeedNumber) {
                    this.pveUpgradeNumber = nextLevelNeedNumber;
                    break;
                }
                this.pveLevel = level;
            }
            if (this.pveLevel > 0) {
                this.pveBuff = EnhanceService.getInstance().getEffect(this.hostPveDataID, this.pveLevel);
            }
        } else {
            this.pveUpgradeNumber = 10;
        }
    }

    public byte getPveLevel() {
        return this.pveLevel;
    }

    public boolean addPvpNumber() {
        ++this.pvpNumber;
        if (this.pvpLevel < 12) {
            int nextLevelNeedNumber = 0;
            byte oldPvpLevel = this.pvpLevel;
            for (byte level = 1; level <= 12; ++level) {
                nextLevelNeedNumber += level * level * 100;
                if (this.pvpNumber < nextLevelNeedNumber) {
                    this.pvpUpgradeNumber = nextLevelNeedNumber;
                    break;
                }
                this.pvpLevel = level;
            }
            if (this.pvpLevel != oldPvpLevel) {
                this.pvpBuff = EnhanceService.getInstance().getEffect(this.hostPvpDataID, this.pvpLevel);
                return true;
            }
        }
        return false;
    }

    public int getPvpNumber() {
        return this.pvpNumber;
    }

    public int getPvpUpgradeNumber() {
        return this.pvpUpgradeNumber;
    }

    public void setPvpNumber(final int _pvpNumber) {
        this.pvpNumber = _pvpNumber;
        if (this.pveNumber > 0) {
            int nextLevelNeedNumber = 0;
            for (byte level = 1; level <= 12; ++level) {
                nextLevelNeedNumber += level * level * 100;
                if (this.pvpNumber < nextLevelNeedNumber) {
                    this.pvpUpgradeNumber = nextLevelNeedNumber;
                    break;
                }
                this.pvpLevel = level;
            }
            if (this.pvpLevel > 0) {
                this.pvpBuff = EnhanceService.getInstance().getEffect(this.hostPvpDataID, this.pvpLevel);
            }
        } else {
            this.pvpUpgradeNumber = 100;
        }
    }

    public byte getPvpLevel() {
        return this.pvpLevel;
    }

    public TouchEffect getPveBuff() {
        return this.pveBuff;
    }

    public TouchEffect getPvpBuff() {
        return this.pvpBuff;
    }
}
