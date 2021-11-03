// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.bag;

import yoyo.core.packet.AbsResponseMessage;
import yoyo.core.queue.ResponseMessageQueue;
import hero.player.message.HotKeySumByMedicament;
import hero.item.detail.EGoodsType;
import hero.player.service.PlayerServiceImpl;
import java.util.ArrayList;
import hero.item.service.GoodsServiceImpl;
import hero.item.bag.exception.PackageExceptionFactory;
import hero.item.bag.exception.BagException;
import hero.item.dictionary.GoodsContents;
import hero.item.SingleGoods;
import hero.item.SpecialGoods;
import java.util.Enumeration;
import hero.item.special.PetPerCard;
import hero.item.special.BigTonicBall;
import java.util.Hashtable;
import hero.player.HeroPlayer;
import org.apache.log4j.Logger;

public class SingleGoodsBag {

    private static Logger log;
    private HeroPlayer master;
    private short emptyGridNumber;
    private int size;
    private int[][] items;
    public Hashtable<Integer, BigTonicBall> tonicList;
    public Hashtable<Integer, PetPerCard> petPerCardList;

    static {
        SingleGoodsBag.log = Logger.getLogger((Class) SingleGoodsBag.class);
    }

    public SingleGoodsBag(final short _size) {
        this.size = _size;
        this.emptyGridNumber = _size;
        this.items = new int[_size][2];
        this.tonicList = new Hashtable<Integer, BigTonicBall>(500);
        this.petPerCardList = new Hashtable<Integer, PetPerCard>(500);
    }

    public boolean usePetPerCard(int index, final int _goodsID, final HeroPlayer _player) {
        boolean useUp = false;
        PetPerCard card = this.petPerCardList.get(index);
        if (index == -1) {
            Enumeration<Integer> keys = this.petPerCardList.keys();
            if (keys.hasMoreElements()) {
                index = keys.nextElement();
                card = this.petPerCardList.get(index);
            }
        }
        useUp = card.beUse(_player, _player, index);
        return useUp;
    }

    public boolean eatTonicBall(int index, final int _goodsID, final HeroPlayer _player) {
        boolean useUp = false;
        BigTonicBall ball = this.tonicList.get(index);
        if (index == -1) {
            BigTonicBall reference = new BigTonicBall(_goodsID, (short) 1);
            Enumeration<Integer> keys = this.tonicList.keys();
            while (keys.hasMoreElements()) {
                index = keys.nextElement();
                ball = this.tonicList.get(index);
                if (ball.isActivate == 2 && ball.tonincType == reference.tonincType) {
                    break;
                }
            }
        }
        useUp = ball.beUse(_player, _player, index);
        return useUp;
    }

    public boolean installTonicBall(int index, final HeroPlayer _player) {
        boolean useUp = false;
        BigTonicBall ball = this.tonicList.get(index);
        if (index == -1) {
            Enumeration<Integer> keys = this.tonicList.keys();
            while (keys.hasMoreElements()) {
                index = keys.nextElement();
                ball = this.tonicList.get(index);
                if (ball.isActivate != 2) {
                    break;
                }
            }
        }
        useUp = ball.beUse(_player, _player, index);
        return useUp;
    }

    public int getSize() {
        return this.size;
    }

    public int getEmptyGridNumber() {
        return this.emptyGridNumber;
    }

    public int getFullGridNumber() {
        return this.size - this.emptyGridNumber;
    }

    public void load(final int _goodsID, final int _number, final int _bagIndex) {
        if (this.items[_bagIndex][0] == 0) {
            --this.emptyGridNumber;
        }
        this.items[_bagIndex][0] = _goodsID;
        this.items[_bagIndex][1] = _number;
    }

    public void loadBigTonicBall(final int _goodsID, final int _number, final int _bagIndex, final int _surplus, final int _type, final SpecialGoods _specialGoods) {
        if (_specialGoods != null) {
            BigTonicBall ball = new BigTonicBall(_specialGoods.getID(), (short) 1);
            ball.initData(_surplus, _type, _bagIndex);
            ball.copyGoodsData(_specialGoods, this.master);
            this.tonicList.put(_bagIndex, ball);
        }
    }

