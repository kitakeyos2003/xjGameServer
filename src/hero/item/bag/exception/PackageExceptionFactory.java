// 
// Decompiled by Procyon v0.5.36
// 
package hero.item.bag.exception;

import hero.item.detail.EGoodsType;

public class PackageExceptionFactory {

    private static PackageExceptionFactory instance;

    private PackageExceptionFactory() {
    }

    public static PackageExceptionFactory getInstance() {
        if (PackageExceptionFactory.instance == null) {
            PackageExceptionFactory.instance = new PackageExceptionFactory();
        }
        return PackageExceptionFactory.instance;
    }

    public BagException getException(final String _exceptionMsg) {
        return new BagException(_exceptionMsg);
    }

    public BagException getFullException(final EGoodsType _goodsType) {
        switch (_goodsType) {
            case EQUIPMENT: {
                return new BagException("\u88c5\u5907\u80cc\u5305\u6ca1\u5730\u513f\u4e86\u5440");
            }
            case MATERIAL: {
                return new BagException("\u6750\u6599\u80cc\u5305\u6ca1\u5730\u513f\u4e86\u5440");
            }
            case MEDICAMENT: {
                return new BagException("\u836f\u54c1\u80cc\u5305\u6ca1\u5730\u513f\u4e86\u5440");
            }
            case TASK_TOOL: {
                return new BagException("\u4efb\u52a1\u80cc\u5305\u6ca1\u5730\u513f\u4e86\u5440");
            }
            case SPECIAL_GOODS: {
                return new BagException("\u5b9d\u7269\u80cc\u5305\u6ca1\u5730\u513f\u4e86\u5440");
            }
            case PET: {
                return new BagException("\u6bcf\u4e2a\u73a9\u5bb6\u6700\u591a\u53ea\u80fd\u62e5\u6709 2 \u53ea\u5ba0\u7269\uff01");
            }
            default: {
                return null;
            }
        }
    }
}
