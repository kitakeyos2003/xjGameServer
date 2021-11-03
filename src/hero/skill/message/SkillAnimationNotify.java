// 
// Decompiled by Procyon v0.5.36
// 
package hero.skill.message;

import java.io.IOException;
import java.util.ArrayList;
import hero.share.ME2GameObject;
import org.apache.log4j.Logger;
import yoyo.core.packet.AbsResponseMessage;

public class SkillAnimationNotify extends AbsResponseMessage {

    private static Logger log;
    private byte releaserObjectType;
    private int releaserID;
    private int[][] targetList;
    private int releaseAnimationID;
    private byte actionID;
    private int releaseImageID;
    private byte tierRelation;
    private int acceptAnimationID;
    private int acceptImageID;
    private byte direction;
    private byte heightRelation;
    private byte releaseHeightRelation;
    private byte isDirection;

    static {
        SkillAnimationNotify.log = Logger.getLogger((Class) SkillAnimationNotify.class);
    }

    public SkillAnimationNotify(final ME2GameObject _releaser, final int _releaseAnimationID, final int _releaseImageID, final ME2GameObject _target, final int _accepteAnimationID, final int _acceptImageID, final byte _actionID, final byte _tierRelation, final byte _releaseHeightRelation, final byte _heightRelation, final byte _isDirection) {
        if (_releaser != null) {
            this.releaserObjectType = _releaser.getObjectType().value();
            this.releaserID = _releaser.getID();
            this.releaseAnimationID = _releaseAnimationID;
            this.releaseImageID = _releaseImageID;
            this.direction = _releaser.getDirection();
            this.actionID = _actionID;
            this.tierRelation = _tierRelation;
            this.releaseHeightRelation = _releaseHeightRelation;
            this.isDirection = _isDirection;
        }
        if (_accepteAnimationID != 0) {
            this.acceptAnimationID = _accepteAnimationID;
            this.acceptImageID = _acceptImageID;
            this.heightRelation = _heightRelation;
            this.targetList = new int[1][2];
            this.targetList[0][0] = _target.getObjectType().value();
            this.targetList[0][1] = _target.getID();
        }
    }

    public SkillAnimationNotify(final ME2GameObject _releaser, final ArrayList<ME2GameObject> _targetList, final int _releaseAnimationID, final int _releaseImageID, final int _accepteAnimationID, final int _acceptImageID, final byte _actionID, final byte _tierRelation, final byte _releaseHeightRelation, final byte _heightRelation, final byte _isDirection) {
        if (_releaser != null) {
            this.releaserObjectType = _releaser.getObjectType().value();
            this.releaserID = _releaser.getID();
            this.releaseAnimationID = _releaseAnimationID;
            this.releaseImageID = _releaseImageID;
            this.direction = _releaser.getDirection();
            this.actionID = _actionID;
            this.tierRelation = _tierRelation;
            this.releaseHeightRelation = _releaseHeightRelation;
            this.isDirection = _isDirection;
        }
        if (_accepteAnimationID != 0) {
            this.acceptAnimationID = _accepteAnimationID;
            this.acceptImageID = _acceptImageID;
            this.heightRelation = _heightRelation;
            this.targetList = new int[_targetList.size()][2];
            for (int i = 0; i < _targetList.size(); ++i) {
                ME2GameObject target = _targetList.get(i);
                this.targetList[i][0] = target.getObjectType().value();
                this.targetList[i][1] = target.getID();
            }
        }
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        this.yos.writeShort(this.releaseImageID);
        this.yos.writeShort(this.releaseAnimationID);
        this.yos.writeByte(this.actionID);
        this.yos.writeByte(this.tierRelation);
        this.yos.writeByte(this.releaseHeightRelation);
        this.yos.writeByte(this.releaserObjectType);
        this.yos.writeInt(this.releaserID);
        this.yos.writeByte(this.direction);
        this.yos.writeByte(this.isDirection);
        if (this.acceptAnimationID == 0) {
            this.yos.writeByte(0);
        } else {
            this.yos.writeByte(this.targetList.length);
            this.yos.writeShort(this.acceptImageID);
            this.yos.writeShort(this.acceptAnimationID);
            this.yos.writeByte(this.heightRelation);
            int[][] targetList;
            for (int length = (targetList = this.targetList).length, i = 0; i < length; ++i) {
                int[] targetInfo = targetList[i];
                this.yos.writeByte(targetInfo[0]);
                this.yos.writeInt(targetInfo[1]);
            }
        }
    }
}
