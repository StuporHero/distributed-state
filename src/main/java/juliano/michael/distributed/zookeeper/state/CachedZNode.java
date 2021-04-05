package juliano.michael.distributed.zookeeper.state;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;
import juliano.michael.distributed.state.NoDataReturned;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;

public final class CachedZNode implements ZNode {
    private final LazyZNode node;
    private final AtomicReference<byte[]> data;

    public CachedZNode(final LazyZNode node) {
        this(node, null);
    }

    public CachedZNode(final LazyZNode node, final byte[] initialValue) {
        this.node = node;
        this.data = new AtomicReference<>(initialValue);
    }

    @Override
    public InputStream data() throws NoDataReturned {
        if (this.data.get() == null) {
            try {
                this.data.set(this.node.data().readAllBytes());
                this.node.watch(
                    event -> {
                        if (event.getType() == Watcher.Event.EventType.NodeDataChanged) {
                            this.data.set(null);
                        }
                    }
                );
            } catch (final IOException e) {
                throw new NoDataReturned();
            } catch (final KeeperException e) {
                throw new IllegalStateException(
                    "There was an error establishing a watch on the data.",
                    e
                );
            }
        }
        return new ByteArrayInputStream(this.data.get());
    }

    @Override
    public void watch(final Watcher watcher) throws KeeperException {
        this.node.watch(watcher);
    }
}
