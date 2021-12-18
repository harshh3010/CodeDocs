package mainClasses;

import models.Peer;
import requests.appRequests.AppRequest;
import requests.peerRequests.SendPeerConnectionRequest;
import utilities.RequestType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class EditorBroadcastServerConnection extends Thread {

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

        // Update online status
        try {
            while (true) {
                AppRequest request = (AppRequest) inputStream.readObject();
                if (request.getRequestType() == RequestType.SEND_PEER_CONNECTION_REQUEST) {

                    SendPeerConnectionRequest connectionRequest = (SendPeerConnectionRequest) request;

                    Peer peer = new Peer();
                    peer.setUser(connectionRequest.getUser());
                    peer.setPort(connectionRequest.getPort());
                    peer.setIpAddress(connection.getInetAddress().getCanonicalHostName());

                    Socket socket = new Socket(peer.getIpAddress(), peer.getPort());

                    peer.setSocket(connection);
                    peer.setOutputStream(new ObjectOutputStream(socket.getOutputStream()));
                    peer.setInputStream(new ObjectInputStream(socket.getInputStream()));

                    EditorConnection.connectedPeers.put(peer.getUser().getUserID(), peer);
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