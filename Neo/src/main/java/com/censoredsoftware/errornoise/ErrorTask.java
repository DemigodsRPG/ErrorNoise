package com.censoredsoftware.errornoise;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;

import java.util.Arrays;
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
        Arrays.asList(Bukkit.getServer().getOnlinePlayers()).stream()
                .filter(online -> online.hasPermission("errornoise.annoy"))
                .forEach(online -> online.playSound(online.getLocation(), sound, volume, pitch));
    }

    private void annoyWithText()
    {
        Arrays.asList(Bukkit.getServer().getOnlinePlayers()).stream()
                .filter(online -> online.hasPermission("errornoise.annoytext"))
                .forEach(online -> online.sendMessage(message));
    }
}
