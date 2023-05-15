package me.drpot.staffnotes;

import me.drpot.staffnotes.command.StaffNotesCommand;
import me.drpot.staffnotes.manager.DatabaseManager;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class StaffNotes extends JavaPlugin {
    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        // Load configuration and retrieve database URI
        saveDefaultConfig();
        String databaseUri = getConfig().getString("database.uri");

        // Connect to the database
        try {
            databaseManager = new DatabaseManager(databaseUri);
            logToConsole("Successfully connected to the database.");
        } catch (Exception e) {
            logToConsole("Failed to connect to the database. Disabling the plugin.");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Registers the command
        String commandPrefix = this.getConfig().getString("messages.prefix");
        List<String> helpMessages = this.getConfig().getStringList("messages.help");
        String permissions = this.getConfig().getString("permissions.note");

        getCommand("notes").setExecutor(new StaffNotesCommand(databaseManager, commandPrefix, helpMessages, permissions));

    }

    @Override
    public void onDisable() {

    }

    private void logToConsole(String message) {
        ConsoleCommandSender console = getServer().getConsoleSender();
        console.sendMessage(ChatColor.GOLD + "[StaffNotes] " + ChatColor.WHITE + message);
    }
}
