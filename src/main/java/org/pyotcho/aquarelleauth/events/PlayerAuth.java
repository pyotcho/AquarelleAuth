package org.pyotcho.aquarelleauth.events;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.pyotcho.aquarelleauth.util.DatabaseManager;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerAuth implements Listener, CommandExecutor {
    private final DatabaseManager databaseManager;
    private final Set<UUID> unauthenticatedPlayers = new HashSet<>();

    public PlayerAuth(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!databaseManager.isPlayerRegistered(uuid)) {
            player.sendMessage(ChatColor.YELLOW + "Please register with /reg <password> <password>");
            unauthenticatedPlayers.add(uuid);
        } else {
            player.sendMessage(ChatColor.YELLOW + "Please login with /login <password>");
            unauthenticatedPlayers.add(uuid);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (unauthenticatedPlayers.contains(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You can't move while authorisation!");
        }
    }

    @Override
    public boolean onCommand(@Nullable CommandSender sender, @Nullable Command command,
                             @Nullable String label, @Nullable String[] args) {
        if (!(sender instanceof Player player)) {
            assert sender != null;
            sender.sendMessage("This command only for players!");
            return true;
        }

        UUID uuid = player.getUniqueId();

        assert command != null;
        if (command.getName().equalsIgnoreCase("reg")) {
            if (databaseManager.isPlayerRegistered(uuid)) {
                player.sendMessage(ChatColor.RED + "You already registered! Use /login.");
                return true;
            }
            assert args != null;
            if (args.length != 2 || !args[0].equals(args[1])) {
                player.sendMessage(ChatColor.RED + "Use: /reg <password> <password> (passwords must be same)");
                return true;
            }
            databaseManager.registerPlayer(uuid, args[0]);
            unauthenticatedPlayers.remove(uuid);
            player.sendMessage(ChatColor.GREEN + "Successfully registered!");
            return true;
        }

        if (command.getName().equalsIgnoreCase("login")) {
            if (!databaseManager.isPlayerRegistered(uuid)) {
                player.sendMessage(ChatColor.RED + "You aren't registered yet! Use /reg.");
                return true;
            }
            assert args != null;
            if (args.length != 1) {
                player.sendMessage(ChatColor.RED + "Use: /login <password>");
                return true;
            }
            if (databaseManager.checkPassword(uuid, args[0])) {
                unauthenticatedPlayers.remove(uuid);
                player.sendMessage(ChatColor.GREEN + "You successfully logged in!");
            } else {
                player.sendMessage(ChatColor.RED + "Wrong password!");
            }
            return true;
        }
        return false;
    }
}
