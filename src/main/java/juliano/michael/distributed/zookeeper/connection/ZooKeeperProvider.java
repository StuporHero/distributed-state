package juliano.michael.distributed.zookeeper.connection;

import org.apache.zookeeper.ZooKeeper;

public interface ZooKeeperProvider {
    ZooKeeper get();
}
