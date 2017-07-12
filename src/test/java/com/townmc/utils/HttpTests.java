package com.townmc.utils;

import org.junit.Test;

/**
 * Created by meng on 2016/9/6.
 */
public class HttpTests {

    //@Test
    public void postTest() {
        Http http = new Http();
        String body = "{\"component_appid\":\"wxa8f16cd3d54e8d12\" ,\"component_appsecret\": \"7c05d6a58ac07d767ca006b67f7c4234\", \"component_verify_ticket\": \"ticket_value\"}";
        String re = http.post("https://api.weixin.qq.com/cgi-bin/component/api_component_token", body);

        System.out.println(re);
    }
}
