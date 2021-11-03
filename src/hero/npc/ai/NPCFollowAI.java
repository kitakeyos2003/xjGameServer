// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.ai;

import yoyo.core.packet.AbsResponseMessage;
import hero.npc.message.NpcResetNotify;
import hero.map.broadcast.MapSynchronousInfoBroadcast;
import hero.npc.service.AStar;
import hero.npc.service.NotPlayerServiceImpl;
import hero.npc.service.NpcConfig;
import hero.share.service.ThreadPoolFactory;
import hero.player.HeroPlayer;
import hero.npc.Npc;
import java.util.concurrent.Future;

public class NPCFollowAI implements Runnable {

    private Future followAITask;
    private Npc actor;
    private HeroPlayer followTarget;

    public NPCFollowAI(final Npc _actor) {
        this.actor = _actor;
    }

    public void startFollow(final HeroPlayer _followTarget) {
        if (this.followAITask == null) {
            this.followTarget = _followTarget;
            this.followAITask = ThreadPoolFactory.getInstance().excuteAI(this, 100L, 1000L);
        }
    }

    @Override
    public void run() {
        if (this.followTarget != null && this.followTarget.isEnable()) {
            if (this.actor.where() == this.followTarget.where()) {
                short targetLocationX = this.followTarget.getCellX();
                short targetLocationY = this.followTarget.getCellY();
                int npc_follow_grid_distance_of_target = NotPlayerServiceImpl.getInstance().getConfig().NPC_FOLLOW_GRID_DISTANCE_OF_TARGET * 16;
                boolean inDistance = (this.actor.getCellX() - targetLocationX) * 16 * ((this.actor.getCellX() - targetLocationX) * 16) + (this.actor.getCellY() - targetLocationY) * 16 * ((this.actor.getCellY() - targetLocationY) * 16) > npc_follow_grid_distance_of_target * npc_follow_grid_distance_of_target;
                if (inDistance) {
                    byte[] path = AStar.getPath(this.actor.getCellX(), this.actor.getCellY(), targetLocationX, targetLocationY, NotPlayerServiceImpl.getInstance().getConfig().NPC_FOLLOW_MOST_FAST_GRID, NotPlayerServiceImpl.getInstance().getConfig().NPC_FOLLOW_GRID_DISTANCE_OF_TARGET, this.actor.where());
                    if (path != null && (path.length > 1 || (path.length == 1 && path[0] > 0))) {
                        this.actor.goAlone(path, null);
                    }
                }
            }
        } else {
            this.actor.stopFollowTask();
            this.actor.setCellX(this.actor.getOrgX());
            this.actor.setCellY(this.actor.getOrgY());
            if (this.actor.where() != this.actor.getOrgMap()) {
                this.actor.gotoMap(this.actor.getOrgMap());
            } else {
                MapSynchronousInfoBroadcast.getInstance().put(this.actor.where(), new NpcResetNotify(this.actor.getID(), this.actor.getCellX(), this.actor.getCellY()), false, 0);
            }
        }
    }

    public void stopFollow() {
        if (this.followAITask != null) {
            this.followAITask.cancel(true);
            this.followAITask = null;
        }
        this.followTarget = null;
    }

    public HeroPlayer getFollowTarget() {
        return this.followTarget;
    }
}
