// 
// Decompiled by Procyon v0.5.36
// 
package hero.ui;

import hero.ui.data.SingleGoodsListData;
import hero.ui.data.EquipmentListData;
import hero.item.expand.ExpandGoods;
import java.util.Hashtable;
import hero.ui.data.EquipmentPackageData;
import hero.item.bag.EquipmentContainer;
import java.util.Iterator;
import java.io.IOException;
import hero.ui.data.SingleGoodsPackageData;
import yoyo.tools.YOYOOutputStream;
import hero.item.bag.SingleGoodsBag;
import java.util.ArrayList;

public class UI_GoodsListWithOperation {

    public static byte[] getBytes(final String[] _menuList, final ArrayList<byte[]>[] _followOptionData, final SingleGoodsBag _singleGoodsPackage, final String _tabName) {
        YOYOOutputStream output = new YOYOOutputStream();
        try {
            output.writeByte(getType().getID());
            output.writeBytes(SingleGoodsPackageData.getData(_singleGoodsPackage, false, null, _tabName));
            output.writeByte(_menuList.length);
            for (int i = 0; i < _menuList.length; ++i) {
                String menu = _menuList[i];
                output.writeUTF(menu);
                if (_followOptionData != null && _followOptionData[i] != null) {
                    output.writeByte(_followOptionData[i].size());
                    for (final byte[] _data : _followOptionData[i]) {
                        output.writeBytes(_data);
                    }
                } else {
                    output.writeByte(0);
                }
            }
            output.flush();
            return output.getBytes();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                output.close();
            } catch (IOException ex) {
            }
            output = null;
        }
    }

    public static byte[] getStorageBytes(final String[] _menuList, final SingleGoodsBag _singleGoodsPackage, final String _tabName) {
        YOYOOutputStream output = new YOYOOutputStream();
        try {
            output.writeByte(getType().getID());
            output.writeBytes(SingleGoodsPackageData.getData(_singleGoodsPackage, false, null, _tabName));
            output.writeByte(_menuList.length);
            for (int i = 0; i < _menuList.length; ++i) {
                String menu = _menuList[i];
                output.writeUTF(menu);
                output.writeByte(0);
            }
            output.flush();
            return output.getBytes();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                output.close();
            } catch (IOException ex) {
            }
            output = null;
        }
    }

    public static byte[] getData(final String[] _menuList, final ArrayList<byte[]>[] _followOptionData, final EquipmentContainer _equipmentPackage, final String _tabName) {
        YOYOOutputStream output = new YOYOOutputStream();
        try {
            output.writeByte(getType().getID());
            output.writeBytes(EquipmentPackageData.getData(_equipmentPackage, _tabName));
            output.writeByte(_menuList.length);
            for (int i = 0; i < _menuList.length; ++i) {
                String menu = _menuList[i];
                output.writeUTF(menu);
                if (_followOptionData != null && _followOptionData[i] != null) {
                    output.writeByte(_followOptionData[i].size());
                    for (final byte[] _data : _followOptionData[i]) {
                        output.writeBytes(_data);
                    }
                } else {
                    output.writeByte(0);
                }
            }
            output.flush();
            return output.getBytes();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                output.close();
            } catch (IOException ex) {
            }
            output = null;
        }
    }

    public static byte[] getStorageData(final String[] _menuList, final EquipmentContainer _equipmentPackage, final String _tabName) {
        YOYOOutputStream output = new YOYOOutputStream();
        try {
            output.writeByte(getType().getID());
            output.writeBytes(EquipmentPackageData.getData(_equipmentPackage, _tabName));
            output.writeByte(_menuList.length);
            for (int i = 0; i < _menuList.length; ++i) {
                String menu = _menuList[i];
                output.writeUTF(menu);
                output.writeByte(0);
            }
            output.flush();
            return output.getBytes();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                output.close();
            } catch (IOException ex) {
            }
            output = null;
        }
    }

    public static byte[] getData(final String[] _menuList, final EquipmentContainer _equipmentPackage, final String _tabName) {
        YOYOOutputStream output = new YOYOOutputStream();
        try {
            output.writeByte(getType().getID());
            output.writeBytes(EquipmentPackageData.getData(_equipmentPackage, _tabName));
            output.writeByte(_menuList.length);
            for (final String menu : _menuList) {
                output.writeUTF(menu);
                output.writeByte(0);
            }
            output.flush();
            return output.getBytes();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                output.close();
            } catch (IOException ex) {
            }
            output = null;
        }
    }

    public static byte[] getBytes(final String[] _menuList, final Hashtable<String, ArrayList<ExpandGoods>> _equipmentList, final int _gridNumsOfExsitsGoods) {
        YOYOOutputStream output = new YOYOOutputStream();
        try {
            output.writeByte(getType().getID());
            output.writeBytes(EquipmentListData.getData(_equipmentList, _gridNumsOfExsitsGoods));
            output.writeByte(_menuList.length);
            for (final String menu : _menuList) {
                output.writeUTF(menu);
                output.writeByte(0);
            }
            output.flush();
            return output.getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (IOException ex) {
            }
        }
        return null;
    }

    public static byte[] getBytes(final String[] _menuList, final ArrayList<byte[]>[] _followOptionData, final Hashtable<String, ArrayList<ExpandGoods>> _singleGoodsList, final int _traceGoodsType) {
        YOYOOutputStream output = new YOYOOutputStream();
        try {
            output.writeByte(getType().getID());
            output.writeBytes(SingleGoodsListData.getData(_singleGoodsList, _traceGoodsType));
            output.writeByte(_menuList.length);
            for (int i = 0; i < _menuList.length; ++i) {
                String menu = _menuList[i];
                output.writeUTF(menu);
                if (_followOptionData != null && _followOptionData[i] != null) {
                    output.writeByte(_followOptionData[i].size());
                    for (final byte[] _data : _followOptionData[i]) {
                        output.writeBytes(_data);
                    }
                } else {
                    output.writeByte(0);
                }
            }
            output.flush();
            return output.getBytes();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                output.close();
            } catch (IOException ex) {
            }
            output = null;
        }
    }

    public static EUIType getType() {
        return EUIType.GOODS_OPERATE_LIST;
    }
}
