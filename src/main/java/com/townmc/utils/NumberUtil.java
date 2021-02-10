package com.townmc.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;
import java.util.Random;

/**
 * Created by meng on 2015-4-11.
 */
public class NumberUtil {
    private static final Log log = LogFactory.getLog(NumberUtil.class);

    private static char[] HEX_BASE = new char[]{'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r',
            's','t','u','v','w','x','y','z','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R',
            'S','T','U','V','W','X','Y','Z','_','-'};

    /**
     * 两个数相乘
     * @param one 第一个数
     * @param two 第二个数
     * @param scale 保留精度小数位数
     * @return
     */
    public static BigDecimal multiplay(Object one, Object two, int scale) {
        boolean valid;
        if (one != null && (one instanceof Number || (one instanceof String && one.toString().matches("\\d+(\\.\\d+)?")))) {
            valid = true;
        } else {
            valid = false;
        }
        if (two != null && (two instanceof Number || (two instanceof String && two.toString().matches("\\d+(\\.\\d+)?")))) {
            valid = true;
        } else {
            valid = false;
        }
        if (!valid) {
            throw new RuntimeException("invalid parameter.(" + one + "*" + two + ")");
        }
        BigDecimal re = new BigDecimal(one.toString()).multiply(new BigDecimal(two.toString())).setScale(scale, BigDecimal.ROUND_HALF_UP);
        return re;
    }

    /**
     * 两个数相除
     * @param one 第一个数
     * @param two 第二个数
     * @param scale 保留精度小数位数
     * @return
     */
    public static BigDecimal divide(Object one, Object two, int scale) {
        boolean valid = false;
        if (one != null && (one instanceof Number || (one instanceof String && one.toString().matches("\\d+(\\.\\d+)?")))) {
            valid = true;
        } else {
            valid = false;
        }
        if (two != null && (two instanceof Number || (two instanceof String && two.toString().matches("\\d+(\\.\\d+)?")))) {
            valid = true;
        } else {
            valid = false;
        }
        if (!valid) {
            throw new RuntimeException("invalid parameter.(" + one + "/" + two + ")");
        }
        BigDecimal re = new BigDecimal(one.toString()).divide(new BigDecimal(two.toString()),scale,BigDecimal.ROUND_HALF_UP);
        return re;
    }

    /**
     * 36进制转10进制
     * @return
     */
    public static long hex36To10(String str) {
        char[] arr = str.toCharArray();
        int position = arr.length;
        long re = 0;
        for(char c : arr) {
            --position;
            int ac = c;
            int sub = (ac >= 48 && ac <= 57) ? 48 : 87;
            int val = ac-sub;
            re += val * Math.pow(36, position);
        }
        return re;
    }

    /**
     * 10进制转36进制
     * @param num
     * @return
     */
    public static String hex10To36(long num) {
        long temp = num;
        StringBuilder re = new StringBuilder();
        while(temp != 0) {
            int x = (int)(temp % 36);
            temp = temp / 36;

            if(x >= 0 && x <= 9) {
                re.append(x);
            } else {
                x = x + 87;
                char c = (char)x;
                re.append(c);
            }
        }
        return re.reverse().toString();
    }

    public static String hex10To64(BigDecimal big) {
        StringBuilder re = new StringBuilder();
        while(big.compareTo(new BigDecimal("0")) != 0) {
            int remainder = (int)(big.divideAndRemainder(new BigDecimal("64"))[1]).intValue();
            big = big.divide(new BigDecimal("64"), 0, BigDecimal.ROUND_DOWN);
            re.append(HEX_BASE[remainder]);
        }
        return re.reverse().toString();
    }

    /**
     * 36进制相加
     * @param value
     * @param toAdd
     * @return
     */
    public static String hex36Add(String value, String toAdd) {
        long v1 = hex36To10(value);
        long v2 = hex36To10(toAdd);
        return hex10To36(v1 + v2);
    }

    /**
     * min和max之间的随机数
     * @param min
     * @param max
     * @return
     */
    public static int getRandom(int min, int max){
        int x = min;
        int y = max;
        if(x > y){
            x = max;
            y = min;
        }
        int rand = new Random().nextInt(y-x) + x;
        return rand;
    }

    public static void main(String[] args) {
        String re = hex10To64(new BigDecimal("1476758519673203664161"));
        log.info("===== " + re);
    }

}
