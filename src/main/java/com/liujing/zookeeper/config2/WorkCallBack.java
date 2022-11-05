package com.liujing.zookeeper.config2;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

public class WorkCallBack implements Watcher, AsyncCallback.DataCallback, AsyncCallback.StatCallback{

    private CountDownLatch cd;

    private MyConf myConf;

    private ZooKeeper zk;

    private CountDownLatch cc = new CountDownLatch(1);

    public void setCd(CountDownLatch cd) {
        this.cd = cd;
    }

    public void setMyConf(MyConf myConf) {
        this.myConf = myConf;
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    public void aWait() {
        zk.exists("/AppConf", this, this, "abcdef");
        try {
            cc.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        //work watcher
        System.out.println("work watch:"+watchedEvent.toString());
        Watcher.Event.KeeperState state = watchedEvent.getState();
        Watcher.Event.EventType type = watchedEvent.getType();

        switch (type) {
            case None:
                System.out.println("work type:None......");
                break;
            case NodeCreated:
                System.out.println("work type:NodeCreated......");
                zk.getData("/AppConf", this, this, "abcde");
                break;
            case NodeDeleted:
                System.out.println("work type:NodeDeleted......");
                zk.getData("/AppConf", this, this, "abcde");
                break;
            case NodeDataChanged:
                System.out.println("work type:NodeDataChanged......");
                zk.getData("/AppConf", this, this, "abcde");
                break;
            case NodeChildrenChanged:
                System.out.println("work type:NodeChildrenChanged......");
                break;
            case DataWatchRemoved:
                System.out.println("work type:DataWatchRemoved......");
                break;
            case ChildWatchRemoved:
                System.out.println("work type:ChildWatchRemoved......");
                break;
            case PersistentWatchRemoved:
                System.out.println("work type:type......");
                break;
        }
    }

    @Override
    public void processResult(int i, String s, Object o, byte[] bytes, Stat stat) {
        //Data Callback
        if (bytes != null) {
            //返回数据不为空表示该节点存在数据了
            System.out.println("Data CallBack："+new String(bytes));
            myConf.setConf(new String(bytes));
            cc.countDown();
        }
    }

    @Override
    public void processResult(int i, String s, Object o, Stat stat) {
        //Stat CallBack
        if (stat != null) {
            //返回的节点元数据信息不为空表示已经创建好节点了
            System.out.println("Stat CallBack:"+i);
            zk.getData("/AppConf", this, this, "statcallbacksss");
        }

    }
}
