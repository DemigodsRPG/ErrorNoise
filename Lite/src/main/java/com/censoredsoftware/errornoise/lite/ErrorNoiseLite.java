package com.censoredsoftware.errornoise.lite;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class ErrorNoiseLite extends JavaPlugin {
    private static boolean ERROR;
    private static boolean TEXT;
    private static long TIME;
    @Override
    public void onEnable() {
        new ErrorHandler();
        new Annoy(this);
        getLogger().info("Successfully enabled.");
    }
    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        getLogger().info("Successfully disabled.");
    }
    static class Annoy extends BukkitRunnable {
        public Annoy(Plugin instance) {
            ERROR = false;
            TIME = System.currentTimeMillis();
            Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, this, 1, 1);
        }
        @Override
        public void run() {
            if(ERROR) {
                for(Player online : Bukkit.getServer().getOnlinePlayers())
                    if(online.hasPermission("errornoise.annoy")) online.playSound(online.getLocation(), Sound.BAT_IDLE, 2F, 0.9F);
                ERROR = false;
            }
            if(TEXT) {
                for(Player online : Bukkit.getServer().getOnlinePlayers())
                    if(online.hasPermission("errornoise.annoytext")) online.sendMessage(ChatColor.RED + "An error has occurred, please check the server console.");
                TEXT = false;
            }
        }
    }
    static class ErrorHandler extends Handler {
        public ErrorHandler() { Bukkit.getServer().getLogger().addHandler(this); }
        @Override
        public void publish(LogRecord record) {
            if(record.getMessage().contains("moved wrongly") || record.getMessage().contains("moved too quickly") || record.getMessage().contains("Can't keep up!") || record.getMessage().contains("No compatible nms block class found.")) return;
            if(record.getLevel().equals(Level.SEVERE) || record.getLevel().equals(Level.WARNING)) {
                ERROR = true;
                if(TIME + 8000 <= System.currentTimeMillis()) TEXT = true;
                TIME = System.currentTimeMillis();
            }
        }
        @Override
        public void flush() {}
        @Override
        public void close() throws SecurityException {}
    }
}
