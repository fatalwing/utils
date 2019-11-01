package com.townmc.utils;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;
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

    private UrlEncodedFormEntity getUrlEncodedFormEntity(Map<String, String> parameterMap) {
        List<NameValuePair> param = new ArrayList<NameValuePair>();
        for(Entry<String, String> paramEntry : parameterMap.entrySet()) {
            param.add(new BasicNameValuePair(paramEntry.getKey(), (String)paramEntry.getValue()));
        }
        return new UrlEncodedFormEntity(param, Consts.UTF_8);
    }

    private String getQueryString(Map<String, String> parameterMap) throws UnsupportedEncodingException {
        if(null == parameterMap || parameterMap.size() == 0) {
            return "";
        }
        String str = "";
        for(Entry<String, String> ent : parameterMap.entrySet()) {
            Object value = ent.getValue();
            if(null == value) value = "";
            str += "&" + ent.getKey() + "=" + URLEncoder.encode(value.toString(), "UTF-8");
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
     * post 请求
     * @param url 地址
     * @param param 参数
     * @return response body
     */
    public String post(String url, Map<String, String> param) {
        try {
            return new String(this.postx(url, param), StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * post 请求
     * @param url 地址
     * @param param 参数
     * @return response body
     */
    public byte[] postx(String url, Map<String, String> param) {
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

        httpPost.setEntity(this.getUrlEncodedFormEntity(param));

        try {
            HttpResponse response = httpClient.execute(httpPost, context);

            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                return transferByte(httpEntity);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * post 请求
     * @param url 地址
     * @param body 参数
     * @return response body
     */
    public String post(String url, String body) {
        try {
            return new String(this.postx(url, body), StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * post 请求
     * @param url 地址
     * @param body 参数
     * @return response body
     */
    public byte[] postx(String url, String body) {
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

        try {
            HttpResponse response = httpClient.execute(httpPost, context);

            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                return transferByte(httpEntity);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * get 请求
     * @param url 地址
     * @param param 参数
     * @return response body
     */
    public String get(String url, Map<String, String> param) {

        try {
            return new String(this.getx(url, param), StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * get 请求
     * @param url 地址
     * @param param 参数
     * @return response body
     */
    public byte[] getx(String url, Map<String, String> param) {
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
        if(requestConfig != null) httpGet.setConfig(requestConfig);
        if(this.header != null && !this.header.isEmpty()) {
            for(Map.Entry<String, String> entry : this.header.entrySet()) {
                httpGet.addHeader(entry.getKey(), entry.getValue());
            }
        }
        try {
            HttpResponse response = httpClient.execute(httpGet, context);
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                return transferByte(httpEntity);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * get 请求
     * @param url 地址
     * @return response body
     */
    public String get(String url) {
        return this.get(url, null);
    }

    /**
     * get 请求
     * @param url 地址
     * @return response body
     */
    public byte[] getx(String url) {
        return this.getx(url, null);
    }

    public void getFile(String url, String destFileName) {
        HttpGet httpGet = new HttpGet(url);
        if(this.header != null && !this.header.isEmpty()) {
            for(Map.Entry<String, String> entry : this.header.entrySet()) {
                httpGet.addHeader(entry.getKey(), entry.getValue());
            }
        }
        InputStream ins = null;
        File file = new File(destFileName);
        FileOutputStream fout = null;
        try {
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();

            ins = entity.getContent();
            fout = new FileOutputStream(file);
            int l = -1;
            byte[] tmp = new byte[1024];
            while ((l = ins.read(tmp)) != -1) {
                fout.write(tmp, 0, l);
                // 注意这里如果用OutputStream.write(buff)的话，图片会失真，大家可以试试
            }
            fout.flush();
        } catch(Exception ex) {

        } finally {
            // 关闭低层流。
            try {
                ins.close();
                fout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String uploadFile(String url, String name, File file) {
        //File file = new File(textFileName, ContentType.DEFAULT_BINARY);
        String re = null;
        HttpPost post = new HttpPost(url);
        if(requestConfig != null) post.setConfig(requestConfig);
        if(this.header != null && !this.header.isEmpty()) {
            for(Map.Entry<String, String> entry : this.header.entrySet()) {
                post.addHeader(entry.getKey(), entry.getValue());
            }
        }
        FileBody fileBody = new FileBody(file);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addPart(name, fileBody);
        HttpEntity entity = builder.build();
        post.setEntity(entity);
        HttpResponse response;
        try {
            response = httpClient.execute(post);
            if(HttpStatus.SC_OK==response.getStatusLine().getStatusCode()){

                HttpEntity entitys = response.getEntity();
                if (entity != null) {
                    re = (EntityUtils.toString(entitys));
                }
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return re;
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
                String jsonStr = toStringInfo(response.getEntity(),"UTF-8");

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


    private String toStringInfo(HttpEntity entity, String defaultCharset) throws Exception, IOException{
        final InputStream instream = entity.getContent();
        if (instream == null) {
            return null;
        }
        try {
            Args.check(entity.getContentLength() <= Integer.MAX_VALUE,
                    "HTTP entity too large to be buffered in memory");
            int i = (int)entity.getContentLength();
            if (i < 0) {
                i = 4096;
            }
            Charset charset = null;

            if (charset == null) {
                charset = Charset.forName(defaultCharset);
            }
            if (charset == null) {
                charset = org.apache.http.protocol.HTTP.DEF_CONTENT_CHARSET;
            }
            final Reader reader = new InputStreamReader(instream, charset);
            final CharArrayBuffer buffer = new CharArrayBuffer(i);
            final char[] tmp = new char[1024];
            int l;
            while((l = reader.read(tmp)) != -1) {
                buffer.append(tmp, 0, l);
            }
            return buffer.toString();
        } finally {
            instream.close();
        }
    }

    public void close() {
        // nothing now
    }
}
