// 
// Decompiled by Procyon v0.5.36
// 
package hero.task.service;

import java.sql.ResultSet;
import hero.task.target.TaskTargetGoods;
import hero.player.HeroPlayer;
import hero.task.TaskInstance;
import java.util.Iterator;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import hero.share.service.LogWriter;
import hero.task.target.ETastTargetType;
import hero.task.target.BaseTaskTarget;
import yoyo.service.tools.database.DBServiceImpl;
import hero.task.Task;

public class TaskDAO {

    private static final String INSERT_NEW_TASK = "INSERT INTO player_exsits_task(user_id,task_id,task_target1_id,task_target2_id,task_target3_id,task_target4_id,task_target5_id) VALUES(?,?,?,?,?,?,?)";
    private static final String UPDATE_TASK_PROGRESS = "UPDATE player_exsits_task SET task_target1_id=?,task_target1_number=?,task_target2_id=?,task_target2_number=?,task_target3_id=?,task_target3_number=?,task_target4_id=?,task_target4_number=?,task_target5_id=?,task_target5_number=? WHERE user_id=? AND task_id=? LIMIT 1";
    private static final String SELECT_RECEIVING_TASK = "SELECT * FROM player_exsits_task WHERE user_id=? LIMIT 20";
    private static final String DELETE_TASK = "DELETE FROM player_exsits_task WHERE user_id=? AND task_id=? LIMIT 1";
    private static final String INSERT_COMPLETED_TASK = "INSERT INTO player_completed_task VALUES(?,?)";
    private static final String SELECT_COMPLETED_TASK = "SELECT * FROM player_completed_task WHERE user_id=?";

