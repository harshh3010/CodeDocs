package services;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    /**
     * Method for creating connection between
     * Mysql Database and CodeDocs server
     *
     * @return Connection Object , null if Connection not Established
     */
    public static Connection connect() throws IOException, ClassNotFoundException, SQLException {

        Properties properties = new Properties();
        FileReader fileReader = new FileReader("CodeDocs-Server/src/main/resources/configurations/db.properties");
        properties.load(fileReader);

        final String host = properties.getProperty("HOST");
        final String username = properties.getProperty("USERNAME");
        final String password = properties.getProperty("PASSWORD");

        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(host, username, password);
    }
}