package mainClasses;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * EditorBroadcastServer is for starting a server for current user on a separate thread,
 * this server will allow other user's editing the same CodeDoc to connect to current user.
 */
public class EditorBroadcastServer extends Thread {

    private final int port; // Port on which server starts
    private final ServerSocket serverSocket;

    private final EditorConnection editorConnection; // Reference to current editor connection

    public EditorBroadcastServer(EditorConnection editorConnection) throws IOException {

        // Starting the server socket on system allocated port
        serverSocket = new ServerSocket(0);

        port = serverSocket.getLocalPort();

        this.editorConnection = editorConnection;
    }

    /**
     * Actions to be performed on start of the new server thread
     */
    @Override
    public void run() {

        // Listening for editor connections after current user has connected
        while (serverSocket.isBound() && !serverSocket.isClosed()) {
            try {

                // Accept connection from a user
                Socket newlyConnectedClient = serverSocket.accept();

                // Starting a new connection handler thread to listen to all requests from newly connected user
                EditorBroadcastServerConnection connection = new EditorBroadcastServerConnection(newlyConnectedClient, editorConnection);
                connection.start();

            } catch (IOException e) {
                System.out.println("Editor connection destroyed!");
            }
        }

    }

    /**
     * Function to stop the server
     */
    public void stopServer() throws IOException {
        serverSocket.close();
    }

    /**
     * Getter for server's port
     */
    public int getPort() {
        return port;
    }
}
