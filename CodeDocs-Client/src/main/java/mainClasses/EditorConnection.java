package mainClasses;

import mainClasses.audioConnection.AudioReceiver;
import mainClasses.audioConnection.AudioTransmitter;
import models.Peer;
import models.User;
import requests.peerRequests.SendPeerConnectionRequest;
import response.editorResponse.EditorConnectionResponse;
import services.EditorService;
import utilities.CodeEditor;
import utilities.Status;
import utilities.UserApi;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * EditorConnection class is for establishing a new editor connection for some CodeDoc
 * for the current user. A new server is started and connections with the other online users are
 * established by connecting to their servers. Instances of this class for multiple users form
 * a complete network for collaborative code editing.
 */
public class EditorConnection {

    private CodeEditor codeEditor;  // Reference to the CodeEditor corresponding to this connection
    private final String userInControl; // User currently in control of the CodeEditor

    private final HashMap<String, Peer> connectedPeers = new HashMap<>(); // Collection of all connected users

    private final EditorBroadcastServer server; // Server to receive requests from other connected users
    private final AudioReceiver audioReceiver;
    private final AudioTransmitter audioTransmitter;

    /**
     * @param codeDocId of the CodeDoc to be edited
     * @throws IOException if connection cannot be established
     */
    public EditorConnection(String codeDocId) throws IOException, ClassNotFoundException {

        // Start editor broadcast server for broadcasting your changes
        server = new EditorBroadcastServer(this);
        server.start();

        audioReceiver = new AudioReceiver(this);
        audioReceiver.start();

        audioTransmitter = new AudioTransmitter(this);
        audioTransmitter.start();

        // Send editor connection request to server
        EditorConnectionResponse response = EditorService.establishConnection(codeDocId, server.getPort(), audioReceiver.getPort());
        if (response.getStatus() == Status.FAILED) {
            // Stop the broadcasting server in case of failure
            server.stopServer();
            audioReceiver.stopServer();
            audioTransmitter.stop();
            throw new IOException();
        }

        // Fetch the list of active users in current editor
        ArrayList<Peer> activePeers = response.getActivePeers();
        boolean hasWritePermissions = response.isHasWritePermissions();
        userInControl = response.getUserInControl();

        // Connect to the broadcasting server of each active user
        for (Peer peer : activePeers) {

            // Connecting to the user's broadcast server
            Socket socket = new Socket(peer.getIpAddress(), peer.getPort());
            peer.setSocket(socket);

            // Storing the IO streams for later use
            peer.setOutputStream(new ObjectOutputStream(socket.getOutputStream()));
            peer.setInputStream(new ObjectInputStream(socket.getInputStream()));

            Socket audioSocket = new Socket(peer.getIpAddress(), peer.getAudioPort());

            peer.setAudioOutputStream(new DataOutputStream(audioSocket.getOutputStream()));
            peer.setAudioInputStream(new DataInputStream(audioSocket.getInputStream()));

            // Sending port to the peer to allow him to connect to current user's broadcast server
            // Creating a new connection request to connect to the user's server
            SendPeerConnectionRequest request = new SendPeerConnectionRequest();

            User user = new User();
            user.setUserID(UserApi.getInstance().getId());
            user.setFirstName(UserApi.getInstance().getFirstName());
            user.setLastName(UserApi.getInstance().getLastName());
            user.setEmail(UserApi.getInstance().getEmail());

            request.setUser(user);
            request.setPort(server.getPort());
            request.setAudioPort(audioReceiver.getPort());
            request.setHasWritePermissions(hasWritePermissions);

            peer.getOutputStream().writeObject(request);
            peer.getOutputStream().flush();

            // Storing the p2p connection info in hashmap
            connectedPeers.put(peer.getUser().getUserID(), peer);
        }
    }

    /**
     * To close the editor connection
     *
     * @throws IOException in case of failure
     */
    public void closeConnection() throws IOException {
        server.stopServer();
    }

    /**
     * Getter for collection of online users
     */
    public HashMap<String, Peer> getConnectedPeers() {
        return connectedPeers;
    }

    /**
     * Getter for CodeEditor instance of current connection
     */
    public CodeEditor getCodeEditor() {
        return codeEditor;
    }

    /**
     * Setter for CodeEditor instance of current connection
     */
    public void setCodeEditor(CodeEditor codeEditor) {
        this.codeEditor = codeEditor;
    }

    /**
     * Getter for user in control of the CodeEditor
     */
    public String getUserInControl() {
        return userInControl;
    }
}
