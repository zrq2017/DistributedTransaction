package com.zrq.transaction.server;

import org.apache.ibatis.annotations.Insert;
import org.springframework.stereotype.Repository;

@Repository
public interface DemoDao {
    //test数据库仅一张表test两个字段，id与name
    @Insert("insert test(name) value(#{name})")
    void insert(String name);
}
