package com.netty.rpc.registry;

import com.netty.rpc.constant.Constant;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * @author lebron374
 * https://www.jianshu.com/u/1e38a3346f93
 */
public class ServiceRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRegistry.class);

    private String registryAddress;
    private CountDownLatch latch = new CountDownLatch(1);

    public ServiceRegistry(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public void register(String data){
        if (null != data) {
            ZooKeeper zk = connectServer();
            if (null != zk) {
                createNode(zk, data);
            }
        }
    }

    private ZooKeeper connectServer() {
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(registryAddress, Constant.ZK_SESSION_TIMEOUT, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                        latch.countDown();
                    }
                }
            });

            latch.await();
        } catch (Exception e) {
            LOGGER.error("connectServer 连接zk异常 ", e);
        }

        return zk;
    }

    private void createNode(ZooKeeper zooKeeper, String data) {
        try {
            byte[] dataBytes = data.getBytes();
            Stat stat = zooKeeper.exists(Constant.ZK_REGISTRY_ROOT_PATH, false);
            if (null == stat) {
                zooKeeper.create(Constant.ZK_REGISTRY_ROOT_PATH, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }

            String path = zooKeeper.create(Constant.ZK_DATA_PATH, dataBytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

            LOGGER.info("createNode 创建节点 {} 成功", path);
        } catch (Exception e) {
            LOGGER.error("createNode 创建节点异常 ", e);
        }
    }
}
