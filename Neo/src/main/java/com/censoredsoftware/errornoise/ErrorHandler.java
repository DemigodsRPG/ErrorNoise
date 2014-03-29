package com.censoredsoftware.errornoise;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Handler class for the errors.
 */
public class ErrorHandler extends Handler
{
    // -- STATIC -- //

    static List<String> ignoredMessages = new ArrayList<>();

    // -- END STATIC -- //

    private final ErrorLambda lambda;

    ErrorHandler(ErrorLambda lambda)
    {
        this. lambda = lambda;
    }

    @Override
    public void publish(LogRecord record)
    {
        if(!ignoredMessages.contains(record.getMessage()))
            lambda.publish(record);
    }

    @Override
    public void flush()
    {}

    @Override
    public void close() throws SecurityException
    {}
}
