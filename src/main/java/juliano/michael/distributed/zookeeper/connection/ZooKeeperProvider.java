package juliano.michael.distributed.zookeeper.connection;

import java.util.function.Supplier;
import org.apache.zookeeper.ZooKeeper;

public interface ZooKeeperProvider extends Supplier<ZooKeeper> {
}
