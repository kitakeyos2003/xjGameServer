// 
// Decompiled by Procyon v0.5.36
// 
package hero.charge.service;

import java.util.Iterator;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import javolution.util.FastMap;
import org.dom4j.Element;
import java.io.File;
import org.dom4j.io.SAXReader;
import yoyo.service.YOYOSystem;

public class CostToPointConfig {

    private static CostToPointConfig instance;
    private Card[] cards;
    public static String COST_TO_POINT_FILE;

    static {
        CostToPointConfig.COST_TO_POINT_FILE = String.valueOf(YOYOSystem.HOME) + "res/config/charge/cost_to_point.xml";
    }

    public CostToPointConfig() {
        this.cards = null;
        SAXReader reader = new SAXReader();
        try {
            Document dom = reader.read(new File(CostToPointConfig.COST_TO_POINT_FILE));
            Element root = dom.getRootElement();
            Element eCardType = root.element("card_type");
            if (eCardType != null) {
                List<Element> list = (List<Element>) eCardType.elements("card");
                int length = list.size();
                if (length > 0) {
                    this.cards = new Card[length];
                    int index = 0;
                    for (final Element e : list) {
                        this.cards[index] = new Card();
                        this.cards[index].id = Integer.parseInt(e.attributeValue("id"));
                        this.cards[index].name = e.attributeValue("name");
                        this.cards[index].tip = e.attributeValue("tip");
                        List<Element> ePoints = (List<Element>) e.elements("point");
                        int lenPoint = ePoints.size();
                        if (lenPoint > 0) {
                            this.cards[index].points = (FastMap<Integer, Integer>) new FastMap();
                            for (final Element ep : ePoints) {
                                this.cards[index].points.put(Integer.parseInt(ep.attributeValue("amount")), Integer.parseInt(ep.getTextTrim()));
                            }
                        }
                        ++index;
                    }
                }
            }
        } catch (DocumentException e2) {
            e2.printStackTrace();
        }
    }

    public static CostToPointConfig getInstance() {
        if (CostToPointConfig.instance == null) {
            CostToPointConfig.instance = new CostToPointConfig();
        }
        return CostToPointConfig.instance;
    }

    public String[] getChargeCardPoint(final int _cardID, final int _amount) {
        String[] result = new String[2];
        if (this.cards != null && this.cards.length > 0) {
            for (int cardLen = this.cards.length, i = 0; i < cardLen; ++i) {
                if (this.cards[i].id == _cardID) {
                    result[0] = this.cards[i].tip;
                    if (this.cards[i].points != null && this.cards[i].points.size() > 0) {
                        int pointLen = this.cards[i].points.size();
                        int j = 0;
                        if (j < pointLen) {
                            if (this.cards[i].points.containsKey((Object) _amount)) {
                            }
                            result[1] = String.valueOf(this.cards[i].points.get((Object) _amount));
                            break;
                        }
                        break;
                    }
                }
            }
        }
        return result;
    }

    public class Card {

        public int id;
        public String name;
        public String tip;
        public FastMap<Integer, Integer> points;
    }
}
