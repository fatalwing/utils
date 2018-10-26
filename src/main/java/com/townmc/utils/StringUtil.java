package com.townmc.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by meng on 2015-1-27.
 */
public class StringUtil {
    private static final Log log = LogFactory.getLog(StringUtil.class);

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

    /**
     * HMAC加密
     * @param data 需要加密的字节数组
     * @param key 密钥
     * @return 字节数组
     */
    public static byte[] encryptHMAC(byte[] data, String key) {
        SecretKey secretKey;
        byte[] bytes = null;
        try {
            secretKey = new SecretKeySpec(Base64Plus.encode(key.getBytes()).getBytes(), "HmacSHA1");
            Mac mac = Mac.getInstance(secretKey.getAlgorithm());
            mac.init(secretKey);
            bytes = mac.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * HMAC加密
     * @param data 需要加密的字符串
     * @param key 密钥
     * @return 字符串
     */
    public static String encryptHMAC(String data, String key) {
        if (StringUtil.isBlank(data)) {
            return null;
        }
        byte[] bytes = encryptHMAC(data.getBytes(), key);
        return byteArrayToHexString(bytes);
    }

    /**
     * 转换字节数组为十六进制字符串
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    private static String byteArrayToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(byteToHexString(bytes[i]));
        }
        return sb.toString();
    }

    /**
     * 全局数组
     */
    private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
    /**
     * 将一个字节转化成十六进制形式的字符串
     * @param b 字节数组
     * @return 字符串
     */
    private static String byteToHexString(byte b) {
        int ret = b;
        if (ret < 0) {
            ret += 256;
        }
        int m = ret / 16;
        int n = ret % 16;
        return hexDigits[m] + hexDigits[n];
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

    public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        String aaa = "GET&%2F&AccessKeyId%3Dtestid%26Action%3DDescribeCdnService%26Format%3DJSON%26SignatureMethod%3DHMAC-SHA1%26SignatureNonce%3D9b7a44b0-3be1-11e5-8c73-08002700c460%26SignatureVersion%3D1.0%26TimeStamp%3D2015-08-06T02%253A19%253A46Z%26Version%3D2014-11-11";
        String key = "testsecret&";

        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA1");
        mac.init(new javax.crypto.spec.SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA1"));
        byte[] signData = mac.doFinal(aaa.getBytes("UTF-8"));

        String signature = new String(Base64Plus.encode(signData));

        log.info(signature);
    }

}
