package mainClasses.editor;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import models.Chat;
import models.Peer;
import models.User;
import requests.appRequests.AppRequest;
import requests.peerRequests.*;
import utilities.RequestType;
import utilities.UserApi;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.Optional;

/**
 * This class represents the connection between current user and a single online user editing
 * the same CodeDoc. A new thread is started for listening to requests from the connected user
 */
public class EditorServerConnection extends Thread {

    private volatile boolean isActive = true;
    private final Socket connection; // info of connected user
    private final ObjectOutputStream outputStream; // Output stream to send response to connected user
    private final ObjectInputStream inputStream; // Input stream to receive requests from connected user

    private User connectedUser;

    private final EditorConnection editorConnection; // Reference to current editor connection

    /**
     * @param connection       stores info of current connection with other user
     * @param editorConnection stores reference to current editor connection
     */
    public EditorServerConnection(Socket connection, EditorConnection editorConnection) throws IOException {

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

            // Accept requests from connected user while the server is active
            while (isActive) {

                // Reading request from the input stream
                AppRequest request = (AppRequest) inputStream.readObject();

                // Performing specified action depending on type of request received
                if (request.getRequestType() == RequestType.SEND_PEER_CONNECTION_REQUEST) {

                    // On receiving connection request, connect to the sender's audio and editor server

                    SendPeerConnectionRequest connectionRequest = (SendPeerConnectionRequest) request;

                    // Create a peer object corresponding to newly connected client
                    Peer peer = new Peer();
                    peer.setUser(connectionRequest.getUser());
                    peer.setPort(connectionRequest.getPort());
                    peer.setAudioPort(connectionRequest.getAudioPort());
                    peer.setIpAddress(connection.getInetAddress().getCanonicalHostName());
                    peer.setHasWritePermissions(connectionRequest.isHasWritePermissions());

                    // Connect to peer's editor server
                    Socket socket = new Socket(peer.getIpAddress(), peer.getPort());

                    // Store the IO streams for later use
                    peer.setOutputStream(new ObjectOutputStream(socket.getOutputStream()));
                    peer.setInputStream(new ObjectInputStream(socket.getInputStream()));

                    // Connect to peer's audio server
                    Socket audioSocket = new Socket(peer.getIpAddress(), peer.getAudioPort());

                    // Store audio IO streams
                    peer.setAudioOutputStream(new DataOutputStream(audioSocket.getOutputStream()));
                    peer.setAudioInputStream(new DataInputStream(audioSocket.getInputStream()));
                    peer.setMuted(false);

                    // Write current user's info to newly connected peer
                    SendPeerInfoRequest infoRequest = new SendPeerInfoRequest();
                    User user = new User();
                    user.setUserID(UserApi.getInstance().getId());
                    user.setFirstName(UserApi.getInstance().getFirstName());
                    user.setLastName(UserApi.getInstance().getLastName());
                    user.setEmail(UserApi.getInstance().getEmail());
                    infoRequest.setUser(user);
                    peer.getOutputStream().writeObject(infoRequest);
                    peer.getOutputStream().flush();

                    // Store the peer's info in connected clients set
                    editorConnection.getConnectedPeers().put(peer.getUser().getUserID(), peer);

                } else if (request.getRequestType() == RequestType.SEND_PEER_INFO_REQUEST) {

                    // Receive user details of connected peer
                    connectedUser = ((SendPeerInfoRequest) request).getUser();

                    // Update chat tab
                    Platform.runLater(() -> editorConnection.getChatController().updateActiveUsers());

                } else if (request.getRequestType() == RequestType.STREAM_CONTENT_CHANGES_REQUEST) {

                    // Received content changes from the user in control
                    StreamContentChangeRequest contentChangeRequest = (StreamContentChangeRequest) request;
                    Platform.runLater(() -> {
                        String insertedText = contentChangeRequest.getInsertedContent();
                        int insertedStart = contentChangeRequest.getInsertedStart();
                        int removedEnd = contentChangeRequest.getRemovedEnd();
                        int removedLength = contentChangeRequest.getRemovedLength();

                        // Update the code editor depending on the changes received
                        editorConnection.getCodeEditor().removeContent(removedEnd, removedLength);
                        editorConnection.getCodeEditor().insertContent(insertedStart, insertedText);
                    });

                } else if (request.getRequestType() == RequestType.STREAM_CONTENT_SELECTION_REQUEST) {

                    // Received content selection from user in control
                    StreamContentSelectionRequest contentSelectionRequest = (StreamContentSelectionRequest) request;

                    // Update the selection on current user's code editor
                    Platform.runLater(() -> editorConnection.getCodeEditor().selectContent(contentSelectionRequest.getStart(), contentSelectionRequest.getEnd()));

                } else if (request.getRequestType() == RequestType.STREAM_CURSOR_POSITION_REQUEST) {

                    // Received cursor position change request
                    StreamCursorPositionRequest cursorPositionRequest = (StreamCursorPositionRequest) request;

                    // Update the cursor and label of user corresponding to the cursor on home screen
                    Platform.runLater(() -> editorConnection.getCodeEditor().moveCursor(cursorPositionRequest.getUserId(), cursorPositionRequest.getPosition()));

                } else if (request.getRequestType() == RequestType.SEND_MESSAGE_REQUEST) {

                    // Received a message from some user
                    SendMessageRequest messageRequest = (SendMessageRequest) request;

                    Platform.runLater(() -> {

                        // Updating the UI accordingly
                        Chat chat = new Chat();
                        chat.setFirstName(connectedUser.getFirstName());
                        chat.setDate(new Date());
                        chat.setMessage(messageRequest.getContent());
                        chat.setUserID(connectedUser.getUserID());
                        chat.setPrivate(messageRequest.isPrivate());

                        editorConnection.getChatController().addNewMessage(chat);
                    });

                } else if (request.getRequestType() == RequestType.TAKE_CONTROL_REQUEST) {

                    // The connected user is requesting control for editing the codedoc

                    Platform.runLater(() -> {

                        // Showing a message and confirmation dialog to current user
                        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                        String name = connectedUser.getFirstName() + " " + connectedUser.getLastName();
                        confirmationAlert.setContentText(name + " is requesting control!");
                        Optional<ButtonType> pressedButton = confirmationAlert.showAndWait();

                        if (pressedButton.get() == ButtonType.OK) {

                            // If current user agrees to give up the control

                            // Make UI changes for current user
                            editorConnection.setUserInControl(connectedUser.getUserID());

                            // Inform all the connected users about the control switch
                            ControlSwitchRequest switchRequest = new ControlSwitchRequest();
                            switchRequest.setUserId(connectedUser.getUserID());

                            for (Peer peer : editorConnection.getConnectedPeers().values()) {
                                try {
                                    peer.getOutputStream().writeObject(switchRequest);
                                    peer.getOutputStream().flush();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    });

                } else if (request.getRequestType() == RequestType.CONTROL_SWITCH_REQUEST) {

                    // Received a control switch request from the user in control
                    ControlSwitchRequest switchRequest = (ControlSwitchRequest) request;

                    // Make UI changes accordingly
                    editorConnection.setUserInControl(switchRequest.getUserId());

                } else if (request.getRequestType() == RequestType.SYNC_CONTENT_REQUEST) {

                    // Received a request to sync the contents of code editor

                    // Send the updates contents to the connected user
                    UpdateContentRequest updateContentRequest = new UpdateContentRequest();
                    updateContentRequest.setContent(editorConnection.getCodeEditor().getText());

                    Peer peer = editorConnection.getConnectedPeers().get(connectedUser.getUserID());
                    peer.getOutputStream().writeObject(updateContentRequest);
                    peer.getOutputStream().flush();

                } else if (request.getRequestType() == RequestType.UPDATE_CONTENT_REQUEST) {

                    // Received the updated contents of the code editor
                    UpdateContentRequest contentRequest = (UpdateContentRequest) request;

                    // Make UI changes
                    Platform.runLater(() -> editorConnection.getCodeEditor().insertContent(0, contentRequest.getContent()));
                }
            }

        } catch (ClassNotFoundException | IOException e) {
            System.out.println("A user got disconnected!");
        }

        // Connected user just disconnected from current user
        handleDisconnectedUser();

        // Update chat tab
        editorConnection.getChatController().updateActiveUsers();
    }

    /**
     * This function handles all the changes that will take place after a user gets
     * disconnected.
     */
    private void handleDisconnectedUser() {

        Platform.runLater(() -> {
            // Remove the cursor corresponding to disconnected user
            editorConnection.getCodeEditor().removeCursor(connectedUser.getUserID());

            // Remove the user from list of active users
            editorConnection.getConnectedPeers().remove(connectedUser.getUserID());
        });


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
        }
    }

    public void closeConnection() {
        isActive = false;
    }
}