    public static boolean insertNewTask(final int _userID, final Task _task) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("INSERT INTO player_exsits_task(user_id,task_id,task_target1_id,task_target2_id,task_target3_id,task_target4_id,task_target5_id) VALUES(?,?,?,?,?,?,?)");
            pstm.setInt(1, _userID);
            pstm.setInt(2, _task.getID());
            int[] targetIDs = new int[5];
            ArrayList<BaseTaskTarget> targetList = _task.getTargetList();
            int j = 0;
            if (targetList.size() > 0) {
                for (final BaseTaskTarget target : targetList) {
                    if (ETastTargetType.GOODS != target.getType()) {
                        targetIDs[j] = target.getID();
                        ++j;
                    }
                }
            }
            for (j = 3; j < 8; ++j) {
                pstm.setInt(j, targetIDs[j - 3]);
            }
            if (pstm.executeUpdate() == 1) {
                return true;
            }
        } catch (Exception ex) {
            LogWriter.error(null, ex);
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex2) {
            }
        }
        try {
            if (pstm != null) {
                pstm.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex3) {
        }
        return false;
    }

    public static boolean updateTaskProgress(final int _userID, final TaskInstance _taskInstance) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("UPDATE player_exsits_task SET task_target1_id=?,task_target1_number=?,task_target2_id=?,task_target2_number=?,task_target3_id=?,task_target3_number=?,task_target4_id=?,task_target4_number=?,task_target5_id=?,task_target5_number=? WHERE user_id=? AND task_id=? LIMIT 1");
            _taskInstance.getTargetList();
            int[][] targetProgress = new int[5][2];
            ArrayList<BaseTaskTarget> targetList = _taskInstance.getTargetList();
            int j = 0;
            if (targetList.size() > 0) {
                for (final BaseTaskTarget target : targetList) {
                    if (ETastTargetType.GOODS != target.getType()) {
                        targetProgress[j][0] = target.getID();
                        if (ETastTargetType.KILL_MONSTER == target.getType()) {
                            targetProgress[j][1] = target.getCurrentNumber();
                        } else {
                            targetProgress[j][1] = (target.isCompleted() ? 1 : 0);
                        }
                        ++j;
                    }
                }
            }
            for (j = 1; j < 6; ++j) {
                pstm.setInt((j - 1) * 2 + 1, targetProgress[j - 1][0]);
                pstm.setInt((j - 1) * 2 + 2, targetProgress[j - 1][1]);
            }
            pstm.setInt(11, _userID);
            pstm.setInt(12, _taskInstance.getArchetype().getID());
            if (pstm.executeUpdate() == 1) {
                return true;
            }
        } catch (Exception ex) {
            LogWriter.error(null, ex);
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex2) {
            }
        }
        try {
            if (pstm != null) {
                pstm.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex3) {
        }
        return false;
    }

    public static boolean deleteTask(final int _userID, final int _taskID) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("DELETE FROM player_exsits_task WHERE user_id=? AND task_id=? LIMIT 1");
            pstm.setInt(1, _userID);
            pstm.setInt(2, _taskID);
            if (pstm.executeUpdate() == 1) {
                return true;
            }
        } catch (Exception ex) {
            LogWriter.error(null, ex);
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex2) {
            }
        }
        try {
            if (pstm != null) {
                pstm.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex3) {
        }
        return false;
    }

    public static void loadTask(final HeroPlayer _player, final ArrayList<TaskInstance> _exsitsTaskList, final ArrayList<Integer> _completeTaskIDsList) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet set = null;
        _exsitsTaskList.clear();
        _completeTaskIDsList.clear();
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            pstm = conn.prepareStatement("SELECT * FROM player_exsits_task WHERE user_id=? LIMIT 20");
            pstm.setInt(1, _player.getUserID());
            set = pstm.executeQuery();
            TaskInstance task = null;
            ArrayList<Integer> existsTaskIDs = new ArrayList<Integer>();
            while (set.next()) {
                int taskID = set.getInt("task_id");
                if (existsTaskIDs.contains(taskID)) {
                    continue;
                }
                Task taskModel = TaskServiceImpl.getInstance().getTask(taskID);
                if (taskModel == null) {
                    continue;
                }
                task = new TaskInstance(taskModel);
                int target1ID = set.getInt("task_target1_id");
                short target1Number = set.getShort("task_target1_number");
                int target2ID = set.getInt("task_target2_id");
                short target2Number = set.getShort("task_target2_number");
                int target3ID = set.getInt("task_target3_id");
                short target3Number = set.getShort("task_target3_number");
                int target4ID = set.getInt("task_target4_id");
                short target4Number = set.getShort("task_target4_number");
                int target5ID = set.getInt("task_target5_id");
                short target5Number = set.getShort("task_target5_number");
                ArrayList<BaseTaskTarget> targetList = task.getTargetList();
                if (targetList != null) {
                    for (final BaseTaskTarget target : targetList) {
                        if (ETastTargetType.GOODS != target.getType()) {
                            int targetID = target.getID();
                            if (targetID == target1ID) {
                                target.setCurrentNumber(target1Number);
                            } else if (targetID == target2ID) {
                                target.setCurrentNumber(target2Number);
                            } else if (targetID == target3ID) {
                                target.setCurrentNumber(target3Number);
                            } else if (targetID == target4ID) {
                                target.setCurrentNumber(target4Number);
                            } else {
                                if (targetID != target5ID) {
                                    continue;
                                }
                                target.setCurrentNumber(target5Number);
                            }
                        } else {
                            target.setCurrentNumber((short) _player.getInventory().getTaskToolBag().getGoodsNumber(((TaskTargetGoods) target).goods.getID()));
                        }
                    }
                }
                _exsitsTaskList.add(task);
            }
            set.close();
            set = null;
            pstm.close();
            pstm = null;
            pstm = conn.prepareStatement("SELECT * FROM player_completed_task WHERE user_id=?");
            pstm.setInt(1, _player.getUserID());
            set = pstm.executeQuery();
            while (set.next()) {
                int taskID = set.getInt("task_id");
                _completeTaskIDsList.add(taskID);
            }
        } catch (Exception ex) {
            LogWriter.error(null, ex);
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex2) {
            }
        }
        try {
            if (pstm != null) {
                pstm.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex3) {
        }
    }

    public static boolean completeTask(final int _userID, final int _taskID, final boolean _isRepeatedTask) {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBServiceImpl.getInstance().getConnection();
            if (!_isRepeatedTask) {
                pstm = conn.prepareStatement("INSERT INTO player_completed_task VALUES(?,?)");
                pstm.setInt(1, _userID);
                pstm.setInt(2, _taskID);
                pstm.executeUpdate();
                pstm.close();
                pstm = null;
            }
            pstm = conn.prepareStatement("DELETE FROM player_exsits_task WHERE user_id=? AND task_id=? LIMIT 1");
            pstm.setInt(1, _userID);
            pstm.setInt(2, _taskID);
            if (pstm.executeUpdate() == 1) {
                return true;
            }
        } catch (Exception ex) {
            LogWriter.error(null, ex);
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex2) {
            }
        }
        try {
            if (pstm != null) {
                pstm.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex3) {
        }
        return false;
    }
}
