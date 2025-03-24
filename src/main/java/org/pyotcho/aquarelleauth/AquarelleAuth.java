package org.pyotcho.aquarelleauth;

import org.bukkit.plugin.java.JavaPlugin;
import org.pyotcho.aquarelleauth.events.PlayerAuth;
import org.pyotcho.aquarelleauth.events.PlayerListener;
import org.pyotcho.aquarelleauth.util.DatabaseManager;

public final class AquarelleAuth extends JavaPlugin {
    public static final String PLUGIN_NAME = "AquarelleAuth";

    private DatabaseManager databaseManager;
    private PlayerAuth playerAuth;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        databaseManager = new DatabaseManager(this);
        playerAuth = new PlayerAuth(this, databaseManager);

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(playerAuth, this);
        getCommand("reg").setExecutor(playerAuth);
        getCommand("login").setExecutor(playerAuth);

        getLogger().info("Plugin enabled!");
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) databaseManager.closeConnection();
        getLogger().info("Plugin disabled!");
    }

    public PlayerAuth getPlayerAuth() {
        return playerAuth;
    }
}
