package com.zrq.transaction.server.netty;

import com.alibaba.fastjson.JSONObject;
import com.zrq.transaction.server.transactional.TransactionType;
import com.zrq.transaction.server.transactional.ZrqTransaction;
import com.zrq.transaction.server.transactional.ZrqTransactionManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private ChannelHandlerContext context;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        context=ctx;
    }

    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx,Object msg) throws Exception{
        System.out.println("接受数据："+msg.toString());
        JSONObject jsonObject=JSONObject.parseObject((String) msg);

        String command=jsonObject.getString("command");//create-创建事务组，add-添加事务
        String groupId=jsonObject.getString("groupId");//事务组id

        System.out.println("接收command："+command);
        //对事务进行操作
        //接收事务的通知

        ZrqTransaction zrqTransaction= ZrqTransactionManager.getTransaction(groupId);
        if(command.equals("rollback")){
            zrqTransaction.setTransactionType(TransactionType.rollback);
        }else if (command.equals("commit")){
            zrqTransaction.setTransactionType(TransactionType.commit);
        }
        zrqTransaction.getTask().signalTask();
    }

    public synchronized Object call(JSONObject data) throws Exception{
        context.writeAndFlush(data.toJSONString());
        return null;
    }
}
