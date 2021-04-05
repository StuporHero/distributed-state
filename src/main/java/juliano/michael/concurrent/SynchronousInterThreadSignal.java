package juliano.michael.concurrent;

import java.util.Optional;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public class SynchronousInterThreadSignal<T> implements InterThreadSignal<T> {
    private final SynchronousQueue<T> queue;

    public SynchronousInterThreadSignal() {
        this.queue = new SynchronousQueue<>();
    }

    @Override
    public void signal(T value) throws InterruptedException {
        this.queue.put(value);
    }

    @Override
    public void signal(T value, final long timeout) {
        this.signal(value, timeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public void signal(T value, final long timeout, final TimeUnit unit) {
        try {
            this.queue.offer(value, timeout, unit);
        } catch (final InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Optional<T> await() {
        final T value = this.queue.poll();
        if (value == null) {
            return Optional.empty();
        } else {
            return Optional.of(value);
        }
    }

    @Override
    public Optional<T> await(final long timeout) throws InterruptedException {
        return this.await(timeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public Optional<T> await(final long timeout, final TimeUnit unit) throws InterruptedException {
        final T value = this.queue.poll(timeout, unit);
        if (value == null) {
            return Optional.empty();
        } else {
            return Optional.of(value);
        }
    }
}
