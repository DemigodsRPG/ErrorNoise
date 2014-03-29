package com.censoredsoftware.errornoise.lite;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Handler class for the errors.
 */
public class ErrorHandlerLite extends Handler
{
    // -- STATIC -- //

    private static boolean text;
    private static Long time;

    // -- END STATIC -- //

    public ErrorHandlerLite()
    {
        time = System.currentTimeMillis();
    }

    @Override
    public void publish(LogRecord record)
    {
        if(Level.WARNING.equals(record.getLevel()) || Level.SEVERE.equals(record.getLevel()))
            run();
    }

    @Override
    public void flush()
    {}

    @Override
    public void close() throws SecurityException
    {}

    public void run()
    {
        // Set the text boolean.
        if(time + 8000 <= System.currentTimeMillis()) text = true;

        // Set the time.
        time = System.currentTimeMillis();

        annoyWithNoise();

        // If the text boolean is true, alert with text.
        if(text)
        {
            annoyWithText();
            text = false;
        }
    }

    private void annoyWithNoise()
    {
        for(Player online : Bukkit.getServer().getOnlinePlayers())
            if(online.hasPermission("errornoise.annoy"))
                online.playSound(online.getLocation(), Sound.BAT_IDLE, 2F, 0.9F);
    }

    private void annoyWithText()
    {
        for(Player online : Bukkit.getServer().getOnlinePlayers())
            if(online.hasPermission("errornoise.annoytext"))
                online.sendMessage(ChatColor.RED + "An error has occurred, please check the server console.");
    }
}