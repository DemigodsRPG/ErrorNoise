package com.censoredsoftware.errornoise;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class ErrorNoise extends JavaPlugin
{
	// Settings
	protected static boolean warning = true;
	protected static boolean severe = true;
	protected static int secondsToWait = 8;
	protected static String message = ChatColor.RED + "An error has occurred, please check the server console.";
	protected static Sound sound = Sound.BAT_IDLE;
	protected static Float volume = 2F, pitch = 0.9F;

	/**
	 * The Bukkit enable method.
	 */
	@Override
	public void onEnable()
	{
		loadConfig();
		new API();
		startHandler();
		new Annoy(this, message, sound, volume, pitch);
		getLogger().info("Successfully enabled.");
	}

	/**
	 * The Bukkit disable method.
	 */
	@Override
	public void onDisable()
	{
		getServer().getScheduler().cancelTasks(this);
		getLogger().info("Successfully disabled.");
	}

	private void loadConfig()
	{
		// Save defaults
		Configuration config = getConfig();
		config.options().copyDefaults(true);
		saveConfig();

		// Get settings
        try { warning = config.getBoolean("error.warning"); }
        catch(Exception e) { getLogger().warning("Could not load the 'error.warning' setting."); }
        try { severe = config.getBoolean("error.severe"); }
        catch(Exception e) { getLogger().warning("Could not load the 'error.severe' setting."); }
        try { secondsToWait = config.getInt("error.seconds_to_wait"); }
        catch(Exception e) { getLogger().warning("Could not load the 'error.seconds_to_wait' setting."); }
        try { message = ChatColor.translateAlternateColorCodes('&', config.getString("error.message")); }
        catch(Exception e) { getLogger().warning("Could not load the 'error.message' setting."); }
        try { sound = Sound.valueOf(config.getString("error.sound")); }
        catch(Exception e) { getLogger().warning("Could not load the 'error.sound' setting."); }
        try { volume = Float.parseFloat(config.getString("error.volume")); }
        catch(Exception e) { getLogger().warning("Could not load the 'error.volume' setting."); }
        try { pitch = Float.parseFloat(config.getString("error.pitch")); }
        catch(Exception e) { getLogger().warning("Could not load the 'error.pitch' setting."); }
	}

	private void startHandler()
	{
		if(warning && severe) new WarningSevereHandler();
		else if(warning) new WarningHandler();
		else if(severe) new SevereHandler();
	}

	public static class API
	{
		public static void triggerError()
		{
			Annoy.ERROR = true;
			if(Annoy.TIME + (secondsToWait * 1000) <= System.currentTimeMillis()) Annoy.TEXT = true;
			Annoy.TIME = System.currentTimeMillis();
		}

        public static void triggerError(String pluginName)
        {
            Annoy.PLUGIN = pluginName;
            triggerError();
        }

        public static void triggerError(Plugin plugin)
        {
            triggerError(plugin.getName());
        }
	}

	protected static class Annoy extends BukkitRunnable
	{
		protected static boolean ERROR, TEXT;
		protected static long TIME;
        protected static String PLUGIN;
		private String message;
		private Sound sound;
		private float volume, pitch;

		public Annoy(Plugin instance, String message, Sound sound, float volume, float pitch)
		{
			ERROR = false;
			TIME = System.currentTimeMillis();
            PLUGIN = "";
			this.message = message;
			this.sound = sound;
			this.volume = volume;
			this.pitch = pitch;
			Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, this, 1, 1);
		}

		@Override
		public void run()
		{
			if(ERROR)
			{
				annoyWithNoise(sound, volume, pitch);
				ERROR = false;
			}
			if(TEXT)
			{
				annoyWithText(message);
				TEXT = false;
			}
            if(!PLUGIN.equals("")) PLUGIN = "";
		}

		private void annoyWithNoise(Sound sound, float volume, float pitch)
		{
			for(Player online : Bukkit.getServer().getOnlinePlayers())
				if(online.hasPermission("errornoise.annoy")) online.playSound(online.getLocation(), sound, volume, pitch);
		}

		private void annoyWithText(String message)
		{
			for(Player online : Bukkit.getServer().getOnlinePlayers())
				if(online.hasPermission("errornoise.annoytext"))
                {
                    if(!PLUGIN.equals("")) online.sendMessage(ChatColor.getLastColors(message) + "[" + PLUGIN + "] " + message);
                    else online.sendMessage(message);
                }
		}
	}

    protected static abstract class ErrorHandler extends Handler
	{
		public ErrorHandler()
		{
			Bukkit.getServer().getLogger().addHandler(this);
		}

		@Override
		public abstract void publish(LogRecord record);

		@Override
		public void flush()
		{}

		@Override
		public void close() throws SecurityException
		{}
	}

    protected static class WarningHandler extends ErrorHandler
	{
		@Override
		public void publish(LogRecord record)
		{
			if(record.getMessage().contains("moved wrongly") || record.getMessage().contains("moved too quickly") || record.getMessage().contains("Can't keep up!") || record.getMessage().contains("No compatible nms block class found.")) return;
			if(record.getLevel().equals(Level.WARNING));
		}
	}

    protected static class SevereHandler extends ErrorHandler
	{
		@Override
		public void publish(LogRecord record)
		{
			if(record.getLevel().equals(Level.SEVERE)) API.triggerError();
		}
	}

    protected static class WarningSevereHandler extends ErrorHandler
	{
		@Override
		public void publish(LogRecord record)
		{
			if(record.getMessage().contains("moved wrongly") || record.getMessage().contains("moved too quickly") || record.getMessage().contains("Can't keep up!") || record.getMessage().contains("No compatible nms block class found.")) return;
			if(record.getLevel().equals(Level.SEVERE) || record.getLevel().equals(Level.WARNING)) API.triggerError();
		}
	}
}
