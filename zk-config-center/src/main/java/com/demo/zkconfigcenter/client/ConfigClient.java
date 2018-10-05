package com.demo.zkconfigcenter.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.demo.zkconfigcenter.redis.RedisConfig;
import jdk.nashorn.internal.parser.JSONParser;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

/**
 * @author AYL    2018/10/5 11:48
 */
@Component
public class ConfigClient {


    /**
     * zk节点关系图
     * ZKConfigCenterNameSpace
     *      |__config
     *      |     |__redisConfig
     */
    private static final String PATH = "/config";
    private static final String SUB_PATH = "/redisConfig";

    @Autowired
    CuratorFramework client;

    public void autoConfig(CountDownLatch countDownLatch) throws Exception {
        if (client.checkExists().forPath(PATH + SUB_PATH) == null) {
            client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(PATH + SUB_PATH);
        }

        PathChildrenCache pathChildrenCache = new PathChildrenCache(client, PATH, true);
        pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);

        pathChildrenCache.getListenable().addListener(
                (cli, event) -> {
                    if (event.getType() == PathChildrenCacheEvent.Type.CHILD_UPDATED) {
                        String config = new String(event.getData().getData());
                        System.out.println("==config===" + config);
                        RedisConfig redisConfig = JSON.parseObject(config, RedisConfig.class);
                        switch (redisConfig.getType()) {
                            case "add":
                                System.out.println("added, url:" + redisConfig.getUrl());
                                break;
                            case "update":
                                System.out.println("update, url:" + redisConfig.getUrl());
                                break;
                            case "delete":
                                System.out.println("delete");
                                break;
                            case "stop":
                                //事与愿违，这里并不会唤醒main线程。方法失败。
                                countDownLatch.countDown();
                                break;
                            default:
                                System.out.println("redisconfig invalid value for type,should be in (add,updte,delete)");
                                break;
                        }
                    }
                }
        );
        Thread.sleep(Integer.MAX_VALUE);
    }
}
