package com.censoredsoftware.errornoise;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Handler class for the errors.
 */
public class ErrorHandler extends Handler
{
    // -- STATIC -- //

    static List<String> ignoredMessages = new ArrayList<String>();

    // -- END STATIC -- //

    private final Level level;

    ErrorHandler(Level level)
    {
        this.level = level;
    }

    @Override
    public void publish(LogRecord record)
    {
        if(!ignoredMessages.contains(record.getMessage()) && level.equals(record.getLevel()))
            ErrorNoiseRegistry.alertErrorTasks(level);
    }

    @Override
    public void flush()
    {}

    @Override
    public void close() throws SecurityException
    {}
}
