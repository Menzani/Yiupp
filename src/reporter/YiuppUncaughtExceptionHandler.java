package it.menzani.yiupp.reporter;

import it.menzani.yiupp.Yiupp;
import it.menzani.yiupp.storage.ResourceMap;
import it.menzani.yiupp.util.Desktop;
import it.menzani.yiupp.util.Desktops;
import it.menzani.yiupp.util.TemplateVariable;
import it.menzani.yiupp.util.TextManager;

import java.io.*;
import java.text.DateFormat;
import java.util.Calendar;

public final class YiuppUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    // This should be a sandbox, cannot use DataFolder class
    private final File folder = new File(new File(System.getProperty("user.home"), "Yiupp"), "logs");

    public YiuppUncaughtExceptionHandler() {
        folder.mkdirs();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        try {
            String timestamp = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT)
                    .format(Calendar.getInstance().getTime());
            File file = new File(folder, timestamp + ".log");
            try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
                logThrowable(throwable, thread, new WrappedPrintWriter(writer));
            }

            Desktop desktop = Desktops.getDesktop();
            String message = ResourceMap.lookupString(ResourceMap.CRASH_TEXT);
            message = TextManager.fillTemplate(message, Yiupp.DISPLAY_NAME, new TemplateVariable("report", file));
            desktop.sendMessage(message);
            System.exit(1);
        } catch (Throwable localThrowable) {
            logThrowable(localThrowable, Thread.currentThread(), new WrappedPrintStream(System.err));
        }
    }

    private static void logThrowable(Throwable throwable, Thread thread, PrintStreamOrWriter streamOrWriter) {
        if (throwable instanceof Error) {
            streamOrWriter.print("Grave errore");
        } else {
            streamOrWriter.print("Errore");
        }
        streamOrWriter.print(" imprevisto nel thread \"" + thread.getName() + "\" ");

        StringWriter stringWriter = new StringWriter();
        try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
            throwable.printStackTrace(printWriter);
        }
        streamOrWriter.println(stringWriter.toString());
    }

    private abstract static class PrintStreamOrWriter {

        abstract void print(String text);

        abstract void println(String text);

    }

    private static final class WrappedPrintStream extends PrintStreamOrWriter {

        private final PrintStream stream;

        private WrappedPrintStream(PrintStream stream) {
            this.stream = stream;
        }

        @Override
        void print(String text) {
            stream.print(text);
        }

        @Override
        void println(String text) {
            stream.println(text);
        }

    }

    private static final class WrappedPrintWriter extends PrintStreamOrWriter {

        private final PrintWriter writer;

        private WrappedPrintWriter(PrintWriter writer) {
            this.writer = writer;
        }

        @Override
        void print(String text) {
            writer.print(text);
        }

        @Override
        void println(String text) {
            writer.println(text);
        }

    }

}
