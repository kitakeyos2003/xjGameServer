// Decompiled with: CFR 0.151
// Class Version: 6
package hero.share;

import hero.player.define.EClan;
import hero.share.EVocationType;

public enum EVocation {
    ALL(0, "ALL") {

        @Override
        public boolean baseIs(EVocation eVocation) {
            return false;
        }

        @Override
        public float getAgilityCalcPara() {
            return 0.0f;
        }

        @Override
        public EVocation getBasicVoction() {
            return null;
        }

        @Override
        public float getInteCalcPara() {
            return 0.0f;
        }

        @Override
        public float getLuckyCalcPara() {
            return 0.0f;
        }

        @Override
        public float getPhysicsAttackParaA() {
            return 0.0f;
        }

        @Override
        public float getPhysicsAttackParaB() {
            return 0.0f;
        }

        @Override
        public float getPhysicsAttackParaC() {
            return 0.0f;
        }

        @Override
        public int getPhysicsDefenceAgilityPara() {
            return 0;
        }

        @Override
        public int getPhysicsDefenceSpiritPara() {
            return 0;
        }

        @Override
        public float getSpiritCalcPara() {
            return 0.0f;
        }

        @Override
        public float getStaminaCalPara() {
            return 0.0f;
        }

        @Override
        public float getStrengthCalcPara() {
            return 0.0f;
        }

        @Override
        public EVocation[] getSubVoction(EClan eClan) {
            return null;
        }

        @Override
        public EVocationType getType() {
            return null;
        }

        @Override
        public int wearableArmor() {
            return 0;
        }
    },
    LI_SHI(1, "力士") {

        @Override
        public EVocation getBasicVoction() {
            return null;
        }

        @Override
        public EVocation[] getSubVoction(EClan eClan) {
            EVocation[] eVocationArray = null;
            eVocationArray = eClan == EClan.LONG_SHAN ? new EVocation[]{JIN_GANG_LI_SHI, QING_TIAN_LI_SHI} : new EVocation[]{LUO_CHA_LI_SHI, XIU_LUO_LI_SHI};
            return eVocationArray;
        }

        @Override
        public EVocationType getType() {
            return EVocationType.PHYSICS;
        }

        @Override
        public int wearableArmor() {
            return 0;
        }

        @Override
        public float getStrengthCalcPara() {
            return 5.0f;
        }

        @Override
        public float getAgilityCalcPara() {
            return 4.0f;
        }

        @Override
        public float getStaminaCalPara() {
            return 5.0f;
        }

        @Override
        public float getInteCalcPara() {
            return 3.0f;
        }

        @Override
        public float getSpiritCalcPara() {
            return 3.0f;
        }

        @Override
        public float getLuckyCalcPara() {
            return 4.0f;
        }

        @Override
        public float getPhysicsAttackParaA() {
            return 0.5f;
        }

        @Override
        public float getPhysicsAttackParaB() {
            return 0.3f;
        }

        @Override
        public float getPhysicsAttackParaC() {
            return 0.0f;
        }

        @Override
        public int getPhysicsDefenceAgilityPara() {
            return 3;
        }

        @Override
        public int getPhysicsDefenceSpiritPara() {
            return 0;
        }

        @Override
        public boolean baseIs(EVocation eVocation) {
            return false;
        }
    },
    CHI_HOU(2, "斥候") {

        @Override
        public EVocation getBasicVoction() {
            return null;
        }

        @Override
        public EVocationType getType() {
            return EVocationType.RANGER;
        }

        @Override
        public EVocation[] getSubVoction(EClan eClan) {
            EVocation[] eVocationArray = null;
            eVocationArray = eClan == EClan.LONG_SHAN ? new EVocation[]{LI_JIAN_CHI_HOU, SHEN_JIAN_CHI_HOU} : new EVocation[]{XIE_REN_CHI_HOU, GUI_YI_CHI_HOU};
            return eVocationArray;
        }

        @Override
        public int wearableArmor() {
            return 0;
        }

        @Override
        public float getStrengthCalcPara() {
            return 5.0f;
        }

        @Override
        public float getAgilityCalcPara() {
            return 4.0f;
        }

        @Override
        public float getStaminaCalPara() {
            return 4.0f;
        }

        @Override
        public float getInteCalcPara() {
            return 3.0f;
        }

        @Override
        public float getSpiritCalcPara() {
            return 3.0f;
        }

        @Override
        public float getLuckyCalcPara() {
            return 5.0f;
        }

        @Override
        public float getPhysicsAttackParaA() {
            return 0.3f;
        }

        @Override
        public float getPhysicsAttackParaB() {
            return 0.5f;
        }

        @Override
        public float getPhysicsAttackParaC() {
            return 0.0f;
        }

        @Override
        public int getPhysicsDefenceAgilityPara() {
            return 1;
        }

        @Override
        public int getPhysicsDefenceSpiritPara() {
            return 0;
        }

        @Override
        public boolean baseIs(EVocation eVocation) {
            return false;
        }
    },
    FA_SHI(3, "法师") {

        @Override
        public EVocation getBasicVoction() {
            return null;
        }

        @Override
        public EVocation[] getSubVoction(EClan eClan) {
            EVocation[] eVocationArray = null;
            eVocationArray = eClan == EClan.LONG_SHAN ? new EVocation[]{YU_HUO_FA_SHI, TIAN_JI_FA_SHI} : new EVocation[]{YAN_MO_FA_SHI, XUAN_MING_FA_SHI};
            return eVocationArray;
        }

        @Override
        public EVocationType getType() {
            return EVocationType.MAGIC;
        }

        @Override
        public int wearableArmor() {
            return 0;
        }

        @Override
        public float getStrengthCalcPara() {
            return 3.0f;
        }

        @Override
        public float getAgilityCalcPara() {
            return 3.0f;
        }

        @Override
        public float getStaminaCalPara() {
            return 4.0f;
        }

        @Override
        public float getInteCalcPara() {
            return 5.5f;
        }

        @Override
        public float getSpiritCalcPara() {
            return 4.5f;
        }

        @Override
        public float getLuckyCalcPara() {
            return 5.0f;
        }

        @Override
        public float getPhysicsAttackParaA() {
            return 0.1f;
        }

        @Override
        public float getPhysicsAttackParaB() {
            return 0.1f;
        }

        @Override
        public float getPhysicsAttackParaC() {
            return 0.0f;
        }

        @Override
        public int getPhysicsDefenceAgilityPara() {
            return 3;
        }

        @Override
        public int getPhysicsDefenceSpiritPara() {
            return 1;
        }

        @Override
        public boolean baseIs(EVocation eVocation) {
            return false;
        }
    },
    WU_YI(4, "巫医") {

        @Override
        public EVocation getBasicVoction() {
            return null;
        }

        @Override
        public EVocation[] getSubVoction(EClan eClan) {
            EVocation[] eVocationArray = null;
            eVocationArray = eClan == EClan.LONG_SHAN ? new EVocation[]{MIAO_SHOU_WU_YI, LING_QUAN_WU_YI} : new EVocation[]{XIE_JI_WU_YI, YIN_YANG_WU_YI};
            return eVocationArray;
        }

        @Override
        public EVocationType getType() {
            return EVocationType.PRIEST;
        }

        @Override
        public int wearableArmor() {
            return 0;
        }

        @Override
        public float getStrengthCalcPara() {
            return 3.0f;
        }

        @Override
        public float getAgilityCalcPara() {
            return 3.0f;
        }

        @Override
        public float getStaminaCalPara() {
            return 4.0f;
        }

        @Override
        public float getInteCalcPara() {
            return 4.5f;
        }

        @Override
        public float getSpiritCalcPara() {
            return 5.5f;
        }

        @Override
        public float getLuckyCalcPara() {
            return 5.0f;
        }

        @Override
        public float getPhysicsAttackParaA() {
            return 0.1f;
        }

        @Override
        public float getPhysicsAttackParaB() {
            return 0.1f;
        }

        @Override
        public float getPhysicsAttackParaC() {
            return 0.0f;
        }

        @Override
        public int getPhysicsDefenceAgilityPara() {
            return 3;
        }

        @Override
        public int getPhysicsDefenceSpiritPara() {
            return 1;
        }

        @Override
        public boolean baseIs(EVocation eVocation) {
            return false;
        }
    },
    JIN_GANG_LI_SHI(5, "金刚力士") {

        @Override
        public EVocation getBasicVoction() {
            return LI_SHI;
        }

        @Override
        public EVocation[] getSubVoction(EClan eClan) {
            return null;
        }

        @Override
        public EVocationType getType() {
            return EVocationType.PHYSICS;
        }

        @Override
        public int wearableArmor() {
            return 0;
        }

        @Override
        public float getStrengthCalcPara() {
            return 6.0f;
        }

        @Override
        public float getAgilityCalcPara() {
            return 6.0f;
        }

        @Override
        public float getStaminaCalPara() {
            return 7.0f;
        }

        @Override
        public float getInteCalcPara() {
            return 3.0f;
        }

        @Override
        public float getSpiritCalcPara() {
            return 3.0f;
        }

        @Override
        public float getLuckyCalcPara() {
            return 4.0f;
        }

        @Override
        public float getPhysicsAttackParaA() {
            return 0.5f;
        }

        @Override
        public float getPhysicsAttackParaB() {
            return 0.3f;
        }

        @Override
        public float getPhysicsAttackParaC() {
            return 0.0f;
        }

        @Override
        public int getPhysicsDefenceAgilityPara() {
            return 3;
        }

        @Override
        public int getPhysicsDefenceSpiritPara() {
            return 0;
        }

        @Override
        public boolean baseIs(EVocation eVocation) {
            return eVocation == LI_SHI;
        }
    },
    QING_TIAN_LI_SHI(6, "擎天力士") {

        @Override
        public EVocation getBasicVoction() {
            return LI_SHI;
        }

        @Override
        public EVocation[] getSubVoction(EClan eClan) {
            return null;
        }

        @Override
        public EVocationType getType() {
            return EVocationType.PHYSICS;
        }

        @Override
        public int wearableArmor() {
            return 0;
        }

        @Override
        public float getStrengthCalcPara() {
            return 7.0f;
        }

        @Override
        public float getAgilityCalcPara() {
            return 6.0f;
        }

        @Override
        public float getStaminaCalPara() {
            return 6.0f;
        }

        @Override
        public float getInteCalcPara() {
            return 3.0f;
        }

        @Override
        public float getSpiritCalcPara() {
            return 3.0f;
        }

        @Override
        public float getLuckyCalcPara() {
            return 4.0f;
        }

        @Override
        public float getPhysicsAttackParaA() {
            return 0.5f;
        }

        @Override
        public float getPhysicsAttackParaB() {
            return 0.3f;
        }

        @Override
        public float getPhysicsAttackParaC() {
            return 0.0f;
        }

        @Override
        public int getPhysicsDefenceAgilityPara() {
            return 3;
        }

        @Override
        public int getPhysicsDefenceSpiritPara() {
            return 0;
        }

        @Override
        public boolean baseIs(EVocation eVocation) {
            return eVocation == LI_SHI;
        }
    },
    LUO_CHA_LI_SHI(7, "罗刹力士") {

        @Override
        public EVocation getBasicVoction() {
            return LI_SHI;
        }

        @Override
        public EVocation[] getSubVoction(EClan eClan) {
            return null;
        }

        @Override
        public EVocationType getType() {
            return EVocationType.PHYSICS;
        }

        @Override
        public int wearableArmor() {
            return 0;
        }

        @Override
        public float getStrengthCalcPara() {
            return 6.0f;
        }

        @Override
        public float getAgilityCalcPara() {
            return 6.0f;
        }

        @Override
        public float getStaminaCalPara() {
            return 7.0f;
        }

        @Override
        public float getInteCalcPara() {
            return 3.0f;
        }

        @Override
        public float getSpiritCalcPara() {
            return 3.0f;
        }

        @Override
        public float getLuckyCalcPara() {
            return 4.0f;
        }

        @Override
        public float getPhysicsAttackParaA() {
            return 0.5f;
        }

        @Override
        public float getPhysicsAttackParaB() {
            return 0.3f;
        }

        @Override
        public float getPhysicsAttackParaC() {
            return 0.0f;
        }

        @Override
        public int getPhysicsDefenceAgilityPara() {
            return 3;
        }

        @Override
        public int getPhysicsDefenceSpiritPara() {
            return 0;
        }

        @Override
        public boolean baseIs(EVocation eVocation) {
            return eVocation == LI_SHI;
        }
    },
    XIU_LUO_LI_SHI(8, "修罗力士") {

        @Override
        public EVocation getBasicVoction() {
            return LI_SHI;
        }

        @Override
        public EVocation[] getSubVoction(EClan eClan) {
            return null;
        }

        @Override
        public EVocationType getType() {
            return EVocationType.PHYSICS;
        }

        @Override
        public int wearableArmor() {
            return 0;
        }

        @Override
        public float getStrengthCalcPara() {
            return 7.0f;
        }

        @Override
        public float getAgilityCalcPara() {
            return 6.0f;
        }

        @Override
        public float getStaminaCalPara() {
            return 6.0f;
        }

        @Override
        public float getInteCalcPara() {
            return 3.0f;
        }

        @Override
        public float getSpiritCalcPara() {
            return 3.0f;
        }

        @Override
        public float getLuckyCalcPara() {
            return 4.0f;
        }

        @Override
        public float getPhysicsAttackParaA() {
            return 0.5f;
        }

        @Override
        public float getPhysicsAttackParaB() {
            return 0.3f;
        }

        @Override
        public float getPhysicsAttackParaC() {
            return 0.0f;
        }

        @Override
        public int getPhysicsDefenceAgilityPara() {
            return 3;
        }

        @Override
        public int getPhysicsDefenceSpiritPara() {
            return 0;
        }

        @Override
        public boolean baseIs(EVocation eVocation) {
            return eVocation == LI_SHI;
        }
    },
    LI_JIAN_CHI_HOU(9, "砺剑斥候") {

        @Override
        public EVocation getBasicVoction() {
            return CHI_HOU;
        }

        @Override
        public EVocation[] getSubVoction(EClan eClan) {
            return null;
        }

        @Override
        public EVocationType getType() {
            return EVocationType.RANGER;
        }

        @Override
        public int wearableArmor() {
            return 0;
        }

        @Override
        public float getStrengthCalcPara() {
            return 5.5f;
        }

        @Override
        public float getAgilityCalcPara() {
            return 7.5f;
        }

        @Override
        public float getStaminaCalPara() {
            return 5.0f;
        }

        @Override
        public float getInteCalcPara() {
            return 3.0f;
        }

        @Override
        public float getSpiritCalcPara() {
            return 3.0f;
        }

        @Override
        public float getLuckyCalcPara() {
            return 5.0f;
        }

        @Override
        public float getPhysicsAttackParaA() {
            return 0.3f;
        }

        @Override
        public float getPhysicsAttackParaB() {
            return 0.5f;
        }

        @Override
        public float getPhysicsAttackParaC() {
            return 0.0f;
        }

        @Override
        public int getPhysicsDefenceAgilityPara() {
            return 3;
        }

        @Override
        public int getPhysicsDefenceSpiritPara() {
            return 0;
        }

        @Override
        public boolean baseIs(EVocation eVocation) {
            return eVocation == CHI_HOU;
        }
    },
    SHEN_JIAN_CHI_HOU(10, "神箭斥候") {

        @Override
        public EVocation getBasicVoction() {
            return CHI_HOU;
        }

        @Override
        public EVocation[] getSubVoction(EClan eClan) {
            return null;
        }

        @Override
        public EVocationType getType() {
            return EVocationType.RANGER;
        }

        @Override
        public int wearableArmor() {
            return 0;
        }

        @Override
        public float getStrengthCalcPara() {
            return 5.5f;
        }

        @Override
        public float getAgilityCalcPara() {
            return 7.0f;
        }

        @Override
        public float getStaminaCalPara() {
            return 5.5f;
        }

        @Override
        public float getInteCalcPara() {
            return 3.0f;
        }

        @Override
        public float getSpiritCalcPara() {
            return 3.0f;
        }

        @Override
        public float getLuckyCalcPara() {
            return 5.0f;
        }

        @Override
        public float getPhysicsAttackParaA() {
            return 0.3f;
        }

        @Override
        public float getPhysicsAttackParaB() {
            return 0.5f;
        }

        @Override
        public float getPhysicsAttackParaC() {
            return 0.0f;
        }

        @Override
        public int getPhysicsDefenceAgilityPara() {
            return 1;
        }

        @Override
        public int getPhysicsDefenceSpiritPara() {
            return 0;
        }

        @Override
        public boolean baseIs(EVocation eVocation) {
            return eVocation == CHI_HOU;
        }
    },
    XIE_REN_CHI_HOU(11, "血刃斥候") {

        @Override
        public EVocation getBasicVoction() {
            return CHI_HOU;
        }

        @Override
        public EVocation[] getSubVoction(EClan eClan) {
            return null;
        }

        @Override
        public EVocationType getType() {
            return EVocationType.RANGER;
        }

        @Override
        public int wearableArmor() {
            return 0;
        }

        @Override
        public float getStrengthCalcPara() {
            return 5.5f;
        }

        @Override
        public float getAgilityCalcPara() {
            return 7.5f;
        }

        @Override
        public float getStaminaCalPara() {
            return 5.0f;
        }

        @Override
        public float getInteCalcPara() {
            return 3.0f;
        }

        @Override
        public float getSpiritCalcPara() {
            return 3.0f;
        }

        @Override
        public float getLuckyCalcPara() {
            return 5.0f;
        }

        @Override
        public float getPhysicsAttackParaA() {
            return 0.3f;
        }

        @Override
        public float getPhysicsAttackParaB() {
            return 0.5f;
        }

        @Override
        public float getPhysicsAttackParaC() {
            return 0.0f;
        }

        @Override
        public int getPhysicsDefenceAgilityPara() {
            return 1;
        }

        @Override
        public int getPhysicsDefenceSpiritPara() {
            return 0;
        }

        @Override
        public boolean baseIs(EVocation eVocation) {
            return eVocation == CHI_HOU;
        }
    },
    GUI_YI_CHI_HOU(12, "鬼羿斥候") {

        @Override
        public EVocation getBasicVoction() {
            return CHI_HOU;
        }

        @Override
        public EVocation[] getSubVoction(EClan eClan) {
            return null;
        }

        @Override
        public EVocationType getType() {
            return EVocationType.RANGER;
        }

        @Override
        public int wearableArmor() {
            return 0;
        }

        @Override
        public float getStrengthCalcPara() {
            return 5.5f;
        }

        @Override
        public float getAgilityCalcPara() {
            return 7.0f;
        }

        @Override
        public float getStaminaCalPara() {
            return 5.5f;
        }

        @Override
        public float getInteCalcPara() {
            return 3.0f;
        }

        @Override
        public float getSpiritCalcPara() {
            return 3.0f;
        }

        @Override
        public float getLuckyCalcPara() {
            return 5.0f;
        }

        @Override
        public float getPhysicsAttackParaA() {
            return 0.3f;
        }

        @Override
        public float getPhysicsAttackParaB() {
            return 0.5f;
        }

        @Override
        public float getPhysicsAttackParaC() {
            return 0.0f;
        }

        @Override
        public int getPhysicsDefenceAgilityPara() {
            return 1;
        }

        @Override
        public int getPhysicsDefenceSpiritPara() {
            return 0;
        }

        @Override
        public boolean baseIs(EVocation eVocation) {
            return eVocation == CHI_HOU;
        }
    },
    YU_HUO_FA_SHI(13, "浴火法师") {

        @Override
        public EVocation getBasicVoction() {
            return FA_SHI;
        }

        @Override
        public EVocation[] getSubVoction(EClan eClan) {
            return null;
        }

        @Override
        public EVocationType getType() {
            return EVocationType.MAGIC;
        }

        @Override
        public int wearableArmor() {
            return 0;
        }

        @Override
        public float getStrengthCalcPara() {
            return 3.0f;
        }

        @Override
        public float getAgilityCalcPara() {
            return 3.0f;
        }

        @Override
        public float getStaminaCalPara() {
            return 4.5f;
        }

        @Override
        public float getInteCalcPara() {
            return 7.0f;
        }

        @Override
        public float getSpiritCalcPara() {
            return 6.5f;
        }

        @Override
        public float getLuckyCalcPara() {
            return 5.0f;
        }

        @Override
        public float getPhysicsAttackParaA() {
            return 0.1f;
        }

        @Override
        public float getPhysicsAttackParaB() {
            return 0.1f;
        }

        @Override
        public float getPhysicsAttackParaC() {
            return 0.0f;
        }

        @Override
        public int getPhysicsDefenceAgilityPara() {
            return 3;
        }

        @Override
        public int getPhysicsDefenceSpiritPara() {
            return 1;
        }

        @Override
        public boolean baseIs(EVocation eVocation) {
            return eVocation == FA_SHI;
        }
    },
    TIAN_JI_FA_SHI(14, "天机法师") {

        @Override
        public EVocation getBasicVoction() {
            return FA_SHI;
        }

        @Override
        public EVocation[] getSubVoction(EClan eClan) {
            return null;
        }

        @Override
        public EVocationType getType() {
            return EVocationType.MAGIC;
        }

        @Override
        public int wearableArmor() {
            return 0;
        }

        @Override
        public float getStrengthCalcPara() {
            return 3.0f;
        }

        @Override
        public float getAgilityCalcPara() {
            return 3.0f;
        }

        @Override
        public float getStaminaCalPara() {
            return 5.5f;
        }

        @Override
        public float getInteCalcPara() {
            return 6.5f;
        }

        @Override
        public float getSpiritCalcPara() {
            return 6.0f;
        }

        @Override
        public float getLuckyCalcPara() {
            return 5.0f;
        }

        @Override
        public float getPhysicsAttackParaA() {
            return 0.1f;
        }

        @Override
        public float getPhysicsAttackParaB() {
            return 0.1f;
        }

        @Override
        public float getPhysicsAttackParaC() {
            return 0.0f;
        }

        @Override
        public int getPhysicsDefenceAgilityPara() {
            return 3;
        }

        @Override
        public int getPhysicsDefenceSpiritPara() {
            return 1;
        }

        @Override
        public boolean baseIs(EVocation eVocation) {
            return eVocation == FA_SHI;
        }
    },
    YAN_MO_FA_SHI(15, "炎魔法师") {

        @Override
        public EVocation getBasicVoction() {
            return FA_SHI;
        }

        @Override
        public EVocation[] getSubVoction(EClan eClan) {
            return null;
        }

        @Override
        public EVocationType getType() {
            return EVocationType.MAGIC;
        }

        @Override
        public int wearableArmor() {
            return 0;
        }

        @Override
        public float getStrengthCalcPara() {
            return 3.0f;
        }

        @Override
        public float getAgilityCalcPara() {
            return 3.0f;
        }

        @Override
        public float getStaminaCalPara() {
            return 4.5f;
        }

        @Override
        public float getInteCalcPara() {
            return 7.0f;
        }

        @Override
        public float getSpiritCalcPara() {
            return 6.5f;
        }

        @Override
        public float getLuckyCalcPara() {
            return 5.0f;
        }

        @Override
        public float getPhysicsAttackParaA() {
            return 0.1f;
        }

        @Override
        public float getPhysicsAttackParaB() {
            return 0.1f;
        }

        @Override
        public float getPhysicsAttackParaC() {
            return 0.0f;
        }

        @Override
        public int getPhysicsDefenceAgilityPara() {
            return 3;
        }

        @Override
        public int getPhysicsDefenceSpiritPara() {
            return 1;
        }

        @Override
        public boolean baseIs(EVocation eVocation) {
            return eVocation == FA_SHI;
        }
    },
    XUAN_MING_FA_SHI(16, "玄冥法师") {

        @Override
        public EVocation getBasicVoction() {
            return FA_SHI;
        }

        @Override
        public EVocation[] getSubVoction(EClan eClan) {
            return null;
        }

        @Override
        public EVocationType getType() {
            return EVocationType.MAGIC;
        }

        @Override
        public int wearableArmor() {
            return 0;
        }

        @Override
        public float getStrengthCalcPara() {
            return 3.0f;
        }

        @Override
        public float getAgilityCalcPara() {
            return 3.0f;
        }

        @Override
        public float getStaminaCalPara() {
            return 5.5f;
        }

        @Override
        public float getInteCalcPara() {
            return 6.5f;
        }

        @Override
        public float getSpiritCalcPara() {
            return 6.0f;
        }

        @Override
        public float getLuckyCalcPara() {
            return 5.0f;
        }

        @Override
        public float getPhysicsAttackParaA() {
            return 0.1f;
        }

        @Override
        public float getPhysicsAttackParaB() {
            return 0.1f;
        }

        @Override
        public float getPhysicsAttackParaC() {
            return 0.0f;
        }

        @Override
        public int getPhysicsDefenceAgilityPara() {
            return 3;
        }

        @Override
        public int getPhysicsDefenceSpiritPara() {
            return 1;
        }

        @Override
        public boolean baseIs(EVocation eVocation) {
            return eVocation == FA_SHI;
        }
    },
    MIAO_SHOU_WU_YI(17, "妙手巫医") {

        @Override
        public EVocation getBasicVoction() {
            return WU_YI;
        }

        @Override
        public EVocation[] getSubVoction(EClan eClan) {
            return null;
        }

        @Override
        public EVocationType getType() {
            return EVocationType.PRIEST;
        }

        @Override
        public int wearableArmor() {
            return 0;
        }

        @Override
        public float getStrengthCalcPara() {
            return 3.0f;
        }

        @Override
        public float getAgilityCalcPara() {
            return 3.0f;
        }

        @Override
        public float getStaminaCalPara() {
            return 4.7f;
        }

        @Override
        public float getInteCalcPara() {
            return 6.3f;
        }

        @Override
        public float getSpiritCalcPara() {
            return 7.0f;
        }

        @Override
        public float getLuckyCalcPara() {
            return 5.0f;
        }

        @Override
        public float getPhysicsAttackParaA() {
            return 0.1f;
        }

        @Override
        public float getPhysicsAttackParaB() {
            return 0.1f;
        }

        @Override
        public float getPhysicsAttackParaC() {
            return 0.0f;
        }

        @Override
        public int getPhysicsDefenceAgilityPara() {
            return 3;
        }

        @Override
        public int getPhysicsDefenceSpiritPara() {
            return 1;
        }

        @Override
        public boolean baseIs(EVocation eVocation) {
            return eVocation == WU_YI;
        }
    },
    LING_QUAN_WU_YI(18, "灵泉巫医") {

        @Override
        public EVocation getBasicVoction() {
            return WU_YI;
        }

        @Override
        public EVocation[] getSubVoction(EClan eClan) {
            return null;
        }

        @Override
        public EVocationType getType() {
            return EVocationType.PRIEST;
        }

        @Override
        public int wearableArmor() {
            return 0;
        }

        @Override
        public float getStrengthCalcPara() {
            return 3.0f;
        }

        @Override
        public float getAgilityCalcPara() {
            return 3.0f;
        }

        @Override
        public float getStaminaCalPara() {
            return 4.0f;
        }

        @Override
        public float getInteCalcPara() {
            return 6.5f;
        }

        @Override
        public float getSpiritCalcPara() {
            return 6.5f;
        }

        @Override
        public float getLuckyCalcPara() {
            return 6.0f;
        }

        @Override
        public float getPhysicsAttackParaA() {
            return 0.1f;
        }

        @Override
        public float getPhysicsAttackParaB() {
            return 0.1f;
        }

        @Override
        public float getPhysicsAttackParaC() {
            return 0.0f;
        }

        @Override
        public int getPhysicsDefenceAgilityPara() {
            return 3;
        }

        @Override
        public int getPhysicsDefenceSpiritPara() {
            return 1;
        }

        @Override
        public boolean baseIs(EVocation eVocation) {
            return eVocation == WU_YI;
        }
    },
    XIE_JI_WU_YI(19, "血祭巫医") {

        @Override
        public EVocation getBasicVoction() {
            return WU_YI;
        }

        @Override
        public EVocation[] getSubVoction(EClan eClan) {
            return null;
        }

        @Override
        public EVocationType getType() {
            return EVocationType.PRIEST;
        }

        @Override
        public int wearableArmor() {
            return 0;
        }

        @Override
        public float getStrengthCalcPara() {
            return 3.0f;
        }

        @Override
        public float getAgilityCalcPara() {
            return 7.0f;
        }

        @Override
        public float getStaminaCalPara() {
            return 6.0f;
        }

        @Override
        public float getInteCalcPara() {
            return 6.0f;
        }

        @Override
        public float getSpiritCalcPara() {
            return 6.0f;
        }

        @Override
        public float getLuckyCalcPara() {
            return 7.0f;
        }

        @Override
        public float getPhysicsAttackParaA() {
            return 0.1f;
        }

        @Override
        public float getPhysicsAttackParaB() {
            return 0.1f;
        }

        @Override
        public float getPhysicsAttackParaC() {
            return 0.0f;
        }

        @Override
        public int getPhysicsDefenceAgilityPara() {
            return 3;
        }

        @Override
        public int getPhysicsDefenceSpiritPara() {
            return 1;
        }

        @Override
        public boolean baseIs(EVocation eVocation) {
            return eVocation == WU_YI;
        }
    },
    YIN_YANG_WU_YI(20, "阴阳巫医") {

        @Override
        public EVocation getBasicVoction() {
            return WU_YI;
        }

        @Override
        public EVocation[] getSubVoction(EClan eClan) {
            return null;
        }

        @Override
        public EVocationType getType() {
            return EVocationType.PRIEST;
        }

        @Override
        public int wearableArmor() {
            return 0;
        }

        @Override
        public float getStrengthCalcPara() {
            return 3.0f;
        }

        @Override
        public float getAgilityCalcPara() {
            return 7.0f;
        }

        @Override
        public float getStaminaCalPara() {
            return 6.0f;
        }

        @Override
        public float getInteCalcPara() {
            return 6.0f;
        }

        @Override
        public float getSpiritCalcPara() {
            return 6.0f;
        }

        @Override
        public float getLuckyCalcPara() {
            return 7.0f;
        }

        @Override
        public float getPhysicsAttackParaA() {
            return 0.1f;
        }

        @Override
        public float getPhysicsAttackParaB() {
            return 0.1f;
        }

        @Override
        public float getPhysicsAttackParaC() {
            return 0.0f;
        }

        @Override
        public int getPhysicsDefenceAgilityPara() {
            return 3;
        }

        @Override
        public int getPhysicsDefenceSpiritPara() {
            return 1;
        }

        @Override
        public boolean baseIs(EVocation eVocation) {
            return eVocation == WU_YI;
        }
    };

