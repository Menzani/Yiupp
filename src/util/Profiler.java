package it.menzani.yiupp.util;

public class Profiler {

    private final String name;
    private final long start = java.lang.System.nanoTime();

    public Profiler(String name) {
        this.name = name;
    }

    public void report() {
        java.lang.System.out.println("Profiling " + name + " - took " + stop() + "ms to complete.");
    }

    public long stop() {
        long end = java.lang.System.nanoTime();
        long diff = end - start;
        return diff / 1000000L;
    }

}
