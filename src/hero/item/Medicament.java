// 
// Decompiled by Procyon v0.5.36
// 
package hero.item;

import hero.log.service.LogServiceImpl;
import hero.effect.service.EffectServiceImpl;
import hero.fight.service.FightServiceImpl;
import hero.share.ME2GameObject;
import hero.skill.service.SkillServiceImpl;
import hero.player.HeroPlayer;
import hero.item.detail.EGoodsType;
import java.util.Random;
import hero.item.detail.AdditionEffect;

public class Medicament extends SingleGoods {

    private boolean canUseInFight;
    private boolean isDisappearAfterDead;
    private int publicCdVariable;
    private int maxCdTime;
    private int traceCdTime;
    private int resumeHp;
    private int resumeMp;
    private int resumeForceQuantity;
    private int resumeGasQuantity;
    private int releaseAnimation;
    private int releaseImage;
    public AdditionEffect[] additionEffectList;
    private static final Random RANDOM;

    static {
        RANDOM = new Random();
    }

    public Medicament(final short _stackNumss) {
        super(_stackNumss);
    }

    @Override
    public EGoodsType getGoodsType() {
        return EGoodsType.MEDICAMENT;
    }

    @Override
    public void initDescription() {
    }

    @Override
    public boolean isIOGoods() {
        return false;
    }

    public void setResumeHp(final int _resumeHp) {
        this.resumeHp = _resumeHp;
    }

    public int getResumeHp() {
        return this.resumeHp;
    }

    public void setResumeMp(final int _resumeMp) {
        this.resumeMp = _resumeMp;
    }

    public int getResumeMp() {
        return this.resumeMp;
    }

    public void setResumeForceQuantity(final int _resumeForceQuantity) {
        this.resumeForceQuantity = _resumeForceQuantity;
    }

    public int getResumeForceQuantity() {
        return this.resumeForceQuantity;
    }

    public void setResumeGasQuantity(final int _resumeGasQuantity) {
        this.resumeGasQuantity = _resumeGasQuantity;
    }

    public int getResumeGasQuantity() {
        return this.resumeGasQuantity;
    }

    public boolean canUseInFight() {
        return this.canUseInFight;
    }

    public void setCanUseInFight(final boolean _canUse) {
        this.canUseInFight = _canUse;
    }

    public boolean isDisappearAfterDead() {
        return this.isDisappearAfterDead;
    }

    public void setIsDisappearAfterDead(final boolean _isDisappear) {
        this.isDisappearAfterDead = _isDisappear;
    }

    public int getPublicCdVariable() {
        return this.publicCdVariable;
    }

    public void setPublicCdVariable(final int _publicCdVariable) {
        this.publicCdVariable = _publicCdVariable;
    }

    public int getMaxCdTime() {
        return this.maxCdTime;
    }

    public void setMaxCdTime(final int _maxCdTime) {
        this.maxCdTime = _maxCdTime;
    }

    public int getReleaseImage() {
        return this.releaseImage;
    }

    public void setReleaseImage(final int _releaseImage) {
        this.releaseImage = _releaseImage;
    }

    public int getReleaseAnimation() {
        return this.releaseAnimation;
    }

    public void setReleaseAnimation(final int _releaseAnimation) {
        this.releaseAnimation = _releaseAnimation;
    }

    public int getTraceCdTime() {
        return this.traceCdTime;
    }

    @Override
    public byte getSingleGoodsType() {
        return 1;
    }

    @Override
    public boolean beUse(final HeroPlayer _player, final Object _target) {
        if (_player.isDead()) {
            return false;
        }
        SkillServiceImpl.getInstance().sendSingleSkillAnimation(_player, null, this.releaseAnimation, this.releaseImage, 0, 0, (byte) (-1), (byte) 1, (byte) 2, (byte) 2, (byte) 0);
        if (this.resumeHp > 0) {
            FightServiceImpl.getInstance().processAddHp(_player, _player, this.resumeHp, true, false);
        }
        if (this.resumeMp > 0) {
            _player.addMp(this.resumeMp);
            FightServiceImpl.getInstance().processSingleTargetMpChange(_player, true);
        }
        if (this.resumeForceQuantity > 0) {
            _player.consumeForceQuantity(-this.resumeForceQuantity);
            FightServiceImpl.getInstance().processPersionalForceQuantityChange(_player);
        } else if (this.resumeGasQuantity > 0) {
            _player.consumeGasQuantity(-this.resumeGasQuantity);
            FightServiceImpl.getInstance().processPersionalForceQuantityChange(_player);
        }
        if (this.additionEffectList != null) {
            AdditionEffect[] additionEffectList;
            for (int length = (additionEffectList = this.additionEffectList).length, i = 0; i < length; ++i) {
                AdditionEffect additionEffect = additionEffectList[i];
                if (Medicament.RANDOM.nextFloat() <= additionEffect.activeOdds) {
                    EffectServiceImpl.getInstance().appendSkillEffect(_player, _player, additionEffect.effectUnitID);
                }
            }
        }
        LogServiceImpl.getInstance().goodsUsedLog(_player.getLoginInfo().accountID, _player.getLoginInfo().username, _player.getUserID(), _player.getName(), this.getID(), this.getName(), this.getTrait().getDesc(), this.getGoodsType().getDescription());
        return true;
    }
}
