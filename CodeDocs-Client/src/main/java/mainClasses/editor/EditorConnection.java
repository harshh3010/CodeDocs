package mainClasses.editor;

import controllers.chat.ChatTabController;
import mainClasses.audio.AudioReceiver;
import mainClasses.audio.AudioTransmitter;
import models.CodeDoc;
import models.Peer;
import models.User;
import requests.peerRequests.SendPeerConnectionRequest;
import requests.peerRequests.SendPeerInfoRequest;
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

    private ChatTabController chatController;
    private final CodeDoc codeDoc;
    private CodeEditor codeEditor;  // Reference to the CodeEditor corresponding to this connection
    private String userInControl; // User currently in control of the CodeEditor
    private boolean hasWritePermissions;
    private boolean isMute = false;

    private final HashMap<String, Peer> connectedPeers = new HashMap<>(); // Collection of all connected users

    private final EditorServer server; // Server to receive requests from other connected users
    private final AudioReceiver audioReceiver;
    private final AudioTransmitter audioTransmitter;

    /**
     * @param codeDoc to be edited
     * @throws IOException if connection cannot be established
     */
    public EditorConnection(CodeDoc codeDoc) throws IOException, ClassNotFoundException {

        this.codeDoc = codeDoc;

        // Start editor broadcast server for broadcasting your changes
        server = new EditorServer(this);
        audioReceiver = new AudioReceiver(this);
        audioTransmitter = new AudioTransmitter(this);

        // Send editor connection request to server
        EditorConnectionResponse response = EditorService.establishConnection(codeDoc.getCodeDocId(), server.getPort(), audioReceiver.getPort());
        if (response.getStatus() == Status.FAILED) {
            // Stop the broadcasting server in case of failure
            server.stopServer();
            audioReceiver.stopServer();
            audioTransmitter.stopTransmission();
            throw new IOException();
        }

        server.start();
        audioReceiver.start();
        audioTransmitter.start();

        // Fetch the list of active users in current editor
        ArrayList<Peer> activePeers = response.getActivePeers();
        hasWritePermissions = response.isHasWritePermissions();
        userInControl = response.getUserInControl();

        // Connect to the broadcasting server of each active user
        for (Peer peer : activePeers) {
            try {
                // Connecting to the user's broadcast server
                Socket socket = new Socket(peer.getIpAddress(), peer.getPort());

                // Storing the IO streams for later use
                peer.setOutputStream(new ObjectOutputStream(socket.getOutputStream()));
                peer.setInputStream(new ObjectInputStream(socket.getInputStream()));

                Socket audioSocket = new Socket(peer.getIpAddress(), peer.getAudioPort());

                peer.setAudioOutputStream(new DataOutputStream(audioSocket.getOutputStream()));
                peer.setAudioInputStream(new DataInputStream(audioSocket.getInputStream()));
                peer.setMuted(false);

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

                SendPeerInfoRequest infoRequest = new SendPeerInfoRequest();
                infoRequest.setUser(user);
                peer.getOutputStream().writeObject(infoRequest);
                peer.getOutputStream().flush();

                // Storing the p2p connection info in hashmap
                connectedPeers.put(peer.getUser().getUserID(), peer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * To close the editor connection
     *
     * @throws IOException in case of failure
     */
    public void closeConnection() throws IOException {

        EditorService.destroyConnection(codeDoc.getCodeDocId());

        for(Peer peer : connectedPeers.values()) {
            peer.getInputStream().close();
            peer.getOutputStream().close();
            peer.getAudioOutputStream().close();
            peer.getAudioInputStream().close();

            peer = null;
        }

        connectedPeers.clear();

        audioTransmitter.stopTransmission();
        audioReceiver.stopServer();
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

    /**
     * Setter for user in control of the CodeEditor
     */
    public void setUserInControl(String userInControl) {
        this.userInControl = userInControl;

        // If current user is the new user in control then inform server and make ui changes
        if (userInControl != null && userInControl.equals(UserApi.getInstance().getId())) {

            // Inform the server of control transfer
            try {
                EditorService.transferControl(getCodeDoc().getCodeDocId(), UserApi.getInstance().getId());
            } catch (IOException e) {
                e.printStackTrace();
            }

            // TODO: Make other UI changes
            System.out.println("Taking control!");
            getCodeEditor().setEditable(true);
        } else {
            getCodeEditor().setEditable(false);
        }
    }

    /**
     * Getter for the current codedoc
     */
    public CodeDoc getCodeDoc() {
        return codeDoc;
    }

    public boolean isHasWritePermissions() {
        return hasWritePermissions;
    }

    public void setHasWritePermissions(boolean hasWritePermissions) {
        this.hasWritePermissions = hasWritePermissions;
    }

    public ChatTabController getChatController() {
        return chatController;
    }

    public void setChatController(ChatTabController chatController) {
        this.chatController = chatController;
    }

    public boolean isMute() {
        return isMute;
    }

    public void setMute(boolean mute) {
        isMute = mute;
    }
}
