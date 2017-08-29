/*
1. 将一个json串中的值解析出来。
假设我们有一个这样的json数据: [{"item_name":"a","price":12,"member_price":8,"stock_count":500,"box_count":1,"box_price":2}]

import com.townmc.util.jackson.databind.JsonNode;
import com.townmc.util.jackson.databind.ObjectMapper;

ObjectMapper mapper = new ObjectMapper();
try {
    JsonNode root = mapper.readTree(json);
    for(Iterator<JsonNode> its = root.elements(); its.hasNext();) {
        JsonNode node = its.next();
        String itemName = node.path("item_name").asText();
        int price = node.path("price").asInt();
        int memberPrice = node.path("member_price").asInt();
        int stockCount = node.path("stock_count").asInt();
        int boxCount = node.path("box_count").asInt();
        int boxPrice = node.path("box_price").asInt();

        log.debug("item_name:" + itemName + ". price:" + price + ". member_price:" + memberPrice +
                ". stock_count:" + stockCount + ". box_count:" + boxCount + ". box_price:" + boxPrice);
    }
} catch (IOException e) {
    // do something
}

2. 将一个对象转换成json串,以及将这个串解析回对象
Merchant m = new Merchant();
m.setMerchantId("1001");
m.setMerchantName("测试");
m.setDateCreated(new Date());
m.setLastUpdated(new Date());
m.setVersion(12098741092873412L);
m.setIsDeleted(0);

ObjectMapper objectMapper = new ObjectMapper();
try {
    String json = objectMapper.writeValueAsString(m);
    log.debug(json);
    // 输出 {"merchantId":"1001","merchantName":"测试","version":12098741092873412,"dateCreated":1470386210410,"lastUpdated":1470386210410,"isDeleted":0}

    Merchant rs = objectMapper.readValue(json, Merchant.class);
    log.debug(rs.getMerchantName());
    // 输出"测试"
} catch (JsonProcessingException e) {
    e.printStackTrace();
} catch (IOException e) {
    e.printStackTrace();
}
*/
package com.townmc.utils;


import com.townmc.utils.jackson.core.JsonProcessingException;
import com.townmc.utils.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonUtil {

    public static <T> T json2Object(String txt, Class<T> clazz) {

        ObjectMapper objectMapper = new ObjectMapper();
        T obj = null;
        try {
            obj = objectMapper.readValue(txt, clazz);
        } catch (IOException e) {
            e.printStackTrace();
            throw new LogicException("parser json error! " + e.getMessage());
        }
        return obj;
    }

    public static String object2Json(Object obj) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(obj);
            return json;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new LogicException("parser json error! " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            throw new LogicException("parser json error! " + e.getMessage());
        }
    }
}
