package com.myim.NetService;

import org.apache.http.client.methods.HttpPost;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.MalformedInputException;

/**
 * Created by Administrator on 2015/4/18.
 */
public class HttpConnet {
    private static final String url = Constant.HTTP_HOST+"plugins/myofplugin/";

    public HttpURLConnection getConn(String urlPath) {
        String fianlUrl = url + urlPath;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(fianlUrl);
            connection = (HttpURLConnection) url.openConnection();     /*������*/
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout(5*1000);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
        } catch (MalformedInputException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public  HttpPost getHttpPost(String urlPath) {
        HttpPost httpPost = new HttpPost(url+urlPath);
        System.out.println(url+urlPath);
        return httpPost;
    }
}
