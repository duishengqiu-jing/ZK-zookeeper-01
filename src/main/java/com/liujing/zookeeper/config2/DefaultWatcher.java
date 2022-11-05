package com.liujing.zookeeper.config2;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.CountDownLatch;

public class DefaultWatcher implements Watcher{

    private CountDownLatch cd;

    public void setCd(CountDownLatch cd) {
        this.cd = cd;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        //Default Watcher
        System.out.println("default:"+watchedEvent.toString());
        Watcher.Event.KeeperState state = watchedEvent.getState();
        Watcher.Event.EventType type = watchedEvent.getType();
        switch (state) {
            case Unknown:
                System.out.println("default watch:Unknown......");
                break;
            case Disconnected:
                System.out.println("default watch:Disconnected......");
                break;
            case NoSyncConnected:
                System.out.println("default watch:NoSyncConnected......");
                break;
            case SyncConnected:
                System.out.println("default watch:SyncConnected......");
                cd.countDown();
                break;
            case AuthFailed:
                System.out.println("default watch:AuthFailed......");
                break;
            case ConnectedReadOnly:
                System.out.println("default watch:ConnectedReadOnly......");
                break;
            case SaslAuthenticated:
                System.out.println("default watch:SaslAuthenticated......");
                break;
            case Expired:
                System.out.println("default watch:Unknown......");
                break;
            case Closed:
                System.out.println("default watch:Closed......");
                break;
        }

    }


}
