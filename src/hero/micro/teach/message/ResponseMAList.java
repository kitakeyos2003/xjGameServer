// 
// Decompiled by Procyon v0.5.36
// 
package hero.micro.teach.message;

import java.io.IOException;
import hero.player.HeroPlayer;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.service.PlayerServiceImpl;
import hero.micro.service.MicroServiceImpl;
import hero.micro.teach.MasterApprentice;
import org.apache.log4j.Logger;
import yoyo.core.packet.AbsResponseMessage;

public class ResponseMAList extends AbsResponseMessage {

    private static Logger log;
    private MasterApprentice masterApprenticeList;
    private int selfID;
    private boolean isApprentice;

    static {
        ResponseMAList.log = Logger.getLogger((Class) ResponseMAList.class);
    }

    public ResponseMAList(final MasterApprentice _masterApprenticeList, final int _selfID) {
        this.isApprentice = false;
        this.masterApprenticeList = _masterApprenticeList;
        this.selfID = _selfID;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    protected void write() throws IOException {
        MasterApprentice apprenticeMasterList = null;
        if (this.masterApprenticeList == null) {
            this.yos.writeByte(0);
        } else {
            int number = 0;
            if (this.masterApprenticeList.masterUserID > 0) {
                ++number;
            }
            number += this.masterApprenticeList.apprenticeNumber;
            if (this.masterApprenticeList.apprenticeNumber == 0 && this.masterApprenticeList.masterUserID > 0) {
                this.isApprentice = true;
                apprenticeMasterList = MicroServiceImpl.getInstance().getMasterApprentice(this.masterApprenticeList.masterUserID);
                if (apprenticeMasterList != null) {
                    ResponseMAList.log.debug((Object) ("\u5f92\u5f1f\u7684\u5e08\u5f92\u5217\u8868 apprenticeMasterList apprenticeNumber = " + apprenticeMasterList.apprenticeNumber));
                    number += apprenticeMasterList.apprenticeNumber;
                    --number;
                }
            }
            this.yos.writeByte(number);
            this.yos.writeInt(this.selfID);
            this.yos.writeByte(this.isApprentice);
            if (this.masterApprenticeList.masterUserID > 0) {
                if (this.masterApprenticeList.masterUserID == this.selfID) {
                    HeroPlayer master = PlayerServiceImpl.getInstance().getPlayerByUserID(this.masterApprenticeList.masterUserID);
                    ResponseMessageQueue.getInstance().put(master.getMsgQueueIndex(), new Warning("\u4f60\u6ca1\u6709\u5e08\u5085"));
                } else {
                    this.yos.writeInt(this.masterApprenticeList.masterUserID);
                    this.yos.writeUTF(this.masterApprenticeList.masterName);
                    ResponseMAList.log.debug((Object) ("master[" + this.masterApprenticeList.masterName + "] is online = " + this.masterApprenticeList.masterIsOnline));
                    if (this.masterApprenticeList.masterIsOnline) {
                        HeroPlayer master = PlayerServiceImpl.getInstance().getPlayerByUserID(this.masterApprenticeList.masterUserID);
                        if (master != null && master.isEnable()) {
                            this.yos.writeByte(true);
                            this.yos.writeByte(master.getVocation().value());
                            this.yos.writeShort(master.getLevel());
                            this.yos.writeByte(master.getSex().value());
                        } else {
                            this.yos.writeByte(false);
                        }
                    } else {
                        this.yos.writeByte(false);
                    }
                    this.yos.writeByte((byte) 1);
                }
            }
            if (!this.isApprentice && this.masterApprenticeList.apprenticeNumber > 0) {
                ResponseMAList.log.debug((Object) ("response MAList master's apprentice number = " + this.masterApprenticeList.apprenticeNumber));
                for (int i = 0; i < this.masterApprenticeList.apprenticeNumber; ++i) {
                    ResponseMAList.log.debug((Object) ("apprentice id=" + this.masterApprenticeList.apprenticeList[i].userID + ",name=" + this.masterApprenticeList.apprenticeList[i].name));
                    this.yos.writeInt(this.masterApprenticeList.apprenticeList[i].userID);
                    this.yos.writeUTF(this.masterApprenticeList.apprenticeList[i].name);
                    if (this.masterApprenticeList.apprenticeList[i].isOnline) {
                        HeroPlayer apprentice = PlayerServiceImpl.getInstance().getPlayerByUserID(this.masterApprenticeList.apprenticeList[i].userID);
                        if (apprentice != null && apprentice.isEnable()) {
                            this.yos.writeByte(true);
                            this.yos.writeByte(apprentice.getVocation().value());
                            this.yos.writeShort(apprentice.getLevel());
                            this.yos.writeByte(apprentice.getSex().value());
                        } else {
                            this.yos.writeByte(false);
                        }
                    } else {
                        this.yos.writeByte(false);
                    }
                    this.yos.writeByte((byte) 2);
                }
            } else if (this.isApprentice && this.masterApprenticeList.apprenticeNumber == 0 && apprenticeMasterList != null) {
                for (int i = 0; i < apprenticeMasterList.apprenticeNumber; ++i) {
                    if (this.selfID != apprenticeMasterList.apprenticeList[i].userID) {
                        this.yos.writeInt(apprenticeMasterList.apprenticeList[i].userID);
                        this.yos.writeUTF(apprenticeMasterList.apprenticeList[i].name);
                        if (apprenticeMasterList.apprenticeList[i].isOnline) {
                            HeroPlayer apprentice = PlayerServiceImpl.getInstance().getPlayerByUserID(apprenticeMasterList.apprenticeList[i].userID);
                            if (apprentice != null && apprentice.isEnable()) {
                                this.yos.writeByte(true);
                                this.yos.writeByte(apprentice.getVocation().value());
                                this.yos.writeShort(apprentice.getLevel());
                                this.yos.writeByte(apprentice.getSex().value());
                            } else {
                                this.yos.writeByte(false);
                            }
                        } else {
                            this.yos.writeByte(false);
                        }
                        this.yos.writeByte((byte) 2);
                    }
                }
            }
        }
    }
}
