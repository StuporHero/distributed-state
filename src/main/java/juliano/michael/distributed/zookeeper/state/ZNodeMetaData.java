package juliano.michael.distributed.zookeeper.state;

import java.util.List;
import juliano.michael.distributed.state.NodeMetaData;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.ACL;

public final class ZNodeMetaData implements NodeMetaData {
    private final List<ACL> acls;
    private final CreateMode mode;

    public ZNodeMetaData(final List<ACL> acl, final CreateMode createMode) {
        this.acls = acl;
        this.mode = createMode;
    }

    public List<ACL> acl() {
        return this.acls;
    }

    public CreateMode createMode() {
        return this.mode;
    }
}
