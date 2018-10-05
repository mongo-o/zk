package com.demo.zk.sample;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

/**
 * @author AYL    2018/10/4 16:36
 */
public class Create_Node_Sample {
    public static void main(String[] args) throws Exception {
        String path = "/create/235";
        CuratorFramework zkClient = ZKClient.getZKClient();
        try {
            zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)
                    .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE).forPath(path, "init".getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Stat stat = new Stat();
        //读取
        System.out.println("====" + new String(zkClient.getData().storingStatIn(stat).forPath(path)));
        //更新
        zkClient.setData().withVersion(stat.getVersion()).forPath(path, "newData".getBytes());

        //删除
        zkClient.getData().storingStatIn(stat).forPath(path);
        zkClient.delete().withVersion(stat.getVersion()).forPath(path);
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        try {
//            zkClient.delete().deletingChildrenIfNeeded().forPath(path);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


    }
}