    public void loadBigPetPerCard(final int _goodsID, final int _number, final int _bagIndex, final int _surplus, final SpecialGoods _specialGoods) {
        if (_specialGoods != null) {
            PetPerCard card = new PetPerCard(_specialGoods.getID(), (short) 1);
            card.initData(_surplus, _bagIndex);
            card.copyGoodsData(_specialGoods);
            this.petPerCardList.put(_bagIndex, card);
        }
    }

    protected short[] add(final int _goodsID, final int _number) throws BagException {
        return this.add((SingleGoods) GoodsContents.getGoods(_goodsID), _number);
    }

    public int getFirstEmptyGridIndex() {
        int result = 0;
        int emptyGridIndex = -1;
        synchronized (this.items) {
            for (int i = 0; i < this.size; ++i) {
                if (this.items[i][0] == 0) {
                    emptyGridIndex = i;
                    break;
                }
            }
        }
        // monitorexit(this.items)
        result = emptyGridIndex;
        return result;
    }

    public short[] add(final SingleGoods _goods, final int _number) throws BagException {
        if (_goods == null || _number <= 0) {
            throw PackageExceptionFactory.getInstance().getException("\u6dfb\u52a0\u65e0\u6548\u7269\u54c1\u6570\u636e");
        }
        if (_number > _goods.getMaxStackNums()) {
            throw PackageExceptionFactory.getInstance().getException("\u4e00\u6b21\u6027\u6dfb\u52a0\u7684\u7269\u54c1\u6570\u91cf\u4e0d\u80fd\u8d85\u8fc7\u53ef\u53e0\u52a0\u7684\u6700\u5927\u6570\u91cf");
        }
        int emptyGridIndex = -1;
        synchronized (this.items) {
            for (int i = 0; i < this.size; ++i) {
                if (this.items[i][0] == 0) {
                    if (emptyGridIndex == -1) {
                        emptyGridIndex = i;
                    }
                } else if (this.items[i][0] == _goods.getID() && _number <= _goods.getMaxStackNums() - this.items[i][1]) {
                    int[] array = this.items[i];
                    int n = 1;
                    array[n] += _number;
                    // monitorexit(this.items)
                    return new short[]{(short) i, (short) this.items[i][1]};
                }
            }
            if (-1 == emptyGridIndex) {
                throw PackageExceptionFactory.getInstance().getFullException(_goods.getGoodsType());
            }
            this.items[emptyGridIndex][0] = _goods.getID();
            this.items[emptyGridIndex][1] = _number;
            if (_goods instanceof BigTonicBall) {
                BigTonicBall ball = new BigTonicBall(_goods.getID(), (short) 1);
                ball.copyGoodsData((SpecialGoods) _goods, this.master);
                this.tonicList.put(emptyGridIndex, ball);
            }
            if (_goods instanceof PetPerCard) {
                PetPerCard card = new PetPerCard(_goods.getID(), (short) 1);
                card.copyGoodsData((SpecialGoods) _goods);
                this.petPerCardList.put(emptyGridIndex, card);
            }
            --this.emptyGridNumber;
        }
        // monitorexit(this.items)
        this.medicamentChange(_goods.getID(), _goods.getGoodsType());
        return new short[]{(short) emptyGridIndex, (short) _number};
    }

    public boolean remove(final int _gridIndex, final int _goodsID) {
        synchronized (this.items) {
            if (_gridIndex >= 0 && _gridIndex < this.size && this.items[_gridIndex][0] == _goodsID && this.items[_gridIndex][1] > 0) {
                this.tonicList.remove(_gridIndex);
                this.petPerCardList.remove(_gridIndex);
                this.items[_gridIndex][0] = 0;
                this.items[_gridIndex][1] = 0;
                ++this.emptyGridNumber;
                this.medicamentChange(_goodsID, GoodsServiceImpl.getInstance().getGoodsByID(_goodsID).getGoodsType());
                // monitorexit(this.items)
                return true;
            }
        }
        // monitorexit(this.items)
        return false;
    }

