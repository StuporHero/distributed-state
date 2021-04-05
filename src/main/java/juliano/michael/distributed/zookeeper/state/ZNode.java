package juliano.michael.distributed.zookeeper.state;

import juliano.michael.distributed.state.Node;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;

public interface ZNode extends Node {
    void watch(Watcher watcher) throws KeeperException;
}
