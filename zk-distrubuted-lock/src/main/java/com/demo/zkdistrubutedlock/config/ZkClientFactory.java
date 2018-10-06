package com.demo.zkdistrubutedlock.config;

import com.demo.zkdistrubutedlock.util.LogUtil;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * @author AYL    2018/10/5 10:57
 */
@Component
public class ZkClientFactory {
    @Autowired
    ZKConnectConfig zkConnectConfig;

    private CuratorFramework client;

    @Bean
    public CuratorFramework getClient() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(zkConnectConfig.getBaseSleepTimeMs(), zkConnectConfig.getMaxRetries());

        client = CuratorFrameworkFactory.builder()
                .namespace(zkConnectConfig.getNameSpace())
                .connectString(zkConnectConfig.getConnectString())
                .connectionTimeoutMs(zkConnectConfig.getConnectionTimeoutMs())
                .sessionTimeoutMs(zkConnectConfig.getSessionTimeoutMs())
                .retryPolicy(retryPolicy)
                .build();
        client.start();
        return client;
    }

    @PreDestroy
    public void closeClient() {
        LogUtil.print("===========predestory called===============");
        if (client != null) {
            LogUtil.print("=============zkclient closed================");
            client.close();
        }
    }
}
