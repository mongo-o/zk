package com.demo.zkdistrubutedlock.lock;

import com.demo.zkdistrubutedlock.util.LogUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.CountDownLatch;

/**
 * @author AYL    2018/10/5 18:43
 */
@Component
public class ExclusiveLock {
    @Autowired
    CuratorFramework client;

    /**
     * zk节点关系图
     * distributedLockNameSpace
     *    |__lock
     *    |   |__xlock
     */
    private static final String PATH = "/lock";
    private static final String SUB_PATH = "/xlock";
    private static CountDownLatch latch = new CountDownLatch(1);

    @PostConstruct
    public void init() throws Exception {
        LogUtil.print("=======inited===============");
        if (client.checkExists().forPath(PATH) == null) {
            client.create().withMode(CreateMode.PERSISTENT)
                    .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                    .forPath(PATH);
        }
        addLockWatcher();
    }

    public void acquire() {
        while(true) {
            try {
                client.create().withMode(CreateMode.EPHEMERAL)
                        .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                        .forPath(PATH + SUB_PATH);
                LogUtil.print("获取XLock成功");
                return;
            } catch (Exception e) {
                LogUtil.print("获取XLock失败。");
                try {
                    if (latch.getCount() <= 0) {
                        latch = new CountDownLatch(1);
                    }
                    latch.await();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public boolean release() {
        LogUtil.print("开始释放XLock");
        try {
            client.delete().forPath(PATH + SUB_PATH);
            latch.countDown();
            LogUtil.print("释放XLock成功");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.print("释放XLock失败");
            return false;
        }
    }

    private void addLockWatcher() throws Exception {
        PathChildrenCache childrenCache = new PathChildrenCache(client, PATH, true);
        childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);

        /**
         * 需要注意不是每次的节点变化都会触发监听器,在更新频率很快的情况下，没办法每个变更都触发监听器。
         * 所以释放锁不能靠这里进行。还需要在release（）方法中删除节点后再latch.countDown()
         * 参考:https://www.jianshu.com/p/eec133595c68
         */
        childrenCache.getListenable().addListener(
                (cli, event) -> {
                    LogUtil.print("event.Type:" + event.getType().name());
                    if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)) {
                        LogUtil.print("子节点" + event.getData().getPath() + "被删除");

                        latch.countDown();
                        LogUtil.print("latch.countDown()成功");

                    }
                }
        );
    }
}
