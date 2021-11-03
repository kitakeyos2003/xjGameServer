// 
// Decompiled by Procyon v0.5.36
// 
package hero.npc.dict;

import java.util.Iterator;
import org.dom4j.Document;
import hero.item.dictionary.GoodsContents;
import hero.item.Goods;
import java.util.HashMap;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import javolution.util.FastMap;
import java.util.ArrayList;
import java.util.Random;

public class QuestionDict {

    private static QuestionDict instance;
    private static final Random RANDOM;
    private ArrayList<AnswerQuestionData> anwserQuestion;
    private FastMap<Integer, AwardData> awardDict;
    private Hashtable<Integer, Hashtable<Integer, QuestionData>> questionDict;
    public static final int REFRESH_TYPE_DAY = 1;
    public static final int REFRESH_TYPE_TIME = 2;

    static {
        RANDOM = new Random();
    }

    private QuestionDict() {
        this.awardDict = (FastMap<Integer, AwardData>) new FastMap();
        this.anwserQuestion = new ArrayList<AnswerQuestionData>();
        this.questionDict = new Hashtable<Integer, Hashtable<Integer, QuestionData>>();
    }

    public String[] getAnwserQuestionNames() {
        String[] functionName = new String[this.anwserQuestion.size()];
        for (int i = 0; i < this.anwserQuestion.size(); ++i) {
            functionName[i] = this.anwserQuestion.get(i).name;
        }
        return functionName;
    }

    public int[] getAnwserQuestionIDs() {
        int[] IDs = new int[this.anwserQuestion.size()];
        for (int i = 0; i < this.anwserQuestion.size(); ++i) {
            IDs[i] = i;
        }
        return IDs;
    }

