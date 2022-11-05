package com.liujing.zookeeper.config;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.CountDownLatch;

public class DefaultWatcher implements Watcher {

    private CountDownLatch countDownLatch = null;

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("default watcher....");
        Event.KeeperState state = watchedEvent.getState();
        Event.EventType type = watchedEvent.getType();
        switch (state) {
            case Unknown:
                System.out.println("default watch state:Unknown......");
                break;
            case Disconnected:
                System.out.println("default watch state:Disconnected......");
                break;
            case NoSyncConnected:
                System.out.println("default watch state:NoSyncConnected......");
                break;
            case SyncConnected:
                System.out.println("default watch state:SyncConnected......");
                countDownLatch.countDown();
                break;
            case AuthFailed:
                System.out.println("default watch state:AuthFailed......");
                break;
            case ConnectedReadOnly:
                System.out.println("default watch state:ConnectedReadOnly......");
                break;
            case SaslAuthenticated:
                System.out.println("default watch state:SaslAuthenticated......");
                break;
            case Expired:
                System.out.println("default watch state:Expired......");
                break;
            case Closed:
                System.out.println("default watch state:Closed......");
                break;
        }

        switch (type) {
            case None:
                System.out.println("default watch type:None......");
                break;
            case NodeCreated:
                System.out.println("default watch type:NodeCreated......");
                break;
            case NodeDeleted:
                System.out.println("default watch type:NodeDeleted......");
                break;
            case NodeDataChanged:
                System.out.println("default watch type:NodeDataChanged......");
                break;
            case NodeChildrenChanged:
                System.out.println("default watch type:NodeChildrenChanged......");
                break;
            case DataWatchRemoved:
                System.out.println("default watch type:DataWatchRemoved......");
                break;
            case ChildWatchRemoved:
                System.out.println("default watch type:ChildWatchRemoved......");
                break;
            case PersistentWatchRemoved:
                System.out.println("default watch type:PersistentWatchRemoved......");
                break;
        }
    }
}
