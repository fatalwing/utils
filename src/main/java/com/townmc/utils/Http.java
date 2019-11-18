package com.townmc.utils;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;

public class Http {
    private HttpClient httpClient = null;
    private HttpClientContext context = null;
    private Map<String, String> header = null;
    private RequestConfig requestConfig = null;
    private Charset charset = null;

    public Http() {
        this.httpClient = HttpClients.createDefault();

        this.setContext();
        this.header = new HashMap<String, String>();

    }

    public Http(RequestConfig requestConfig) {
        this.requestConfig = requestConfig;
        this.httpClient = HttpClients.createDefault();
        this.setContext();
        this.header = new HashMap<String, String>();
    }

    public void addHeader(String key, String value) {
        this.header.put(key, value);
    }

    public void cleanHeader() {
        this.header.clear();
    }

    public void removeHeader(String key) {
        this.header.remove(key);
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public void setTimeout(int connectTimeout, int requestTimeout, int socketTimeout) {
        requestConfig = RequestConfig.custom().setConnectTimeout(connectTimeout)
                .setConnectionRequestTimeout(requestTimeout)
                .setSocketTimeout(socketTimeout).build();;
    }

    private HttpEntity getUrlEncodedFormEntity(Map<String, Object> parameterMap) {

        List<NameValuePair> param = new ArrayList<NameValuePair>();
        for(Entry<String, Object> paramEntry : parameterMap.entrySet()) {
            Object val = paramEntry.getValue();
            String value = "";
            if (val instanceof String) value = (String)val;
            else value = String.valueOf(val);

            param.add(new BasicNameValuePair(paramEntry.getKey(), value));
        }
        return new UrlEncodedFormEntity(param, Consts.UTF_8);
    }

    private HttpEntity getMultipartEntity(Map<String, Object> parameterMap) {

        //创建 MultipartEntityBuilder,以此来构建我们的参数
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        //设置字符编码，防止乱码
        ContentType contentType = ContentType.create("text/plain",Charset.forName("UTF-8"));
        //填充我们的文本内容，这里相当于input 框中的 name 与value
        for(Map.Entry<String, Object> entry: parameterMap.entrySet()) {
            String key = entry.getKey();
            Object val = entry.getValue();

            if (val instanceof String) {
                entityBuilder.addPart(key, new StringBody((String)val, contentType));
            } else if (val instanceof Number) {
                entityBuilder.addPart(key, new StringBody(String.valueOf(val), contentType));
            } else if (val instanceof File) {
                entityBuilder.addBinaryBody(key, (File)val);
            } else if (val instanceof byte[]) {
                entityBuilder.addBinaryBody(key, (byte[])val, ContentType.DEFAULT_BINARY, StringUtil.uuid());
            } else if (val instanceof InputStream) {
                entityBuilder.addBinaryBody(key, (InputStream)val, ContentType.DEFAULT_BINARY, StringUtil.uuid());
            }
        }
        return entityBuilder.build();
    }

    private String getQueryString(Map<String, Object> parameterMap) throws UnsupportedEncodingException {
        if(null == parameterMap || parameterMap.size() == 0) {
            return "";
        }
        String str = "";
        for(Entry<String, Object> ent : parameterMap.entrySet()) {
            Object value = ent.getValue();
            if(null == value) value = "";
            str += "&" + ent.getKey() + "=" + URLEncoder.encode(String.valueOf(value), "UTF-8");
        }
        if(str.startsWith("&")) str = str.substring(1);
        return str;
    }

    private byte[] transferByte(HttpEntity entity) throws IllegalStateException, IOException {
        if (null == this.charset) {
            this.charset = ContentType.getOrDefault(entity).getCharset();
        }
        if(null == this.charset) {
            this.charset = Charset.forName("UTF-8");
        }
        InputStream is = entity.getContent();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] temp = new byte[1024];
        int length;
        while((length = is.read(temp)) != -1) {
            output.write(temp, 0, length);
        }
        is.close();

        output.close();

        return output.toByteArray();
    }

    private Map<String, String> transferHeader(Header[] headers) {
        Map<String, String> map = new HashMap<>();
        if (null != headers && headers.length > 0) {
            for (Header header : headers) {
                map.put(header.getName(), header.getValue());
            }
        }
        return map;
    }

    /**
     * 设置上下文
     */
    private void setContext() {

        context = HttpClientContext.create();
        Registry<CookieSpecProvider> registry = RegistryBuilder.<CookieSpecProvider>create()
                .register(CookieSpecs.BEST_MATCH, new BestMatchSpecFactory())
                .register(CookieSpecs.BROWSER_COMPATIBILITY, new BrowserCompatSpecFactory()).build();
        context.setCookieSpecRegistry(registry);
    }

    /**
     * 发送post请求
     * 默认为application/x-www-form-urlencoded方式提交请求
     * @param url 请求地址
     * @param param 请求参数
     * @return
     */
    public HttpRes post(String url, Map<String, Object> param) {
        return this.post(url, param, false);
    }

