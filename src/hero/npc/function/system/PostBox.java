// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.function.system;

import hero.item.bag.SingleGoodsBag;
import hero.item.bag.exception.BagException;
import hero.item.EquipmentInstance;
import hero.item.dictionary.GoodsContents;
import hero.ui.UI_GridGoodsNumsChanged;
import hero.log.service.LogServiceImpl;
import java.util.Date;
import java.util.List;
import hero.item.Goods;
import hero.npc.function.system.postbox.Mail;
import hero.item.bag.EquipmentContainer;
import hero.ui.UI_GoodsListWithOperation;
import hero.item.service.GoodsConfig;
import hero.share.message.MailStatusChanges;
import hero.log.service.CauseLog;
import hero.item.service.GoodsServiceImpl;
import hero.charge.service.ChargeServiceImpl;
import hero.player.service.PlayerServiceImpl;
import hero.share.message.Warning;
import hero.ui.UI_SelectOperation;
import yoyo.core.packet.AbsResponseMessage;
import hero.npc.message.NpcInteractiveResponse;
import hero.ui.UI_MailGoodsList;
import hero.npc.function.system.postbox.MailService;
import yoyo.core.queue.ResponseMessageQueue;
import yoyo.tools.YOYOInputStream;
import hero.player.HeroPlayer;
import hero.npc.detail.NpcHandshakeOptionData;
import hero.npc.function.ENpcFunctionType;
import hero.ui.UI_InputDigidal;
import hero.ui.UI_InputString;
import hero.share.service.Tip;
import java.util.ArrayList;
import hero.npc.function.BaseNpcFunction;

public class PostBox extends BaseNpcFunction {

    private static final int MAIL_MONEY = 1;
    private static final String[] MAIN_MENU_LIST;
    private static final short[] MAIN_MENU_MARK_IMAGE_ID_LIST;
    private static final String[] MAIL_BOX_OPERTION_LIST;
    private static final String[] SALE_MENU_LIST;
    private static ArrayList<byte[]>[] saleMenuOptionData;
    private static ArrayList<byte[]>[] saleSingleGoodsOptionData;
    private static ArrayList<byte[]>[] saleEquipmentOptionData;
    private static final short TITLE_LENGHT = 20;
    private static final short CONTENT_LENGTH = 200;
    private static final byte OPTION_GET_ATTACHMENT = 0;
    private static final byte OPTION_PREC_PAGE = 1;
    private static final byte OPTION_NEXT_PAGE = 2;
    private static final byte OPTION_DELETE = 3;
    private static final byte OPTION_READ = 4;

    static {
        MAIN_MENU_LIST = new String[]{"\u6536\u4fe1\u7bb1", "\u53d1\u4fe1\u7bb1"};
        MAIN_MENU_MARK_IMAGE_ID_LIST = new short[]{1009, 1009};
        MAIL_BOX_OPERTION_LIST = new String[]{"\u67e5\u3000\u3000\u770b", "\u4e0a\u3000\u3000\u9875", "\u4e0b\u3000\u3000\u9875", "\u6536\u3000\u3000\u53d6", "\u56de\u3000\u3000\u590d", "\u5220\u3000\u3000\u9664"};
        SALE_MENU_LIST = new String[]{"\u88c5\u5907", "\u6d88\u8017", "\u6750\u6599", "\u91d1\u94b1", "\u7279\u6b8a"};
        PostBox.saleMenuOptionData = (ArrayList<byte[]>[]) new ArrayList[PostBox.SALE_MENU_LIST.length];
        PostBox.saleSingleGoodsOptionData = (ArrayList<byte[]>[]) new ArrayList[Tip.FUNCTION_MAIL_SALE_OPERTION_LIST.length];
        PostBox.saleEquipmentOptionData = (ArrayList<byte[]>[]) new ArrayList[Tip.FUNCTION_MAIL_SALE_OPERTION_LIST.length];
    }

