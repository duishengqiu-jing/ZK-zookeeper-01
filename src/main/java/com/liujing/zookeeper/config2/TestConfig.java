package com.liujing.zookeeper.config2;

import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestConfig {

    private ZooKeeper zk;

    @Before
    public void conn(){
        zk = ZKUtils.getZK();
    }

    @After
    public void close(){
        try {
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getConf(){
        WorkCallBack workCallBack = new WorkCallBack();
        MyConf myConf = new MyConf();
        workCallBack.setZk(zk);
        workCallBack.setMyConf(myConf);
        workCallBack.aWait();
        while (true) {
            System.out.println(myConf.getConf());
        }
    }
}
