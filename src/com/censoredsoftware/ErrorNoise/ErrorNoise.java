package com.censoredsoftware.ErrorNoise;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class ErrorNoise extends JavaPlugin
{
	/**
	 * The Bukkit enable method.
	 */
	@Override
	public void onEnable()
	{
		new ErrorHandler(8);
		new Annoy(this, ChatColor.RED + "An error has occurred, please check the server console.", Sound.BAT_IDLE, 2F, 0.9F);
		getLogger().info("Successfully enabled.");
	}

	/**
	 * The Bukkit disable method.
	 */
	@Override
	public void onDisable()
	{
		getLogger().info("Successfully disabled.");
	}
}

class ErrorHandler extends Handler
{
    private int preventSpamSeconds;

	public ErrorHandler(int preventSpamSeconds)
	{
        this.preventSpamSeconds = preventSpamSeconds;
		Bukkit.getServer().getLogger().addHandler(this);
	}

	@Override
	public void publish(LogRecord record)
	{
        if(record.getMessage().contains("moved wrongly") ||  record.getMessage().contains("moved too quickly") || record.getMessage().contains("Can't keep up!") || record.getMessage().contains("No compatible nms block class found.")) return;
		if(record.getLevel().equals(Level.SEVERE) || record.getLevel().equals(Level.WARNING))
		{
			Annoy.ERROR = true;
			if(Annoy.TIME + (preventSpamSeconds * 1000) <= System.currentTimeMillis()) Annoy.TEXT = true;
			Annoy.TIME = System.currentTimeMillis();
		}
	}

	@Override
	public void flush()
	{}

	@Override
	public void close() throws SecurityException
	{}
}

class Annoy implements Runnable
{
	public static boolean ERROR, TEXT;
	public static long TIME;
	private String message;
	private Sound sound;
	private float volume, pitch;

	public Annoy(Plugin instance, String message, Sound sound, float volume, float pitch)
	{
		ERROR = false;
		TIME = System.currentTimeMillis();
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
	}

	private void annoyWithNoise(Sound sound, float volume, float pitch)
	{
		for(Player online : Bukkit.getServer().getOnlinePlayers())
		{
			if(online.hasPermission("errornoise.annoy")) online.playSound(online.getLocation(), sound, volume, pitch);
		}
	}

	private void annoyWithText(String message)
	{
		for(Player online : Bukkit.getServer().getOnlinePlayers())
		{
			if(online.hasPermission("errornoise.annoy")) online.sendMessage(message);
		}
	}
}
