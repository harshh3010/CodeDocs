package mainClasses;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class EditorBroadcastServer extends Thread{

    private final int port;
    private final ServerSocket serverSocket;

    public EditorBroadcastServer() throws IOException {
        // TODO: Read port from config file
        port = 3003;
        serverSocket = new ServerSocket(port);
    }

    @Override
    public void run() {
        // Listening for editor connections after current user has connected
        while(serverSocket.isBound() && !serverSocket.isClosed()) {
            try {
                // Start a new thread for listening to requests from every connected user
                Socket newlyConnectedClient = serverSocket.accept();
                EditorBroadcastServerConnection connection = new EditorBroadcastServerConnection(newlyConnectedClient);
                connection.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopServer() throws IOException {
        serverSocket.close();
    }

    public int getPort() {
        return port;
    }
}
