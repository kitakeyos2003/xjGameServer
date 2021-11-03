// 
// Decompiled by Procyon v0.5.36
// 
package hero.guild.service;

import org.dom4j.Element;
import yoyo.service.base.AbsConfig;

public class GuildConfig extends AbsConfig {

    public byte level_of_creator;
    public int money_of_create;
    public int officer_sum;

    @Override
    public void init(final Element node) throws Exception {
        Element subNode = node.element("para");
        if (subNode != null) {
            this.level_of_creator = Byte.parseByte(subNode.elementTextTrim("level_of_creator"));
            this.money_of_create = Integer.parseInt(subNode.elementTextTrim("money_of_create"));
            this.officer_sum = Integer.parseInt(subNode.elementTextTrim("officer_sum"));
        }
    }
}
