# Java: platform threads vs virtual threads

The smallest comparison of the same blocking workload on traditional threads (a fixed pool of
platform threads) and on virtual threads (Java 21+). The two `main` classes differ in one line:

```java
Executors.newFixedThreadPool(200)            // PlatformThreads
Executors.newVirtualThreadPerTaskExecutor()  // VirtualThreads
```

## Workload

10,000 tasks, each blocking for 100 ms (`Thread.sleep`), simulating an HTTP call or a database query.
The workload lives in `BlockingWorkload`; each `main` only chooses the `ExecutorService`.

## Run

Requires JDK 21 or later. The Maven wrapper downloads Maven on the first run.

```sh
./mvnw -q compile
java -cp target/classes io.github.rafaelrnunes.virtualthreads.PlatformThreads
java -cp target/classes io.github.rafaelrnunes.virtualthreads.VirtualThreads
```

Run with `java` directly, not `mvn exec:java`: exec:java runs inside the Maven JVM, and Maven's own
threads would be counted in the peak.

## Results

AMD Ryzen 7 5700X3D (8 cores, 16 hardware threads), Linux, Amazon Corretto 21.0.11, 3 runs each.

| Version | Time | Peak OS threads |
|---|---|---|
| Fixed pool of 200 platform threads | 5,023 ms (all 3 runs) | 206 |
| Virtual thread per task | 142 to 144 ms | 23 |

The pool runs 200 tasks at a time: 10,000 / 200 = 50 rounds of 100 ms, about 5 s.
Virtual threads let all 10,000 tasks wait at the same time on a few OS threads.

## When to use each

- Tasks that wait (database, HTTP), on Java 21+: virtual threads, one per task, never pooled.
- Tasks that compute (CPU-bound): a fixed pool of platform threads, one per CPU.
- Below Java 21: a pool of platform threads.

## Limits of this example

- `Thread.sleep` is not real I/O. It shows the scheduling model, not the latency of a real network or database.
- Virtual threads do not speed up CPU-bound work, and they do not make a database faster.
- The fixed pool also limited the load on downstream systems. With virtual threads that limit disappears;
  protect a database or an external API with a connection pool or a `Semaphore`.
- On JDK 21 to 23, a virtual thread blocking inside `synchronized` pins its OS thread (fixed in JDK 24, JEP 491).

## License

MIT
