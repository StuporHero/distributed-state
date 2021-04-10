package juliano.michael.distributed.state;

public final class NoDataReturned extends Exception {
    public NoDataReturned(final String key, final Throwable cause) {
        super(
            String.format("No data could be returned from %s", key),
            cause
        );
    }
}
