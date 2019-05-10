package com.zrq.transaction.server;

import com.zrq.transaction.server.annotation.ZrqTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DemoService {

    @Autowired
    private DemoDao demoDao;

    @ZrqTransactional(isEnd=true)
    @Transactional
    public void test(){
        demoDao.insert("server2");
    }
}
