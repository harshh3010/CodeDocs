package mainClasses;

import javafx.application.Platform;
import models.Peer;
import requests.appRequests.AppRequest;
import requests.peerRequests.SendPeerConnectionRequest;
import requests.peerRequests.StreamContentChangeRequest;
import requests.peerRequests.StreamContentSelectionRequest;
import requests.peerRequests.StreamCursorPositionRequest;
import utilities.RequestType;

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

                    peer.setSocket(connection);
                    peer.setOutputStream(new ObjectOutputStream(socket.getOutputStream()));
                    peer.setInputStream(new ObjectInputStream(socket.getInputStream()));

                    Socket audioSocket = new Socket(peer.getIpAddress(), peer.getAudioPort());

                    peer.setAudioOutputStream(new DataOutputStream(audioSocket.getOutputStream()));
                    peer.setAudioInputStream(new DataInputStream(audioSocket.getInputStream()));

                    editorConnection.getConnectedPeers().put(peer.getUser().getUserID(), peer);

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
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // TODO: Free resources corresponding to disconnected user

        try {
            inputStream.close();
            outputStream.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}