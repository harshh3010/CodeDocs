package mainClasses;

import models.Peer;
import requests.peerRequests.SendConnectionPortRequest;
import response.editorResponse.EditorConnectionResponse;
import services.EditorService;
import utilities.Status;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class EditorConnection {

    private ArrayList<Peer> activePeers;

    public EditorConnection(String codeDocId) throws IOException, ClassNotFoundException {

        // Start editor broadcast server for broadcasting your changes
        EditorBroadcastServer server = new EditorBroadcastServer();
        server.start();

        // Send editor connection request to server
        EditorConnectionResponse response = EditorService.establishConnection(codeDocId, server.getPort());
        if(response.getStatus() == Status.FAILED) {
            // Stop the broadcasting server in case of failure
            server.stopServer();
            throw new IOException();
        }

        // Fetch the list of active users in current editor
        activePeers = response.getActivePeers();

        // Connect to the broadcasting server of each active user
        for(Peer peer : activePeers) {

            System.out.println(peer.getPort());
            // Connecting to the peer's broadcast server
            Socket socket = new Socket(peer.getIpAddress(), peer.getPort());

            // Storing the IO streams for later use
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            EditorBroadcastServer.outputStreams.put(socket, outputStream);
            EditorBroadcastServer.inputStreams.put(socket, new ObjectInputStream(socket.getInputStream()));

            // Sending port to the peer to allow him to connect to current user's broadcast server
            System.out.println("Writing " + server.getPort());
            SendConnectionPortRequest request = new SendConnectionPortRequest();
            request.setPort(server.getPort());
            outputStream.writeObject(request);
            outputStream.flush();
        }
    }
}
