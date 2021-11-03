// 
// Decompiled by Procyon v0.5.36
// 
package hero.ui;

import java.io.IOException;
import yoyo.tools.YOYOOutputStream;
import java.util.ArrayList;

public class UI_AnswerQuestion {

    public static EUIType getType() {
        return EUIType.ANSWER_QUESTION;
    }

    public static EUIType getEndType() {
        return EUIType.TIP_UI;
    }

    public static byte[] getBytes(final String _question, final ArrayList<String> _answer, final String _title) {
        YOYOOutputStream output = new YOYOOutputStream();
        try {
            output.writeByte(getType().getID());
            output.writeUTF(_title);
            output.writeUTF(_question);
            output.writeByte(_answer.size());
            for (int i = 0; i < _answer.size(); ++i) {
                output.writeUTF(_answer.get(i));
            }
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output.getBytes();
    }

    public static byte[] getEndBytes(final String _endContent, final String _award, final String _title) {
        YOYOOutputStream output = new YOYOOutputStream();
        try {
            output.writeByte(getEndType().getID());
            output.writeUTF(_title);
            output.writeUTF(_endContent);
            output.writeUTF(_award);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output.getBytes();
    }
}
