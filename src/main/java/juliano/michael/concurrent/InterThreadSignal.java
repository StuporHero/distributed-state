package juliano.michael.concurrent;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public interface InterThreadSignal<T> {
    void signal(T value) throws InterruptedException;
    void signal(T value, long timeout);
    void signal(T value, long timeout, TimeUnit unit);
    Optional<T> await();
    Optional<T> await(long timeout) throws InterruptedException;
    Optional<T> await(long timeout, TimeUnit unit) throws InterruptedException;
}
