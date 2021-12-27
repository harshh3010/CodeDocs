package services;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * This class defines functions for managing database connection
 */
public class DatabaseConnection {

    /**
     * Function to connect to a database
     */
    public static Connection connect() throws IOException, ClassNotFoundException, SQLException {

        // Reading the database credentials from files
        Properties properties = new Properties();
        FileReader fileReader = new FileReader("CodeDocs-Server/src/main/resources/configurations/db.properties");
        properties.load(fileReader);

        final String host = properties.getProperty("HOST");
        final String username = properties.getProperty("USERNAME");
        final String password = properties.getProperty("PASSWORD");

        // Connecting to database
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(host, username, password);
    }
}