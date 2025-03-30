package org.pyotcho.aquarelleauth.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.pyotcho.aquarelleauth.AquarelleAuth;

import java.sql.*;
import java.util.UUID;

public class DatabaseManager {
    private final AquarelleAuth plugin;
    private Connection connection;

    public DatabaseManager(AquarelleAuth plugin) {
        this.plugin = plugin;
        initializeDatabase();
    }

    private void initializeDatabase() {
        FileConfiguration config = plugin.getConfig();
        String host = config.getString("database.host");
        String port = config.getString("database.port");
        String database = config.getString("database.name");
        String username = config.getString("database.username");
        String password = config.getString("database.password");

        String url = "jdbc:postgresql://" + host + ":" + port + "/" + database;

        try {
            connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS players (" +
                    "uuid VARCHAR(36) PRIMARY KEY, " +
                    "password VARCHAR(255) NOT NULL)");
            statement.close();
            plugin.getLogger().info("Connection successful!");
        } catch (SQLException e) {
            plugin.getLogger().severe("Connection refused: " + e.getMessage());
        }
    }

    public boolean isPlayerRegistered(UUID uuid) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT 1 FROM players WHERE uuid = ?");
            statement.setString(1, uuid.toString());
            ResultSet rs = statement.executeQuery();
            boolean exists = rs.next();
            rs.close();
            statement.close();
            return exists;
        } catch (SQLException e) {
            plugin.getLogger().severe("Error when registration: " + e.getMessage());
            return false;
        }
    }

    public void registerPlayer(UUID uuid, String password) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO players (uuid, password) VALUES (?, ?)");
            statement.setString(1, uuid.toString());
            statement.setString(2, password);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("Error when registration: " + e.getMessage());
        }
    }

    public boolean checkPassword(UUID uuid, String password) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT password FROM players WHERE uuid = ?");
            statement.setString(1, uuid.toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String storedPassword = resultSet.getString("password");
                resultSet.close();
                statement.close();
                return storedPassword.equals(password);
            }
            resultSet.close();
            statement.close();
            return false;
        } catch (SQLException e) {
            plugin.getLogger().severe("Error when checking password " + e.getMessage());
            return false;
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error when closing connection: " + e.getMessage());
        }
    }
}