    public PostBox(final int _npcID) {
        super(_npcID);
        ArrayList<byte[]> data = new ArrayList<byte[]>();
        byte[] inputString = UI_InputString.getBytes("\u8bf7\u8f93\u5165\u6536\u4fe1\u73a9\u5bb6\u7684\u6635\u79f0");
        data.add(UI_InputDigidal.getBytes("\u8bf7\u8f93\u5165\u90ae\u5bc4\u6570\u91cf"));
        data.add(inputString);
        PostBox.saleSingleGoodsOptionData[0] = data;
        data = new ArrayList<byte[]>();
        data.add(inputString);
        PostBox.saleEquipmentOptionData[0] = data;
        PostBox.saleMenuOptionData[0] = null;
        data = new ArrayList<byte[]>();
        data.add(UI_InputDigidal.getBytes("\u8bf7\u8f93\u5165\u90ae\u5bc4\u6570\u91cf", 0, 20));
        data.add(inputString);
        PostBox.saleMenuOptionData[1] = null;
        data = new ArrayList<byte[]>();
        data.add(UI_InputDigidal.getBytes("\u8bf7\u8f93\u5165\u90ae\u5bc4\u6570\u91cf", 0, 20));
        data.add(inputString);
        PostBox.saleMenuOptionData[2] = null;
        data = new ArrayList<byte[]>();
        data.add(UI_InputDigidal.getBytes("\u8bf7\u8f93\u5165\u90ae\u5bc4\u7684\u91d1\u5e01\u6570", 0, 1000000000));
        data.add(inputString);
        PostBox.saleMenuOptionData[3] = data;
        data = new ArrayList<byte[]>();
        data.add(UI_InputDigidal.getBytes("\u8bf7\u8f93\u5165\u90ae\u5bc4\u6570\u91cf", 0, 20));
        data.add(inputString);
        PostBox.saleMenuOptionData[4] = null;
    }

    @Override
    public ENpcFunctionType getFunctionType() {
        return ENpcFunctionType.POST_BOX;
    }

    @Override
    public void initTopLayerOptionList() {
        for (int i = 0; i < PostBox.MAIN_MENU_LIST.length; ++i) {
            NpcHandshakeOptionData data = new NpcHandshakeOptionData();
            data.miniImageID = this.getMinMarkIconID();
            data.optionDesc = PostBox.MAIN_MENU_LIST[i];
            data.functionMark = this.getFunctionType().value() * 100000 + i;
            this.optionList.add(data);
        }
    }

    private String stringLengthVerify(final short _len, final String _content) {
        String result = "";
        if (_content != null && _content.length() > _len) {
            result = _content.substring(0, _len - 1);
        } else {
            result = _content;
        }
        return result;
    }

