package com.demo.zk.sample;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorEventType;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;

/**
 * @author AYL    2018/10/2 18:45
 */
public class Node_Cache_Sample {
    private static String connString = "192.168.11.89:2181,192.168.11.90:2181,192.168.11.88:2181";

    private static String path = "/zk/create_node_bg_sample";
    private static RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

    static CuratorFramework client = CuratorFrameworkFactory.newClient(connString, 5000, 3000,
            retryPolicy);

    public static void main(String[] args) throws Exception {
        client.start();
        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, "init".getBytes());

        System.out.println("node created success");

        /**
         * 注意：如果zk的节点上的value是压缩格式的，dataIsCompressed参数才设定成true
         */
        NodeCache nodeCache = new NodeCache(client, path, false);
        nodeCache.start(true);
        nodeCache.getListenable().addListener(
                //创建/修改/删除的时候都会触发该事件。
                //并且无法取得具体的变化是因为创建动作，还是修改动作，还是删除动作
                () -> {
                    System.out.println("node data changed,new data now is:" +
                            new String(nodeCache.getCurrentData().getData()));
                }
        );

        /**
         * 注意，如果在 nodeCache.start(true);中设定成true，则如下两个setDate动作都会触发回调。
         * 如果设定成false，则第二次的setData才会触发。
         * 设定成true，是说nodecache在启动时就会读取node节点的数据内容并保存
         */
        client.setData().forPath(path, "u".getBytes());
        client.setData().forPath(path, "cache_start_init_false_test".getBytes());

        /**
         * 注意，这个delete操作时，第36行会抛NPE异常，因为此时nodeCache.getCurrentData()已经是null。
         * 说明delete操作也会产生回调。
         */
//        client.delete().deletingChildrenIfNeeded().forPath(path);

        Thread.sleep(Integer.MAX_VALUE);
    }
}
