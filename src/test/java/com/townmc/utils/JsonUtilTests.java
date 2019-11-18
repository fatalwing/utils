package com.townmc.utils;

import org.junit.Test;

import java.util.List;
import java.util.Map;

public class JsonUtilTests {

    //@Test
    public void testJson2Object() {

        /*
        {"name":"meng","age":1, "sex":"male", "list":["a","b","c"], "map":{"k1":"v1","k2":2}}
         */
        String jsonTxt = "{\"name\":\"meng\",\"age\":1, \"sex\":\"male\", \"list\":[\"a\",\"b\",\"c\"], \"map\":{\"k1\":\"v1\",\"k2\":2}}";

        Map<String, Object> re = JsonUtil.json2Object(jsonTxt, Map.class);

        String json = JsonUtil.object2Json(re);
    }
}
