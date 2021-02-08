package com.townmc.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.*;

import static com.auth0.jwt.impl.PublicClaims.ISSUER;

/**
 * 生成访问sign值的帮助类
 * @author meng
 */
public class SignUtil {
    private static final Log log = LogFactory.getLog(SignUtil.class);

    /**
     * 排序计算sign值
     * @param param
     * @param key
     * @return
     */
    public static String sign(Map<String, Object> param, String key) {
        if (null == param || param.size() < 1) {
            return "";
        }

        // 将参数的key做一个排序
        Object[] paramKeyArr = (Object[])param.keySet().toArray();
        Arrays.sort(paramKeyArr);

        // 按照排序之后的顺序用`&`符号重新拼接
        StringBuilder sb = new StringBuilder();
        for (Object paramKey : paramKeyArr) {
            if ("sign".equals(paramKey)) {
                continue;
            }
            Object paramValue = param.get(paramKey);
            String val = "";
            if (null == paramValue) {
                val = "";
            } else if (paramValue instanceof String) {
                try {
                    val = URLEncoder.encode(paramValue.toString(), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else if(paramValue instanceof Number) {
                val = String.valueOf(paramValue);
            } else {
                val = paramValue.toString();
            }
            if (StringUtil.isNotBlank(val)) {
                sb.append(paramKey).append("=").append(val).append("&");
            }
        }
        // 最后接上约定的秘钥key
        sb.append("key=").append(key);

        return StringUtil.md5(sb.toString());
    }

    /**
     * 检查sign是否正确
     * @param requestBody 对象
     * @param key
     */
    public static void checkSign(Object requestBody, String key) {

        Map<String, Object> map = new HashMap<>();

        Class clazz = requestBody.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            try {
                Method method = clazz.getMethod("get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1));
                Object val = method.invoke(requestBody);
                map.put(fieldName, val);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        checkSign(map, key);
    }

    /**
     * 检查sign是否正确
     * @param map map
     * @param key
     */
    public static void checkSign(Map<String, Object> map, String key) {

        String trueSign = sign(map, key);
        String fromSign = (String)map.get("sign");

        if (!trueSign.equals(fromSign)) {
            log.debug("from sign : " + fromSign + ". calc sign : " + trueSign);
            throw new LogicException("auth_fail", "error.auth_fail");
        }
    }

    /**
     * 生成token
     *
     * @param claims
     * @return
     */
    public static String createToken(Map<String, String> claims, String key, int expiredSecond) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(key);
            JWTCreator.Builder builder = JWT.create();
            builder.withIssuer(ISSUER);
            //设置过期时间
            if (expiredSecond > 0) {
                builder.withExpiresAt(DateUtil.rollDate(new Date(), Calendar.SECOND, expiredSecond));
            }

            claims.forEach(builder::withClaim);
            return builder.sign(algorithm);
        } catch (IllegalArgumentException e) {
            throw new LogicException("sign_error", "鉴权失败", false);
        }
    }

    /**
     * 获得token中携带的自定义数据
     * @param token
     * @return
     */
    public static Map<String, String> tokenClaim(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        String payload = new String(Base64.getUrlDecoder().decode(decodedJWT.getPayload()));

        return JsonUtil.json2Object(payload, Map.class);
    }

    /**
     * 验证jwt，并返回数据
     */
    public static Map<String, String> verifyToken(String token, String key) {
        Algorithm algorithm;
        Map<String, Claim> map;
        try {
            algorithm = Algorithm.HMAC256(key);
            JWTVerifier verifier = JWT.require(algorithm).withIssuer(ISSUER).build();
            DecodedJWT jwt = verifier.verify(token);
            map = jwt.getClaims();
        } catch (TokenExpiredException e) {
            throw new LogicException("sign_error", "请重新进行系统登录", false);
        } catch (Exception e) {
            throw new LogicException("sign_error", "鉴权失败", false);
        }
        Map<String, String> resultMap = new HashMap<>(map.size());
        map.forEach((k, v) -> resultMap.put(k, v.asString()));
        return resultMap;
    }

    public static void main(String[] args) {
        Map<String, String> param = new HashMap<>();
        param.put("msUserId", "abcdefghijklmnopqrsuvwxyz123456");
        param.put("mobile", "13800138000");
        param.put("random", "2329830201238");

        String token = SignUtil.createToken(param, "abcdefghijklmnopqrsuvwxyz123456", 1);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info(token);

        Map<String, String> re1 = SignUtil.tokenClaim(token);

        log.info(JsonUtil.object2Json(re1));

        Map<String, String> re = SignUtil.verifyToken(token, "abcdefghijklmnopqrsuvwxyz123456");

        log.info(JsonUtil.object2Json(re));
    }

}
