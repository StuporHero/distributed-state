package juliano.michael.distributed.zookeeper.state;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
        } catch (final KeeperException ignored) {
            throw new NoDataReturned();
        } catch (InterruptedException e) {
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
}
