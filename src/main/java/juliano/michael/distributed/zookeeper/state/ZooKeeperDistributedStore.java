package juliano.michael.distributed.zookeeper.state;

import java.io.IOException;
import java.io.InputStream;
import juliano.michael.distributed.state.DistributedStore;
import juliano.michael.distributed.state.FailedToDelete;
import juliano.michael.distributed.state.FailedToUpsert;
import juliano.michael.distributed.state.NoDataReturned;
import juliano.michael.distributed.zookeeper.connection.ConnectStringUpdater;
import juliano.michael.distributed.zookeeper.connection.ZooKeeperProvider;
import org.apache.zookeeper.KeeperException;

public final class ZooKeeperDistributedStore implements DistributedStore<ZNode, ZNodeMetaData> {
    private final ZooKeeperProvider provider;
    private final ConnectStringUpdater updater;

    public ZooKeeperDistributedStore(
        final ZooKeeperProvider provider
    ) throws NoDataReturned, IOException {
        this.provider = provider;
        this.updater = new ConnectStringUpdater(provider);
    }

    @Override
    public ZNode nodeAt(final String key) {
        return new LazyZNode(this.provider, key);
    }

    @Override
    public void upsertAt(
        final String key,
        final InputStream data,
        final ZNodeMetaData metaData
    ) throws FailedToUpsert {
        try {
            this.provider.get().create(
                key,
                data.readAllBytes(),
                metaData.acl(),
                metaData.createMode()
            );
        } catch (final KeeperException | IOException e) {
            throw new FailedToUpsert(key, e);
        } catch (final InterruptedException e) {
            throw new IllegalStateException("Interrupted.", e);
        }
    }

    @Override
    public void deleteAt(final String key) throws FailedToDelete {
        try {
            this.provider.get().delete(
                key,
                this.provider.get().exists(key, false).getVersion()
            );
        } catch (final InterruptedException e) {
            throw new IllegalStateException("Interrupted.", e);
        } catch (final KeeperException e) {
            throw new FailedToDelete(key, e);
        }
    }
}
