package com.censoredsoftware.errornoise;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The JavaPlugin class for ErrorNoise.
 */
public class ErrorNoisePlugin extends JavaPlugin
{
    private Set<Level> logLevels = new HashSet<Level>();

    /**
     * The Bukkit enable method.
     */
    @Override
    public void onEnable()
    {
        // Load the config.
        loadConfig();

        // Register the levels.
        for(Level level : logLevels)
            ErrorNoiseRegistry.register(level);

        // Alert the console.
        getLogger().info("Successfully enabled.");
    }

    /**
     * The Bukkit disable method.
     */
    @Override
    public void onDisable()
    {
        // Remove all handlers.
        for(Handler handler : ErrorNoiseRegistry.getErrorHandlers())
            Bukkit.getServer().getLogger().removeHandler(handler);

        // Alert the console.
        getLogger().info("Successfully disabled.");
    }

    // Load the config with error handling.
    private void loadConfig()
    {
        // Save defaults
        Configuration config = getConfig();
        config.options().copyDefaults(true);
        saveConfig();

        // Logger
        Logger logger = getLogger();

        // Get settings
        try
        {
            for(String level : config.getStringList("error.levels"))
                logLevels.add(Level.parse(level.toUpperCase()));
        }
        catch(Exception e)
        {
            logger.warning("Could not load the 'error.levels' setting.");
            logLevels.add(Level.WARNING);
            logLevels.add(Level.SEVERE);
        }
        try
        {
            ErrorTask.secondsToWait = config.getInt("error.seconds_to_wait");
        }
        catch(Exception e)
        {
            logger.warning("Could not load the 'error.seconds_to_wait' setting.");
        }
        try
        {
            ErrorTask.message = ChatColor.translateAlternateColorCodes('&', config.getString("error.message"));
        }
        catch(Exception e)
        {
            logger.warning("Could not load the 'error.message' setting.");
        }
        try
        {
            ErrorTask.sound = Sound.valueOf(config.getString("error.sound"));
        }
        catch(Exception e)
        {
            logger.warning("Could not load the 'error.sound' setting.");
        }
        try
        {
            ErrorTask.volume = Float.parseFloat(config.getString("error.volume"));
        }
        catch(Exception e)
        {
            logger.warning("Could not load the 'error.volume' setting.");
        }
        try
        {
            ErrorTask.pitch = Float.parseFloat(config.getString("error.pitch"));
        }
        catch(Exception e)
        {
            logger.warning("Could not load the 'error.pitch' setting.");
        }
        try
        {
            ErrorHandler.ignoredMessages = config.getStringList("ignore");
        }
        catch(Exception e)
        {
            logger.warning("Could not load the 'ignore' list setting.");
        }
    }
}
