package com.zhang.yong.sso.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtils {
    private static Logger log = LoggerFactory.getLogger(HttpUtils.class);
    private final static String CHARSET = "UTF-8";

    public static String post(String url,String params,String contentType) throws Exception{
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        StringEntity stringEntity = new StringEntity(params, CHARSET);
        stringEntity.setContentType(contentType);
        httpPost.setEntity(stringEntity);
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return new String(EntityUtils.toByteArray(entity),CHARSET);
            }
        } catch (Exception e) {
            log.error("发送HTTP POST 请求出错!", e);
        } finally {
            if(response != null) response.close();
            httpclient.close();
        }
        return null;
    }

    public static String get(String url) throws Exception{
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return new String(EntityUtils.toByteArray(entity),CHARSET);
            }
        } catch (Exception e) {
            log.error("发送HTTP GET 请求出错！", e);
        } finally {
            if(response != null) response.close();
            httpclient.close();
        }
        return null;
    }
}
