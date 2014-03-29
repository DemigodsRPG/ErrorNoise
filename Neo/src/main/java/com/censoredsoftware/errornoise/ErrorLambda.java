package com.censoredsoftware.errornoise;

import java.util.logging.LogRecord;

/**
 * Handler interface for lambda expressions.
 */
public interface ErrorLambda
{
    void publish(LogRecord record);
}
