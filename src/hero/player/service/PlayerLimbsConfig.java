// 
// Decompiled by Procyon v0.5.36
// 
package hero.player.service;

import hero.player.define.EClan;
import hero.player.define.ESex;
import org.dom4j.Element;

public class PlayerLimbsConfig {

    public short init_hair_long_male_icon_id;
    public short init_hair_long_male_image_id;
    public short init_hair_long_male_animation;
    public short init_hair_long_female_icon_id;
    public short init_hair_long_female_image_id;
    public short init_hair_long_female_animation_id;
    public short init_hair_mo_male_icon_id;
    public short init_hair_mo_male_image_id;
    public short init_hair_mo_male_animation;
    public short init_hair_mo_female_icon_id;
    public short init_hair_mo_female_image_id;
    public short init_hair_mo_female_animation_id;
    public short init_head_male_image_id;
    public short init_head_male_animation_id;
    public short init_head_female_image_id;
    public short init_head_female_animation_id;
    public short init_leg_male_image_id;
    public short init_leg_male_animation_id;
    public short init_leg_female_image_id;
    public short init_leg_female_animation_id;
    public short init_tail_male_image_id;
    public short init_tail_male_animation_id;
    public short init_tail_female_image_id;
    public short init_tail_female_animation_id;
    public short init_long_die_image_id;
    public short init_long_die_animation_id;
    public short init_mo_die_image_id;
    public short init_mo_die_animation_id;

    public PlayerLimbsConfig(final Element _paraElement) {
        this.init_hair_long_male_image_id = Short.parseShort(_paraElement.elementTextTrim("init_hair_long_male_image_id"));
        this.init_hair_long_male_animation = Short.parseShort(_paraElement.elementTextTrim("init_hair_long_male_animation"));
        this.init_hair_long_female_image_id = Short.parseShort(_paraElement.elementTextTrim("init_hair_long_female_image_id"));
        this.init_hair_long_female_animation_id = Short.parseShort(_paraElement.elementTextTrim("init_hair_long_female_animation_id"));
        this.init_hair_mo_male_image_id = Short.parseShort(_paraElement.elementTextTrim("init_hair_mo_male_image_id"));
        this.init_hair_mo_male_animation = Short.parseShort(_paraElement.elementTextTrim("init_hair_mo_male_animation"));
        this.init_hair_mo_female_image_id = Short.parseShort(_paraElement.elementTextTrim("init_hair_mo_female_image_id"));
        this.init_hair_mo_female_animation_id = Short.parseShort(_paraElement.elementTextTrim("init_hair_mo_female_animation_id"));
        this.init_head_male_image_id = Short.parseShort(_paraElement.elementTextTrim("init_head_male_image_id"));
        this.init_head_male_animation_id = Short.parseShort(_paraElement.elementTextTrim("init_head_male_animation_id"));
        this.init_head_female_image_id = Short.parseShort(_paraElement.elementTextTrim("init_head_female_image_id"));
        this.init_head_female_animation_id = Short.parseShort(_paraElement.elementTextTrim("init_head_female_animation_id"));
        this.init_leg_male_image_id = Short.parseShort(_paraElement.elementTextTrim("init_leg_male_image_id"));
        this.init_leg_male_animation_id = Short.parseShort(_paraElement.elementTextTrim("init_leg_male_animation_id"));
        this.init_leg_female_image_id = Short.parseShort(_paraElement.elementTextTrim("init_leg_female_image_id"));
        this.init_leg_female_animation_id = Short.parseShort(_paraElement.elementTextTrim("init_leg_female_animation_id"));
        this.init_tail_male_image_id = Short.parseShort(_paraElement.elementTextTrim("init_tail_male_image_id"));
        this.init_tail_male_animation_id = Short.parseShort(_paraElement.elementTextTrim("init_tail_male_animation_id"));
        this.init_tail_female_image_id = Short.parseShort(_paraElement.elementTextTrim("init_tail_female_image_id"));
        this.init_tail_female_animation_id = Short.parseShort(_paraElement.elementTextTrim("init_tail_female_animation_id"));
        this.init_long_die_image_id = Short.parseShort(_paraElement.elementTextTrim("init_long_die_image_id"));
        this.init_long_die_animation_id = Short.parseShort(_paraElement.elementTextTrim("init_long_die_animation_id"));
        this.init_mo_die_image_id = Short.parseShort(_paraElement.elementTextTrim("init_mo_die_image_id"));
        this.init_mo_die_animation_id = Short.parseShort(_paraElement.elementTextTrim("init_mo_die_animation_id"));
        this.init_hair_long_male_icon_id = Short.parseShort(_paraElement.elementTextTrim("init_hair_long_male_icon_id"));
        this.init_hair_long_female_icon_id = Short.parseShort(_paraElement.elementTextTrim("init_hair_long_female_icon_id"));
        this.init_hair_mo_male_icon_id = Short.parseShort(_paraElement.elementTextTrim("init_hair_mo_male_icon_id"));
        this.init_hair_mo_female_icon_id = Short.parseShort(_paraElement.elementTextTrim("init_hair_mo_female_icon_id"));
    }

