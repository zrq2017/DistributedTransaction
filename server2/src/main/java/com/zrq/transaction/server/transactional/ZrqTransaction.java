package com.zrq.transaction.server.transactional;

import com.zrq.transaction.server.util.Task;

public class ZrqTransaction {
    private String groupId;
    private String transactionId;
    private TransactionType transactionType;
    private Task task;

    public ZrqTransaction(String groupId, String transactionId) {
        this.groupId = groupId;
        this.transactionId = transactionId;
        this.task=new Task();
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
