package com.demo.zkdistrubutedlock.lock;

import com.demo.zkdistrubutedlock.util.LogUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author AYL    2018/10/6 18:22
 */
@Component
public class SharedLock {
    private CuratorFramework client;

    /**
     * zk节点关系图
     * distributedLockNameSpace
     *    |__lock
     *    |   |__slock
     */
    private static final String PATH = "/lock";
    private static final String R_LOCK_PATH = "/slock_R_";
    private static final String W_LOCK_PATH = "/slock_W_";
    PathChildrenCache pathChildrenCache;
    private List<String> lockQueue;
    private static CountDownLatch latch = new CountDownLatch(1);

    public SharedLock(CuratorFramework client) {
        this.client = client;
    }

    @PostConstruct
    public void init() throws Exception {
        LogUtil.print("=======inited===============");
        if (client.checkExists().forPath(PATH) == null) {
            client.create().withMode(CreateMode.PERSISTENT)
                    .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                    .forPath(PATH);
        }

        pathChildrenCache = new PathChildrenCache(client, PATH, true);
        pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);

    }

    public void auquireReadLock() throws Exception {
        String currentPath = client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(PATH + R_LOCK_PATH);
        LogUtil.print("创建临时有序读节点:" + currentPath);

        List<ChildData> childDataList = pathChildrenCache.getCurrentData();
        for (ChildData childData : childDataList) {
            //todo
        }
    }





}
