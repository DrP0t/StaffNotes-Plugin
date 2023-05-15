package me.drpot.staffnotes.command;

import me.drpot.staffnotes.manager.DatabaseManager;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class StaffNotesCommand implements CommandExecutor {
    private final DatabaseManager databaseManager;
    private final String commandPrefix;
    private final List<String> helpMessages;
    private final String permissions;

    public StaffNotesCommand(DatabaseManager databaseManager, String commandPrefix, List<String> helpMessages, String permissions) {
        this.databaseManager = databaseManager;
        this.commandPrefix = commandPrefix;
        this.helpMessages = helpMessages;
        this.permissions = permissions;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
            return true;
        }

        Player player = (Player) sender;

        // Check permission
        if (!player.hasPermission(permissions)) {
            player.sendMessage(ChatColor.DARK_RED + "You don't have permission to use this command.");
            return true;
        }

        // Handles command sub-commands
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            // Handle /notes or /notes help
            showHelp(player);
        } else if (args[0].equalsIgnoreCase("add")) {
            // Handles /notes add [player] [NoteTitle] [note]
            if (args.length < 4) {
                player.sendMessage(ChatColor.RED +  "Usage: /notes add [player] [NoteTitle] [note]");
                return true;
            }

            String targetPlayer = args[1];
            String noteTitle = args[2];
            String noteContent = args[3];
            String savedBy = player.getName();

            databaseManager.savePlayerNote(targetPlayer, noteTitle, noteContent, savedBy);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',commandPrefix) + "Note added for player: " + ChatColor.GOLD + targetPlayer);
        } else if (args[0].equalsIgnoreCase("remove")) {
            // Handles /notes remove [player] [NoteTitle]
            if (args.length < 3) {
                player.sendMessage(ChatColor.RED +  "Usage: /notes remove [player] [NoteTitle] (Titles are Case-Sensitive)");
                return true;
            }

            String targetPlayer = args[1];
            String noteTitle = args[2];

            databaseManager.removePlayerNote(targetPlayer, noteTitle);
            player.sendMessage( ChatColor.translateAlternateColorCodes('&', commandPrefix)  + "Note removed for player: " + targetPlayer);
        } else if (args[0].equalsIgnoreCase("edit")) {
            // Handles /notes edit [player] [NoteTitle] [newnote]
            if (args.length < 4) {
                player.sendMessage(ChatColor.RED +  "Usage: /notes edit [player] [NoteTitle] [newnote]");
                return true;
            }

            String targetPlayer = args[1];
            String noteTitle = args[2];
            String newNoteContent = args[3];

            databaseManager.updatePlayerNote(targetPlayer, noteTitle, newNoteContent);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', commandPrefix) + "Note updated for player: " + targetPlayer);
        } else if (args[0].equalsIgnoreCase("lookup")) {
            // Retrieve and display player notes using the database manager
            String targetPlayer;
            if (args.length > 1) {
                targetPlayer = args[1];
            } else {
                targetPlayer = player.getName();
            }

            List<Document> playerNotes = databaseManager.getPlayerNotes(targetPlayer);
            if (playerNotes.isEmpty()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', commandPrefix) + "This player has no notes.");
            } else {
                player.sendMessage("---------------------");
                player.sendMessage(ChatColor.WHITE + "=== " + ChatColor.GOLD + player.getDisplayName()  +  "'s Notes" + ChatColor.WHITE + " ===");
                player.sendMessage(" ");
                for (Document note : playerNotes) {
                    String noteTitle = note.getString("title");
                    String noteContent = note.getString("content");
                    String savedBy = note.getString("saved_by");

                    player.sendMessage(ChatColor.GOLD + "Title: "+ ChatColor.WHITE + noteTitle);
                    player.sendMessage(ChatColor.GOLD + "Content: " + ChatColor.WHITE + noteContent);
                    player.sendMessage(ChatColor.GOLD + "Saved by: " + ChatColor.WHITE + savedBy);
                    player.sendMessage("---------------------");
                }
            }
        } else {
            player.sendMessage( ChatColor.translateAlternateColorCodes('&',commandPrefix) + "Unknown command. Use /notes help for command information.");
        }

        return true;
    }

    private void showHelp(Player player) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',commandPrefix));
        for (String message : helpMessages) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',message));
        }
    }
}
