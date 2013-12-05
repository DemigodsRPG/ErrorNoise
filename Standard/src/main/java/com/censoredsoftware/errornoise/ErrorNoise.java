package com.censoredsoftware.errornoise;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class ErrorNoise extends JavaPlugin implements CommandExecutor
{
	// Settings
	private static ErrorNoise ERROR_NOISE;
	private static boolean WARNING = true;
	private static boolean SEVERE = true;
	private static int SECONDS_TO_WAIT = 8;
	private static String MESSAGE = ChatColor.RED + "An error has occurred, please check the server console.";
	private static Sound SOUND = Sound.BAT_IDLE;
	private static Float VOLUME = 2F;
	private static Float PITCH = 0.9F;
	private static boolean PREVENT_IGNORED_INFO = true;

	/**
	 * The Bukkit enable method.
	 */
	@Override
	public void onEnable()
	{
		ERROR_NOISE = this;
		loadConfig();
		new API();
		startHandler();
		new Annoy(this, MESSAGE, SOUND, VOLUME, PITCH);
		getCommand("showerror").setExecutor(this);
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
		try
		{
			WARNING = config.getBoolean("error.warning");
		}
		catch(Exception e)
		{
			getLogger().warning("Could not load the 'error.warning' setting.");
		}
		try
		{
			SEVERE = config.getBoolean("error.severe");
		}
		catch(Exception e)
		{
			getLogger().warning("Could not load the 'error.severe' setting.");
		}
		try
		{
			SECONDS_TO_WAIT = config.getInt("error.seconds_to_wait");
		}
		catch(Exception e)
		{
			getLogger().warning("Could not load the 'error.seconds_to_wait' setting.");
		}
		try
		{
			MESSAGE = ChatColor.translateAlternateColorCodes('&', config.getString("error.message"));
		}
		catch(Exception e)
		{
			getLogger().warning("Could not load the 'error.message' setting.");
		}
		try
		{
			SOUND = Sound.valueOf(config.getString("error.sound"));
		}
		catch(Exception e)
		{
			getLogger().warning("Could not load the 'error.sound' setting.");
		}
		try
		{
			VOLUME = Float.parseFloat(config.getString("error.VOLUME"));
		}
		catch(Exception e)
		{
			getLogger().warning("Could not load the 'error.VOLUME' setting.");
		}
		try
		{
			PITCH = Float.parseFloat(config.getString("error.pitch"));
		}
		catch(Exception e)
		{
			getLogger().warning("Could not load the 'error.pitch' setting.");
		}
		try
		{
			PREVENT_IGNORED_INFO = config.getBoolean("console.prevent_ignored_info");
		}
		catch(Exception e)
		{
			getLogger().warning("Could not load the 'console.block_ignored_info_logs' setting.");
		}
	}

	private void startHandler()
	{
		if(PREVENT_IGNORED_INFO) new IgnoreHandler();
		if(WARNING && SEVERE) new WarningSevereHandler();
		else if(WARNING) new WarningHandler();
		else if(SEVERE) new SevereHandler();
	}

	public static class API
	{
		public static void triggerError()
		{
			Annoy.ERROR = true;
			if(Annoy.TIME + (SECONDS_TO_WAIT * 1000) <= System.currentTimeMillis()) Annoy.TEXT = true;
			Annoy.TIME = System.currentTimeMillis();
		}

		public static void triggerError(String pluginName, String... messages)
		{
			Annoy.MESSAGES = Lists.newArrayList(messages);
			triggerError(pluginName);
		}

		public static void triggerError(Plugin plugin, String... messages)
		{
			triggerError(plugin.getName(), messages);
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

	static class Annoy extends BukkitRunnable
	{
		static boolean ERROR;
		static boolean TEXT;
		static long TIME;
		static String PLUGIN;
		static List<String> MESSAGES;
		private final String message;
		private final Sound sound;
		private final float volume;
		private final float pitch;

		public Annoy(Plugin instance, String message, Sound sound, float volume, float pitch)
		{
			ERROR = false;
			TIME = System.currentTimeMillis();
			PLUGIN = "";
			MESSAGES = Lists.newArrayList();
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
			if(!MESSAGES.isEmpty()) MESSAGES = Lists.newArrayList();
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
					if(!MESSAGES.isEmpty())
					{
						if(!PLUGIN.equals("")) for(String line : MESSAGES)
							online.sendMessage(ChatColor.getLastColors(line) + "[" + PLUGIN + "] " + line);
						else for(String line : MESSAGES)
							online.sendMessage(line);
					}
					else if(!PLUGIN.equals("")) online.sendMessage(ChatColor.getLastColors(message) + "[" + PLUGIN + "] " + message);
					else online.sendMessage(message);
				}
		}
	}

	static abstract class ErrorHandler extends Handler
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

	protected static class IgnoreHandler extends ErrorHandler
	{
		@Override
		public void publish(LogRecord record)
		{
			if(containsIgnoredMessage(record) && record.getLevel().equals(Level.INFO)) record.setLevel(Level.OFF);
		}
	}

	protected static class WarningHandler extends ErrorHandler
	{
		@Override
		public void publish(LogRecord record)
		{
			if(containsIgnoredMessage(record)) return;
			if(record.getLevel().equals(Level.WARNING)) API.triggerError();
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
			if(containsIgnoredMessage(record)) return;
			if(record.getLevel().equals(Level.SEVERE) || record.getLevel().equals(Level.WARNING)) API.triggerError();
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(command.getName().equals("showerror")) Annoy.TEXT = true;
		return true;
	}

	private static boolean containsIgnoredMessage(LogRecord record)
	{
		if(ERROR_NOISE.getConfig().isList("ignore")) for(String ignore : ERROR_NOISE.getConfig().getStringList("ignore"))
			if(record.getMessage().contains(ignore)) return true;
		return false;
	}
}
