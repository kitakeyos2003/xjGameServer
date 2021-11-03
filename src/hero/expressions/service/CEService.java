// 
// Decompiled by Procyon v0.5.36
// 
package hero.expressions.service;

import hero.share.EObjectLevel;
import org.apache.log4j.Logger;

public class CEService {

    private static Logger log;

    static {
        CEService.log = Logger.getLogger((Class) CEService.class);
    }

    public static final boolean inAttackRange(final int _attackerLevelValue, final int _attackerLevel, final int _attackerX, final int _attackerY, final int _targetLevel, final int _targetX, final int _targetY) {
        int absLevelDifference = _attackerLevel - _targetLevel;
        if (absLevelDifference < -10) {
            absLevelDifference = -10;
        } else if (absLevelDifference > 10) {
            absLevelDifference = 10;
        }
        double attackableDistance = 4.0 + absLevelDifference * 0.04 + 1.0;
        if (attackableDistance >= 9.0) {
            attackableDistance = 9.0;
        }
        boolean inDistance = attackableDistance * attackableDistance >= (_attackerX - _targetX) * (_attackerX - _targetX) + (_attackerY - _targetY) * (_attackerY - _targetY);
        return inDistance;
    }

    public static final int cancel(final int _levelBeforeUpgrade, final int _vocationPara) {
        int result = 0;
        int levelPara = 3 - _levelBeforeUpgrade / 20;
        if (_levelBeforeUpgrade >= 60) {
            levelPara = 1;
        }
        result = (_vocationPara + levelPara) / (levelPara * 3);
        int residue = (_vocationPara + levelPara) % (levelPara * 3);
        int temp = (_levelBeforeUpgrade / 10 + 1) * 10 - residue;
        if (_levelBeforeUpgrade > temp) {
            ++result;
        }
        return result;
    }

    public static final int playerBaseAttribute(final int _playerLevel, final float _vocationAttrPara) {
        return (int) (_vocationAttrPara * (_playerLevel + 145) * (_playerLevel + 145) / (_playerLevel + 2500));
    }

    public static final int hpByStamina(final int _stamina, final int _level, final int _atb) {
        return (int) (_stamina * (6.0 + 0.4 * _level) * _atb);
    }

    public static final int mpByInte(final int _inte, final int _level, final int _para) {
        return (int) (_inte * _para * (3.0 + 0.4 * _level) + 0.5);
    }

    public static final int magicHarmByInte(final int _inte) {
        return _inte / 2;
    }

    public static final int magicHarm(final int _inte, final int _weaponMagicHarm) {
        return _inte / 2 + _weaponMagicHarm;
    }

    public static final float basePhysicsHitOdds(final int _luck, final int _hitLevel, final int _level) {
        float value = 0.85f + 0.05f * _luck / (_luck + 200) + 0.01f * _hitLevel * 400.0f / ((_level + 20) * (_level + 20));
        if (value > 1.0f) {
            value = 1.0f;
        }
        return oddsFormat(value);
    }

    public static final short hitLevel(final int _luck) {
        short physicsHitLevel = 0;
        physicsHitLevel = (short) _luck;
        return physicsHitLevel;
    }

    public static final float attackPhysicsHitOdds(final int _luck, final int _hitLevel, final int _attackerLevel, final int _targetLevel) {
        int levelDifference = _targetLevel - _attackerLevel;
        if (levelDifference < 0) {
            levelDifference = 0;
        }
        float value = 0.85f + 0.05f * _luck / (_luck + 200) - 3.0f * levelDifference / 100.0f + 0.01f * _hitLevel * 400.0f / ((_attackerLevel + 20) * (_attackerLevel + 20));
        if (value < 0.25) {
            value = 0.25f;
        } else if (value > 1.0f) {
            value = 1.0f;
        }
        return oddsFormat(value);
    }

    public static final float baseMagicHitOdds(final int _luck, final int _hitLevel, final int _level) {
        float value = 0.9f + 0.05f * _luck / (_luck + 200) + 0.01f * _hitLevel * 400.0f / ((_level + 20) * (_level + 20));
        if (value > 1.0f) {
            value = 1.0f;
        }
        return oddsFormat(value);
    }

