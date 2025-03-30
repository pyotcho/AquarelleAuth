package org.pyotcho.aquarelleauth;

import org.bukkit.plugin.java.JavaPlugin;
import org.pyotcho.aquarelleauth.events.PlayerAuth;
import org.pyotcho.aquarelleauth.util.DatabaseManager;

import java.util.Objects;

public final class AquarelleAuth extends JavaPlugin {
    public static final String PLUGIN_NAME = "AquarelleAuth";

    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        databaseManager = new DatabaseManager(this);
        PlayerAuth playerAuth = new PlayerAuth(databaseManager);

        getServer().getPluginManager().registerEvents(playerAuth, this);
        Objects.requireNonNull(getCommand("reg")).setExecutor(playerAuth);
        Objects.requireNonNull(getCommand("login")).setExecutor(playerAuth);

        getLogger().info("Plugin enabled!");
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) databaseManager.closeConnection();
        getLogger().info("Plugin disabled!");
    }
}
