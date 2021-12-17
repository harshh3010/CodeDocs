package mainClasses;

import requests.appRequests.AppRequest;
import requests.peerRequests.SendConnectionPortRequest;
import utilities.RequestType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class EditorBroadcastServerConnection extends Thread{

    private final Socket connection;
    private final ObjectOutputStream outputStream;
    private final ObjectInputStream inputStream;

    public EditorBroadcastServerConnection(Socket connection) throws IOException {
        this.connection = connection;
        outputStream = new ObjectOutputStream(connection.getOutputStream());
        inputStream = new ObjectInputStream(connection.getInputStream());
    }

    @Override
    public void run() {
        try {
            while (true) {
                AppRequest request = (AppRequest) inputStream.readObject();
                if(request.getRequestType() == RequestType.SEND_CONNECTION_PORT_REQUEST){
                    int port = ((SendConnectionPortRequest) request).getPort();
                    System.out.println(port);
                    Socket socket = new Socket(connection.getInetAddress().getCanonicalHostName(), port);
                    EditorBroadcastServer.outputStreams.put(socket, new ObjectOutputStream(socket.getOutputStream()));
                    EditorBroadcastServer.inputStreams.put(socket, new ObjectInputStream(socket.getInputStream()));
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            inputStream.close();
            outputStream.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
