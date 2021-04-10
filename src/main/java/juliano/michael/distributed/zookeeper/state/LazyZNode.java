package juliano.michael.distributed.zookeeper.state;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Set;
import java.util.stream.Collectors;
import juliano.michael.distributed.state.NoDataReturned;
import juliano.michael.distributed.zookeeper.connection.ZooKeeperProvider;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;

public final class LazyZNode implements ZNode {
    private final ZooKeeperProvider zooKeeperProvider;
    private final String path;
    private final Watcher watcher;

    public LazyZNode(
        final ZooKeeperProvider zooKeeperProvider,
        final String path
    ) {
        this(zooKeeperProvider, path, event -> {});
    }

    public LazyZNode(
        final ZooKeeperProvider zooKeeperProvider,
        final String path,
        final Watcher watcher
    ) {
        this.zooKeeperProvider = zooKeeperProvider;
        this.path = path;
        this.watcher = watcher;
    }

    @Override
    public InputStream data() throws NoDataReturned {
        try {
            return new ByteArrayInputStream(
                this.zooKeeperProvider.get().getData(
                    this.path,
                    this.watcher,
                    this.zooKeeperProvider.get().exists(this.path, false)
                )
            );
        } catch (final KeeperException e) {
            throw new NoDataReturned(e.getPath(), e);
        } catch (final InterruptedException e) {
            throw new IllegalStateException("Interrupted.", e);
        }
    }

    @Override
    public void watch(final Watcher watcher) throws KeeperException {
        try {
            this.zooKeeperProvider.get().exists(this.path, watcher);
        } catch (final InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Set<ZNode> children() throws NoDataReturned {
        try {
            return this.zooKeeperProvider.get().getChildren(this.path, false).stream()
                .map(path -> new LazyZNode(this.zooKeeperProvider, path))
                .collect(Collectors.toUnmodifiableSet());
        } catch (final KeeperException e) {
            throw new NoDataReturned(e.getPath(), e);
        } catch (final InterruptedException e) {
            throw new IllegalStateException("Interrupted.", e);
        }
    }
}
