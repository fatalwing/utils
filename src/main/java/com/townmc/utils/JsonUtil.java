package com.townmc.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
