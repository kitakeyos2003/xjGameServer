// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.service;

import java.util.Iterator;
import hero.npc.Npc;
import javolution.util.FastList;

public class NpcObjectModelManager {

    private static FastList<Class<Npc>> modelClassList;

    static {
        NpcObjectModelManager.modelClassList = (FastList<Class<Npc>>) new FastList();
    }

    private NpcObjectModelManager() {
    }

    public static void registeNpcModel(final String _classNameIncludePackageName) {
        try {
            Class<Npc> c = (Class<Npc>) Class.forName(_classNameIncludePackageName);
            NpcObjectModelManager.modelClassList.add(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeNpcModel(final String _className) {
        try {
            for (final Class<Npc> c : NpcObjectModelManager.modelClassList) {
                if (c.getName().equals(_className) || c.getName().equals(_className)) {
                    NpcObjectModelManager.modelClassList.remove(c);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object createObject(final String _className) {
        try {
            for (final Class<Npc> c : NpcObjectModelManager.modelClassList) {
                if (c.getSimpleName().equals(_className) || c.getName().equals(_className)) {
                    return c.newInstance();
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
