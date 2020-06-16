package com.jwxt.utils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author: me@nitmali.com
 * @create: 2018-11-19 14:22
 **/
public class MathUtil {

    /**
     * 四舍五入到整数
     *
     * @param balance 需要四舍五入的数
     * @return 四舍五入后的整数
     */
    public static int roundInt(Double balance) {
        BigDecimal decimal = new BigDecimal(balance);
        return decimal.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
    }

    /**
     * 向上取整 或者 向下取整 或者 四舍五入
     *
     * @param balance  需要转换的数
     * @param upOrDown 1-四舍五入，2-向上取整，3-向下取整
     * @return 转换后的数
     */
    public static int roundInt(Double balance, Integer upOrDown) {
        if (balance.intValue() - balance == 0) {
            return balance.intValue();
        }
        BigDecimal decimal = new BigDecimal(balance);
        return decimal.setScale(0, getRound(upOrDown)).intValue();
    }

    /**
     * 四舍五入到指定小数
     *
     * @param balance 需要四舍五入的数
     * @return 四舍五入后的数
     */
    public static Double roundDouble(Double balance, Integer newScale) {
        BigDecimal decimal = new BigDecimal(balance);
        return decimal.setScale(newScale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 向上取整 或者 向下取整 或者 四舍五入到指定小数
     *
     * @param balance  需要转换的数
     * @param upOrDown 1-四舍五入，2-向上取整，3-向下取整
     * @return 转换后的数
     */
    public static Double roundDouble(Double balance, Integer newScale, Integer upOrDown) {
//        if (upOrDown != 1) {
//            Double d = 0.5 * Math.pow(0.1, newScale);
//            balance = upOrDown == 2 ? balance + d : balance - d;
//        }

        BigDecimal decimal = BigDecimal.valueOf(balance);
        return decimal.setScale(newScale, getRound(upOrDown)).doubleValue();
    }


    /**
     * 四舍五入到指定小数
     *
     * @param balance 需要四舍五入的数
     * @return 四舍五入后的数
     */
    public static Float roundFloat(Float balance, Integer newScale) {
        BigDecimal decimal = BigDecimal.valueOf(balance);
        return decimal.setScale(newScale, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    /**
     * 向上取整 或者 向下取整 或者 四舍五入到指定小数
     *
     * @param balance 需要转换的数
     * @return 转换后的数
     */
    public static Float roundFloat(Float balance, Integer newScale, Integer upOrDown) {
        if (upOrDown != 1) {
            Float f = Double.valueOf(0.5 * Math.pow(0.1, newScale)).floatValue();
            balance = upOrDown == 2 ? balance + f : balance - f;
        }
        BigDecimal decimal = BigDecimal.valueOf(balance);
        return decimal.setScale(newScale, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入。
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b1 = BigDecimal.valueOf(v1);
        BigDecimal b2 = BigDecimal.valueOf(v2);
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double transformToDouble(Float f) {
        if (f == null) {
            return 0d;
        }
        BigDecimal b = new BigDecimal(String.valueOf(f));
        return b.doubleValue();
    }

    /**
     * 规则转换
     *
     * @param upOrDown Integer
     * @return BigDecimal
     */
    public static int getRound(Integer upOrDown) {
        if (upOrDown == null) {
            return BigDecimal.ROUND_HALF_UP;
        }
        switch (upOrDown) {
            case 1:
                return BigDecimal.ROUND_HALF_UP;
            case 2:
                return BigDecimal.ROUND_UP;
            case 3:
                return BigDecimal.ROUND_DOWN;
            default:
                return BigDecimal.ROUND_HALF_UP;
        }
    }

    public static <T extends Number> T min(T a, T b) {
        if (a == null || b == null) {
            return null;
        }
        switch (a.getClass().getSimpleName()) {
            case "Long":
                return (a.longValue() <= b.longValue()) ? a : b;
            case "Integer":
                return (a.intValue() <= b.intValue()) ? a : b;
            case "Double":
                return (a.doubleValue() <= b.doubleValue()) ? a : b;
            case "Float":
                return (a.floatValue() <= b.floatValue()) ? a : b;
            case "Short" :
                return (a.shortValue() <= b.shortValue()) ? a : b;
            default:
                return (a.byteValue() <= b.byteValue()) ? a : b;
        }
    }
    public static Date min(Date a, Date b) {
        if (a == null || b == null) {
            return null;
        }
        return (a.getTime() <= b.getTime()) ? a : b;
    }

    public static <T extends Number> T max(T a, T b) {
        if (a == null || b == null) {
            return null;
        }
        switch (a.getClass().getSimpleName()) {
            case "Long":
                return (a.longValue() <= b.longValue()) ? b : a;
            case "Integer":
                return (a.intValue() <= b.intValue()) ? b : a;
            case "Double":
                return (a.doubleValue() <= b.doubleValue()) ? b : a;
            case "Float":
                return (a.floatValue() <= b.floatValue()) ? b : a;
            case "Short" :
                return (a.shortValue() <= b.shortValue()) ? b : a;
            default:
                return (a.byteValue() <= b.byteValue()) ? b : a;
        }
    }

    public static Date max(Date a, Date b) {
        if (a == null || b == null) {
            return null;
        }
        return (a.getTime() <= b.getTime()) ? b : a;
    }

    public static double pow(double a, double b) {
        return Math.pow(a, b);
    }

}