    public ArrayList<int[]> remove(final int _goodsID, final short _number) throws BagException {
        synchronized (this.items) {
            if (_number <= 0) {
                throw PackageExceptionFactory.getInstance().getException("\u5220\u9664\u7684\u6570\u91cf\u975e\u6cd5");
            }
            if (this.getGoodsNumber(_goodsID) < _number) {
                throw PackageExceptionFactory.getInstance().getException("\u5220\u9664\u7684\u6570\u91cf\u8d85\u8fc7\u80cc\u5305\u4e2d\u6570\u91cf");
            }
            int reduceDeleteNumber = _number;
            ArrayList<int[]> result = new ArrayList<int[]>();
            for (int i = 0; i < this.size; ++i) {
                if (this.items[i][0] == _goodsID) {
                    if (this.items[i][1] > reduceDeleteNumber) {
                        int[] array = this.items[i];
                        int n = 1;
                        array[n] -= reduceDeleteNumber;
                        result.add(new int[]{i, this.items[i][1]});
                        this.medicamentChange(_goodsID, GoodsServiceImpl.getInstance().getGoodsByID(_goodsID).getGoodsType());
                        // monitorexit(this.items)
                        return result;
                    }
                    reduceDeleteNumber -= this.items[i][1];
                    this.items[i][0] = 0;
                    this.items[i][1] = 0;
                    result.add(new int[]{i, 0});
                    ++this.emptyGridNumber;
                    if (reduceDeleteNumber == 0) {
                        this.medicamentChange(_goodsID, GoodsServiceImpl.getInstance().getGoodsByID(_goodsID).getGoodsType());
                        // monitorexit(this.items)
                        return result;
                    }
                }
            }
        }
        // monitorexit(this.items)
        return null;
    }

    public int remove(final SingleGoods _goods) throws BagException {
        return this.remove(_goods.getID());
    }

    public int remove(final int _goodsID) throws BagException {
        int deletedNumber = 0;
        synchronized (this.items) {
            for (int i = 0; i < this.size; ++i) {
                if (this.items[i][0] == _goodsID) {
                    deletedNumber += this.items[i][1];
                    this.items[i][0] = 0;
                    this.items[i][1] = 0;
                    ++this.emptyGridNumber;
                    this.medicamentChange(_goodsID, GoodsServiceImpl.getInstance().getGoodsByID(_goodsID).getGoodsType());
                }
            }
        }
        // monitorexit(this.items)
        return deletedNumber;
    }

    public short[] removeOne(final int _goodsID) throws BagException {
        synchronized (this.items) {
            for (int i = 0; i < this.size; ++i) {
                if (this.items[i][0] == _goodsID && this.items[i][1] > 0) {
                    int[] array = this.items[i];
                    int n = 1;
                    --array[n];
                    if (this.items[i][1] == 0) {
                        this.items[i][0] = 0;
                        ++this.emptyGridNumber;
                    }
                    this.medicamentChange(_goodsID, GoodsServiceImpl.getInstance().getGoodsByID(_goodsID).getGoodsType());
                    // monitorexit(this.items)
                    return new short[]{(short) i, (short) this.items[i][1]};
                }
            }
        }
        // monitorexit(this.items)
        return null;
    }

