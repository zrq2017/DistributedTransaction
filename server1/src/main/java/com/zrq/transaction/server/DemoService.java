package com.zrq.transaction.server;

import com.zrq.transaction.server.annotation.ZrqTransactional;
import com.zrq.transaction.server.util.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DemoService {
    @Autowired
    private DemoDao demoDao;

    @ZrqTransactional(isStart=true)
    @Transactional
    public void test(){
        demoDao.insert("server1");
        HttpClient.get("http://localhost:8089/server2/test");
        int i=100/0;
    }
}
