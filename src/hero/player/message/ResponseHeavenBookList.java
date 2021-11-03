// 
// Decompiled by Procyon v0.5.36
// 
package hero.player.message;

import java.io.IOException;
import java.util.Iterator;
import java.util.ArrayList;
import hero.item.special.HeavenBook;
import java.util.List;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseHeavenBookList extends AbsResponseMessage {

    private List<HeavenBook> heavenBookList;

    public ResponseHeavenBookList(final List<HeavenBook> heavenBookList) {
        this.heavenBookList = new ArrayList<HeavenBook>();
        this.heavenBookList = heavenBookList;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeByte(this.heavenBookList.size());
        for (final HeavenBook book : this.heavenBookList) {
            this.yos.writeInt(book.getID());
            this.yos.writeShort(book.getIconID());
            this.yos.writeShort(book.getSkillPoint());
            this.yos.writeByte(book.getTrait().value());
            this.yos.writeUTF(book.getName());
            this.yos.writeUTF(book.getDescription());
        }
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
