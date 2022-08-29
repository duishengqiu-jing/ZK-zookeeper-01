package com.liujing.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception {
        System.out.println( "Hello World!" );

        //zk 有session概念的，watch emch节点也好都是跟session绑定的，没有线程池的概念
        //不要想着zk集群15台，那么客户端是不是要得到一个线程池准备十个二十个线程连进去
        //如果并发的话就可以复用连接池了，没有连接池的概念，不要这样想
        //因为每一个连接得到一个独立的session，监控还分在不同的session里边做观察，未来会出现问题
        //传参 随机负载，不确定连到谁
        // watch分为两类：在new zk的时候传入的watch，是session级别，跟path，node没有关系，
        // 如果只给默认watch的话，只能收到关于我这个session连接或者某一个server断掉之后重新连接别人的过程
        // 先来演示这个过程
        final CountDownLatch cd = new CountDownLatch(1);
        final ZooKeeper zooKeeper = new ZooKeeper(
                "172.16.157.129:2181,172.16.157.130:2181,172.16.157.131:2181,172.16.157.132:2181",
                3000,
                new Watcher() {
                    @Override
                    public void process(WatchedEvent watchedEvent) {
                        //实现被回调的方法,依赖事件回调的，到底是什么事件呢，可以根据state type做switch
                        //依赖
                        Event.KeeperState state = watchedEvent.getState();
                        Event.EventType type = watchedEvent.getType();
                        String path = watchedEvent.getPath();
                        //只跟连接关系有关系
                        System.out.println("new zk watch："+watchedEvent.toString());
                        //alt+enter,可以把匹配的状态都罗列出来
                        //可以根据状态和类型做不同的事情，但是到底是哪个类型呢
                        //只有等类型事件发生的时候才做回调
                        switch (state) {
                            case Unknown:
                                break;
                            case Disconnected:
                                System.out.println("session:disconnected");
                                break;
                            case NoSyncConnected:
                                break;
                            case SyncConnected:
                                //如果连接成功，可以打印一句话
                                System.out.println("session:connected");
                                cd.countDown();
                                break;
                            case AuthFailed:
                                break;
                            case ConnectedReadOnly:
                                break;
                            case SaslAuthenticated:
                                break;
                            case Expired:
                                break;
                            case Closed:
                                break;
                        }

                        switch (type) {
                            case None:
                                break;
                            case NodeCreated:
                                break;
                            case NodeDeleted:
                                break;
                            case NodeDataChanged:
                                break;
                            case NodeChildrenChanged:
                                break;
                            case DataWatchRemoved:
                                break;
                            case ChildWatchRemoved:
                                break;
                            case PersistentWatchRemoved:
                                break;
                        }
                    }
                });
        //用法：整个线性代码在这里出现阻塞，直到真正集群回调事件之后
        //事件里边状态是连接成功之后才会往下走
        cd.await();
        //并不是事件里边拿出来的state，因为事件里边是KeeperState
        ZooKeeper.States state = zooKeeper.getState();
        //上面已经连接完了，到这步的时候，拿出来之后看状态
        switch (state) {
            case CONNECTING:
                System.out.println("session:ing...");
                break;
            case ASSOCIATING:
                break;
            case CONNECTED:
                System.out.println("session:ed...");
                break;
            case CONNECTEDREADONLY:
                break;
            case CLOSED:
                break;
            case AUTH_FAILED:
                break;
            case NOT_CONNECTED:
                System.out.println("session:not_conn...");
                break;
        }

        //拿到zk还能crud
        //可以用react模式的调用了create但是不阻塞，
        // 创建成功之后调用回调方法，有两类api：一个同步阻塞的；另一个异步模型
        //eg：同步模型
        //返回值是pathname，如果是带序列的话不确定是00几所以返回值很有必要
        String pathname = zooKeeper.create(
                "/liujing",//路径
                "olddata".getBytes(),//数据 是字节数组
                ZooDefs.Ids.OPEN_ACL_UNSAFE,//ACL，权限，简单的用open没有权限限制的
                CreateMode.EPHEMERAL//先选用一个简单的，随着session的
        );
        final Stat stat = new Stat();
        //补充：watch的注册只会发生在读类型调用，比如get，exist，
        // 因为写方法是产生事件
        //取目录，有四种.
//        同步
//        byte[] getData(String path, Watcher watcher, Stat stat)
//        byte[] getData(String path, boolean watch, Stat stat)
//        异步
//        getData(String path, Watcher watcher, DataCallback cb, Object ctx)
//        getData(String path, boolean watch, DataCallback cb, Object ctx)
//        注册了一个对/liujing的监控
        byte[] node = zooKeeper.getData(
                "/liujing",
                //对于这个path的观察：针对这个path有不同的watch
                //或者是boolean（
                // false只取数据不观察是否有事件，
                // true：发生在写watch回调的时候再写一个true那么会重新监听注册，因为watch是一次性的）
                //你调用了数据不阻塞
                //这个watch才和path有关系
                //如果想只要有变化都调用一次，纠缠重复注册就行，就在上一次事件被回调完立刻注册
                //如果想取数时监测你，且你有事件调完我之后我的事情处理完了我还要监控你，所以继续
                new Watcher() {
                    //这是未来有事件被回调的时候除了自己处理再去往里面注册
                    @Override
                    public void process(WatchedEvent watchedEvent) {
                        //针对这个path的观察，
                        System.out.println("path:getData watch:" + watchedEvent.toString());
                        try {
                            zooKeeper.getData(
                                    "/liujing",
                                    //true,//true表示继续把default watch放进来而不是这个watch
                                    this,//this才是继续把当前watch放进来继续监测变化回调
                                    stat
                            );
                        } catch (KeeperException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                },
                //path分为两种数据：1、1M数据；2、元数据，含cZxid,ctime等元数据,
                //元数据是通过stat放进去的，调用getData的时候返回的是数据，
                // 传stat是放的node的元数据
                stat//既取了数据又拿到了元数据
        );
        System.out.println(node.toString());
        //修改/liujing路径的数据会发生：
        //1、修改数据；2、回调当时注册这个路径的观察回调方法
        Stat stat1 = zooKeeper.setData("/liujing", "newdata".getBytes(), 0);
        //改的第二次还会触发回调吗？不会
        Stat stat2 = zooKeeper.setData("/liujing", "newdata01".getBytes(), stat1.getVersion());
        //1、由于上面node是EPHEMERAL，zk设置了timeout设置了3s所以连接断开之后会留3s，3s之后会消失
        //2、判断输出取到了一个olddata，打印出来了，getdata

        //如何实现不断开?
//        Thread.sleep(1000000);
        //1、new zk时候的watch是session级别，session上边有什么事儿都能被调起
        //2、能够failover，可以切换到别的server
        //3、切换之后sessionid不会变
        //以上三点被验证了

        System.out.println("--------async start----------");

        //异步回调
        zooKeeper.getData(
                "/liujing",
                false,
                new AsyncCallback.DataCallback() {
                    @Override
                    public void processResult(int i, String s, Object o, byte[] bytes, Stat stat) {
                        //实现回调，get行不会阻塞因为没有返回值，get完成之后立马往下走，
                        // 请求回来之后会调用此方法，回调方法中可以拿到返回状态码，路径，
                        // 以及如果取到了他的数据是啥，元数据是啥，context是啥，
                        System.out.println("---async callback----");
                        System.out.println(i+s+o.toString()+new String(bytes)+stat.toString());
                        System.out.println(new String(bytes));
                    }
                },
                "abc");
        System.out.println("--------------async over-------------");

        Thread.sleep(10000);
        //回调敏不敏捷，让你所有代码曾今你是规划者，第一二三步做什么，执行顺序
        //但是顺序可能产生阻塞，产生线程进入等待，cpu因为线程等待而浪费空转
        //react模型回调方式好处：你是方法内容的缔造者而不是逻辑执行顺序的缔造者
        //逻辑执行顺序是框架决定的，你只要把方法实现了，放到这个，未来有事件发生的时候调用即可
        //所以这时候计算机里边的逻辑是什么事情发生了就立刻处理而减少等到和空转

    }
}
