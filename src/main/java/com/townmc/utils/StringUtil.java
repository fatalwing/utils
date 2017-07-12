package com.townmc.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by meng on 2015-1-27.
 */
public class StringUtil {

    /**
     * 生成uuid
     * @return String
     */
    public static String uuid() {
        java.util.UUID uuid = java.util.UUID.randomUUID();
        return uuid.toString().replaceAll("-", "");
    }

    /**
     * 生成md5
     * @param text String
     * @return String
     */
    public static String md5(String text) {
        String result = "";
        try {
            java.security.MessageDigest md5 = java.security.MessageDigest.getInstance("MD5");
            byte[] buf = text.getBytes();
            byte[] dig = md5.digest(buf);
            String hex = null;
            for (int i = 0; i < dig.length; i++) {
                int n = dig[i] < 0 ? (256 + dig[i]) : dig[i];
                hex = Integer.toHexString(n);
                if (hex.length() < 2)
                    hex = "0" + hex;
                result += hex;
            }
        } catch (java.security.NoSuchAlgorithmException e) {
            //e.printStackTrace();
            result = null;
        }
        return result;
    }


    public static String sha1(String decript) {
        try {
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("SHA-1");
            digest.update(decript.getBytes());
            byte[] messageDigest = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (java.security.NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String sha(String decript) {
        try {
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("SHA");
            digest.update(decript.getBytes());
            byte[] messageDigest = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (java.security.NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 生成指定位数的随机数
     * @param length int
     * @return String
     */
    public static String randomNum(int length) {
        java.util.Random ram = new java.util.Random();
        int inum = java.lang.Math.abs(ram.nextInt());
        String numt = String.valueOf(inum);
        StringBuffer sbbase = new StringBuffer("0");
        for (int i = 0; i < length; i++) {
            sbbase.append("0");
        }
        String sbase = sbbase.toString();
        String snum = null;
        if (numt.length() < length) {
            snum = sbase.substring(0, length - numt.length()) + numt;
        } else {
            snum = numt.substring(0, length);
        }
        return snum;
    }

    /**
     * 将inputstream转化为字符串
     * @param is
     * @return
     */
    public static String streamToString(InputStream is) {
        /*
          * To convert the InputStream to String we use the BufferedReader.readLine()
          * method. We iterate until the BufferedReader return null which means
          * there's no more data to read. Each line will appended to a StringBuilder
          * and returned as String.
          */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    /**
     * 检查是否为空字符串
     * @param str String
     * @return boolean
     */
    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查是否为非空字符串
     * @param str String
     * @return boolean
     */
    public static boolean isNotBlank(String str) {
        return !StringUtil.isBlank(str);
    }

    /**
     * 给定的字符串包含在给定的集合里
     * @param str
     * @param arr
     * @return
     */
    public static boolean inSet(String str, List<String> arr) {
        for (String s : arr) {
            if (str.equals(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 给定的字符串不包含在给定的集合里
     * @param str
     * @param arr
     * @return
     */
    public static boolean notInSet(String str, List<String> arr) {
        return !inSet(str, arr);
    }

    public static String leftPad(String value, int length, String left) {
        if (null == value) {
            return null;
        }
        if (value.length() > length) {
            return value;
        }
        String repeat = "";
        for (int i = 0; i < length - value.length(); i++) {
            repeat += left;
        }
        return repeat + value;
    }

    /**
     * 是否为数字（包括小数点）
     * @param str
     * @return
     */
    public static boolean isNumber(Object str) {
        if (str instanceof Integer) return true;
        if (str instanceof Long) return true;
        if (str instanceof Double) return true;
        if (str instanceof Float) return true;
        if (str instanceof BigDecimal) return true;

        if (str instanceof String) {
            String s = str.toString();
            if (isBlank(s)) {
                return false;
            } else {
                return s.matches("\\d+(.\\d+)?");
            }
        }
        return false;
    }

    /**
     * 中文转unicode
     * @param str
     * @return
     */
    public static String chinaToUnicode(String str){
        String result="";
        for (int i = 0; i < str.length(); i++){
            int chr1 = (char) str.charAt(i);
            if(chr1>=19968&&chr1<=171941){//汉字范围 \u4e00-\u9fa5 (中文)
                result+="\\u" + Integer.toHexString(chr1);
            }else{
                result+=str.charAt(i);
            }
        }
        return result;
    }

}
