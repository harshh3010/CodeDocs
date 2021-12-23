package mainClasses.audio;

import mainClasses.editor.EditorConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class AudioReceiver extends Thread{

    private final EditorConnection editorConnection;

    private final ServerSocket serverSocket;
    private final int port;

    public AudioReceiver(EditorConnection editorConnection) throws IOException {
        this.editorConnection = editorConnection;

        // Starting the server socket on system allocated port
        serverSocket = new ServerSocket(0);
        port = serverSocket.getLocalPort();
    }


    @Override
    public void run() {

        while(serverSocket.isBound() && !serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                AudioReceiverConnection connection = new AudioReceiverConnection(socket, editorConnection);
                connection.start();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Client disconnected!");
            }
        }
    }

    public void stopServer() throws IOException {
        this.serverSocket.close();
    }

    public int getPort() {
        return port;
    }
}