    private byte value;
    private String desc;

    private EVocation(int n2, String string2) {
        this.value = (byte) n2;
        this.desc = string2;
    }

    public byte value() {
        return this.value;
    }

    public String getDesc() {
        return this.desc;
    }

    public static EVocation getVocationByDesc(String string) {
        EVocation[] eVocationArray = EVocation.values();
        int n = eVocationArray.length;
        int n2 = 0;
        while (n2 < n) {
            EVocation eVocation = eVocationArray[n2];
            if (eVocation.getDesc().equals(string)) {
                return eVocation;
            }
            ++n2;
        }
        return null;
    }

    public static String[] getVocationDescList() {
        int n = EVocation.values().length;
        String[] stringArray = new String[n];
        int n2 = 0;
        while (n2 < n) {
            stringArray[n2] = EVocation.values()[n2].getDesc();
            ++n2;
        }
        return stringArray;
    }

    public static EVocation getVocationByID(int n) {
        EVocation[] eVocationArray = EVocation.values();
        int n2 = eVocationArray.length;
        int n3 = 0;
        while (n3 < n2) {
            EVocation eVocation = eVocationArray[n3];
            if (eVocation.value() == n) {
                return eVocation;
            }
            ++n3;
        }
        return null;
    }

    public abstract EVocation getBasicVoction();

    public abstract EVocation[] getSubVoction(EClan var1);

    public abstract EVocationType getType();

    public abstract float getStrengthCalcPara();

    public abstract float getAgilityCalcPara();

    public abstract float getStaminaCalPara();

    public abstract float getInteCalcPara();

    public abstract float getSpiritCalcPara();

    public abstract float getLuckyCalcPara();

    public abstract int wearableArmor();

    public abstract float getPhysicsAttackParaA();

    public abstract float getPhysicsAttackParaB();

    public abstract float getPhysicsAttackParaC();

    public abstract int getPhysicsDefenceAgilityPara();

    public abstract int getPhysicsDefenceSpiritPara();

    public abstract boolean baseIs(EVocation var1);

    EVocation(String string, int n, int n2, String string2, EVocation eVocation) {
        this(n2, string2);
    }
}
