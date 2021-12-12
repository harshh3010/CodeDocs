import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class CodeDocsServer {

    private static int port;
    private static ServerSocket serverSocket;
    private static Socket client;

    public static void main(String[] args) throws FileNotFoundException {

        try {
            Properties properties = new Properties();
            FileReader fileReader = new FileReader("CodeDocs-Server/src/main/resources/configurations/server.properties");
            properties.load(fileReader);

            port = Integer.parseInt(properties.getProperty("PORT"));

        } catch (IOException e) {
            System.out.println("Unable to load server config file!");
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
                client = serverSocket.accept();

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