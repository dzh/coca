/**
 * 
 */
package coca.co;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author dzh
 * @date Sep 13, 2017 12:54:06 PM
 * @since 0.0.1
 */
public abstract class BasicFuture<V> implements CoFuture<V> {

    protected CoFuture<?> next;

    protected volatile boolean cancelled = false;

    protected V result;

    private CountDownLatch gotLatch;

    public BasicFuture() {
        gotLatch = new CountDownLatch(1);
    }

    public void result(V result) {
        synchronized (this) {
            if (isCancelled() || isDone()) return; // TODO

            this.result = result;
            if (isDone()) {
                gotLatch.countDown();
                doDone();
            }
        }
    }

    protected void doDone() {
        if (this.next != null) {
            next().change(result);
        }
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        synchronized (this) {
            if (!isDone()) {
                cancelled = true;
                gotLatch.countDown();
            }
            return true;
        }
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public boolean isDone() {
        if (result == null) return false;
        return isCancelled();
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        gotLatch.await();
        return isCancelled() ? null : result;
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        gotLatch.await(timeout, unit);
        if (isCancelled()) return null;
        return isDone() ? result : null;
    }

    @Override
    public CoFuture<?> next() {
        return next;
    }

    @Override
    public CoFuture<?> next(CoFuture<?> next) {
        this.next = next;
        return this;
    }

}
