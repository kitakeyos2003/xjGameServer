// 
// Decompiled by Procyon v0.5.36
// 
package hero.task;

import hero.micro.teach.MasterApprentice;
import hero.player.HeroPlayer;
import java.util.Iterator;
import java.util.List;
import hero.micro.teach.TeachService;
import hero.lover.service.LoverServiceImpl;
import hero.player.service.PlayerServiceImpl;
import hero.manufacture.Manufacture;
import hero.manufacture.service.ManufactureServerImpl;
import java.util.ArrayList;
import hero.manufacture.ManufactureType;
import hero.task.target.TaskType;
import hero.player.define.EClan;
import hero.share.EVocation;
import org.apache.log4j.Logger;

public class Condition {

    private static Logger log;
    public EVocation vocation;
    public EClan clan;
    public short level;
    public int completeTaskID;
    public int taskNext;
    public TaskType taskType;
    public ManufactureType manufactureType;

    static {
        Condition.log = Logger.getLogger((Class) Condition.class);
    }

    public boolean check(final int userID, final EVocation _vocation, final EClan _clan, final short _level, final ArrayList<Integer> _completeTaskIDList) {
        Condition.log.debug((Object) ("conditon check start ... player level= " + _level + "  condition level= " + this.level));
        if (this.vocation != null && this.vocation != EVocation.ALL && this.vocation != _vocation) {
            return false;
        }
        if (EClan.NONE != this.clan && this.clan != _clan) {
            return false;
        }
        if (_level < this.level) {
            return false;
        }
        if (this.manufactureType != null) {
            List<Manufacture> manufactureList = ManufactureServerImpl.getInstance().getManufactureListByUserID(userID);
            if (manufactureList == null) {
                Condition.log.debug((Object) "need manuf skill == null return false..");
                return false;
            }
            boolean noManuf = true;
            for (final Manufacture manuf : manufactureList) {
                if (manuf.getManufactureType() == this.manufactureType) {
                    noManuf = false;
                }
            }
            if (noManuf) {
                Condition.log.debug((Object) "had manuf,but != task manuf...");
                return false;
            }
        }
        Condition.log.debug((Object) ("task type = " + this.taskType));
        if (this.taskType != TaskType.SINGLE) {
            if (this.taskType == TaskType.MARRY) {
                HeroPlayer player = PlayerServiceImpl.getInstance().getPlayerByUserID(userID);
                String spouse = LoverServiceImpl.getInstance().whoLoveMe(player.getName());
                if (spouse == null) {
                    spouse = LoverServiceImpl.getInstance().whoMarriedMe(player.getName());
                    if (spouse == null) {
                        return false;
                    }
                }
            }
            if (this.taskType == TaskType.MASTER) {
                MasterApprentice masterApprentice = TeachService.get(userID);
                Condition.log.debug((Object) ("task check master apprentice : " + masterApprentice));
                if (masterApprentice != null && !masterApprentice.isValidate()) {
                    Condition.log.debug((Object) "task check master is not validate");
                    return false;
                }
                if (masterApprentice == null) {
                    Condition.log.debug((Object) "task check master = null");
                    return false;
                }
            }
        }
        Condition.log.debug((Object) "next check contains taskid");
        if (this.completeTaskID != 0 && _completeTaskIDList != null && !_completeTaskIDList.contains(this.completeTaskID)) {
            Condition.log.debug((Object) "not ");
            return false;
        }
        return true;
    }
}
