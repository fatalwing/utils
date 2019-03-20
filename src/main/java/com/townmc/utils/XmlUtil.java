package com.townmc.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

public class XmlUtil {
    private static final Log log = LogFactory.getLog(XmlUtil.class);

    public static Map<String, Object> xml2Map(String xml) {
        try {
            Document doc = DocumentHelper.parseText(xml);
            return dom2Map(doc);
        } catch (DocumentException e) {
            throw new LogicException("xml_2_map_error", e.getMessage());
        }
    }

    public static Map<String, Object> dom2Map(Document doc){
        Map<String, Object> map = new HashMap<String, Object>();
        if(doc == null)
            return map;
        Element root = doc.getRootElement();
        for (Iterator iterator = root.elementIterator(); iterator.hasNext();) {
            Element e = (Element) iterator.next();
            List list = e.elements();
            if(list.size() > 0){
                map.put(e.getName(), dom2Map(e));
            }else
                map.put(e.getName(), e.getText());
        }
        return map;
    }

    public static Map dom2Map(Element e){
        Map map = new HashMap();
        List list = e.elements();
        if(list.size() > 0){
            for (int i = 0;i < list.size(); i++) {
                Element iter = (Element) list.get(i);
                List mapList = new ArrayList();

                if(iter.elements().size() > 0){
                    Map m = dom2Map(iter);
                    if(map.get(iter.getName()) != null){
                        Object obj = map.get(iter.getName());
                        if(!obj.getClass().getName().equals("java.util.ArrayList")){
                            mapList = new ArrayList();
                            mapList.add(obj);
                            mapList.add(m);
                        }
                        if(obj.getClass().getName().equals("java.util.ArrayList")){
                            mapList = (List) obj;
                            mapList.add(m);
                        }
                        map.put(iter.getName(), mapList);
                    }else
                        map.put(iter.getName(), m);
                }
                else{
                    if(map.get(iter.getName()) != null){
                        Object obj = map.get(iter.getName());
                        if(!obj.getClass().getName().equals("java.util.ArrayList")){
                            mapList = new ArrayList();
                            mapList.add(obj);
                            mapList.add(iter.getText());
                        }
                        if(obj.getClass().getName().equals("java.util.ArrayList")){
                            mapList = (List) obj;
                            mapList.add(iter.getText());
                        }
                        map.put(iter.getName(), mapList);
                    }else
                        map.put(iter.getName(), iter.getText());
                }
            }
        }else
            map.put(e.getName(), e.getText());
        return map;
    }

    /**
     * (多层)map转换为xml格式字符串
     *
     * @param map 需要转换为xml的map
     * @param isCDATA 是否加入CDATA标识符 true:加入 false:不加入
     * @return xml字符串
     */
    public static String map2Xml(Map<String, Object> map, boolean isCDATA){
        String rootName = "xml";
        return map2Xml(map, isCDATA, rootName);
    }

    /**
     * (多层)map转换为xml格式字符串
     *
     * @param map 需要转换为xml的map
     * @param isCDATA 是否加入CDATA标识符 true:加入 false:不加入
     * @return xml字符串
     */
    public static String map2Xml(Map<String, Object> map, boolean isCDATA, String rootName){
        Document doc = DocumentHelper.createDocument();
        doc.addElement(rootName);
        String xml = map2Xml(doc.getRootElement(), rootName, map, isCDATA);
        return formatXML(xml);
    }


    /**
     * multilayerMapToXml核心方法，递归调用
     *
     * @param element 节点元素
     * @param parentName 根元素属性名
     * @param map 需要转换为xml的map
     * @param isCDATA 是否加入CDATA标识符 true:加入 false:不加入
     * @return xml字符串
     */
    private static String map2Xml(Element element, String parentName, Map<String, Object> map, boolean isCDATA) {
        Element xmlElement = element.addElement(parentName);
        for(Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object obj = entry.getValue();
            if (obj instanceof Map) {
                map2Xml(xmlElement, key, (Map<String, Object>)obj, isCDATA);
            } else {
                String value = obj == null ? "" : obj.toString();
                if (isCDATA) {
                    xmlElement.addElement(key).addCDATA(value);
                } else {
                    xmlElement.addElement(key).addText(value);
                }
            }
        }
        return xmlElement.asXML();
    }

    /**
     * 格式化xml,显示为容易看的XML格式
     *
     * @param xml 需要格式化的xml字符串
     * @return
     */
    public static String formatXML(String xml) {
        String requestXML = null;
        try {
            // 拿取解析器
            SAXReader reader = new SAXReader();
            Document document = reader.read(new StringReader(xml));
            if (null != document) {
                StringWriter stringWriter = new StringWriter();
                // 格式化,每一级前的空格
                OutputFormat format = new OutputFormat("    ", true);
                // xml声明与内容是否添加空行
                format.setNewLineAfterDeclaration(false);
                // 是否设置xml声明头部
                format.setSuppressDeclaration(false);
                // 是否分行
                format.setNewlines(true);
                XMLWriter writer = new XMLWriter(stringWriter, format);
                writer.write(document);
                writer.flush();
                writer.close();
                requestXML = stringWriter.getBuffer().toString();
            }
            return requestXML;
        } catch (Exception e) {
            log.error("格式化xml，失败 --> {}", e);
            return null;
        }
    }
}
