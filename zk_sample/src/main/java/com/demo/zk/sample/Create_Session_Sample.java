package com.demo.zk.sample;

import org.apache.curator.RetryPolicy;
import org.apache.curator.RetrySleeper;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @author AYL    2018/10/2 11:32
 */
public class Create_Session_Sample {
    public static void main(String[] args) {
        String connString = "192.168.11.89:2181,192.168.11.90:2181,192.168.11.88:2181";
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(connString,
                 5000, 3000, retryPolicy);
        client.start();
        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
