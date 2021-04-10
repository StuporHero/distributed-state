package juliano.michael.distributed.zookeeper.state;

import java.io.IOException;
import java.io.InputStream;
import juliano.michael.distributed.state.DistributedStore;
import juliano.michael.distributed.state.FailedToDelete;
import juliano.michael.distributed.state.FailedToUpsert;
import juliano.michael.distributed.state.NoDataReturned;
import juliano.michael.distributed.zookeeper.connection.ZooKeeperProvider;

public class CachingZooKeeperDistributedStore implements DistributedStore<ZNode, ZNodeMetaData> {
    private final ZooKeeperDistributedStore store;
    private final ZooKeeperProvider zooKeeperProvider;

    public CachingZooKeeperDistributedStore(
        final ZooKeeperProvider zooKeeperProvider
    ) throws NoDataReturned, IOException {
        this.store = new ZooKeeperDistributedStore(zooKeeperProvider);
        this.zooKeeperProvider = zooKeeperProvider;
    }

    @Override
    public ZNode nodeAt(final String key) {
        return new CachedZNode(this.zooKeeperProvider, key);
    }

    @Override
    public void upsertAt(final String key, final InputStream data, final ZNodeMetaData metaData) throws FailedToUpsert {
        this.store.upsertAt(key, data, metaData);
    }

    @Override
    public void deleteAt(final String key) throws FailedToDelete {
        this.store.deleteAt(key);
    }
}
