package com.townmc.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlUtilTests {
    private static final Log log = LogFactory.getLog(XmlUtilTests.class);

    //@Test
    public void testMap2Xml() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("a", "v1");
        map.put("b", "v2");

        Map<String, Object> ii = new HashMap<String, Object>();
        ii.put("c", "v3");
        ii.put("d", "v4");

        List<Map<String, Object>> list = new ArrayList<>();
        list.add(ii);

        map.put("e", ii);

        log.info(XmlUtil.map2Xml(map, true, "merchant"));

        log.info(XmlUtil.map2Xml(map, false, "merchant"));
    }

    //@Test
    public void testXml2Map() {
        String xml = "<xml>\n" +
                "    <a><![CDATA[v1]]></a>\n" +
                "    <b><![CDATA[v2]]></b>\n" +
                "    <e>\n" +
                "        <c><![CDATA[v3]]></c>\n" +
                "        <d><![CDATA[v4]]></d>\n" +
                "    </e>\n" +
                "</xml>";

        Map<String, Object> map = XmlUtil.xml2Map(xml);

        log.info(JsonUtil.object2Json(map));
    }
}