    public static final float attackMagicHitOdds(final int _luck, final int _hitLevel, final int _attackerLevel, final int _targetLevel, final int _targetMagicFastness) {
        int levelDifference = _targetLevel - _attackerLevel;
        if (levelDifference < 0) {
            levelDifference = 0;
        }
        float value = 0.45f + (0.45f - _targetMagicFastness / (30.0f * _targetLevel)) + 0.05f * _luck / (_luck + 200) - 3 * levelDifference * 0.01f + 0.01f * _hitLevel * 400.0f / ((_attackerLevel + 20) * (_attackerLevel + 20));
        if (value < 0.25) {
            value = 0.25f;
        } else if (value > 1.0f) {
            value = 1.0f;
        }
        return oddsFormat(value);
    }

    public static final float basePhysicsDeathblowOdds(final int _physicsDeathblowLevel, final int _roleLevel) {
        float value = 0.01f * (_physicsDeathblowLevel * 4) * (2 * _roleLevel + 40) / ((_roleLevel + 35) * (_roleLevel + 35));
        if (value > 1.0f) {
            value = 1.0f;
        }
        return oddsFormat(value);
    }

    public static final short physicsDeathblowLevel(final int _agility, final int _luck) {
        short deathblowLevel = 0;
        deathblowLevel = (short) ((_agility + _luck) / 2);
        return deathblowLevel;
    }

    public static final float attackPhysicsDeathblowOdds(final int _agility, final int _physicsDeathblowLevel, final int _attackerLevel, final int _targetLevel) {
        int levelDifference = _targetLevel - _attackerLevel;
        if (levelDifference < 0) {
            levelDifference = 0;
        }
        float value = 0.01f * (_physicsDeathblowLevel * 4) * (2 * _attackerLevel + 40) / ((_attackerLevel + 35) * (_attackerLevel + 35)) - 2.0f * levelDifference * 0.01f;
        if (value < 0.0f) {
            value = 0.0f;
        } else if (value > 0.5f) {
            value = 0.5f;
        }
        return oddsFormat(value);
    }

    public static final short magicDeathblowLevel(final int _inte, final int _luck) {
        short deathblowLevel = 0;
        deathblowLevel = (short) ((_inte + _luck) / 2);
        return deathblowLevel;
    }

    public static final float baseMagicDeathblowOdds(final int _magicDeathblowLevel, final int _roleLevel) {
        float value = 0.01f * (_magicDeathblowLevel * 4) * (2 * _roleLevel + 40) / ((_roleLevel + 35) * (_roleLevel + 35));
        if (value > 1.0f) {
            value = 1.0f;
        }
        return oddsFormat(value);
    }

    public static final float attackMagicDeathblowOdds(final int _magicDeathblowLevel, final int _attackerLevel, final int _targetLevel) {
        int levelDifference = _targetLevel - _attackerLevel;
        if (levelDifference < 0) {
            levelDifference = 0;
        }
        float value = 0.01f * (_magicDeathblowLevel * 4) * (2 * _attackerLevel + 40) / ((_attackerLevel + 35) * (_attackerLevel + 35) - 2 * levelDifference * 0.01f);
        if (value < 0.0f) {
            value = 0.0f;
        } else if (value > 0.5f) {
            value = 0.5f;
        }
        return oddsFormat(value);
    }

    public static final float basePhysicsDuckOdds(final int _duckLevel, final int _roleLevel) {
        float value = 0.01f * (_duckLevel * 4) * (2 * _roleLevel + 40) / ((_roleLevel + 35) * (_roleLevel + 35));
        if (value > 1.0f) {
            value = 1.0f;
        }
        return oddsFormat(value);
    }

    public static final short duckLevel(final int _agility, final int _luck) {
        short result = (short) ((_agility + _luck) / 12);
        return result;
    }

    public static final float attackPhysicsDuckOdds(final int _attackerLevel, final int _targetAgility, final int _targetLucky, final int _targetDuckLevel, final int _targetLevel) {
        int levelDifference = _attackerLevel - _targetLevel;
        if (levelDifference < 0) {
            levelDifference = 0;
        }
        float value = 0.01f * (_targetDuckLevel * 5 + _targetAgility * 1 + _targetLucky) * (2 * _targetLevel + 40) / ((_targetLevel + 35) * (_targetLevel + 35)) - 2 * levelDifference * 0.01f;
        if (value < 0.0f) {
            value = 0.0f;
        } else if (value > 0.5f) {
            value = 0.5f;
        }
        return oddsFormat(value);
    }

