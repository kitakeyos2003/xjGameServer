// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.service;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import hero.share.ME2GameObject;
import java.util.ArrayList;

public class ME2ObjectList extends ArrayList<ME2GameObject> {

    private static final long serialVersionUID = 1L;
    private HashMap<Integer, ME2GameObject> objectTable;

    public ME2ObjectList() {
        this.objectTable = new HashMap<Integer, ME2GameObject>();
    }

    @Override
    public synchronized boolean add(final ME2GameObject _object) {
        if (_object != null && !this.contains(_object)) {
            super.add(_object);
            this.objectTable.put(_object.getID(), _object);
            return true;
        }
        return false;
    }

    public int resetID(final ME2GameObject _object) {
        int orgID = -1;
        if (_object != null && this.contains(_object)) {
            Iterator<Map.Entry<Integer, ME2GameObject>> itr = this.objectTable.entrySet().iterator();
            while (itr.hasNext()) {
                Map.Entry<Integer, ME2GameObject> entry = itr.next();
                if (entry.getValue() == _object) {
                    orgID = entry.getKey();
                    itr.remove();
                    break;
                }
            }
            this.objectTable.put(_object.getID(), _object);
        }
        return orgID;
    }

    public synchronized boolean remove(final ME2GameObject _role) {
        if (_role != null) {
            this.objectTable.remove(_role.getID());
        }
        return super.remove(_role);
    }

    public synchronized ME2GameObject remove(final String _objectName) {
        if (_objectName != null) {
            for (int i = 0; i < super.size(); ++i) {
                ME2GameObject object = super.get(i);
                if (object.getName().equals(_objectName)) {
                    super.remove(i);
                    this.objectTable.remove(object.getID());
                    return object;
                }
            }
        }
        return null;
    }

    public ME2GameObject getObject(final int _id) {
        return this.objectTable.get(_id);
    }
}
