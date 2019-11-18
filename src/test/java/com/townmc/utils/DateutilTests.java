package com.townmc.utils;

import org.junit.Test;

import java.util.Date;
import java.util.TimeZone;

public class DateutilTests {

    //@Test
    public void fecthAllTimeZoneIds() {
        String[] res = DateUtil.fecthAllTimeZoneIds();

        System.out.println(JsonUtil.object2Json(res));
    }

    //@Test
    public void test() {
        String date = DateUtil.dateToString(DateUtil.FULL_TIME_PATTERN, new Date(), TimeZone.getTimeZone("Canada/Eastern"));
        System.out.println(date);
    }
}
