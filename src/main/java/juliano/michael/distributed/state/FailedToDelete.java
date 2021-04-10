package juliano.michael.distributed.state;

public final class FailedToDelete extends Exception {
    public FailedToDelete(final String key, final Throwable cause) {
        super(
            String.format("Failed to delete node at %s", key),
            cause
        );
    }
}
