package com.demo.zkdistrubutedlock.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author AYL    2018/10/5 10:57
 */
@Getter
@Setter
@ToString(callSuper = true)
@Component
@ConfigurationProperties(prefix = "zookeeper.curator")
public class ZKConnectConfig {

    private String connectString;
    private String nameSpace;
    private int sessionTimeoutMs;
    private int connectionTimeoutMs;

    /**
     * retryPolicy
     */
    private int baseSleepTimeMs;
    private int maxRetries;
}
