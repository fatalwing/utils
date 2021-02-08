package com.townmc.utils;

import java.io.*;
import java.util.Random;

/**
 * base64 增强类
 * Created by meng on 2017/7/12.
 */
public class Base64Plus {
    /**
     * Base64编码表。
     */
    private static final char[] BASE64CODE = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
            'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
            'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3',
            '4', '5', '6', '7', '8', '9', '+', '/'};

    /**
     * Base64解码表。
     */
    private static final byte[] BASE64DECODE = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1,
            // 注意两个63，为兼容SMP，
            -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, 63,
            -1,
            // “/”和“-”都翻译成63。
            63,
            52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, 0, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
            12, 13,
            // 注意两个0：
            14,
            15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1,
            // “A”和“=”都翻译成0。
            -1,
            -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51,
            -1, -1, -1, -1, -1};

    private static final int HEX_255 = 0x0000ff;

    private static final int HEX_16515072 = 0xfc0000;

    private static final int HEX_258048 = 0x3f000;

    private static final int HEX_4032 = 0xfc0;

    private static final int HEX_63 = 0x3f;

    private static final int HEX_16711680 = 0xff0000;

    private static final int HEX_65280 = 0x00ff00;

    private static final int NUMBER_TWO = 2;

    private static final int NUMBER_THREE = 3;

    private static final int NUMBER_FOUR = 4;

    private static final int NUMBER_SIX = 6;

    private static final int NUMBER_EIGHT = 8;

    private static final int NUMBER_TWELVE = 12;

    private static final int NUMBER_SIXTEEN = 16;

    private static final int NUMBER_EIGHTEEN = 18;

    private static final char EQUALS_CHAR = '=';

    private static final String ZERO = "0";

    /**
     * 构造方法私有化，防止实例化。
     */
    private Base64Plus() {
    }

    /**
     * Base64编码。将字节数组中字节3个一组编码成4个可见字符。
     *
     * @param b
     *            需要被编码的字节数据。
     * @return 编码后的Base64字符串。
     */
    public static String encode(byte[] b) {
        int code = 0;

        // 按实际编码后长度开辟内存，加快速度
        int kengdie = (b.length - 1) / NUMBER_THREE;
        StringBuilder sb = new StringBuilder(kengdie << (NUMBER_TWO + NUMBER_FOUR));

        // 进行编码
        for (int i = 0; i < b.length; i++) {
            code |= (b[i] << (NUMBER_SIXTEEN - i % NUMBER_THREE * NUMBER_EIGHT)) & (HEX_255 << (NUMBER_SIXTEEN - i % NUMBER_THREE * NUMBER_EIGHT));
            if (i % NUMBER_THREE == NUMBER_TWO || i == b.length - 1) {
                sb.append(BASE64CODE[(code & HEX_16515072) >>> NUMBER_EIGHTEEN]);
                sb.append(BASE64CODE[(code & HEX_258048) >>> NUMBER_TWELVE]);
                sb.append(BASE64CODE[(code & HEX_4032) >>> NUMBER_SIX]);
                sb.append(BASE64CODE[code & HEX_63]);
                code = 0;
            }
        }

        // 对于长度非3的整数倍的字节数组，编码前先补0，编码后结尾处编码用=代替，
        // =的个数和短缺的长度一致，以此来标识出数据实际长度
        if (b.length % NUMBER_THREE > 0) {
            sb.setCharAt(sb.length() - 1, EQUALS_CHAR);
        }
        if (b.length % NUMBER_THREE == 1) {
            sb.setCharAt(sb.length() - NUMBER_TWO, EQUALS_CHAR);
        }
        return sb.toString();
    }

    /**
     * Base64解码。
     *
     * @param code
     *            用Base64编码的ASCII字符串
     * @return 解码后的字节数据
     */
    public static byte[] decode(String code) {
        // 检查参数合法性
        if (code == null) {
            return null;
        }
        int len = code.length();
        if (len % NUMBER_FOUR != 0) {
            throw new IllegalArgumentException("Base64Plus string length must be 4*n");
        }
        if (code.length() == 0) {
            return new byte[0];
        }

        // 统计填充的等号个数
        int pad = 0;
        if (code.charAt(len - 1) == EQUALS_CHAR) {
            pad++;
        }
        if (code.charAt(len - NUMBER_TWO) == EQUALS_CHAR) {
            pad++;
        }

        // 根据填充等号的个数来计算实际数据长度
        int retLen = len / NUMBER_FOUR * NUMBER_THREE - pad;

        // 分配字节数组空间
        byte[] ret = new byte[retLen];

        // 查表解码
        char ch1, ch2, ch3, ch4;
        int i;
        for (i = 0; i < len; i += NUMBER_FOUR) {
            int j = i / NUMBER_FOUR * NUMBER_THREE;
            ch1 = code.charAt(i);
            ch2 = code.charAt(i + 1);
            ch3 = code.charAt(i + NUMBER_TWO);
            ch4 = code.charAt(i + NUMBER_THREE);
            int tmp = (BASE64DECODE[ch1] << NUMBER_EIGHTEEN) | (BASE64DECODE[ch2] << NUMBER_TWELVE) | (BASE64DECODE[ch3] << NUMBER_SIX) | (BASE64DECODE[ch4]);
            ret[j] = (byte) ((tmp & HEX_16711680) >> NUMBER_SIXTEEN);
            if (i < len - NUMBER_FOUR) {
                ret[j + 1] = (byte) ((tmp & HEX_65280) >> NUMBER_EIGHT);
                ret[j + NUMBER_TWO] = (byte) ((tmp & HEX_255));

            } else {
                if (j + 1 < retLen) {
                    ret[j + 1] = (byte) ((tmp & HEX_65280) >> NUMBER_EIGHT);
                }
                if (j + NUMBER_TWO < retLen) {
                    ret[j + NUMBER_TWO] = (byte) ((tmp & HEX_255));
                }
            }
        }
        return ret;
    }

    /**
     * Base64解码成String对象
     * @param code 解码字符串
     * @return String
     */
    public static String decodeToString(String code) {
        return new String(decode(code));
    }

    /**
     * 通过key对字符串进行加密
     * @param txt 需要加密的内容
     * @param key 加密key
     * @return String
     */
    public static String passportEncrypt(String txt, String key) {
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        String rand = "" + random.nextInt() % 32000;
        String encryptKey = md5(rand);
        int ctr = 0;
        StringBuilder tmp = new StringBuilder();
        for (int i = 0; i < txt.length(); i++) {
            ctr = (ctr == encryptKey.length() ? 0 : ctr);
            tmp.append(encryptKey.charAt(ctr));
            char c = (char)(txt.charAt(i) ^ encryptKey.charAt(ctr));
            tmp.append(c);
            ctr++;
        }
        String passportKey = passportKey(tmp.toString(), key);
        return Base64Plus.encode(passportKey.getBytes());
    }

    /**
     * 解密某个加密过的字符串
     * @param txt 需要解密的内容
     * @param key 解密key
     * @return String
     */
    public static String passportDecrypt(String txt, String key) {
        byte[] bytes;
        try {
            bytes = Base64Plus.decode(txt);
            txt = new String(bytes);
        } catch (Exception e) {
            return null;
        }
        txt = passportKey(txt, key);
        StringBuilder tmp = new StringBuilder();
        for (int i = 0; i < txt.length(); i++) {
            char c = (char)(txt.charAt(i) ^ txt.charAt(++i));
            tmp.append(c);
        }
        return tmp.toString();
    }


    /**
     * token加密
     * @param token 加密目标token
     * @param key 秘钥
     * @return String
     */
    public static String tokenEncrypt(String token, String key) {

        String re = passportEncrypt(token, key);
        re = re.replaceAll("\\+", "-");
        re = re.replaceAll("/", "*");
        re = re.replaceAll("=", "_");

        return re;
    }

    /**
     * token解密
     * @param token 解密目标token
     * @param key 秘钥
     * @return String
     */
    public static String tokenDecrypt(String token, String key) {

        token = token.replaceAll("-", "+");
        token = token.replaceAll("\\*", "/");
        token = token.replaceAll("_", "=");

        return passportDecrypt(token, key);
    }

    private static String passportKey(String txt, String key) {
        String encryptKey = md5(key);
        int ctr = 0;
        StringBuilder tmp = new StringBuilder();
        for(int i = 0; i < txt.length(); i++) {
            ctr = (ctr == encryptKey.length()) ? 0 : ctr;
            char c = (char)(txt.charAt(i) ^ encryptKey.charAt(ctr));
            tmp.append(c);
            ctr++;
        }
        return tmp.toString();
    }

    /**
     * 序列化对象为String字符串，先对序列化后的结果进行BASE64编码，否则不能直接进行反序列化
     * @param o 序列化对象
     * @return String
     * @throws Exception 异常
     */
    public static String writeObject(Object o) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(o);
        oos.flush();
        oos.close();
        bos.close();
        return encode(bos.toByteArray());
    }

    /**
     * 反序列化String字符串为对象
     * @param  objectStr 序列化对象的字符串
     * @return Object
     * @throws Exception 异常
     */
    public static Object readObject(String objectStr) throws Exception{
        ByteArrayInputStream bis = new ByteArrayInputStream(decode(objectStr));
        ObjectInputStream ois = new ObjectInputStream(bis);
        Object o = null;
        try {
            o = ois.readObject();
        } catch(EOFException e) {
            e.printStackTrace();
            System.err.print("read finished");
        }
        bis.close();
        ois.close();
        return o;
    }

    private static String md5(String text) {
        StringBuilder result = new StringBuilder();
        try {
            java.security.MessageDigest md5 = java.security.MessageDigest.getInstance("MD5");
            byte[] buf = text.getBytes();
            byte[] dig = md5.digest(buf);
            String hex;
            for (byte b : dig) {
                int n = b < 0 ? (256 + b) : b;
                hex = Integer.toHexString(n);
                if (hex.length() < 2) {
                    hex = ZERO + hex;
                }
                result.append(hex);
            }
        } catch (java.security.NoSuchAlgorithmException e) {
            result = null;
        }
        assert result != null;
        return result.toString();
    }

}
