package it.menzani.yiupp.reporter;

import java.io.PrintStream;
import java.io.PrintWriter;

public class UncaughtException extends RuntimeException {

    public UncaughtException(Throwable cause) {
        super(cause);
    }

    @Override
    public void printStackTrace(PrintStream s) {
        getCause().printStackTrace(s);
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        getCause().printStackTrace(s);
    }

}