    public static final int maxPhysicsAttack(final int _strength, final int _agility, final float _vocationParaA, final float _vocationParaB, final float _vocationParaC, final int _weaponMaxAttack, final int _weaponPara, final float _attackImmobilityTime, final int _attackPara) {
        int result = 0;
        result = (int) (((_strength * _vocationParaA + _agility * _vocationParaB) / 3.0f + (_weaponMaxAttack + _weaponPara * _vocationParaC) / _attackImmobilityTime) * _attackImmobilityTime * _attackPara);
        return result;
    }

    public static final int minPhysicsAttack(final int _strength, final int _agility, final float _vocationParaA, final float _vocationParaB, final float _vocationParaC, final int _weaponMinAttack, final int _weaponPara, final float _attackImmobilityTime, final int _attackPara) {
        return (int) (((_strength * _vocationParaA + _agility * _vocationParaB) / 3.0f + (_weaponMinAttack + _weaponPara * _vocationParaC) / _attackImmobilityTime) * _attackImmobilityTime * _attackPara);
    }

    public static final int defenseBySpirit(final int _spirit, final int _vocationParaB) {
        return _spirit * _vocationParaB;
    }

    public static final int physicsHarm(final int _attackerLevel, final int _attackerPhysicsAttack, final int _targetLevel, final int _targetDefense) {
        int levelDifference = _attackerLevel - _targetLevel;
        int result = 0;
        if (levelDifference >= 0) {
            if (levelDifference > 10) {
                levelDifference = 10;
            }
        } else if (levelDifference < -10) {
            levelDifference = -10;
        }
        float para = 0.0f;
        if (levelDifference >= 0) {
            for (int i = 0; i < levelDifference; ++i) {
                para += (float) Math.sin((i + 1) * 3.1416 / 10.0);
            }
        } else {
            for (int i = 0; i > levelDifference; --i) {
                para += (float) Math.sin((i - 1) * 3.1416 / 10.0);
            }
        }
        result = (int) (_attackerPhysicsAttack * (1.0f - _targetDefense / (_targetDefense + 85.0f * _targetLevel + 400.0f)) * (1.0f + 5.0f * para / 100.0f));
        return result;
    }

    public static final int weaponPhysicsAttackBySkill(final int _sumAttack, final float _multiples, final int _valueAdded) {
        return (int) (_sumAttack * _multiples + _valueAdded);
    }

    public static final int magicHarmBySkill(final float _attackerMagicHarmValue, final int _skillMagicHarmValue, final float _releaseTime, final int _pLevel, final int _skLevel) {
        return (int) (_skillMagicHarmValue * (_pLevel + _skLevel * 2) / (_pLevel + _skLevel) + _attackerMagicHarmValue * (_releaseTime + 1.0f) / 3.5f);
    }

    public static final int attackMagicHarm(final int _attackerLevel, final int _attakerMagicAttack, final int _targetLevel, final int _targetMagicFastness) {
        int result = 0;
        int levelDifference = _attackerLevel - _targetLevel;
        if (levelDifference >= 10) {
            levelDifference = 10;
        } else if (levelDifference < -10) {
            levelDifference = -10;
        }
        float para = 0.0f;
        if (levelDifference >= 0) {
            for (int i = 0; i < levelDifference; ++i) {
                para += (float) Math.sin((i + 1) * 3.1416 / 10.0);
            }
        } else {
            for (int i = 0; i > levelDifference; --i) {
                para += (float) Math.sin((i - 1) * 3.1416 / 10.0);
            }
        }
        result = (int) (_attakerMagicAttack * (1.0f - 30 * _targetMagicFastness / (30 * _targetMagicFastness + 85.0f * _targetLevel + 400.0f)) * (1.0f + 8.0f * para / 100.0f));
        return result;
    }

    public static final int magicResume(final int _skillResumeValue, final int _spiritValue, final int _inteValue, final float _weaponSanctityMagicValue, final float _releaseTime) {
        return (int) (_skillResumeValue + (_spiritValue / 2 + _weaponSanctityMagicValue * 2.0f - _inteValue / 2) * (_releaseTime + 1.0f) / 3.5f);
    }

    public static final int calculateDeathblowHarm(final int _originalHarmValue, final int _attackerLucky) {
        return (int) (_originalHarmValue * 1.5 + _originalHarmValue * 0.5 * _attackerLucky / 300.0);
    }

    public static final int expToNextLevel(final int _currrentLevel, final float _expOfCurrentLevel) {
        return (int) (_expOfCurrentLevel * 1.13f + 80 * (_currrentLevel - 1));
    }

