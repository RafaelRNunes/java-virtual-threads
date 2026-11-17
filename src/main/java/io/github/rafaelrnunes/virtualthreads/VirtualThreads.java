package io.github.rafaelrnunes.virtualthreads;

import java.util.concurrent.Executors;

/**
 * Virtual threads (Java 21+): one per task, never pooled.
 * When a virtual thread blocks, the JVM unmounts it and its OS thread runs another one.
 */
public class VirtualThreads {

    public static void main(String[] args) {
        BlockingWorkload.run("virtual threads, one per task", Executors.newVirtualThreadPerTaskExecutor());
    }
}
