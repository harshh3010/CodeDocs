package mainClasses;

import javafx.application.Platform;
import models.Peer;
import models.User;
import requests.appRequests.AppRequest;
import requests.peerRequests.*;
import services.EditorService;
import utilities.RequestType;
import utilities.UserApi;

import java.io.*;
import java.net.Socket;

/**
 * This class represents the connection between current user and a single online user editing
 * the same CodeDoc. A new thread is started for listening to requests from the connected user
 */
public class EditorBroadcastServerConnection extends Thread {

    private final Socket connection; // info of connected user
    private final ObjectOutputStream outputStream; // Output stream to send response to connected user
    private final ObjectInputStream inputStream; // Input stream to receive requests from connected user

    private User connectedUser;

    private final EditorConnection editorConnection; // Reference to current editor connection

    /**
     * @param connection       stores info of current connection with other user
     * @param editorConnection stores reference to current editor connection
     */
    public EditorBroadcastServerConnection(Socket connection, EditorConnection editorConnection) throws IOException {

        this.connection = connection;
        this.editorConnection = editorConnection;

        outputStream = new ObjectOutputStream(connection.getOutputStream());
        inputStream = new ObjectInputStream(connection.getInputStream());
    }


    /**
     * Actions to be performed on start of new connection thread
     */
    @Override
    public void run() {

        try {
            while (true) {
                AppRequest request = (AppRequest) inputStream.readObject();
                if (request.getRequestType() == RequestType.SEND_PEER_CONNECTION_REQUEST) {

                    SendPeerConnectionRequest connectionRequest = (SendPeerConnectionRequest) request;

                    Peer peer = new Peer();
                    peer.setUser(connectionRequest.getUser());
                    peer.setPort(connectionRequest.getPort());
                    peer.setAudioPort(connectionRequest.getAudioPort());
                    peer.setIpAddress(connection.getInetAddress().getCanonicalHostName());
                    peer.setHasWritePermissions(connectionRequest.isHasWritePermissions());

                    Socket socket = new Socket(peer.getIpAddress(), peer.getPort());

                    peer.setOutputStream(new ObjectOutputStream(socket.getOutputStream()));
                    peer.setInputStream(new ObjectInputStream(socket.getInputStream()));

                    SendPeerInfoRequest infoRequest = new SendPeerInfoRequest();
                    User user = new User();
                    user.setUserID(UserApi.getInstance().getId());
                    user.setFirstName(UserApi.getInstance().getFirstName());
                    user.setLastName(UserApi.getInstance().getLastName());
                    user.setEmail(UserApi.getInstance().getEmail());
                    infoRequest.setUser(user);
                    peer.getOutputStream().writeObject(infoRequest);
                    peer.getOutputStream().flush();

                    Socket audioSocket = new Socket(peer.getIpAddress(), peer.getAudioPort());

                    peer.setAudioOutputStream(new DataOutputStream(audioSocket.getOutputStream()));
                    peer.setAudioInputStream(new DataInputStream(audioSocket.getInputStream()));

                    editorConnection.getConnectedPeers().put(peer.getUser().getUserID(), peer);

                } else if (request.getRequestType() == RequestType.SEND_PEER_INFO_REQUEST) {
                    connectedUser = ((SendPeerInfoRequest) request).getUser();
                } else if (request.getRequestType() == RequestType.STREAM_CONTENT_CHANGES_REQUEST) {
                    StreamContentChangeRequest contentChangeRequest = (StreamContentChangeRequest) request;
                    Platform.runLater(() -> {
                        String insertedText = contentChangeRequest.getInsertedContent();
                        String removedText = contentChangeRequest.getRemovedContent();

                        int insertedStart = contentChangeRequest.getInsertedStart();
                        int removedEnd = contentChangeRequest.getRemovedEnd();
                        int removedStart = removedEnd - removedText.length();

                        // TODO: Check
                        editorConnection.getCodeEditor().removeContent(removedStart, removedEnd);
                        editorConnection.getCodeEditor().insertContent(insertedStart, insertedText);
                    });
                } else if (request.getRequestType() == RequestType.STREAM_CONTENT_SELECTION_REQUEST) {
                    StreamContentSelectionRequest contentSelectionRequest = (StreamContentSelectionRequest) request;
                    Platform.runLater(() -> editorConnection.getCodeEditor().selectContent(contentSelectionRequest.getStart(), contentSelectionRequest.getEnd()));
                } else if (request.getRequestType() == RequestType.STREAM_CURSOR_POSITION_REQUEST) {
                    StreamCursorPositionRequest cursorPositionRequest = (StreamCursorPositionRequest) request;
//                    Platform.runLater(() -> editorConnection.getCodeEditor().moveCursor(cursorPositionRequest.getUserId(), cursorPositionRequest.getPosition()));
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            handleDisconnectedUser();
        }
    }

    /**
     * This function handles all the changes that will take place after a user gets
     * disconnected.
     */
    private void handleDisconnectedUser() {

        // Remove the user from list of active users
        editorConnection.getConnectedPeers().remove(connectedUser.getUserID());

        System.out.println(connectedUser.getFirstName() + " disconnected!");

        // Check if the user who left was the user in control of CodeEditor
        if (connectedUser.getUserID().equals(editorConnection.getUserInControl())) {

            // If the user was in control of editor, then control needs
            // to be transferred to some other user

            // Finding the lexicographically smallest userId from active users with write permissions
            String newUserInControl = editorConnection.isHasWritePermissions() ? UserApi.getInstance().getId() : null;
            for (Peer peer : editorConnection.getConnectedPeers().values()) {
                if (peer.isHasWritePermissions()) {
                    if (newUserInControl == null) {
                        newUserInControl = peer.getUser().getUserID();
                    } else if (peer.getUser().getUserID().compareTo(newUserInControl) < 0) {
                        newUserInControl = peer.getUser().getUserID();
                    }
                }
            }

            // Setting the new user in control of the code editor
            editorConnection.setUserInControl(newUserInControl);

            System.out.println(newUserInControl + " is taking control!");

            // If current user is the new user in control then inform server and make ui changes
            if (newUserInControl != null && newUserInControl.equals(UserApi.getInstance().getId())) {

                // Inform the server of control transfer
                try {
                    EditorService.transferControl(editorConnection.getCodeDoc().getCodeDocId(), UserApi.getInstance().getId());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // TODO: Make other UI changes
                System.out.println("Taking control!");
                editorConnection.getCodeEditor().setEditable(true);
            }
        }
    }
}