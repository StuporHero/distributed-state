package juliano.michael.distributed.state;

public final class NoDataReturned extends Exception {
    public NoDataReturned() {
        super("No data could be returned.");
    }
}
