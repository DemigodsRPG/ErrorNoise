package com.demigodsrpg.errornoise.lite;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class ErrorHandlerLite extends Handler {
    
    // -- DATA -- //

    private boolean text;
    private Long time;

    public ErrorHandlerLite() {
        time = System.currentTimeMillis();
    }

    @Override
    public void publish(LogRecord record) {
        if (Level.WARNING.equals(record.getLevel()) || Level.SEVERE.equals(record.getLevel())) {
            // Set the text boolean.
            if (time + 8000 <= System.currentTimeMillis()) text = true;

            // Set the time.
            time = System.currentTimeMillis();

            for (Player online : Bukkit.getOnlinePlayers()) {
                if (online.hasPermission("errornoise.annoy")) online.playSound(online.getLocation(),
                        Sound.ENTITY_BAT_AMBIENT, 2F, 0.9F);

                // If the text boolean is true, alert with text.
                if (text && online.hasPermission("errornoise.annoytext")) online.sendMessage(ChatColor.RED +
                        "An error has occurred, please check the server console.");
            }

            // Set text to false.
            text = false;
        }
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }
}
