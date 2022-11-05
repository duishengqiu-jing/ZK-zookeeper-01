package com.liujing.zookeeper.config;

import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

public class ZKUtils {
    private static ZooKeeper zooKeeper;
    private static String parentPath = "172.16.157.129:2181,172.16.157.130:2181,172.16.157.131:2181,172.16.157.132:2181/testConfig";
    private static CountDownLatch countDownLatch = new CountDownLatch(1);
    public static ZooKeeper getZK() {
        DefaultWatcher defaultWatcher = new DefaultWatcher();

        try {
            zooKeeper = new ZooKeeper(parentPath, 3000, defaultWatcher);
            defaultWatcher.setCountDownLatch(countDownLatch);
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return zooKeeper;
    }
}
