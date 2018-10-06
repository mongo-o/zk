package com.demo.zk.sample;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;

import java.util.List;

/**
 * @author AYL    2018/10/4 18:11
 */
public class PathChildrenCache_Sample {
    public static void main(String[] args) throws Exception {
        CuratorFramework client = ZKClient.getZKClient();
        String path = "/ChildrenNodeCache/cache";

        PathChildrenCache childrenCache = new PathChildrenCache(client, path, true);


        /**
         * StartMode：
         * NORMAL,异步初始化
         * POST_INITIALIZED_EVENT，异步初始化，并且触发INITIALIZED事件
         * BUILD_INITIAL_CACHE，同步初始化
         */
        childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);


        /**
         *异步初始化的时候，这里获取不到子节点的值
         */
        List<ChildData> childDataList = childrenCache.getCurrentData();
        for (ChildData cd : childDataList) {
            String cdData = new String(cd.getData());
            System.out.println("======node:" + cdData);
        }

        childrenCache.getListenable().addListener(
                (cli, event) -> {
                    switch (event.getType()) {
                        //当程序启动时，如果已经存在子节点，也会触发这个事件。有N个子节点将触发N次。
                        case CHILD_ADDED:
                            System.out.println("===kkkk===" + new String(event.getData().getData()));
                            System.out.println("========" + "CHILD_ADDED");
                            break;
                        case INITIALIZED:
                            System.out.println("========" + "INITIALIZED");
                            break;
                        case CHILD_REMOVED:
                            System.out.println("========" + "CHILD_REMOVED");
                            break;
                        case CHILD_UPDATED:
                            System.out.println("========" + "CHILD_UPDATED");
                            break;
                        case CONNECTION_LOST:
                            System.out.println("========" + "CONNECTION_LOST");
                            break;
                        case CONNECTION_SUSPENDED:
                            System.out.println("========" + "CONNECTION_SUSPENDED");
                            break;
                        case CONNECTION_RECONNECTED:
                            System.out.println("========" + "CONNECTION_RECONNECTED");
                            break;
                        default:
                            break;
                    }
                }
        );

        Thread.sleep(Integer.MAX_VALUE);
    }
}
