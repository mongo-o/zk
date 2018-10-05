package com.demo.zk.sample;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author AYL    2018/10/2 17:22
 */
public class Create_Node_bg_sample {
    private static String connString = "192.168.11.89:2181,192.168.11.90:2181,192.168.11.88:2181";

    private static String path = "/zk/create_node_bg_sample";

    static RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
    static CuratorFramework client = CuratorFrameworkFactory.newClient(connString,
            5000, 3000, retryPolicy);
    static CountDownLatch latch = new CountDownLatch(2);
    static ExecutorService es = Executors.newFixedThreadPool(2);

    public static void main(String[] args) {
        client.start();

        System.out.println("Current threadname:" + Thread.currentThread().getName());
        try {
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).inBackground(
                    (client, event) -> {
                        System.out.println("1 event[code:" + event.getResultCode() + "type:" + event.getType() + "]");
                        System.out.println("1 operator thread:" + Thread.currentThread().getName());
                        latch.countDown();
                    }, es
            ).forPath(path, "sample".getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).inBackground(
                    (client, event) -> {
                        System.out.println("2 event[code:" + event.getResultCode() + "type:" + event.getType() + "]");
                        System.out.println("2 operator thread:" + Thread.currentThread().getName());
                        latch.countDown();
                    }
            ).forPath(path, "sample".getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        es.shutdown();

        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