    @Override
    public void process(final HeroPlayer _player, final byte _step, final int _selectIndex, final YOYOInputStream _content) throws Exception {
        if (Step.TOP.tag == _step) {
            if (_selectIndex == 0) {
                short page = 0;
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.MAIL_BOX.tag, UI_MailGoodsList.getBytes(page, MailService.getInstance().getMailList(_player.getUserID(), page), PostBox.MAIL_BOX_OPERTION_LIST)));
            } else if (1 == _selectIndex) {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.SEND_CATEGORY.tag, UI_SelectOperation.getBytes("\u8bf7\u9009\u62e9\u90ae\u5bc4\u7269\u54c1\u7684\u79cd\u7c7b", PostBox.SALE_MENU_LIST, PostBox.saleMenuOptionData)));
            }
        } else if (Step.MAIL_BOX.tag == _step) {
            byte optionIndex = _content.readByte();
            int mailID = _content.readInt();
            short page2 = _content.readShort();
            if (optionIndex == 0) {
                Mail mail = MailService.getInstance().getMail(_player.getUserID(), mailID);
                if (mail != null) {
                    if (mail.getMoney() <= 0 && mail.getEquipment() == null && mail.getSingleGoods() == null) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6ca1\u6709\u9644\u4ef6\uff01"));
                        return;
                    }
                    boolean addFinish = false;
                    if (mail.getType() == 0) {
                        addFinish = PlayerServiceImpl.getInstance().addMoney(_player, mail.getMoney(), 1.0f, 2, "\u63d0\u53d6\u9644\u4ef6\u91d1\u5e01");
                    } else if (mail.getType() == 1) {
                        addFinish = ChargeServiceImpl.getInstance().updatePointAmount(_player, mail.getGamePoint());
                    } else {
                        short[] addSuccessful = null;
                        if (mail.getType() == 3) {
                            addSuccessful = GoodsServiceImpl.getInstance().addEquipmentInstance2Bag(_player, mail.getEquipment(), CauseLog.MAIL);
                        } else {
                            Goods goods = mail.getSingleGoods();
                            addSuccessful = GoodsServiceImpl.getInstance().addGoods2Package(_player, goods, mail.getSingleGoodsNumber(), CauseLog.MAIL);
                        }
                        if (addSuccessful == null && addFinish) {
                            return;
                        }
                    }
                    MailService.getInstance().removeAttachment(_player.getUserID(), mailID);
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new MailStatusChanges(MailStatusChanges.TYPE_OF_POST_BOX, false));
                } else {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6307\u5b9a\u7684\u90ae\u4ef6\u4e0d\u5b58\u5728\uff01"));
                }
            } else if (optionIndex == 1) {
                if (page2 == 0) {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6ca1\u6709\u4e0a\u4e00\u9875\u4e86"));
                    return;
                }
                --page2;
                List<Mail> mailsList = MailService.getInstance().getMailList(_player.getUserID(), page2);
                if (mailsList.size() == 0) {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6ca1\u6709\u4e0a\u4e00\u9875\u4e86"));
                } else {
                    NpcInteractiveResponse msg = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.MAIL_BOX.tag, UI_MailGoodsList.getBytes(page2, mailsList, PostBox.MAIL_BOX_OPERTION_LIST));
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);
                }
            } else if (optionIndex == 2) {
                ++page2;
                List<Mail> mailsList = MailService.getInstance().getMailList(_player.getUserID(), page2);
                if (mailsList.size() == 0) {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6ca1\u6709\u4e0b\u4e00\u9875\u4e86"));
                } else {
                    NpcInteractiveResponse msg = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(_selectIndex).functionMark, Step.MAIL_BOX.tag, UI_MailGoodsList.getBytes(page2, mailsList, PostBox.MAIL_BOX_OPERTION_LIST));
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg);
                }
            } else if (optionIndex == 3) {
                boolean result = MailService.getInstance().removeMail(_player.getUserID(), mailID);
                if (result) {
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u5220\u9664\u90ae\u4ef6\u6210\u529f"));
                }
            } else if (optionIndex == 4) {
                MailService.getInstance().readMail(_player.getUserID(), mailID);
            }
        } else if (Step.SEND_CATEGORY.tag == _step) {
            byte index = _content.readByte();
            NpcInteractiveResponse msg2 = null;
            if (index == 3) {
                int money = _content.readInt();
                String nickname = _content.readUTF();
                String title = this.stringLengthVerify((short) 20, _content.readUTF());
                if (title == "") {
                    title = "\u65b0\u90ae\u4ef6";
                }
                String content = this.stringLengthVerify((short) 200, _content.readUTF());
                this.addNewMail(_player, (byte) 0, _step, (byte) 1, money, 1, nickname, title, content);
                return;
            }
            if (index == 5) {
                String nickname2 = _content.readUTF();
                String title2 = this.stringLengthVerify((short) 20, _content.readUTF());
                if (title2 == "") {
                    title2 = "\u65b0\u90ae\u4ef6";
                }
                String content2 = this.stringLengthVerify((short) 200, _content.readUTF());
                this.addNewMail(_player, (byte) 4, _step, (byte) 1, 0, 1, nickname2, title2, content2);
                return;
            }
            if (index == 6) {
                int gamePoint = _content.readInt();
                String nickname = _content.readUTF();
                String title = this.stringLengthVerify((short) 20, _content.readUTF());
                if (title == "") {
                    title = "\u65b0\u90ae\u4ef6";
                }
                String content = this.stringLengthVerify((short) 200, _content.readUTF());
                this.addNewMail(_player, (byte) 1, _step, (byte) 1, gamePoint, 1, nickname, title, content);
                return;
            }
            if (index == 0) {
                msg2 = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(1).functionMark, Step.SEND_EQUIPMENT.tag, UI_GoodsListWithOperation.getData(Tip.FUNCTION_MAIL_SALE_OPERTION_LIST, PostBox.saleEquipmentOptionData, _player.getInventory().getEquipmentBag(), GoodsServiceImpl.getInstance().getConfig().equipment_bag_tab_name));
            } else if (index == 1) {
                msg2 = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(1).functionMark, Step.SEND_XHDJ.tag, UI_GoodsListWithOperation.getBytes(Tip.FUNCTION_MAIL_SALE_OPERTION_LIST, PostBox.saleSingleGoodsOptionData, _player.getInventory().getMedicamentBag(), GoodsServiceImpl.getInstance().getConfig().medicament_bag_tab_name));
            } else if (index == 2) {
                msg2 = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(1).functionMark, Step.SEND_CL.tag, UI_GoodsListWithOperation.getBytes(Tip.FUNCTION_MAIL_SALE_OPERTION_LIST, PostBox.saleSingleGoodsOptionData, _player.getInventory().getMaterialBag(), GoodsServiceImpl.getInstance().getConfig().material_bag_tab_name));
            } else if (index == 4) {
                msg2 = new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(1).functionMark, Step.SEND_TSDJ.tag, UI_GoodsListWithOperation.getBytes(Tip.FUNCTION_MAIL_SALE_OPERTION_LIST, PostBox.saleSingleGoodsOptionData, _player.getInventory().getSpecialGoodsBag(), GoodsServiceImpl.getInstance().getConfig().special_bag_tab_name));
            }
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), msg2);
        } else if (Step.SEND_EQUIPMENT.tag == _step) {
            byte optionIndex = _content.readByte();
            byte gridIndex = _content.readByte();
            int goodsID = _content.readInt();
            int num = _content.readInt();
            String nickname3 = _content.readUTF();
            String title3 = this.stringLengthVerify((short) 20, _content.readUTF());
            if (title3 == "") {
                title3 = "\u65b0\u90ae\u4ef6";
            }
            String content3 = this.stringLengthVerify((short) 200, _content.readUTF());
            this.addNewMail(_player, (byte) 3, _step, gridIndex, goodsID, num, nickname3, title3, content3);
        } else if (Step.SEND_XHDJ.tag == _step) {
            byte optionIndex = _content.readByte();
            byte gridIndex = _content.readByte();
            int goodsID = _content.readInt();
            int nums = _content.readInt();
            String nickname3 = _content.readUTF();
            String title3 = this.stringLengthVerify((short) 20, _content.readUTF());
            if (title3 == "") {
                title3 = "\u65b0\u90ae\u4ef6";
            }
            String content3 = this.stringLengthVerify((short) 200, _content.readUTF());
            this.addNewMail(_player, (byte) 2, _step, gridIndex, goodsID, nums, nickname3, title3, content3);
        } else if (Step.SEND_CL.tag == _step) {
            byte optionIndex = _content.readByte();
            byte gridIndex = _content.readByte();
            int goodsID = _content.readInt();
            int nums = _content.readInt();
            String nickname3 = _content.readUTF();
            String title3 = this.stringLengthVerify((short) 20, _content.readUTF());
            if (title3 == "") {
                title3 = "\u65b0\u90ae\u4ef6";
            }
            String content3 = this.stringLengthVerify((short) 200, _content.readUTF());
            this.addNewMail(_player, (byte) 2, _step, gridIndex, goodsID, nums, nickname3, title3, content3);
        } else if (Step.SEND_TSDJ.tag == _step) {
            byte optionIndex = _content.readByte();
            byte gridIndex = _content.readByte();
            int goodsID = _content.readInt();
            int nums = _content.readInt();
            String nickname3 = _content.readUTF();
            String title3 = this.stringLengthVerify((short) 20, _content.readUTF());
            if (title3 == "") {
                title3 = "\u65b0\u90ae\u4ef6";
            }
            String content3 = this.stringLengthVerify((short) 200, _content.readUTF());
            this.addNewMail(_player, (byte) 2, _step, gridIndex, goodsID, nums, nickname3, title3, content3);
        }
    }

    @Override
    public ArrayList<NpcHandshakeOptionData> getTopLayerOptionList(final HeroPlayer _player) {
        return this.optionList;
    }

    private boolean hasMoney(final HeroPlayer _player, final byte _mailType, final int _goodsID) {
        int needMoney = 1;
        if (_mailType == 0) {
            needMoney += _goodsID;
        }
        return _player.getMoney() >= needMoney;
    }

    private void addNewMail(final HeroPlayer _player, final byte _mailType, final byte _step, final byte _gridIndex, final int _goodsID, final int _number, final String _nickname, final String _title, final String _content) {
        HeroPlayer receiver = PlayerServiceImpl.getInstance().getPlayerByName(_nickname);
        int userID = 0;
        Date date = new Date(System.currentTimeMillis());
        byte social = MailService.getInstance().getSocial(_player.getName(), _nickname);
        if (receiver == null) {
            userID = PlayerServiceImpl.getInstance().getUserIDByNameFromDB(_nickname);
            if (userID == 0) {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u73a9\u5bb6\u2018" + _nickname + "\u2019\u4e0d\u5b58\u5728\uff0c\u65e0\u6cd5\u53d1\u9001\u90ae\u4ef6\uff01"));
                return;
            }
        } else {
            userID = receiver.getUserID();
        }
        boolean receiverPostBoxFull = false;
        if (_mailType == 0) {
            if (_goodsID > _player.getMoney()) {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u91d1\u94b1\u4e0d\u591f"));
                return;
            }
            Mail mail = new Mail(MailService.getInstance().getUseableMailID(), userID, _nickname, _player.getName(), (byte) 0, _goodsID, _content, _title, date, social);
            if (MailService.getInstance().addMail(mail, false)) {
                PlayerServiceImpl.getInstance().addMoney(_player, -_goodsID, 1.0f, 0, "\u90ae\u5bc4\u91d1\u5e01");
                LogServiceImpl.getInstance().mailLog(_player.getLoginInfo().accountID, _player.getUserID(), _player.getName(), _player.getLoginInfo().loginMsisdn, mail.getID(), userID, _nickname, _goodsID, 0, "");
            } else {
                receiverPostBoxFull = true;
            }
        } else if (_mailType == 4) {
            Mail mail = new Mail(MailService.getInstance().getUseableMailID(), userID, _nickname, _player.getName(), (byte) 4, _goodsID, _content, _title, date, social);
            if (MailService.getInstance().addMail(mail, false)) {
                LogServiceImpl.getInstance().mailLog(_player.getLoginInfo().accountID, _player.getUserID(), _player.getName(), _player.getLoginInfo().loginMsisdn, mail.getID(), userID, _nickname, 0, _goodsID, "");
            } else {
                receiverPostBoxFull = true;
            }
        } else if (_mailType == 1) {
            if (_goodsID > _player.getChargeInfo().pointAmount) {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6e38\u620f\u70b9\u6570\u4e0d\u591f"));
                return;
            }
            Mail mail = new Mail(MailService.getInstance().getUseableMailID(), userID, _nickname, _player.getName(), (byte) 1, _goodsID, _content, _title, date, social);
            if (MailService.getInstance().addMail(mail, false)) {
                ChargeServiceImpl.getInstance().updatePointAmount(_player, -_goodsID);
                LogServiceImpl.getInstance().mailLog(_player.getLoginInfo().accountID, _player.getUserID(), _player.getName(), _player.getLoginInfo().loginMsisdn, mail.getID(), userID, _nickname, 0, _goodsID, "");
            } else {
                receiverPostBoxFull = true;
            }
        } else {
            try {
                int maxNum = 1;
                if (Step.SEND_EQUIPMENT.tag == _step) {
                    EquipmentInstance ei = _player.getInventory().getEquipmentBag().getEquipmentList()[_gridIndex];
                    if (ei != null && ei.getInstanceID() == _goodsID) {
                        if (!ei.getArchetype().exchangeable()) {
                            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6b64\u7269\u54c1\u4e3a\u4e0d\u53ef\u4ea4\u6613\u7269\u54c1\uff0c\u4e0d\u80fd\u90ae\u5bc4"));
                            return;
                        }
                        if (-1 != GoodsServiceImpl.getInstance().removeEquipmentOfBag(_player, _player.getInventory().getEquipmentBag(), ei, CauseLog.MAIL)) {
                            Mail mail2 = new Mail(MailService.getInstance().getUseableMailID(), userID, _nickname, _player.getName(), (byte) 3, 0, (short) 0, ei, _content, _title, date, social);
                            if (MailService.getInstance().addMail(mail2, false)) {
                                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(1).functionMark, _step, UI_GridGoodsNumsChanged.getBytes(_gridIndex, _goodsID, maxNum - _number)));
                                LogServiceImpl.getInstance().mailLog(_player.getLoginInfo().accountID, _player.getUserID(), _player.getName(), _player.getLoginInfo().loginMsisdn, mail2.getID(), userID, _nickname, 0, 0, String.valueOf(_goodsID) + "," + ei.getArchetype().getName() + ",1");
                            } else {
                                receiverPostBoxFull = true;
                            }
                        }
                    }
                } else {
                    Goods _goods = GoodsContents.getGoods(_goodsID);
                    SingleGoodsBag goodsBag = null;
                    if (!_goods.exchangeable()) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u6b64\u7269\u54c1\u4e3a\u4e0d\u53ef\u4ea4\u6613\u7269\u54c1\uff0c\u4e0d\u80fd\u90ae\u5bc4"));
                        return;
                    }
                    if (Step.SEND_XHDJ.tag == _step) {
                        maxNum = _player.getInventory().getMedicamentBag().getAllItem()[_gridIndex][1];
                        goodsBag = _player.getInventory().getMedicamentBag();
                    } else if (Step.SEND_CL.tag == _step) {
                        maxNum = _player.getInventory().getMaterialBag().getAllItem()[_gridIndex][1];
                        goodsBag = _player.getInventory().getMaterialBag();
                    } else if (Step.SEND_TSDJ.tag == _step) {
                        maxNum = _player.getInventory().getSpecialGoodsBag().getAllItem()[_gridIndex][1];
                        goodsBag = _player.getInventory().getSpecialGoodsBag();
                    }
                    Mail mail3 = new Mail(MailService.getInstance().getUseableMailID(), userID, _nickname, _player.getName(), (byte) 2, _goods.getID(), (short) _number, null, _content, _title, date, social);
                    if (MailService.getInstance().addMail(mail3, false)) {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(1).functionMark, _step, UI_GridGoodsNumsChanged.getBytes(_gridIndex, _goodsID, maxNum - _number)));
                        GoodsServiceImpl.getInstance().reduceSingleGoods(_player, goodsBag, _gridIndex, _goodsID, _number, CauseLog.MAIL);
                        LogServiceImpl.getInstance().mailLog(_player.getLoginInfo().accountID, _player.getUserID(), _player.getName(), _player.getLoginInfo().loginMsisdn, mail3.getID(), userID, _nickname, 0, 0, String.valueOf(_goodsID) + "," + _goods.getName() + "," + _number);
                    }
                }
            } catch (BagException e) {
                e.printStackTrace();
                return;
            }
        }
        if (receiverPostBoxFull) {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u73a9\u5bb6\u2018" + _nickname + "\u2019\u5bf9\u65b9\u90ae\u7bb1\u5df2\u6ee1\uff0c\u65e0\u6cd5\u63a5\u6536\u90ae\u4ef6\uff01"));
        } else {
            ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u53d1\u9001\u90ae\u4ef6\u6210\u529f\uff01"));
            if (receiver != null && receiver.isEnable()) {
                ResponseMessageQueue.getInstance().put(receiver.getMsgQueueIndex(), new Warning("\u60a8\u6709\u4e00\u5c01\u65b0\u7684\u90ae\u4ef6\uff0c\u5feb\u53bb\u90ae\u7bb1\u67e5\u6536\u5427"));
                ResponseMessageQueue.getInstance().put(receiver.getMsgQueueIndex(), new MailStatusChanges(MailStatusChanges.TYPE_OF_POST_BOX, true));
            }
        }
    }

    enum Step {
        TOP("TOP", 0, 1),
        MAIL_BOX("MAIL_BOX", 1, 10),
        MAIL_DEL("MAIL_DEL", 2, 11),
        MAIL_READ("MAIL_READ", 3, 12),
        SEND_CATEGORY("SEND_CATEGORY", 4, 20),
        SEND_EQUIPMENT("SEND_EQUIPMENT", 5, 21),
        SEND_XHDJ("SEND_XHDJ", 6, 22),
        SEND_CL("SEND_CL", 7, 23),
        SEND_TSDJ("SEND_TSDJ", 8, 24);

        byte tag;

        private Step(final String name, final int ordinal, final int _tag) {
            this.tag = (byte) _tag;
        }
    }
}
