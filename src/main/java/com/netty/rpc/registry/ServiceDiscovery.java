package com.netty.rpc.registry;

import com.netty.rpc.constant.Constant;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

public class ServiceDiscovery {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceDiscovery.class);

    private CountDownLatch latch = new CountDownLatch(1);
    private volatile List<String> dataList = new ArrayList<>();

    /**
     * 注册地址
     */
    private String registryAddress;

    public ServiceDiscovery(String registryAddress) {
        this.registryAddress = registryAddress;

        ZooKeeper zk = connectServer();
        if (null != zk) {
            watchNode(zk);
        }
    }

    public String discovery(){
        String data = null;
        int size = dataList.size();
        if(size > 0){
            int temp = ThreadLocalRandom.current().nextInt(size);
            data = dataList.get(temp);

        }
        return data;
    }

    private ZooKeeper connectServer() {
        ZooKeeper zk = null;

        try {
            zk = new ZooKeeper(this.registryAddress, Constant.ZK_SESSION_TIMEOUT, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if(watchedEvent.getState() == Event.KeeperState.SyncConnected){
                        latch.countDown();
                    }
                }
            });
            latch.await();
        } catch (Exception e) {
            LOGGER.error("connectServer 连接zk服务报错", e);
        }

        return zk;
    }

    private void watchNode(final ZooKeeper zk) {
        try {
            List<String> nodeList = zk.getChildren(Constant.ZK_REGISTRY_ROOT_PATH, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if(watchedEvent.getType() == Event.EventType.NodeChildrenChanged){
                        watchNode(zk);
                    }
                }
            });

            List<String> dataList = new ArrayList<>();
            byte[] bytes;
            for (String node : nodeList){
                bytes = zk.getData(Constant.ZK_REGISTRY_ROOT_PATH + "/" + node, false, null);
                dataList.add(new String(bytes));
            }

            this.dataList = dataList;
        } catch (Exception e) {
            LOGGER.error("watchNode 监视zk节点异常", e);
        }
    }
}
