package com.demigodsrpg.errornoise;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Handler class for the errors.
 */
public class ErrorHandler extends Handler {

    static List<String> IGNORED = new ArrayList<>();

    private final Level LEVEL;

    public ErrorHandler(Level level) {
        this.LEVEL = level;
    }

    @Override
    public void publish(LogRecord record) {
        if (!IGNORED.contains(record.getMessage()) && LEVEL.equals(record.getLevel())) {
            ErrorNoisePlugin.registry.alertErrorTasks(LEVEL);
        }
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }
}
