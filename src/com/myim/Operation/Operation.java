package com.myim.Operation;

import com.myim.NetService.HttpConnet;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/4/20.
 */
public class Operation {

    public String login(String url, String username, String password) {
        String result = null;

        /**
         * ���÷�����������
         */
        HttpConnet conNet = new HttpConnet();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("password", password));
        try {
            HttpEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
            HttpPost httpPost = conNet.getHttpPost(url);
            System.out.println(httpPost.toString());
            httpPost.setEntity(entity);
            HttpClient client = new DefaultHttpClient();
            HttpResponse httpResponse = client.execute(httpPost);

            /**
             * �ж�״̬��
             * ���ͳɹ�
             * */
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                result = EntityUtils.toString(httpResponse.getEntity(), "utf-8");

            } else {
                result = "��¼ʧ��";
            }
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        } catch (ClientProtocolException e) {

            e.printStackTrace();
        } catch (ParseException e) {

            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}

