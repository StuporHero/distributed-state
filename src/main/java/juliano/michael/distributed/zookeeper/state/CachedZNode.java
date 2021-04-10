package juliano.michael.distributed.zookeeper.state;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import juliano.michael.distributed.state.NoDataReturned;
import juliano.michael.distributed.zookeeper.connection.ZooKeeperProvider;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;

public final class CachedZNode implements ZNode {
    private final ZooKeeperProvider zooKeeperProvider;
    private final String path;
    private final AtomicReference<byte[]> data;
    private final ZNode node;

    public CachedZNode(
        final ZooKeeperProvider zooKeeperProvider,
        final String path
    ) {
        this(zooKeeperProvider, path, null);
    }

    public CachedZNode(
        final ZooKeeperProvider zooKeeperProvider,
        final String path,
        final byte[] initialValue
    ) {
        this.zooKeeperProvider = zooKeeperProvider;
        this.path = path;
        this.data = new AtomicReference<>(initialValue);
        this.node = new LazyZNode(
            zooKeeperProvider,
            path,
            event -> {
                if (event.getType() == Watcher.Event.EventType.NodeDataChanged) {
                    this.data.set(null);
                }
            }
        );
    }

    @Override
    public InputStream data() throws NoDataReturned {
        if (this.data.get() == null) {
            try {
                this.data.set(this.node.data().readAllBytes());
            } catch (final IOException e) {
                throw new NoDataReturned(e);
            }
        }
        return new ByteArrayInputStream(this.data.get());
    }

    @Override
    public void watch(final Watcher watcher) throws KeeperException {
        this.node.watch(watcher);
    }

    @Override
    public Set<ZNode> children() throws NoDataReturned {
        try {
            return this.zooKeeperProvider.get().getChildren(this.path, false).stream()
                .map(path -> new CachedZNode(this.zooKeeperProvider, path))
                .collect(Collectors.toUnmodifiableSet());
        } catch (final KeeperException e) {
            throw new NoDataReturned(e);
        } catch (final InterruptedException e) {
            throw new IllegalStateException("Interrupted.", e);
        }
    }
}
