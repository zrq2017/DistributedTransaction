package com.zrq.transaction.server.transactional;

import com.alibaba.fastjson.JSONObject;
import com.zrq.transaction.server.netty.NettyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class ZrqTransactionManager {

    private static NettyClient nettyClient;

    private static ThreadLocal<ZrqTransaction> current=new ThreadLocal<>();
    private static ThreadLocal<String> currentGroupId=new ThreadLocal<>();
    private static ThreadLocal<Integer> transactionCount=new ThreadLocal<>();

    @Autowired
    public void setNettyClient(NettyClient nettyClient){
        ZrqTransactionManager.nettyClient=nettyClient;
    }

    private static Map<String,ZrqTransaction> ZRQ_TRANSACTION_MAP=new HashMap<>();

    /**
     * 创建全局事务组
     * @return
     */
    public static String createTransactionGroup(){
        String groupId= UUID.randomUUID().toString();
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("command","create");//create-创建事务组，add-添加事务
        jsonObject.put("groupId",groupId);
        //保存全局事务组ID
        currentGroupId.set(groupId);
        nettyClient.send(jsonObject);
        return groupId;
    }

    /**
     * 创建分支事务
     * @param groupId
     * @return
     */
    public static ZrqTransaction createTranscation(String groupId){
        String transactionId=UUID.randomUUID().toString();
        ZrqTransaction zrqTransaction=new ZrqTransaction(groupId,transactionId);
        ZRQ_TRANSACTION_MAP.put(groupId,zrqTransaction);
        current.set(zrqTransaction);
        return zrqTransaction;
    }

    /**
     * 添加分支事务状态
     * @param zrqTransaction
     * @param isEnd
     * @param transactionType
     * @return
     */
    public static ZrqTransaction addTranscation(ZrqTransaction zrqTransaction,Boolean isEnd,TransactionType transactionType){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("groupId",zrqTransaction.getGroupId());
        jsonObject.put("transcationId",zrqTransaction.getTransactionId());
        jsonObject.put("transcationType",transactionType);
        jsonObject.put("command","add");
        jsonObject.put("isEnd",isEnd);
        nettyClient.send(jsonObject);
        System.out.println("添加事务");
        return zrqTransaction;
    }

    /**
     * 提交全局事务
     * @param groupId
     */
    public static void commitGlobalTransaction(String groupId){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("groupId",groupId);
        jsonObject.put("command","commit");
        nettyClient.send(jsonObject);
        System.out.println("提交全局事务");
    }

    public static ZrqTransaction getTransaction(String groupId){
        return ZRQ_TRANSACTION_MAP.get(groupId);
    }

    public static ZrqTransaction getCurrent(){
        return current.get();
    }

    public static String getCurrentGroupId() {
        return currentGroupId.get();
    }

    public static void setCurrentGroupId(String groupId) {
        currentGroupId.set(groupId);
    }

    public static Integer getTransactionCount() {
        return transactionCount.get();
    }

    public static void setTransactionCount(Integer count) {
        transactionCount.set(count);
    }

    public static Integer addTransactionCount(){
        int i = (transactionCount.get() == null ? 0 : transactionCount.get()) + 1;
        transactionCount.set(i);
        return i;
    }
}
