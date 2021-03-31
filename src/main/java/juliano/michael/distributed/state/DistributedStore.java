package juliano.michael.distributed.state;

import java.io.InputStream;
import java.util.Set;

public interface DistributedStore {
    Node nodeAt(Address address);
    Set<Node> childrenOf(Address address);
    void createAt(Address address, InputStream data);
    void deleteAt(Address address);
    void deleteChildrenOf(Address address);
}
