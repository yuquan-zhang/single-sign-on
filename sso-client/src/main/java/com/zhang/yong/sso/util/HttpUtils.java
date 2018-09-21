package com.zhang.yong.sso.util;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpUtils {
    private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);
    private final static String CHARSET = "UTF-8";

    public static String post(String url, Map<String,String> params, String contentType) throws Exception{
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> nvps = new ArrayList<>();
        for(Map.Entry<String,String> entry : params.entrySet()){
            nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        httpPost.setEntity(new UrlEncodedFormEntity(nvps));
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return new String(EntityUtils.toByteArray(entity),CHARSET);
            }
        } catch (Exception e) {
            logger.error("发送HTTP POST 请求出错!", e);
        } finally {
            try{
                if(response != null) response.close();
                httpclient.close();
            }catch (Exception e){
                logger.info(e.getMessage());
            }
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
            logger.error("发送HTTP GET请求出错！", e);
        } finally {
            if(response != null) response.close();
            httpclient.close();
        }
        return null;
    }
}
