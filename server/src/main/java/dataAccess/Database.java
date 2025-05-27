package dataAccess;
import java.sql;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.properties;


public class Database{
    private static final String Database_Name = "chessgame";
    private static final String Database_Username = "root";
    private static final String Database_Password = "password";
    private final String Connection_URL;

    public Database {
        Connection_URL = "jdbc:mysql://localhost:3306/" + Database_Name;

    }
    public connection getConnection() throws SQLException {
        return DriverManager.getConnection(Connection_URL, Database_Username, Database_Password);
    }
    public void configureDatabase() throws DataAccessException {
        createDatabase();
        createTables();
    }

    public createDatabase() throws DataAccessException{
        try {
            String dbURL = "jdbc:mysql://localhost:3306";
            try (connection conn = DriverManager.getConnection(Connection_URL, Database_Username, Database_Password);
                 Statement stmt = conn.createStatement()) {
                String sql = "CREATE DATABASE IF NOT EXISTS " + Database_Name;
                stmt.executeUpdate(sql);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to create database: " + e.getMessage());
        }
    }

    private void createTables() throws DataAccessException {
        try (connection conn = getConnection()) {
            createUsersTable(conn);
            createGamesTable(conn);
            createAuthsTable(conn);
        } catch (SQLException e) {
            throw new DataAccessException("Unable to create tables: " + e.getMessage());
        }
    }

    private void createUsersTable(connection conn) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS users (
                username VARCHAR(255) NOT NULL PRIMARY KEY,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL
            )
        """;
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    private void createGamesTable(connection conn) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS games (
                gameID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                whiteUsername VARCHAR(255),
                blackUsername VARCHAR(255),
                gameName VARCHAR(255) NOT NULL,
                chessGame TEXT NOT NULL
            )
        """;
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    private void createAuthsTable(connection conn) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS auths (
                authToken VARCHAR(255) NOT NULL PRIMARY KEY,
                username VARCHAR(255) NOT NULL
            )
        """;
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }
}