package com.censoredsoftware.errornoise;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Registry for the error handlers and tasks.
 */
public class ErrorNoiseRegistry
{
    // The logger.
    private static final Logger LOGGER = Bukkit.getServer().getLogger();
    private static final List<ErrorTask> TASKS = new ArrayList<>();

    private ErrorNoiseRegistry()
    {}

    static List<Handler> getErrorHandlers()
    {
        // Get all of the handlers in a new ArrayList.
        List<Handler> handlers = new ArrayList<>(Arrays.asList(Bukkit.getServer().getLogger().getHandlers()));

        // Remove all non-relevant handlers from this list.
        handlers.removeIf(handler -> !(handler instanceof ErrorHandler));

        // Return the list of handlers.
        return handlers;
    }

    static List<ErrorTask> getErrorTasks()
    {
        return TASKS;
    }

    static void register(Level level)
    {
        // Create and register an error handler for this log level.
        LOGGER.addHandler(new ErrorHandler(record -> {
            if(record.getLevel().equals(level)) {
                alertErrorTasks(level);
            }
        }));
        // Create and register an error task for this log level.
        TASKS.add(new ErrorTask(level));
    }

    static void alertErrorTasks(Level level)
    {
        TASKS.stream().filter(task -> level.equals(task.getLevel())).forEach(ErrorTask::run);
    }
}
