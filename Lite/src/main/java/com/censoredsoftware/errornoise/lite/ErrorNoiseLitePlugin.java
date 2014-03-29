package com.censoredsoftware.errornoise.lite;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * The JavaPlugin class for ErrorNoise-Lite.
 */
public class ErrorNoiseLitePlugin extends JavaPlugin
{
	// The error handler.
	private static final ErrorHandlerLite HANDLER = new ErrorHandlerLite();

	/**
	 * The Bukkit enable method.
	 */
	@Override
	public void onEnable()
	{
		// Register the handler.
		getServer().getLogger().addHandler(HANDLER);

		// Alert the console.
		getLogger().info("Successfully enabled.");
	}

	/**
	 * The Bukkit disable method.
	 */
	@Override
	public void onDisable()
	{
		// Remove the handler.
		getServer().getLogger().removeHandler(HANDLER);

		// Alert the console.
		getLogger().info("Successfully disabled.");
	}
}