    public short[] remove(final int _gridIndex, final int _goodsID, final int _number) throws BagException {
        synchronized (this.items) {
            this.tonicList.remove(_gridIndex);
            this.petPerCardList.remove(_gridIndex);
            if (_number <= 0 || this.items[_gridIndex][0] != _goodsID || this.items[_gridIndex][1] < _number) {
                throw PackageExceptionFactory.getInstance().getException("\u5220\u9664\u6570\u636e\u65e0\u6548");
            }
            if (this.items[_gridIndex][1] > _number) {
                int[] array = this.items[_gridIndex];
                int n = 1;
                array[n] -= _number;
                this.medicamentChange(_goodsID, GoodsServiceImpl.getInstance().getGoodsByID(_goodsID).getGoodsType());
                // monitorexit(this.items)
                return new short[]{(short) _gridIndex, (short) this.items[_gridIndex][1]};
            }
            this.items[_gridIndex][0] = 0;
            this.items[_gridIndex][1] = 0;
            ++this.emptyGridNumber;
            this.medicamentChange(_goodsID, GoodsServiceImpl.getInstance().getGoodsByID(_goodsID).getGoodsType());
            // monitorexit(this.items)
            return new short[]{(short) _gridIndex, 0};
        }
    }

    public int getFirstGridIndex(final int _goodsID) {
        if (_goodsID <= 0) {
            return -1;
        }
        for (int i = 0; i < this.size; ++i) {
            if (this.items[i][0] == _goodsID) {
                return i;
            }
        }
        return -1;
    }

    public int getGoodsNumber(final int _goodsID) {
        int number = 0;
        int[][] items;
        for (int length = (items = this.items).length, i = 0; i < length; ++i) {
            int[] goodsDesc = items[i];
            if (goodsDesc[0] == _goodsID) {
                number += goodsDesc[1];
            }
        }
        return number;
    }

    public int[] getItemData(final int _gridIndex) {
        if (_gridIndex >= 0 && _gridIndex < this.items.length) {
            return this.items[_gridIndex];
        }
        return null;
    }

    public int[][] getAllItem() {
        return this.items;
    }

    public boolean clearUp() {
        boolean changed = false;
        for (int i = 0; i < this.size; ++i) {
            for (int j = i + 1; j < this.size; ++j) {
                if (this.items[j][0] != 0) {
                    if (this.items[i][0] == 0 || this.items[i][0] > this.items[j][0]) {
                        int[] temp = this.items[i];
                        this.items[i] = this.items[j];
                        this.items[j] = temp;
                        changed = true;
                    } else if (this.items[i][0] == this.items[j][0]) {
                        int stackNums = GoodsServiceImpl.getInstance().getGoodsByID(this.items[i][0]).getMaxStackNums();
                        if (this.items[i][1] + this.items[j][1] > stackNums) {
                            int[] array = this.items[j];
                            int n = 1;
                            array[n] -= stackNums - this.items[i][1];
                            this.items[i][1] = stackNums;
                            changed = true;
                        } else {
                            int[] array2 = this.items[i];
                            int n2 = 1;
                            array2[n2] += this.items[j][1];
                            this.items[j][0] = 0;
                            this.items[j][1] = 0;
                            ++this.emptyGridNumber;
                            changed = true;
                        }
                    }
                }
            }
        }
        return changed;
    }

    public boolean upgrade() {
        if (this.size == 40) {
            return false;
        }
        int[][] newContainer = new int[this.size + 8][2];
        System.arraycopy(this.items, 0, newContainer, 0, this.size);
        this.items = newContainer;
        this.emptyGridNumber += 8;
        this.size += 8;
        return true;
    }

    public void initMaster(final int _masterID) {
        this.master = PlayerServiceImpl.getInstance().getPlayerByUserID(_masterID);
    }

    private void medicamentChange(final int _goodsID, final EGoodsType _eType) {
        if ((EGoodsType.MEDICAMENT == _eType || EGoodsType.SPECIAL_GOODS == _eType) && this.master != null) {
            SingleGoodsBag.log.info((Object) "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\u836f\u6c34/\u7279\u6b8a\u7269\u54c1\u6570\u91cf\u53d1\u751f\u53d8\u5316");
            HotKeySumByMedicament keyMsg = new HotKeySumByMedicament(this.master, _goodsID);
            if (keyMsg.haveRelation(_goodsID)) {
                ResponseMessageQueue.getInstance().put(this.master.getMsgQueueIndex(), keyMsg);
            }
        }
    }
}
