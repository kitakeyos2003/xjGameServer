// 
// Decompiled by Procyon v0.5.36
// 
package hero.manufacture.service;

import hero.manufacture.dict.ManufSkill;
import java.util.ArrayList;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.manufacture.ManufactureType;
import hero.player.HeroPlayer;
import java.util.Iterator;
import yoyo.service.base.session.Session;
import hero.manufacture.dict.ManufSkillDict;
import hero.manufacture.Manufacture;
import java.util.List;
import java.util.HashMap;
import yoyo.service.base.AbsServiceAdaptor;

public class ManufactureServerImpl extends AbsServiceAdaptor<ManufactureServerConfig> {

    private static ManufactureServerImpl instance;
    private HashMap<Integer, List<Manufacture>> manufList;
    public static final String[] LEVEL_TITLE;
    public static final int FREIFHT_OF_NEW_SKILL = 400;
    private static final String GET_BASIC = "\u4f60\u83b7\u5f97\u4e86 ";
    private static final String MANUF_SKILL = " \u6280\u80fd";
    private static final String NEED_MANUF_SKILL = "\u9700\u8981 ";
    private static final String STUDYED_MANUF_SKILL = "\u60a8\u5df2\u5b66\u4f1a\u6b64\u6280\u80fd\uff01";

    static {
        LEVEL_TITLE = new String[]{"\u521d\u5b66", "\u7cbe\u901a", "\u4e13\u5bb6"};
    }

    public String getTitle(final int level) {
        if (level <= 10) {
            return ManufactureServerImpl.LEVEL_TITLE[0];
        }
        if (level <= 20) {
            return ManufactureServerImpl.LEVEL_TITLE[1];
        }
        return ManufactureServerImpl.LEVEL_TITLE[2];
    }

    private ManufactureServerImpl() {
        this.config = new ManufactureServerConfig();
        this.manufList = new HashMap<Integer, List<Manufacture>>();
    }

    public static ManufactureServerImpl getInstance() {
        if (ManufactureServerImpl.instance == null) {
            ManufactureServerImpl.instance = new ManufactureServerImpl();
        }
        return ManufactureServerImpl.instance;
    }

    @Override
    protected void start() {
        ManufSkillDict.getInstance().loadManufSkills(((ManufactureServerConfig) this.config).dataPath);
    }

    @Override
    public void createSession(final Session _session) {
        List<Manufacture> _manufList = ManufactureDAO.loadManufByUserID(_session.userID);
        if (_manufList != null) {
            this.manufList.put(_session.userID, _manufList);
        }
    }

    public List<Manufacture> getManufactureListByUserID(final int _userID) {
        return this.manufList.get(_userID);
    }

    public Manufacture getManufactureByUserIDAndNpcName(final int _userID, final String _npcName) {
        for (final Manufacture manuf : this.manufList.get(_userID)) {
            if (manuf.getManufactureType().getName().equals(_npcName)) {
                return manuf;
            }
        }
        return null;
    }

    public Manufacture getManufactureByUserIDAndType(final int _userID, final byte _type) {
        for (final Manufacture manuf : this.manufList.get(_userID)) {
            if (manuf.getManufactureType().getID() == _type) {
                return manuf;
            }
        }
        return null;
    }

    public boolean studyManufacture(final HeroPlayer _player, final ManufactureType _type) {
        if (this.manufList.get(_player.getUserID()) != null && this.manufList.get(_player.getUserID()).size() == 2) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6bcf\u4e2a\u73a9\u5bb6\u6700\u591a\u53ea\u80fd\u5b66\u4e60\u4e24\u79cd\u5236\u9020\u6280\u80fd\uff01"));
            return false;
        }
        this.manufList.get(_player.getUserID()).add(new Manufacture(_type));
        ManufactureDAO.studyManuf(_player.getUserID(), _type);
        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u4f60\u83b7\u5f97\u4e86 " + _type.getName() + " \u6280\u80fd"));
        return true;
    }

    public ArrayList<Integer> getCanUseManufIDs(final int _userID) {
        ArrayList<Integer> manufSkillIDList = new ArrayList<Integer>();
        List<Manufacture> manufactureList = this.manufList.get(_userID);
        for (final Manufacture manuf : manufactureList) {
            for (final Integer id : manuf.getManufIDList()) {
                manufSkillIDList.add(id);
            }
        }
        return manufSkillIDList;
    }

    public boolean addManufSkillItem(final HeroPlayer _player, final ManufSkill _skill, final GetTypeOfSkillItem _getType) {
        List<Manufacture> _manufList = this.manufList.get(_player.getUserID());
        Iterator<Manufacture> iterator = _manufList.iterator();
        if (!iterator.hasNext()) {
            return false;
        }
        Manufacture manuf = iterator.next();
        if (manuf.getManufactureType().getID() != _skill.type) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u9700\u8981 " + ManufactureType.get(_skill.type).getName()));
            return false;
        }
        if (!manuf.isStudyedManufSkillID(_skill.id)) {
            manuf.addManufID(_skill.id);
            ManufactureDAO.addManufID(_player.getUserID(), _skill.id);
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(String.valueOf(_getType.toString()) + _skill.name));
            return true;
        }
        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u60a8\u5df2\u5b66\u4f1a\u6b64\u6280\u80fd\uff01"));
        return false;
    }

    public List<Manufacture> forgetManufactureByUserID(final int _userID) {
        List<Manufacture> manufactureList = this.manufList.remove(_userID);
        if (manufactureList != null) {
            ManufactureDAO.forgetManufByUserID(_userID);
        }
        return manufactureList;
    }

    public void lvlUp(final int _userID, final Manufacture _manuf) {
        _manuf.lvlUp();
        ManufactureDAO.updateManuf(_userID, _manuf);
    }

    public void addPoint(final int _userID, final Manufacture _manuf, final int _addPoint) {
        _manuf.addPoint(_addPoint);
        ManufactureDAO.updateManuf(_userID, _manuf);
    }
}
