package juliano.michael.distributed.zookeeper.connection;

import java.io.IOException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public final class SingleSessionZooKeeperProvider implements ZooKeeperProvider, AutoCloseable {
    private final ZooKeeperProvider provider;
    private ZooKeeper zooKeeper;

    public SingleSessionZooKeeperProvider(
        final String connectString,
        final int sessionTimeout,
        final int connectionTimeout,
        final Watcher watcher
    ) {
        this.provider = new LazyBlockingZooKeeperProvider(
            connectString,
            sessionTimeout,
            connectionTimeout,
            watcher
        );
    }

    @Override
    public ZooKeeper get() {
        if (this.zooKeeper == null) {
            this.zooKeeper = this.provider.get();
        } else if (this.zooKeeper.getState() == ZooKeeper.States.CLOSED) {
            throw new IllegalStateException("Instance closed or session expired.");
        }
        return this.zooKeeper;
    }

    @Override
    public void updateConnectString(final String connectString) {
        this.provider.updateConnectString(connectString);
        if (this.zooKeeper != null) {
            try {
                this.zooKeeper.updateServerList(connectString);
            } catch (final IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    @Override
    public void close() throws Exception {
        if (this.zooKeeper != null) {
            this.zooKeeper.close();
        }
    }
}
