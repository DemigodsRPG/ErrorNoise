package com.censoredsoftware.errornoise;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class ErrorTask implements Runnable
{
    // -- STATIC -- //

    private final Level level;

    private static boolean text;
    private static Long time;

    static int secondsToWait = 8;
    static String message = ChatColor.RED + "An error has occurred, please check the server console.";
    static Sound sound = Sound.BAT_IDLE;
    static Float volume = 2F;
    static Float pitch = 0.9F;

    // -- END STATIC -- //

    public ErrorTask(Level level)
    {
        this.level = level;
        time = System.currentTimeMillis();
    }

    @Override
    public void run()
    {
        // Set the text boolean.
        if(time + (secondsToWait * 1000) <= System.currentTimeMillis()) text = true;

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

    Level getLevel()
    {
        return level;
    }

    private void annoyWithNoise()
    {
        for(Player online : Bukkit.getServer().getOnlinePlayers())
            if(online.hasPermission("errornoise.annoy"))
                online.playSound(online.getLocation(), sound, volume, pitch);
    }

    private void annoyWithText()
    {
        for(Player online : Bukkit.getServer().getOnlinePlayers())
            if(online.hasPermission("errornoise.annoytext"))
                online.sendMessage(message);
    }
}
