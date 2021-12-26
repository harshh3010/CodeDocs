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
            while (isActive) {
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
                    peer.setMuted(false);

                    editorConnection.getConnectedPeers().put(peer.getUser().getUserID(), peer);

                } else if (request.getRequestType() == RequestType.SEND_PEER_INFO_REQUEST) {
                    connectedUser = ((SendPeerInfoRequest) request).getUser();
                    Platform.runLater(() -> editorConnection.getChatController().updateActiveUsers());
                } else if (request.getRequestType() == RequestType.STREAM_CONTENT_CHANGES_REQUEST) {
                    StreamContentChangeRequest contentChangeRequest = (StreamContentChangeRequest) request;
                    Platform.runLater(() -> {
                        String insertedText = contentChangeRequest.getInsertedContent();
                        String removedText = contentChangeRequest.getRemovedContent();

                        int insertedStart = contentChangeRequest.getInsertedStart();
                        int removedEnd = contentChangeRequest.getRemovedEnd();
                        int removedStart = removedEnd - removedText.length();

                        // TODO: Resolve issue on remove content
                        editorConnection.getCodeEditor().removeContent(removedStart, removedEnd);
                        editorConnection.getCodeEditor().insertContent(insertedStart, insertedText);
                    });
                } else if (request.getRequestType() == RequestType.STREAM_CONTENT_SELECTION_REQUEST) {
                    StreamContentSelectionRequest contentSelectionRequest = (StreamContentSelectionRequest) request;
                    Platform.runLater(() -> editorConnection.getCodeEditor().selectContent(contentSelectionRequest.getStart(), contentSelectionRequest.getEnd()));
                } else if (request.getRequestType() == RequestType.STREAM_CURSOR_POSITION_REQUEST) {
                    StreamCursorPositionRequest cursorPositionRequest = (StreamCursorPositionRequest) request;
                    Platform.runLater(() -> editorConnection.getCodeEditor().moveCursor(cursorPositionRequest.getUserId(), cursorPositionRequest.getPosition()));
                } else if (request.getRequestType() == RequestType.SEND_MESSAGE_REQUEST) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            SendMessageRequest messageRequest = (SendMessageRequest) request;

                            Chat chat = new Chat();
                            chat.setFirstName(connectedUser.getFirstName());
                            chat.setDate(new Date());
                            chat.setMessage(messageRequest.getContent());
                            chat.setUserID(connectedUser.getUserID());
                            chat.setPrivate(messageRequest.isPrivate());

                            editorConnection.getChatController().addNewMessage(chat);
                        }
                    });

                } else if (request.getRequestType() == RequestType.TAKE_CONTROL_REQUEST) {

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                            String name = connectedUser.getFirstName() + " " + connectedUser.getLastName();
                            confirmationAlert.setContentText(name + " is requesting control!");
                            Optional<ButtonType> pressedButton = confirmationAlert.showAndWait();

                            if (pressedButton.get() == ButtonType.OK) {

                                editorConnection.setUserInControl(connectedUser.getUserID());

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
                        }
                    });

                } else if (request.getRequestType() == RequestType.CONTROL_SWITCH_REQUEST) {
                    ControlSwitchRequest switchRequest = (ControlSwitchRequest) request;
                    editorConnection.setUserInControl(switchRequest.getUserId());
                } else if (request.getRequestType() == RequestType.SYNC_CONTENT_REQUEST) {
                    SyncContentRequest syncContentRequest = (SyncContentRequest) request;

                    UpdateContentRequest updateContentRequest = new UpdateContentRequest();
                    updateContentRequest.setContent(editorConnection.getCodeEditor().getText());
                    Peer peer = editorConnection.getConnectedPeers().get(connectedUser.getUserID());
                    peer.getOutputStream().writeObject(updateContentRequest);
                    peer.getOutputStream().flush();
                } else if (request.getRequestType() == RequestType.UPDATE_CONTENT_REQUEST) {
                    UpdateContentRequest contentRequest = (UpdateContentRequest) request;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            editorConnection.getCodeEditor().insertContent(0, contentRequest.getContent());
                        }
                    });
                }
            }

        } catch (ClassNotFoundException | IOException e) {
            System.out.println("A user got disconnected!");
        }

        handleDisconnectedUser();
        editorConnection.getChatController().updateActiveUsers();
    }

    /**
     * This function handles all the changes that will take place after a user gets
     * disconnected.
     */
    private void handleDisconnectedUser() {

       Platform.runLater(new Runnable() {
           @Override
           public void run() {
               // Remove the cursor corresponding to disconnected user
               editorConnection.getCodeEditor().removeCursor(connectedUser.getUserID());
           }
       });

        // Remove the user from list of active users
        editorConnection.getConnectedPeers().remove(connectedUser.getUserID());

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