    /**
     * 发送post请求
     * @param url 请求地址
     * @param param 请求参数
     * @param isMutipart 是否使用 multipart/form-data 方式提交
     *                   为 false 时 用application/x-www-form-urlencoded方式提交
     * @return
     */
    public HttpRes post(String url, Map<String, Object> param, boolean isMutipart) {
        if(!url.matches("^http(s)?:\\/\\/.*$")) {
            url = "http://" + url;
        }

        HttpPost httpPost = new HttpPost(url);
        if(requestConfig != null) httpPost.setConfig(requestConfig);
        if(this.header != null && !this.header.isEmpty()) {
            for(Map.Entry<String, String> entry : this.header.entrySet()) {
                httpPost.addHeader(entry.getKey(), entry.getValue());
            }
        }

        HttpEntity requestEntiry;
        if (isMutipart) {
            requestEntiry = this.getMultipartEntity(param);
        } else {
            requestEntiry = this.getUrlEncodedFormEntity(param);
        }

        httpPost.setEntity(requestEntiry);
        HttpRes res = new HttpRes();
        try {
            HttpResponse response = httpClient.execute(httpPost, context);
            res.setStatus(response.getStatusLine().getStatusCode());
            res.setHeaders(this.transferHeader(response.getAllHeaders()));

            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                res.setData(transferByte(httpEntity));
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public HttpRes post(String url, String body) {
        if(!url.matches("^http(s)?:\\/\\/.*$")) {
            url = "http://" + url;
        }

        HttpPost httpPost = new HttpPost(url);
        if(requestConfig != null) httpPost.setConfig(requestConfig);
        if(this.header != null && !this.header.isEmpty()) {
            for(Map.Entry<String, String> entry : this.header.entrySet()) {
                httpPost.addHeader(entry.getKey(), entry.getValue());
            }
        }
        httpPost.setEntity(new StringEntity(body, Consts.UTF_8));

        HttpRes res = new HttpRes();
        try {
            HttpResponse response = httpClient.execute(httpPost, context);
            res.setStatus(response.getStatusLine().getStatusCode());
            res.setHeaders(this.transferHeader(response.getAllHeaders()));

            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                res.setData(transferByte(httpEntity));
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * get 请求
     * @param url 地址
     * @param param 参数
     * @return response body
     */
    public HttpRes get(String url, Map<String, Object> param) {
        if(!url.matches("^http(s)?:\\/\\/.*$")) {
            url = "http://" + url;
        }

        if(param != null) {
            try {
                url = url + "?" + this.getQueryString(param);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        HttpGet httpGet = new HttpGet(url);
        HttpRes res = new HttpRes();
        if(requestConfig != null) httpGet.setConfig(requestConfig);
        if(this.header != null && !this.header.isEmpty()) {
            for(Map.Entry<String, String> entry : this.header.entrySet()) {
                httpGet.addHeader(entry.getKey(), entry.getValue());
            }
        }
        try {
            HttpResponse response = httpClient.execute(httpGet, context);
            res.setStatus(response.getStatusLine().getStatusCode());
            res.setHeaders(this.transferHeader(response.getAllHeaders()));

            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                res.setData(transferByte(httpEntity));
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }

    /**
     * get 请求
     * @param url 地址
     * @return response body
     */
    public HttpRes get(String url) {
        return this.get(url, null);
    }

    public String postWithCer(String httpsUrl, String xmlStr, String keyFile, String keyPassWord) throws Exception {
        KeyStore keyStore  = KeyStore.getInstance("PKCS12");
        FileInputStream instream = new FileInputStream(new File(keyFile));//P12文件目录
//    	InputStream instream = HTTPS.class.getResourceAsStream("/apiclient_cert.p12");
        try {
            keyStore.load(instream, keyPassWord.toCharArray());
        } finally {
            instream.close();
        }
        SSLContext sslcontext = SSLContexts.custom()
                .loadKeyMaterial(keyStore, keyPassWord.toCharArray())//这里也是写密码的
                .build();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1" }, null,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
        try {
            HttpPost httpost = new HttpPost(httpsUrl); // 设置响应头信息
            if(this.header != null && !this.header.isEmpty()) {
                for(Map.Entry<String, String> entry : this.header.entrySet()) {
                    httpost.addHeader(entry.getKey(), entry.getValue());
                }
            }
            httpost.addHeader("Connection", "keep-alive");
            httpost.addHeader("Accept", "*/*");
            httpost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            httpost.addHeader("Host", "api.mch.weixin.qq.com");
            httpost.addHeader("X-Requested-With", "XMLHttpRequest");
            httpost.addHeader("Cache-Control", "max-age=0");
            httpost.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");
            httpost.setEntity(new StringEntity(xmlStr, "UTF-8"));
            CloseableHttpResponse response = httpclient.execute(httpost);

            try {
                HttpEntity entity = response.getEntity();
                String jsonStr = new String(this.transferByte(response.getEntity()));

                //微信返回的报文时GBK，直接使用httpcore解析乱码
                //  String jsonStr = EntityUtils.toString(response.getEntity(),"UTF-8");
                EntityUtils.consume(entity);
                return jsonStr;
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }

    }

    public void close() {
        // nothing now
    }

}