    public static final int totalUpgradeExp(final int _level) {
        if (_level == 1) {
            return 200;
        }
        return (int) (totalUpgradeExp(_level - 1) * 1.13f + 80 * (_level - 1));
    }

    public static final int monsterBaseExperience(final int _monsterLevel, final EObjectLevel _monsterType) {
        return (3 + 6 * (_monsterLevel - 1)) * _monsterType.getBaseExperiencePara();
    }

    public static final int getExperienceFromMonster(final int _groupMemberNumber, final int _playerLevel, final int _monsterLevel, final int _monsterBaseExp) {
        if (_groupMemberNumber == 1) {
            if (_playerLevel < _monsterLevel) {
                if (_monsterLevel - _playerLevel > 50) {
                    return 1;
                }
                return (int) (_monsterBaseExp * (1.0f - (_monsterLevel - _playerLevel) * 2 * 0.01f));
            } else {
                if (_playerLevel == _monsterLevel) {
                    return (int) (_monsterBaseExp * (1.0f - (_monsterLevel - _playerLevel) * 2 * 0.01f));
                }
                if (_playerLevel - _monsterLevel <= 10) {
                    return (int) (_monsterBaseExp * (1.0f - (_playerLevel - _monsterLevel) * 8 * 0.01f));
                }
                return 1;
            }
        } else if (_playerLevel < _monsterLevel) {
            if (_monsterLevel - _playerLevel > 50) {
                return 1;
            }
            return (int) (_monsterBaseExp * _groupMemberNumber * 0.05f + _monsterBaseExp * (1.0f - (_monsterLevel - _playerLevel) * 2 * 0.01f) / (_groupMemberNumber + _groupMemberNumber * 0.09));
        } else {
            if (_playerLevel == _monsterLevel) {
                return (int) (_monsterBaseExp * _groupMemberNumber * 0.05f + _monsterBaseExp * (1.0f - (_monsterLevel - _playerLevel) * 2 * 0.01f) / (_groupMemberNumber + _groupMemberNumber * 0.09));
            }
            if (_playerLevel - _monsterLevel <= 10) {
                return (int) (_monsterBaseExp * _groupMemberNumber * 0.05f + _monsterBaseExp * (1.0f - (_playerLevel - _monsterLevel) * 8 * 0.01f) / _groupMemberNumber);
            }
            return 1;
        }
    }

    public static final int hpResumeAuto(final int _playerLevel, final int _spirit, final float _vocationStaminaPara) {
        int result = 0;
        result = (int) (hpByStamina(playerBaseAttribute(_playerLevel, _vocationStaminaPara), _playerLevel, 1) * 0.01);
        if (result <= 0) {
            result = 1;
        }
        return result;
    }

    public static final int mpResumeAuto(final int _playerLevel, final int _spirit, final float _vocationIntePara) {
        int result = 0;
        int mp = mpByInte(playerBaseAttribute(_playerLevel, _vocationIntePara), _playerLevel, EObjectLevel.NORMAL.getMpCalPara());
        result = (int) (mp * 0.01);
        if (result <= 0) {
            result = 1;
        }
        return result;
    }

    public static final int mpResumeAutoInFighting(final int _resumeMpNoneFight) {
        return (int) (_resumeMpNoneFight / 2.5f);
    }

    public static final float repairChargeOfEquipment(final int _price, final int _currentDurabilityPoint, final int _maxDurabilityPoint) {
        return _price / 4.0f * (_maxDurabilityPoint - _currentDurabilityPoint) / _maxDurabilityPoint;
    }

    public static int sellPriceOfEquipment(final int _price, final int _currentDurabilityPoint, final int _maxDurabilityPoint) {
        float money = _price / 17.5f * _currentDurabilityPoint / _maxDurabilityPoint + 0.5f;
        return (money < 1.0f) ? 1 : ((int) money);
    }

    public static final int taskExperience(final short _playerLevel, final short _taskLevel, final int _OriginalExperience) {
        if (_playerLevel < _taskLevel) {
            return _OriginalExperience;
        }
        int exp = (int) (_OriginalExperience * (1.0f - 0.04f * (_playerLevel - _taskLevel)));
        return (exp < 10) ? 10 : exp;
    }

    public static float oddsFormat(final float _value) {
        return Math.round((float) (int) (_value * 10000.0f)) / 100.0f;
    }

    public static void main(final String[] args) {
    }
}
