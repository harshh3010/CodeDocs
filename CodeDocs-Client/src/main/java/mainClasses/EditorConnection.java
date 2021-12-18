package mainClasses;

import models.Peer;
import models.User;
import requests.peerRequests.SendPeerConnectionRequest;
import response.editorResponse.EditorConnectionResponse;
import services.EditorService;
import utilities.Status;
import utilities.UserApi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class EditorConnection {

    public static HashMap<String, Peer> connectedPeers = new HashMap<>();
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
            peer.setSocket(socket);

            // Storing the IO streams for later use
            peer.setOutputStream(new ObjectOutputStream(socket.getOutputStream()));
            peer.setInputStream(new ObjectInputStream(socket.getInputStream()));

            // Sending port to the peer to allow him to connect to current user's broadcast server
            System.out.println("Writing " + server.getPort());

            SendPeerConnectionRequest request = new SendPeerConnectionRequest();

            User user = new User();
            user.setUserID(UserApi.getInstance().getId());
            user.setFirstName(UserApi.getInstance().getFirstName());
            user.setLastName(UserApi.getInstance().getLastName());
            user.setEmail(UserApi.getInstance().getEmail());

            request.setUser(user);
            request.setPort(server.getPort());

            peer.getOutputStream().writeObject(request);
            peer.getOutputStream().flush();

            connectedPeers.put(peer.getUser().getUserID(), peer);
        }
    }
}
