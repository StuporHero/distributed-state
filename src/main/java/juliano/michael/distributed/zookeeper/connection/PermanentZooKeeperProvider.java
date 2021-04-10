package juliano.michael.distributed.zookeeper.connection;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

/**
 * This {@link ZooKeeperProvider} provides a valid {@link ZooKeeper} until it is intentionally
 * closed.
 * @since 0.1
 */
public final class PermanentZooKeeperProvider implements ZooKeeperProvider, AutoCloseable {
    private final LazyBlockingZooKeeperProvider provider;
    private final AtomicBoolean closed;
    private ZooKeeper zooKeeper;

    public PermanentZooKeeperProvider(
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
        this.closed = new AtomicBoolean(false);
    }

    @Override
    public ZooKeeper get() {
        if (this.closed.get()) {
            throw new IllegalStateException("This provider has been closed.");
        } else if (this.zooKeeper == null) {
            this.zooKeeper = this.provider.get();
        }
        switch (this.zooKeeper.getState()) {
            case CLOSED:
            case NOT_CONNECTED:
                this.zooKeeper = this.provider.get();
                break;
            case AUTH_FAILED:
                throw new IllegalStateException("Authentication to the Ensemble failed.");
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
    public void close() throws InterruptedException {
        this.closed.set(true);
        if (this.zooKeeper != null) {
            this.zooKeeper.close();
        }
    }
}
