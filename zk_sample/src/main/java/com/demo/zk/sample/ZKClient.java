package com.demo.zk.sample;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @author AYL    2018/10/4 16:26
 */
public class ZKClient {
    private static final String connString = "192.168.11.89:2181,192.168.11.90:2181,192.168.11.88:2181";;
    private static CuratorFramework client;

    public static CuratorFramework getZKClient() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

        client = CuratorFrameworkFactory.builder().connectString(connString).sessionTimeoutMs(5000)
                .connectionTimeoutMs(3000).retryPolicy(retryPolicy)
                .namespace("zkbook").build();
        client.start();
        return client;
    }

    public static void closeClient() {
        if (client != null) {
            client.close();
        }
    }
}
