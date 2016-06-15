package com.demigodsrpg.errornoise;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Registry for the error handlers and tasks.
 */
public class ErrorNoiseRegistry {

    private final List<ErrorTask> TASKS = new ArrayList<>();

    ErrorNoiseRegistry() {
    }

    public List<Handler> getErrorHandlers() {
        // Get all of the handlers in a new ArrayList.
        List<Handler> handlers = new ArrayList<>();

        // Remove all non-relevant handlers from this list.
        handlers.addAll(Arrays.asList(Bukkit.getServer().getLogger().getHandlers()).stream().
                filter(handler -> handler instanceof ErrorHandler).collect(Collectors.toList()));

        // Return the list of handlers.
        return handlers;
    }

    public List<ErrorTask> getErrorTasks() {
        return TASKS;
    }

    public void register(Level level) {
        // Create and register an error handler for this log level.
        Bukkit.getServer().getLogger().addHandler(new ErrorHandler(level));

        // Create and register an error task for this log level.
        TASKS.add(new ErrorTask(level));
    }

    public void alertErrorTasks(Level level) {
        TASKS.stream().filter(task -> level.equals(task.getLevel())).forEach(ErrorTask::run);
    }
}
