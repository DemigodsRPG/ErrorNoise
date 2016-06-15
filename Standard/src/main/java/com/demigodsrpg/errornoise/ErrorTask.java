package com.demigodsrpg.errornoise;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class ErrorTask implements Runnable {

    // -- DATA -- //

    private final Level level;

    // -- STATIC DATA -- //

    private static boolean text;
    private static Long time;

    // -- HARD-CODED SETTINGS -- //

    static int secondsToWait = 8;
    static String message = ChatColor.RED + "An error has occurred, please check the server console.";
    static Sound sound = Sound.ENTITY_BAT_AMBIENT;
    static Float volume = 2F;
    static Float pitch = 0.9F;

    public ErrorTask(Level level) {
        this.level = level;
        time = System.currentTimeMillis();
    }

    @Override
    public void run() {
        // Set the text boolean.
        if (time + (secondsToWait * 1000) <= System.currentTimeMillis()) text = true;

        // Set the time.
        time = System.currentTimeMillis();

        for (Player online : Bukkit.getOnlinePlayers()) {
            annoyWithNoise(online);

            // If the text boolean is true, alert with text.
            if (text) {
                annoyWithText(online);
            }
        }

        // Set text to false.
        text = false;
    }

    Level getLevel() {
        return level;
    }

    private void annoyWithNoise(Player player) {
        if (player.hasPermission("errornoise.annoy")) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }

    private void annoyWithText(Player player) {
        if (player.hasPermission("errornoise.annoytext")) {
            player.sendMessage(message);
        }
    }
}
