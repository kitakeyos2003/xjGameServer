// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.function.system;

import hero.npc.dict.AnswerQuestionData;
import hero.log.service.CauseLog;
import hero.item.service.GoodsServiceImpl;
import hero.pet.service.PetServiceImpl;
import hero.item.detail.EGoodsType;
import hero.npc.message.NpcInteractiveResponse;
import hero.ui.UI_AnswerQuestion;
import yoyo.core.packet.AbsResponseMessage;
import hero.share.message.Warning;
import yoyo.core.queue.ResponseMessageQueue;
import hero.npc.service.NotPlayerServiceImpl;
import yoyo.tools.YOYOInputStream;
import java.util.Iterator;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import hero.item.Goods;
import hero.npc.dict.QuestionDict;
import hero.player.HeroPlayer;
import hero.npc.function.ENpcFunctionType;
import hero.npc.detail.NpcHandshakeOptionData;
import java.util.ArrayList;
import hero.npc.function.BaseNpcFunction;

public class AnswerQuestion extends BaseNpcFunction {

    protected ArrayList<NpcHandshakeOptionData> optionList;

    public AnswerQuestion(final int npcID) {
        super(npcID);
    }

    @Override
    public ENpcFunctionType getFunctionType() {
        return ENpcFunctionType.ANSWER_QUESTION;
    }

    @Override
    public ArrayList<NpcHandshakeOptionData> getTopLayerOptionList(final HeroPlayer _player) {
        return this.optionList;
    }

    @Override
    public void initTopLayerOptionList() {
        String[] optionName = QuestionDict.getInstance().getAnwserQuestionNames();
        this.optionList = new ArrayList<NpcHandshakeOptionData>();
        for (int i = 0; i < optionName.length; ++i) {
            NpcHandshakeOptionData data = new NpcHandshakeOptionData();
            data.miniImageID = this.getMinMarkIconID();
            data.optionDesc = optionName[i];
            data.functionMark = this.getFunctionType().value() * 100000 + i;
            this.optionList.add(data);
        }
    }

