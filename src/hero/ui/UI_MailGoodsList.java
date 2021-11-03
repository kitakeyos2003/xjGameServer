// 
// Decompiled by Procyon v0.5.36
// 
package hero.ui;

import hero.item.Goods;
import java.util.Iterator;
import java.io.IOException;
import hero.share.service.DateFormatter;
import yoyo.tools.YOYOOutputStream;
import hero.npc.function.system.postbox.Mail;
import java.util.List;

public class UI_MailGoodsList {

    private static final short MONEY_ICON = 245;
    private static final short POINT_ICON = 259;

    public static byte[] getBytes(final short _pageNum, final List<Mail> _mailList, final String[] _menuList) {
        YOYOOutputStream output = new YOYOOutputStream();
        try {
            output.writeByte(getType().getID());
            output.writeShort(_pageNum);
            if (_mailList == null) {
                output.writeByte(0);
            } else {
                output.writeByte(_mailList.size());
                for (final Mail _mail : _mailList) {
                    output.writeInt(_mail.getID());
                    output.writeUTF(_mail.getSender());
                    output.writeUTF(_mail.getTitle());
                    output.writeUTF(_mail.getContent());
                    output.writeByte(_mail.getSocial());
                    output.writeUTF(DateFormatter.getStringTime("yyyy-MM-dd HH:mm", _mail.getDate()));
                    if (_mail.getReadFinish()) {
                        output.writeByte(1);
                    } else {
                        output.writeByte(0);
                    }
                    if (_mail.getType() == 4) {
                        output.writeShort(-1);
                        output.writeUTF("\u6587\u672c");
                        output.writeByte(0);
                        output.writeShort(1);
                    } else if (_mail.getType() == 0) {
                        output.writeShort((short) 245);
                        output.writeUTF(String.valueOf(_mail.getMoney()) + "\u91d1");
                        output.writeByte(0);
                        output.writeShort(1);
                    } else {
                        if (_mail.getType() == 4) {
                            continue;
                        }
                        if (_mail.getType() == 1) {
                            output.writeShort((short) 259);
                            output.writeUTF(String.valueOf(_mail.getGamePoint()) + "\u70b9");
                            output.writeByte(0);
                            output.writeShort(1);
                        } else if (_mail.getType() == 2) {
                            output.writeShort(_mail.getSingleGoods().getIconID());
                            output.writeUTF(_mail.getSingleGoods().getName());
                            output.writeByte(_mail.getSingleGoods().getTrait().value());
                            output.writeShort(_mail.getSingleGoodsNumber());
                        } else {
                            Goods _sg = _mail.getEquipment().getArchetype();
                            output.writeShort(_sg.getIconID());
                            output.writeUTF(_sg.getName());
                            output.writeByte(_sg.getTrait().value());
                            output.writeShort(_mail.getSingleGoodsNumber());
                        }
                    }
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
        return EUIType.MAIL_GOODS_LIST;
    }
}
