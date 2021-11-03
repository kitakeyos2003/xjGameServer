// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.function.system;

import hero.item.detail.EGoodsType;
import hero.task.TaskInstance;
import hero.npc.detail.NpcHandshakeOptionData;
import hero.npc.Npc;
import java.util.Iterator;
import java.util.ArrayList;
import hero.item.Goods;
import hero.task.Task;
import java.io.EOFException;
import hero.share.service.LogWriter;
import hero.ui.message.ReturnMainUI;
import hero.npc.service.NotPlayerServiceImpl;
import hero.task.target.TaskTargetEscortNpc;
import hero.task.target.ETastTargetType;
import hero.task.target.BaseTaskTarget;
import hero.task.Award;
import hero.npc.message.NpcInteractiveResponse;
import hero.ui.UI_TaskContent;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.task.service.TaskServiceImpl;
import yoyo.tools.YOYOInputStream;
import hero.player.HeroPlayer;
import hero.npc.function.ENpcFunctionType;
import org.apache.log4j.Logger;
import hero.npc.function.BaseNpcFunction;

public class TaskPassageway extends BaseNpcFunction {

    private static Logger log;
    public static final byte RECEIVE_TASK_MARK = 1;
    public static final byte SUBMIT_TASK_MARK = 2;
    public static final byte TASK_TALK = 3;
    private String npcModelID;

    static {
        TaskPassageway.log = Logger.getLogger((Class) TaskPassageway.class);
    }

    public TaskPassageway(final int npcID, final String _npcModelID) {
        super(npcID);
        this.npcModelID = _npcModelID;
    }

    @Override
    public ENpcFunctionType getFunctionType() {
        return ENpcFunctionType.TASK;
    }

    @Override
    public void initTopLayerOptionList() {
    }

