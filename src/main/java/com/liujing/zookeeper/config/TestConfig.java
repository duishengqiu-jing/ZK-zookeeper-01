package com.liujing.zookeeper.config;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.List;

public class TestConfig {
    public static void main(String[] args) {
        //1、获得zookeeper
        ZooKeeper zk = ZKUtils.getZK();
        ZooKeeper.States state = zk.getState();
        switch (state) {
            case CONNECTING:
                System.out.println("zk state:CONNECTING......");
                break;
            case ASSOCIATING:
                System.out.println("zk state:ASSOCIATING......");
                break;
            case CONNECTED:
                System.out.println("zk state:CONNECTED......");
                break;
            case CONNECTEDREADONLY:
                System.out.println("zk state:CONNECTEDREADONLY......");
                break;
            case CLOSED:
                System.out.println("zk state:CLOSED......");
                break;
            case AUTH_FAILED:
                System.out.println("zk state:AUTH_FAILED......");
                break;
            case NOT_CONNECTED:
                System.out.println("zk state:NOT_CONNECTED......");
                break;
        }
        long ttl = 3000;
        //2、使用zookeeper
        try {
            zk.create("/lj01", "asdaddad".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.EPHEMERAL);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //3、断开zookeeper
    }

}
