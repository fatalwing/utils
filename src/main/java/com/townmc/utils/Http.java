package com.townmc.utils;

import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.message.BasicNameValuePair;

public class Http {
    private HttpClient httpClient = null;
    private HttpClientContext context = null;

    public Http() {
        this.httpClient = HttpClients.createDefault();

        this.setContext();
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
            str += "&" + ent.getKey() + "=" + URLEncoder.encode(value.toString(), "UTF-8");
        }
        str = str.substring(1);
        return str;
    }

    private String transferString(HttpEntity entity) throws IllegalStateException, IOException {
        Charset charset = ContentType.getOrDefault(entity).getCharset();

        if(null == charset) charset = Charset.forName("UTF-8");
        InputStream is = entity.getContent();
        StringBuffer sb = new StringBuffer();
        BufferedReader br = new BufferedReader(new InputStreamReader(is, charset));
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        is.close();
        return sb.toString();
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
        if(!url.matches("^http(s)?:\\/\\/.*$")) {
            url = "http://" + url;
        }

        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.94 Safari/537.36");

        httpPost.setEntity(this.getUrlEncodedFormEntity(param));

        try {
            HttpResponse response = httpClient.execute(httpPost, context);

            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                return transferString(httpEntity);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
    public String post(String url, String body) {
        if(!url.matches("^http(s)?:\\/\\/.*$")) {
            url = "http://" + url;
        }

        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.94 Safari/537.36");

        httpPost.setEntity(new StringEntity(body, Consts.UTF_8));

        try {
            HttpResponse response = httpClient.execute(httpPost, context);

            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                return transferString(httpEntity);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
    public String get(String url, Map<String, String> param) {
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
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.94 Safari/537.36");

        try {
            HttpResponse response = httpClient.execute(httpGet, context);
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                return transferString(httpEntity);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * get 请求
     * @param url 地址
     * @return response body
     */
    public String get(String url) {
        return this.get(url, null);
    }

    public void getFile(String url, String destFileName) {
        HttpGet httpGet = new HttpGet(url);
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

    public void close() {
        // nothing now
    }
}