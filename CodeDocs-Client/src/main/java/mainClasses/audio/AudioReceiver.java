package mainClasses.audio;

import mainClasses.editor.EditorConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * AudioReceiver starts an audio receiving server for the current user on a separate thread,
 * other users can connect to this server to transmit audio
 */
public class AudioReceiver extends Thread{

    private volatile boolean isActive = true; // Is server still active

    private final EditorConnection editorConnection; // Reference to the current editor connection

    private final ServerSocket serverSocket; // Audio server socket
    private final int port; // Audio server port

    public AudioReceiver(EditorConnection editorConnection) throws IOException {
        this.editorConnection = editorConnection;

        // Starting the server socket on system allocated port
        serverSocket = new ServerSocket(0);
        port = serverSocket.getLocalPort();
    }


    @Override
    public void run() {

        System.out.println("Audio receiver started!");

        // Receive connections from other users
        while(serverSocket.isBound() && !serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                AudioReceiverConnection connection = new AudioReceiverConnection(socket, editorConnection);
                connection.start();
            } catch (IOException e) {
                System.out.println("Unable to connect to a user's audio transmitter.");
            }
        }

        System.out.println("Audio receiver closed!");
    }

    public void stopServer() throws IOException {
        serverSocket.close();
    }

    public int getPort() {
        return port;
    }
}
