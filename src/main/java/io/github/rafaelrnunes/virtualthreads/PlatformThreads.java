package io.github.rafaelrnunes.virtualthreads;

import java.util.concurrent.Executors;

/**
 * Traditional threads: a fixed pool of platform threads.
 * Each platform thread is an OS thread, so at most 200 tasks can wait at the same time.
 * 200 is also the default maximum of Tomcat, the embedded server of Spring Boot.
 */
public class PlatformThreads {

    public static void main(String[] args) {
        BlockingWorkload.run("platform threads, fixed pool of 200", Executors.newFixedThreadPool(200));
    }
}
