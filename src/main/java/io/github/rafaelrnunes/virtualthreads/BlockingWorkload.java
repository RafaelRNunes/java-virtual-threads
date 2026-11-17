package io.github.rafaelrnunes.virtualthreads;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.concurrent.ExecutorService;

/**
 * The same I/O-bound workload for both examples: many tasks that spend their time waiting.
 * Only the {@link ExecutorService} passed in changes.
 */
final class BlockingWorkload {

    static final int TASKS = 10_000;
    static final Duration IO_LATENCY = Duration.ofMillis(100);

    private BlockingWorkload() {
    }

    static void run(String label, ExecutorService executor) {
        long start = System.nanoTime();

        try (executor) { // close() waits for every submitted task to finish
            for (int i = 0; i < TASKS; i++) {
                executor.submit(BlockingWorkload::blockingCall);
            }
        }

        long elapsedMs = Duration.ofNanos(System.nanoTime() - start).toMillis();
        // Counts platform threads only: virtual threads are not OS threads.
        int peakOsThreads = ManagementFactory.getThreadMXBean().getPeakThreadCount();
        System.out.printf("%s: %,d tasks in %,d ms, peak OS threads: %d%n", label, TASKS, elapsedMs, peakOsThreads);
    }

    /** Simulates a blocking I/O call, such as an HTTP request or a database query. */
    private static void blockingCall() {
        try {
            Thread.sleep(IO_LATENCY);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
