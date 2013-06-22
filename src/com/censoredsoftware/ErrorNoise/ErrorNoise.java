package com.censoredsoftware.ErrorNoise;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class ErrorNoise extends JavaPlugin
{
	/**
	 * The Bukkit enable method.
	 */
	@Override
	public void onEnable()
	{
		new ErrorHandler();
		new Noise(this, Sound.BAT_IDLE, 2F, 0.9F);
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
	public ErrorHandler()
	{
		Bukkit.getServer().getLogger().addHandler(this);
	}

	@Override
	public void publish(LogRecord record)
	{
		if(record.getLevel().equals(Level.SEVERE) || record.getLevel().equals(Level.WARNING)) Noise.ERROR = true;
	}

	@Override
	public void flush()
	{}

	@Override
	public void close() throws SecurityException
	{}
}

class Noise implements Runnable
{
	public static boolean ERROR;
	private Sound sound;
	private float volume, pitch;

	public Noise(Plugin instance, Sound sound, float volume, float pitch)
	{
		ERROR = false;
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
	}

	private void annoyWithNoise(Sound sound, float volume, float pitch)
	{
		for(Player online : Bukkit.getServer().getOnlinePlayers())
		{
			if(online.hasPermission("errornoise.annoy")) online.playSound(online.getLocation(), sound, volume, pitch);
		}
	}
}
