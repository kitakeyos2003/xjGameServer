// 
// Decompiled by Procyon v0.5.36
// 
package hero.ui;

import java.io.IOException;
import yoyo.tools.YOYOOutputStream;
import hero.guild.GuildMemberProxy;
import java.util.List;

public class UI_GuildMemberList {

    public static byte[] getBytes(final String[] _menuList, final List<GuildMemberProxy> _list, final int _guildMemberNumber, final int _maxMemberNumber, final int _currentPageNumber, final int _totalPageNumber) {
        YOYOOutputStream output = new YOYOOutputStream();
        try {
            output.writeByte(getType().getID());
            output.writeShort(_guildMemberNumber);
            output.writeShort(_maxMemberNumber);
            output.writeByte(_currentPageNumber);
            output.writeByte(_totalPageNumber);
            output.writeByte(_list.size());
            for (byte i = 0; i < _list.size(); ++i) {
                GuildMemberProxy guildMemberProxy = _list.get(i);
                output.writeInt(guildMemberProxy.userID);
                output.writeUTF(guildMemberProxy.name);
                output.writeByte(guildMemberProxy.memberRank.value());
                output.writeByte(guildMemberProxy.isOnline);
                if (guildMemberProxy.isOnline) {
                    output.writeByte(guildMemberProxy.vocation.value());
                    output.writeShort(guildMemberProxy.level);
                    output.writeByte(guildMemberProxy.sex.value());
                }
            }
            output.writeByte(_menuList.length);
            for (final String menu : _menuList) {
                output.writeUTF(menu);
                output.writeByte(0);
            }
            output.flush();
            return output.getBytes();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                output.close();
            } catch (IOException ex) {
            }
            output = null;
        }
    }

    public static EUIType getType() {
        return EUIType.GUILD_LIST;
    }
}
