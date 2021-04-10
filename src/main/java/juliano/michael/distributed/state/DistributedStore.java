package juliano.michael.distributed.state;

import java.io.InputStream;

public interface DistributedStore<N extends Node, M extends NodeMetaData> {
    N nodeAt(String key);
    void upsertAt(String key, InputStream data, M metaData) throws FailedToUpsert;
    void deleteAt(String key) throws FailedToDelete;
}