    public AnswerQuestionData getAnswerQuestionData(final int _groupID) {
        AnswerQuestionData questionGroup = new AnswerQuestionData();
        try {
            AnswerQuestionData aqData = this.anwserQuestion.get(_groupID);
            aqData.question = new ArrayList<QuestionData>();
            Hashtable<Integer, QuestionData> data = new Hashtable<Integer, QuestionData>();
            data = this.questionDict.get(aqData.id);
            Enumeration<Integer> keys = data.keys();
            ArrayList<Integer> keyList = new ArrayList<Integer>();
            while (keys.hasMoreElements()) {
                keyList.add(keys.nextElement());
            }
            ArrayList<Integer> randomIndex = new ArrayList<Integer>();
            int size = keyList.size();
            int index = 0;
            while (randomIndex.size() < aqData.questionSum) {
                index = QuestionDict.RANDOM.nextInt(size);
                if (!randomIndex.contains(index)) {
                    randomIndex.add(index);
                }
            }
            for (int i = 0; i < randomIndex.size(); ++i) {
                QuestionData question = data.get(keyList.get(randomIndex.get(i)));
                if (question == null) {
                    System.out.println("\u5f97\u5230question\u4e3aNULL,\u8fd9\u4e0d\u5e94\u8be5\u53d1\u751f.");
                }
                aqData.question.add(question);
            }
            questionGroup.award = aqData.award;
            questionGroup.awardID = aqData.awardID;
            questionGroup.endTime = aqData.endTime;
            questionGroup.id = aqData.id;
            questionGroup.isOpen = aqData.isOpen;
            questionGroup.name = aqData.name;
            questionGroup.point = aqData.point;
            questionGroup.question = aqData.question;
            questionGroup.questionSum = aqData.questionSum;
            questionGroup.refreshDay = aqData.refreshDay;
            questionGroup.refreshTimeSum = aqData.refreshTimeSum;
            questionGroup.refreshType = aqData.refreshType;
            questionGroup.startTime = aqData.startTime;
            questionGroup.sumPoint = 0;
            questionGroup.step = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return questionGroup;
    }

    public static void main(final String[] args) {
        getInstance().load(String.valueOf(System.getProperty("user.dir")) + "/" + "res/data/npc/normal_npc/fun_data/anwser_question/", String.valueOf(System.getProperty("user.dir")) + "/" + "res/data/npc/normal_npc/fun_data/question/", String.valueOf(System.getProperty("user.dir")) + "/" + "res/data/npc/normal_npc/fun_data/award/");
        getInstance().getAnswerQuestionData(1);
    }

    public static QuestionDict getInstance() {
        if (QuestionDict.instance == null) {
            QuestionDict.instance = new QuestionDict();
        }
        return QuestionDict.instance;
    }

    private boolean questionLoad(final int _questionID) {
        boolean result = false;
        for (int i = 0; i < this.anwserQuestion.size(); ++i) {
            if (this.anwserQuestion.get(i).id == _questionID) {
                result = true;
            }
        }
        return result;
    }

    private void loadAward(final String _dataPath) {
        try {
            File dataPath = new File(_dataPath);
            File[] dataFileList = dataPath.listFiles();
            File[] array;
            for (int length = (array = dataFileList).length, j = 0; j < length; ++j) {
                File dataFile = array[j];
                if (dataFile.getName().endsWith(".xml")) {
                    SAXReader reader = new SAXReader();
                    Document document = reader.read(dataFile);
                    Element rootE = document.getRootElement();
                    Iterator<Element> rootIt = (Iterator<Element>) rootE.elementIterator();
                    while (rootIt.hasNext()) {
                        Element subE = rootIt.next();
                        if (subE != null) {
                            AwardData awardData = new AwardData();
                            awardData.id = Integer.valueOf(subE.elementTextTrim("id"));
                            awardData.name = subE.elementTextTrim("name");
                            awardData.goodsSum = Integer.valueOf(subE.elementTextTrim("goodsSum"));
                            awardData.goodsMap = new HashMap<Integer, Goods>(awardData.goodsSum);
                            int point = 0;
                            int index = 0;
                            int goodsID = 0;
                            for (int i = 0; i < awardData.goodsSum; ++i) {
                                index = i + 1;
                                point = Integer.valueOf(subE.elementTextTrim("goods" + index + "Point"));
                                goodsID = Integer.valueOf(subE.elementTextTrim("goods" + index + "ID"));
                                Goods goods = GoodsContents.getGoods(goodsID);
                                if (!awardData.goodsMap.containsKey(point) && goods != null) {
                                    awardData.goodsMap.put(point, goods);
                                }
                            }
                            if (this.awardDict.containsKey(awardData.id)) {
                                continue;
                            }
                            this.awardDict.put(awardData.id, awardData);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadQuestion(final String _dataPath) {
        try {
            File dataPath = new File(_dataPath);
            File[] dataFileList = dataPath.listFiles();
            File[] array;
            for (int length = (array = dataFileList).length, j = 0; j < length; ++j) {
                File dataFile = array[j];
                if (dataFile.getName().endsWith(".xml")) {
                    SAXReader reader = new SAXReader();
                    Document document = reader.read(dataFile);
                    Element rootE = document.getRootElement();
                    Iterator<Element> rootIt = (Iterator<Element>) rootE.elementIterator();
                    while (rootIt.hasNext()) {
                        Element subE = rootIt.next();
                        if (subE != null) {
                            QuestionData questionData = new QuestionData();
                            questionData.aid = Integer.valueOf(subE.elementTextTrim("aid"));
                            questionData.answerQuestionID = Integer.valueOf(subE.elementTextTrim("answerQuestionID"));
                            if (!this.questionLoad(questionData.answerQuestionID)) {
                                continue;
                            }
                            questionData.answerSum = Integer.valueOf(subE.elementTextTrim("answerSum"));
                            questionData.answerList = new ArrayList<String>(questionData.answerSum);
                            questionData.rightAnswer = Integer.valueOf(subE.elementTextTrim("rightAnswer"));
                            questionData.question = subE.elementTextTrim("question");
                            int index = 0;
                            String answer = "";
                            for (int i = 0; i < questionData.answerSum; ++i) {
                                index = i + 1;
                                answer = subE.elementTextTrim("answer" + index + "Content");
                                if (answer != null) {
                                    questionData.answerList.add(answer);
                                }
                            }
                            if (this.questionDict.containsKey(questionData.answerQuestionID)) {
                                this.questionDict.get(questionData.answerQuestionID).put(questionData.aid, questionData);
                            } else {
                                Hashtable<Integer, QuestionData> data = new Hashtable<Integer, QuestionData>();
                                data.put(questionData.aid, questionData);
                                this.questionDict.put(questionData.answerQuestionID, data);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load(final String _funPath, final String _questionPath, final String _awardPath) {
        this.loadAward(_awardPath);
        try {
            File dataPath = new File(_funPath);
            File[] dataFileList = dataPath.listFiles();
            File[] array;
            for (int length = (array = dataFileList).length, j = 0; j < length; ++j) {
                File dataFile = array[j];
                if (dataFile.getName().endsWith(".xml")) {
                    SAXReader reader = new SAXReader();
                    Document document = reader.read(dataFile);
                    Element rootE = document.getRootElement();
                    Iterator<Element> rootIt = (Iterator<Element>) rootE.elementIterator();
                    while (rootIt.hasNext()) {
                        Element subE = rootIt.next();
                        if (subE != null) {
                            AnswerQuestionData functionData = new AnswerQuestionData();
                            functionData.id = Integer.valueOf(subE.elementTextTrim("id"));
                            functionData.name = subE.elementTextTrim("name");
                            if (subE.elementTextTrim("isOpen").equals("\u662f")) {
                                functionData.isOpen = true;
                            } else {
                                functionData.isOpen = false;
                            }
                            if (!functionData.isOpen) {
                                continue;
                            }
                            functionData.questionSum = Integer.valueOf(subE.elementTextTrim("questionSum"));
                            functionData.point = Integer.valueOf(subE.elementTextTrim("point"));
                            functionData.awardID = Integer.valueOf(subE.elementTextTrim("awardID"));
                            functionData.award = (AwardData) this.awardDict.get(functionData.awardID);
                            functionData.refreshType = Integer.valueOf(subE.elementTextTrim("refreshType"));
                            if (functionData.refreshType == 1) {
                                functionData.refreshDay = Integer.valueOf(subE.elementTextTrim("refreshDay"));
                            } else if (functionData.refreshType == 2) {
                                functionData.refreshTimeSum = Integer.valueOf(subE.elementTextTrim("refreshTimeSum"));
                                functionData.startTime = new String[functionData.refreshTimeSum];
                                functionData.endTime = new String[functionData.refreshTimeSum];
                                int index = 0;
                                for (int i = 0; i < functionData.refreshTimeSum; ++i) {
                                    index = i + 1;
                                    functionData.startTime[i] = subE.elementTextTrim("refresh" + index + "StartTime");
                                    functionData.endTime[i] = subE.elementTextTrim("refresh" + index + "EndTime");
                                }
                            }
                            this.anwserQuestion.add(functionData);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.loadQuestion(_questionPath);
    }

    public static class AwardData {

        public int id;
        public int goodsSum;
        public HashMap<Integer, Goods> goodsMap;
        public String name;
    }

    public static class QuestionData {

        public int aid;
        public int answerQuestionID;
        public int answerSum;
        public int rightAnswer;
        public ArrayList<String> answerList;
        public String question;
    }
}
