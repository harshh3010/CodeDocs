package mainClasses;

import services.DatabaseConnection;
import services.DestroyResources;

import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * CodeDocsServer class, starts an instance of CodeDocs server
 */
public class CodeDocsServer {

    private static int port; //port on which server is running
    private static ServerSocket serverSocket; // Socket to accept client connections

    public static Connection databaseConnection; // Stores instance of database connection

    /**
     * This function reads the port and tries to connect with the database
     * Once connected with database the server starts at the specified port and listens for clients
     */
    public static void main(String[] args) throws IOException {

        // Reading the server port from config file
        try {
            Properties properties = new Properties();
            FileReader fileReader = new FileReader("CodeDocs-Server/src/main/resources/configurations/server.properties");
            properties.load(fileReader);

            port = Integer.parseInt(properties.getProperty("PORT"));
        } catch (IOException e) {
            System.out.println("Unable to load server config file!");
            return;
        }

        // Establish connection with the database
        try {
            databaseConnection = DatabaseConnection.connect();
            System.out.println("Database connection established.");
            DestroyResources.cleanDB();
        } catch (SQLException | IOException | ClassNotFoundException e) {
            System.out.println("Unable to establish connection with the database!");
            System.out.println("Error: " + e.getMessage());
            return;
        }

        // Starting server using tcp sockets
        try {
            // Binding the server socket on a specified port
            serverSocket = new ServerSocket(port);

            System.out.println("Server started on port " + port);

            // Making the server listen for client connections
            while (serverSocket.isBound() && !serverSocket.isClosed()) {

                System.out.println("Waiting for client connections...");

                // A client gets connected to the server
                Socket client = serverSocket.accept();

                try {
                    // Starting a new thread for handling the client requests
                    ClientConnection connection = new ClientConnection(client);
                    connection.start();

                } catch (IOException e) {
                    // Exception in starting a new connection
                    System.out.println("An error occurred while establishing connection with the client!");
                    System.out.println("Error: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            // Exception occurred while starting the server socket
            System.out.println("Unable to start the server!");
            System.out.println("Error: " + e.getMessage());
        }
    }
}