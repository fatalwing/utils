package com.townmc.utils;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * http请求结果
 * @author meng
 */
public class HttpRes {
    /**
     * http请求状态码
     */
    private int status = 0;
    /**
     * http请求结果数据
     */
    private byte[] data;
    /**
     * 请求返回的Header
     */
    private Map<String, String> headers;

    /**
     * 获得请求的返回状态码， 200/300/400/500等
     * @return
     */
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * 获得请求返回的数据
     * @return
     */
    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * 获得请求返回的Header
     * @param headers
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    /**
     * 获得请求返回的结果字符串
     * @param charset 编码名称
     * @return
     */
    public String toString(String charset) {
        if (null != data) {
            try {
                return new String(this.data, charset);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return this.toString("UTF-8");
    }

}
