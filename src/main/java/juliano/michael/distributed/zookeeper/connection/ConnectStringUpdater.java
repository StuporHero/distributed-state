package juliano.michael.distributed.zookeeper.connection;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import juliano.michael.distributed.state.NoDataReturned;
import juliano.michael.distributed.zookeeper.state.LazyZNode;
import juliano.michael.distributed.zookeeper.state.ZNode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;

public final class ConnectStringUpdater implements AutoCloseable {
    private final ZooKeeperProvider zooKeeperProvider;
    private final AtomicBoolean isClosed;
    private final ZNode config;

    public ConnectStringUpdater(
        final ZooKeeperProvider zooKeeperProvider
    ) throws NoDataReturned, IOException {
        this.zooKeeperProvider = zooKeeperProvider;
        this.isClosed = new AtomicBoolean(false);
        this.config = new LazyZNode(
            zooKeeperProvider,
            ZooDefs.CONFIG_NODE,
            event -> {
                if (
                    !this.isClosed.get()
                    && event.getType() == Watcher.Event.EventType.NodeDataChanged
                ) {
                    try {
                        this.updateConnectString();
                    } catch (final NoDataReturned | IOException e) {
                        throw new IllegalStateException(e);
                    }
                }
            }
        );
        this.updateConnectString();
    }

    private void updateConnectString() throws NoDataReturned, IOException {
        final var connectString = Arrays.stream(
            new String(this.config.data().readAllBytes()).split("\n")
        )
            .filter(line -> !line.startsWith("version="))
            .map(
                line -> {
                    final var parts = line.split("=")[1].split(":");
                    return String.format("%s:%s", parts[0], parts[4]);
                }
            ).collect(Collectors.joining(","));
        if (!connectString.isEmpty()) {
            this.zooKeeperProvider.updateConnectString(connectString);
        }
    }

    @Override
    public void close() {
        this.isClosed.set(true);
    }
}
