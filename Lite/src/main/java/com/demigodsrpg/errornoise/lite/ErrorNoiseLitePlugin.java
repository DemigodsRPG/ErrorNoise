package com.demigodsrpg.errornoise.lite;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * The JavaPlugin class for ErrorNoise-Lite.
 */
public class ErrorNoiseLitePlugin extends JavaPlugin {
    // The error handler.
    private ErrorHandlerLite HANDLER;

    /**
     * The Bukkit enable method.
     */
    @Override
    public void onEnable() {
        // Register the handler.
        HANDLER = new ErrorHandlerLite();
        getServer().getLogger().addHandler(HANDLER);

        // Alert the console.
        getLogger().info("Successfully enabled.");
    }

    /**
     * The Bukkit disable method.
     */
    @Override
    public void onDisable() {
        // Remove the handler.
        getServer().getLogger().removeHandler(HANDLER);

        // Alert the console.
        getLogger().info("Successfully disabled.");
    }
}