    @Override
    public void process(final HeroPlayer _player, final byte _step, final int selectIndex, final YOYOInputStream _content) throws Exception {
        int taskOperateMark = this.parseTaskOperateMark(selectIndex);
        int taskID = this.parseTaskID(selectIndex);
        TaskPassageway.log.debug((Object) ("task process step=" + _step + " -- selectIndex=" + selectIndex + " -- taskOperateMark=" + taskOperateMark + " -- taskID=" + taskID));
        Task task = TaskServiceImpl.getInstance().getTask(taskID);
        if (task != null) {
            TaskPassageway.log.debug((Object) ("npcModelID : " + this.npcModelID + ", task [: " + task.getName()));
            if (Step.TOP.tag == _step) {
                if (1 == taskOperateMark) {
                    if (TaskServiceImpl.getInstance().getTaskList(_player.getUserID()).size() >= 20) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u4efb\u52a1\u6570\u91cf\u5df2\u6ee1,\u65e0\u6cd5\u63a5\u53d7\u4efb\u52a1"));
                        return;
                    }
                    if (task.getReceiveGoodsList() != null && task.getReceiveGoodsList().size() > _player.getInventory().getTaskToolBag().getEmptyGridNumber()) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u4efb\u52a1\u80cc\u5305\u5df2\u6ee1,\u65e0\u6cd5\u63a5\u53d7\u4efb\u52a1"));
                        return;
                    }
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NpcInteractiveResponse(this.getHostNpcID(), this.getFunctionType().value() * 100000 + selectIndex, Step.SURE.tag, UI_TaskContent.getBytes(task, (byte) 1, _player.getLevel())));
                } else if (2 == taskOperateMark) {
                    Award award = task.getAward();
                    String tip = null;
                    if (award.getOptionalGoodsList() != null) {
                        Goods awardGoods = award.getOptionalGoodsList().get(0).goods;
                        switch (awardGoods.getGoodsType()) {
                            case EQUIPMENT: {
                                if (_player.getInventory().getEquipmentBag().getEmptyGridNumber() <= 0) {
                                    tip = "\u88c5\u5907\u80cc\u5305\u5df2\u6ee1,\u65e0\u6cd5\u63d0\u4ea4";
                                    break;
                                }
                                break;
                            }
                            case MEDICAMENT: {
                                if (_player.getInventory().getMedicamentBag().getEmptyGridNumber() <= 0) {
                                    tip = "\u836f\u54c1\u80cc\u5305\u5df2\u6ee1,\u65e0\u6cd5\u63d0\u4ea4";
                                    break;
                                }
                                break;
                            }
                            case MATERIAL: {
                                if (_player.getInventory().getMaterialBag().getEmptyGridNumber() <= 0) {
                                    tip = "\u6750\u6599\u80cc\u5305\u5df2\u6ee1,\u65e0\u6cd5\u63d0\u4ea4";
                                    break;
                                }
                                break;
                            }
                            case TASK_TOOL: {
                                if (_player.getInventory().getTaskToolBag().getEmptyGridNumber() <= 0) {
                                    tip = "\u4efb\u52a1\u80cc\u5305\u5df2\u6ee1,\u65e0\u6cd5\u63d0\u4ea4";
                                    break;
                                }
                                break;
                            }
                            case SPECIAL_GOODS: {
                                if (_player.getInventory().getSpecialGoodsBag().getEmptyGridNumber() <= 0) {
                                    tip = "\u5b9d\u7269\u80cc\u5305\u5df2\u6ee1,\u65e0\u6cd5\u63d0\u4ea4";
                                    break;
                                }
                                break;
                            }
                        }
                        if (tip != null) {
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(tip));
                            return;
                        }
                    }
                    if (award.getBoundGoodsList() != null) {
                        ArrayList<Award.AwardGoodsUnit> awardGoodsList = award.getBoundGoodsList();
                        int euipmentNumber = 0;
                        int materialNumber = 0;
                        int medicamentNumber = 0;
                        int taskToolNumber = 0;
                        int specialGoodsNumber = 0;
                        for (final Award.AwardGoodsUnit awardGoodsUnit : awardGoodsList) {
                            switch (awardGoodsUnit.goods.getGoodsType()) {
                                default: {
                                    continue;
                                }
                                case EQUIPMENT: {
                                    ++euipmentNumber;
                                    continue;
                                }
                                case MEDICAMENT: {
                                    ++medicamentNumber;
                                    continue;
                                }
                                case MATERIAL: {
                                    ++materialNumber;
                                    continue;
                                }
                                case TASK_TOOL: {
                                    ++taskToolNumber;
                                    continue;
                                }
                                case SPECIAL_GOODS: {
                                    ++specialGoodsNumber;
                                    continue;
                                }
                            }
                        }
                        if (euipmentNumber > _player.getInventory().getEquipmentBag().getEmptyGridNumber()) {
                            tip = "\u88c5\u5907\u80cc\u5305\u5df2\u6ee1,\u65e0\u6cd5\u63d0\u4ea4";
                        } else if (medicamentNumber > _player.getInventory().getMedicamentBag().getEmptyGridNumber()) {
                            tip = "\u6750\u6599\u80cc\u5305\u5df2\u6ee1,\u65e0\u6cd5\u63d0\u4ea4";
                        } else if (materialNumber > _player.getInventory().getMaterialBag().getEmptyGridNumber()) {
                            tip = "\u6750\u6599\u80cc\u5305\u5df2\u6ee1,\u65e0\u6cd5\u63d0\u4ea4";
                        } else if (taskToolNumber > _player.getInventory().getTaskToolBag().getEmptyGridNumber()) {
                            tip = "\u4efb\u52a1\u80cc\u5305\u5df2\u6ee1,\u65e0\u6cd5\u63d0\u4ea4";
                        } else if (specialGoodsNumber > _player.getInventory().getSpecialGoodsBag().getEmptyGridNumber()) {
                            tip = "\u5b9d\u7269\u80cc\u5305\u5df2\u6ee1,\u65e0\u6cd5\u63d0\u4ea4";
                        }
                        if (tip != null) {
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning(tip));
                            return;
                        }
                    }
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NpcInteractiveResponse(this.getHostNpcID(), this.getFunctionType().value() * 100000 + selectIndex, Step.SURE.tag, UI_TaskContent.getBytes(task, (byte) 2, _player.getLevel())));
                } else if (3 == taskOperateMark) {
                    if (_player.getEscortTarget() != null) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u62a4\u9001\u4efb\u52a1\u8981\u4e00\u4e2a\u4e00\u4e2a\u505a"));
                        return;
                    }
                    ArrayList<BaseTaskTarget> targetList = task.getTargetList();
                    for (final BaseTaskTarget target : targetList) {
                        if (ETastTargetType.ESCORT_NPC == target.getType() && ((TaskTargetEscortNpc) target).npcModelID.equals(this.npcModelID)) {
                            Npc npc = NotPlayerServiceImpl.getInstance().getNpc(this.npcModelID);
                            byte tox = (byte) npc.getCellX();
                            byte toy = (byte) npc.getCellY();
                            byte x = (byte) _player.getCellX();
                            byte y = (byte) _player.getCellY();
                            boolean resv = this.checkArea(tox, toy, (byte) 4, (byte) 1, x, y);
                            if (resv) {
                                TaskServiceImpl.getInstance().beginEscortNpcTask(_player, task, (TaskTargetEscortNpc) target, NotPlayerServiceImpl.getInstance().getNpc(this.getHostNpcID()));
                                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new ReturnMainUI());
                                break;
                            }
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u60a8\u79bb %fn \u592a\u8fdc\u4e86,\u8d70\u8fd1\u4e00\u4e9b\u5427.".replaceAll("%fn", npc.getName())));
                            break;
                        }
                    }
                }
            } else if (Step.SURE.tag == _step) {
                if (1 == taskOperateMark) {
                    TaskServiceImpl.getInstance().receiveTask(_player, taskID);
                } else if (2 == taskOperateMark) {
                    int selectAwardGoodsID;
                    try {
                        selectAwardGoodsID = _content.readInt();
                    } catch (EOFException eofe) {
                        LogWriter.println("\u4efb\u52a1\u53ef\u9009\u5956\u52b1\u7269\u54c1\u7f16\u53f7\u7f3a\u5931");
                        return;
                    }
                    TaskServiceImpl.getInstance().submitTask(_player, taskID, selectAwardGoodsID);
                }
            }
        }
    }

    public boolean checkArea(final byte _tox, final byte _toy, final byte _area, final byte _size, final byte bytTileX, final byte bytTileY) {
        if (_area == 0) {
            return true;
        }
        byte s_x = bytTileX;
        byte s_y = bytTileY;
        byte e_x = _tox;
        byte e_y = _toy;
        return Math.pow(s_x - e_x, 2.0) + Math.pow(s_y - e_y, 2.0) <= Math.pow(_area + _size / 2, 2.0);
    }

    @Override
    public ArrayList<NpcHandshakeOptionData> getTopLayerOptionList(final HeroPlayer _player) {
        ArrayList<NpcHandshakeOptionData> handshakeOptionList = new ArrayList<NpcHandshakeOptionData>();
        NpcHandshakeOptionData optionData = null;
        ArrayList<TaskInstance> playerList = TaskServiceImpl.getInstance().getTaskList(_player.getUserID());
        for (final TaskInstance taskInstance : playerList) {
            if (taskInstance.isCompleted()) {
                if (!taskInstance.getArchetype().getSubmitNpcID().equals(this.npcModelID)) {
                    continue;
                }
                optionData = new NpcHandshakeOptionData();
                optionData.miniImageID = this.getMinMarkIconID2();
                optionData.optionDesc = taskInstance.getArchetype().getName();
                optionData.functionMark = this.getFunctionType().value() * 100000 + this.produceTaskMark((byte) 2, taskInstance.getArchetype().getID());
                handshakeOptionList.add(optionData);
            } else {
                ArrayList<BaseTaskTarget> targetList = taskInstance.getTargetList();
                for (final BaseTaskTarget target : targetList) {
                    if (ETastTargetType.ESCORT_NPC == target.getType() && !target.isCompleted() && ((TaskTargetEscortNpc) target).npcModelID.equals(this.npcModelID)) {
                        optionData = new NpcHandshakeOptionData();
                        optionData.miniImageID = this.getMinMarkIconID2();
                        optionData.optionDesc = "\u8ddf\u6211\u8d70\u5427";
                        optionData.functionMark = this.getFunctionType().value() * 100000 + this.produceTaskMark((byte) 3, taskInstance.getArchetype().getID());
                        handshakeOptionList.add(optionData);
                        break;
                    }
                }
            }
        }
        ArrayList<Task> taskList = TaskServiceImpl.getInstance().getReceiveableTaskList(this.npcModelID, _player);
        if (taskList != null) {
            for (final Task task : taskList) {
                optionData = new NpcHandshakeOptionData();
                optionData.miniImageID = this.getMinMarkIconID();
                optionData.optionDesc = task.getName();
                optionData.functionMark = this.getFunctionType().value() * 100000 + this.produceTaskMark((byte) 1, task.getID());
                handshakeOptionList.add(optionData);
            }
        }
        return handshakeOptionList;
    }

    private int parseTaskOperateMark(final int _taskMark) {
        return _taskMark / 10000;
    }

    private int parseTaskID(final int _taskMark) {
        return _taskMark % 10000;
    }

    private int produceTaskMark(final byte _distributeOrSubmitMark, final int _taskID) {
        return _distributeOrSubmitMark * 10000 + _taskID;
    }

    enum Step {
        TOP("TOP", 0, 1),
        SURE("SURE", 1, 2);

        byte tag;

        private Step(final String name, final int ordinal, final int _tag) {
            this.tag = (byte) _tag;
        }
    }
}
