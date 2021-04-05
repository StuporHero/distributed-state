package juliano.michael.distributed.state;

import java.io.InputStream;

public interface Node {
    InputStream data() throws NoDataReturned;
}
