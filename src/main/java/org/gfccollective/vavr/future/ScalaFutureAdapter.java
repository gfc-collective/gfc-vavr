package org.gfccollective.vavr.future;

import org.gfccollective.vavr.VavrConverters.VavrTryConverter;
import org.gfccollective.vavr.VavrConverters.VavrOptionConverter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import io.vavr.control.Option;
import io.vavr.control.Try;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

/**
 * ScalaFutureAdapter is implemented in Java because it is impossible to implement in Scala. The
 * Scala compiler does not allow us to subclass io.vavr.concurrent.Future. Vavr's Future implements
 * a method called 'eq' and this makes the Scala compiler very unhappy.
 */
public class ScalaFutureAdapter<T> implements io.vavr.concurrent.Future<T> {

  private final Future<T> scalaFuture;

  public ScalaFutureAdapter(Future<T> scalaFuture) {
    this.scalaFuture = scalaFuture;
  }

  protected Future<T> getScalaFuture() {
    return scalaFuture;
  }

  @Override
  public io.vavr.concurrent.Future<T> await() {
    return await(1000000L, TimeUnit.SECONDS);
  }

  @Override
  public io.vavr.concurrent.Future<T> await(final long timeout, final TimeUnit unit) {
    if (!scalaFuture.isCompleted()) {
      try {
        Await.result(scalaFuture, Duration.apply(timeout, unit));
        return this;
      } catch (InterruptedException e) {
        return io.vavr.concurrent.Future.failed(e);
      } catch (TimeoutException e) {
        return io.vavr.concurrent.Future.failed(e);
      }
    }
    return this;
  }

  @Override
  public boolean cancel(boolean mayInterruptIfRunning) {
    return false;
  }

  @Override
  public ExecutorService executorService() throws UnsupportedOperationException {
    return DEFAULT_EXECUTOR_SERVICE;
  }

  @Override
  public Option<Try<T>> getValue() {
    scala.Option<scala.util.Try<T>> scalaValue = scalaFuture.value();
    VavrOptionConverter optionConverter = new VavrOptionConverter(scalaValue);
    Option<Try<T>> vavrOption =
        optionConverter
            .asVavrOption()
            .map((value) -> new VavrTryConverter((scala.util.Try<T>) value).asVavrTry());
    return vavrOption;
  }

  @Override
  public boolean isCancelled() {
    return false;
  }

  @Override
  public boolean isCompleted() {
    return scalaFuture.isCompleted();
  }

  @Override
  public io.vavr.concurrent.Future<T> onComplete(Consumer<? super Try<T>> action) {
    FutureConverters.registerOnComplete(scalaFuture, action);
    return this;
  }
}