    private Goods getGoods(final HeroPlayer _player, final QuestionDict.AwardData award) {
        Goods goods = null;
        Iterator<Map.Entry<Integer, Goods>> iterator = award.goodsMap.entrySet().iterator();
        ArrayList<Integer> keyList = new ArrayList<Integer>();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Goods> entry = iterator.next();
            int key = entry.getKey();
            keyList.add(key);
        }
        Integer[] keys = new Integer[keyList.size()];
        for (int i = 0; i < keys.length; ++i) {
            keys[i] = keyList.get(i);
        }
        Arrays.sort(keys, Collections.reverseOrder());
        for (int i = 0; i < keys.length; ++i) {
            if (_player.questionGroup.sumPoint >= keys[i]) {
                goods = award.goodsMap.get(keys[i]);
                break;
            }
        }
        return goods;
    }

    @Override
    public void process(final HeroPlayer _player, final byte _step, final int selectIndex, final YOYOInputStream _content) throws Exception {
        if (_step == Step.TOP.tag) {
            if (!NotPlayerServiceImpl.getInstance().isInTime(selectIndex)) {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u7b54\u9898\u65f6\u95f4\u672a\u5230", (byte) 1));
                return;
            }
            if (NotPlayerServiceImpl.getInstance().isJoinQuestion(selectIndex, _player.getUserID())) {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new Warning("\u5df2\u53c2\u4e0e\u4e86\u8be5\u6d3b\u52a8,\u8bf7\u7b49\u5f85\u4e0b\u6b21\u673a\u4f1a", (byte) 1));
                return;
            }
            _player.questionGroup = QuestionDict.getInstance().getAnswerQuestionData(selectIndex);
            if (_player.questionGroup.question.size() > 0) {
                QuestionDict.QuestionData question = _player.questionGroup.question.get(0);
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(selectIndex).functionMark, Step.ANSWER.tag, UI_AnswerQuestion.getBytes(question.question, question.answerList, "\u5f97\u5206\uff1a0\u5206")));
            }
            _player.questionGroup.step = 1;
            NotPlayerServiceImpl.getInstance().joinQuestion(selectIndex, _player.getUserID());
        } else if (_step == Step.ANSWER.tag && _player.questionGroup != null) {
            int answer = _content.readByte() + 1;
            String title = "";
            QuestionDict.QuestionData question2 = _player.questionGroup.question.get(_player.questionGroup.step - 1);
            if (question2.rightAnswer == answer) {
                AnswerQuestionData questionGroup = _player.questionGroup;
                questionGroup.sumPoint += _player.questionGroup.point;
                title = "\u4e0a\u9898\u6b63\u786e   \u7d2f\u8ba1\u5f97\u5206\uff1a" + _player.questionGroup.sumPoint + "\u5206";
            } else {
                title = "\u4e0a\u9898\u9519\u8bef   \u7d2f\u8ba1\u5f97\u5206\uff1a" + _player.questionGroup.sumPoint + "\u5206";
            }
            if (_player.questionGroup.question.size() > 0) {
                if (_player.questionGroup.step >= _player.questionGroup.questionSum) {
                    QuestionDict.AwardData award = _player.questionGroup.award;
                    Goods goods = this.getGoods(_player, award);
                    if (goods != null) {
                        if (goods.getGoodsType() == EGoodsType.PET) {
                            PetServiceImpl.getInstance().addPet(_player.getUserID(), goods.getID());
                        } else {
                            GoodsServiceImpl.getInstance().addGoods2Package(_player, goods.getID(), 1, CauseLog.ANSWERQUESTION);
                        }
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(selectIndex).functionMark, Step.END.tag, UI_AnswerQuestion.getEndBytes("\u7b54\u9898\u7ed3\u675f,\u606d\u559c\u60a8\u83b7\u5f97", goods.getName(), title)));
                        _player.questionGroup = null;
                    } else {
                        ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(selectIndex).functionMark, Step.END.tag, UI_AnswerQuestion.getEndBytes("\u7b54\u9898\u7ed3\u675f,\u4f60\u6ca1\u80fd\u83b7\u5f97\u5956\u52b1\uff0c\u4e0b\u6b21\u518d\u52aa\u529b\u5427", "", title)));
                    }
                } else {
                    question2 = _player.questionGroup.question.get(_player.questionGroup.step);
                    ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(selectIndex).functionMark, Step.ANSWER.tag, UI_AnswerQuestion.getBytes(question2.question, question2.answerList, title)));
                    AnswerQuestionData questionGroup2 = _player.questionGroup;
                    ++questionGroup2.step;
                }
            }
        } else if (_step == Step.END.tag && _player.questionGroup != null) {
            String title2 = "\u7d2f\u8ba1\u5f97\u5206\uff1a" + _player.questionGroup.sumPoint + "\u5206";
            QuestionDict.AwardData award2 = _player.questionGroup.award;
            Goods goods2 = this.getGoods(_player, award2);
            if (goods2 != null) {
                if (goods2.getGoodsType() == EGoodsType.PET) {
                    PetServiceImpl.getInstance().addPet(_player.getUserID(), goods2.getID());
                } else {
                    GoodsServiceImpl.getInstance().addGoods2Package(_player, goods2.getID(), 1, CauseLog.ANSWERQUESTION);
                }
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(selectIndex).functionMark, Step.END.tag, UI_AnswerQuestion.getEndBytes("\u7b54\u9898\u7ed3\u675f,\u606d\u559c\u60a8\u83b7\u5f97", goods2.getName(), title2)));
                _player.questionGroup = null;
            } else {
                ResponseMessageQueue.getInstance().put(_player.getMsgQueueIndex(), new NpcInteractiveResponse(this.getHostNpcID(), this.optionList.get(selectIndex).functionMark, Step.END.tag, UI_AnswerQuestion.getEndBytes("\u7b54\u9898\u7ed3\u675f,\u4f60\u6ca1\u80fd\u83b7\u5f97\u5956\u52b1\uff0c\u4e0b\u6b21\u518d\u52aa\u529b\u5427", "", title2)));
            }
        }
    }

    enum Step {
        TOP("TOP", 0, 1),
        ANSWER("ANSWER", 1, 2),
        END("END", 2, 3),
        CLOSE("CLOSE", 3, 4);

        byte tag;

        private Step(final String name, final int ordinal, final int _tag) {
            this.tag = (byte) _tag;
        }
    }
}
