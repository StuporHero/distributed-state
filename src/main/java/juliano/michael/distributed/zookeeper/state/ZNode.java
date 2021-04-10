package juliano.michael.distributed.zookeeper.state;

import java.util.Set;
import juliano.michael.distributed.state.NoDataReturned;
import juliano.michael.distributed.state.Node;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;

public interface ZNode extends Node {
    void watch(Watcher watcher) throws KeeperException;
    Set<ZNode> children() throws NoDataReturned;
}
