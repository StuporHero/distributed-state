package juliano.michael.distributed.zookeeper.connection;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import juliano.michael.concurrent.SynchronousInterThreadSignal;
import juliano.michael.concurrent.InterThreadSignal;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

/**
 * Provides a brand new {@link ZooKeeper} with every call to get, blocking until the connection is
 * successfully established.
 * @since 0.1
 */
public final class LazyBlockingZooKeeperProvider implements ZooKeeperProvider {
    private String connectString;
    private final int sessionTimeout;
    private final int connectionTimeout;
    private final InterThreadSignal<Boolean> isConnecting;
    private final InterThreadSignal<Watcher.Event.KeeperState> keeperState;
    private final Watcher watcher;


    public LazyBlockingZooKeeperProvider(
        final String connectString,
        final int sessionTimeout,
        final int connectionTimeout,
        final Watcher watcher
    ) {
        this.connectString = connectString;
        this.sessionTimeout = sessionTimeout;
        this.connectionTimeout = connectionTimeout;
        this.isConnecting = new SynchronousInterThreadSignal<>();
        this.keeperState = new SynchronousInterThreadSignal<>();
        this.watcher = event -> {
            try {
                this.isConnecting.await(100, TimeUnit.MILLISECONDS).ifPresent(
                    ignored -> this.keeperState.signal(
                        event.getState(),
                        this.connectionTimeout,
                        TimeUnit.MILLISECONDS
                    )
                );
                watcher.process(event);
            } catch (final InterruptedException e) {
                throw new IllegalStateException(e);
            }
        };
    }

    @Override
    public ZooKeeper get() {
        try {
            final var zooKeeper = new ZooKeeper(
                this.connectString,
                this.sessionTimeout,
                this.watcher
            );
            this.isConnecting.signal(true, this.connectionTimeout, TimeUnit.MILLISECONDS);
            final AtomicBoolean isConnected = new AtomicBoolean(false);
            do {
                this.keeperState.await(this.connectionTimeout, TimeUnit.MILLISECONDS).ifPresent(
                    state -> {
                        switch (state) {
                            case AuthFailed:
                                throw new IllegalStateException("Authentication failed.");
                            case Closed:
                                throw new IllegalStateException(
                                    "ZooKeeper instance was somehow closed at an inappropriate time."
                                );
                            case Expired:
                                throw new IllegalStateException("The session expired prematurely.");
                            case Disconnected:
                                if (zooKeeper.getState() != ZooKeeper.States.CONNECTING) {
                                    throw new IllegalStateException(
                                        "The instance is disconnected and is not attempting to connect"
                                    );
                                }
                                break;
                            case SyncConnected:
                            case SaslAuthenticated:
                                isConnected.set(true);
                        }
                    }
                );
            } while (!isConnected.get());
            return zooKeeper;
        } catch (final IOException | InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void updateConnectString(final String connectString) {
        this.connectString = connectString;
    }
}
