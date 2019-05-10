package com.zrq.transaction.server.util;

import com.zrq.transaction.server.transactional.ZrqTransactionManager;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class HttpClient {
    public static String get(String url) {
        String result="";
        try {
            CloseableHttpClient httpClient= HttpClients.createDefault();

            HttpGet httpGet=new HttpGet(url);
            httpGet.addHeader("Content-type","application/json");
            httpGet.addHeader("groupId", ZrqTransactionManager.getCurrentGroupId());
            httpGet.addHeader("transactionCount",String.valueOf(ZrqTransactionManager.getTransactionCount()));
            CloseableHttpResponse response=httpClient.execute(httpGet);

            if(response.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
                result= EntityUtils.toString(response.getEntity(),"utf-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