    public short getHairIcon(final ESex _sex, final EClan _clan) {
        short hair = 0;
        if (_clan == EClan.LONG_SHAN) {
            if (_sex == ESex.Male) {
                hair = this.init_hair_long_male_icon_id;
            } else {
                hair = this.init_hair_long_female_icon_id;
            }
        } else if (_sex == ESex.Male) {
            hair = this.init_hair_mo_male_icon_id;
        } else {
            hair = this.init_hair_mo_female_icon_id;
        }
        return hair;
    }

    public short getHairImage(final ESex _sex, final EClan _clan) {
        short hair = 0;
        if (_clan == EClan.LONG_SHAN) {
            if (_sex == ESex.Male) {
                hair = this.init_hair_long_male_image_id;
            } else {
                hair = this.init_hair_long_female_image_id;
            }
        } else if (_sex == ESex.Male) {
            hair = this.init_hair_mo_male_image_id;
        } else {
            hair = this.init_hair_mo_female_image_id;
        }
        return hair;
    }

    public short getHairAnimation(final ESex _sex, final EClan _clan) {
        short hair = 0;
        if (_clan == EClan.LONG_SHAN) {
            if (_sex == ESex.Male) {
                hair = this.init_hair_long_male_animation;
            } else {
                hair = this.init_hair_long_female_animation_id;
            }
        } else if (_sex == ESex.Male) {
            hair = this.init_hair_mo_male_animation;
        } else {
            hair = this.init_hair_mo_female_animation_id;
        }
        return hair;
    }

    public short getHeadImage(final ESex _sex) {
        short head = 0;
        if (_sex == ESex.Male) {
            head = this.init_head_male_image_id;
        } else {
            head = this.init_head_female_image_id;
        }
        return head;
    }

    public short getHeadAnimation(final ESex _sex) {
        short head = 0;
        if (_sex == ESex.Male) {
            head = this.init_head_male_animation_id;
        } else {
            head = this.init_head_female_animation_id;
        }
        return head;
    }

    public short getLegImage(final ESex _sex) {
        short leg = 0;
        if (_sex == ESex.Male) {
            leg = this.init_leg_male_image_id;
        } else {
            leg = this.init_leg_female_image_id;
        }
        return leg;
    }

    public short getLegAnimation(final ESex _sex) {
        short leg = 0;
        if (_sex == ESex.Male) {
            leg = this.init_leg_male_animation_id;
        } else {
            leg = this.init_leg_female_animation_id;
        }
        return leg;
    }

    public short getTailImage(final ESex _sex, final EClan clan) {
        short tail = -1;
        if (clan == EClan.HE_MU_DU) {
            if (_sex == ESex.Male) {
                tail = this.init_tail_male_image_id;
            } else {
                tail = this.init_tail_female_image_id;
            }
        }
        return tail;
    }

    public short getTailAnimation(final ESex _sex, final EClan clan) {
        short tail = -1;
        if (clan == EClan.HE_MU_DU) {
            if (_sex == ESex.Male) {
                tail = this.init_tail_male_animation_id;
            } else {
                tail = this.init_tail_female_animation_id;
            }
        }
        return tail;
    }

    public short getDieImage(final EClan _clan) {
        short die = 0;
        if (_clan == EClan.LONG_SHAN) {
            die = this.init_long_die_image_id;
        } else {
            die = this.init_mo_die_image_id;
        }
        return die;
    }

    public short getDieAnimation(final EClan _clan) {
        short die = 0;
        if (_clan == EClan.LONG_SHAN) {
            die = this.init_long_die_animation_id;
        } else {
            die = this.init_mo_die_animation_id;
        }
        return die;
    }
}
