package com.zrq.transaction.txmanager;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private static ChannelGroup channelGroup=new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    //事务组中的事务状态列表
    private static Map<String, List<String>> transactionTypeMap=new HashMap<>();
    //事务组是否已经接收到结束的标记
    private static Map<String,Boolean> isEndMap=new HashMap<>();
    //事务组中应该有的事务个数
    private static Map<String,Integer> transactionCountMap=new HashMap<>();

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception{
        Channel channel=ctx.channel();
        channelGroup.add(ctx.channel());
    }

    /**
     * 创建事务组，并且添加保存事务
     * 并且需要判断，如果所有事务都已经执行了（有结果了，要么回滚，要么提交），且其中有一个事务需要回滚，那么通知所有的客户端进行回滚
     * 否则，则通知所有客户端进行提交
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx,Object msg) throws Exception{
        System.out.println("接受数据："+msg.toString());

        JSONObject jsonObject=JSONObject.parseObject((String) msg);

        String command=jsonObject.getString("command");//create-创建事务组，add-添加/注册事务
        String groupId=jsonObject.getString("groupId");//事务组id
        String transactionType=jsonObject.getString("transactionType");//子事务类型，commit-待提交，rollback-待回滚
        Integer transactionCount=jsonObject.getInteger("transactionCount");//事务数量
//        String transactionId=jsonObject.getString("transactionId");//分支事务ID
        Boolean isEnd=jsonObject.getBoolean("isEnd");//是否结束事务

        if("create".equals(command)){
            //创建事务组，开启全局事务
            transactionTypeMap.put(groupId,new ArrayList<String>());
        }else if("add".equals(command)){
            //加入事务组，注册分支事务
            transactionTypeMap.get(groupId).add(transactionType);
            if(isEnd){
                isEndMap.put(groupId,true);
                transactionCountMap.put(groupId,transactionCount);
            }

            JSONObject result=new JSONObject();
            result.put("groupId",groupId);
            //如果已经接收到结束事务的标记，比较事务是否已经全部到达，如果已经全部到达则看事务是否需要回滚
            if(isEndMap.get(groupId) && transactionCountMap.get(groupId).equals(transactionTypeMap.get(groupId).size())){
                if(transactionTypeMap.get(groupId).contains("rollback")){
                    result.put("command","rollback");
                    sendResult(result);
                }else {
                    result.put("command","commit");
                    sendResult(result);
                }
            }
        }
//        else if("regist".equals(command)){
//            //2.注册分支事务
//            transactionTypeMap.get(groupId).add(transactionId);
//            //3.注册的过程中发现有事务要回滚
//            if("rollback".equals(transactionType)){
//                System.out.println("接收到一个回滚");
//                sentMsg(groupId,"rollback");//整个事务组进行回滚
//            }
//        }else if("commit".equals(command)){
//            System.out.println("全局事务提交");
//            sentMsg(groupId,"commit");
//        }
    }

    private void sendResult(JSONObject result){
        for(Channel channel:channelGroup){
            System.out.println("发送数据："+result.toJSONString());
            channel.writeAndFlush(result.toJSONString());
        }
    }

}
