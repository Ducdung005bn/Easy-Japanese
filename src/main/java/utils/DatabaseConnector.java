package utils;

import java.sql.*;

/**
 * Singleton class responsible for managing database connections.
 */
public class DatabaseConnector {
    private static DatabaseConnector instance;
    private Connection connection;

    private DatabaseConnector() {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/easy_japanese",
                    "root",
                    "Quynh17@"
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DatabaseConnector getInstance() {
        if (instance == null) {
            instance = new DatabaseConnector();
        }
        return instance;
    }

    public ResultSet executeQuery(String query) {
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
