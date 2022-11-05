package com.liujing.zookeeper.config2;

import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

public class ZKUtils {

    private static ZooKeeper zk;

    private static String path = "172.16.157.129:2181,172.16.157.130:2181,172.16.157.131:2181,172.16.157.132:2181/testConf";

    private static CountDownLatch cd = new CountDownLatch(1);

    private static DefaultWatcher defaultWatcher = new DefaultWatcher();

    public static ZooKeeper getZK(){
        try {
            zk = new ZooKeeper(path, 3000, defaultWatcher);
            defaultWatcher.setCd(cd);
            cd.wait();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return zk;
    }
}
