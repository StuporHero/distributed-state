package juliano.michael.distributed.state;

public final class FailedToUpsert extends Exception {
    public FailedToUpsert(final String key, final Throwable cause) {
        super(
            String.format("Failed to upsert data at %s", key),
            cause
        );
    }
}
