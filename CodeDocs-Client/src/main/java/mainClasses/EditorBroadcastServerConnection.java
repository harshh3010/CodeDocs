package mainClasses;

import javafx.application.Platform;
import javafx.scene.layout.StackPane;
import models.Peer;
import org.fxmisc.richtext.Caret;
import org.fxmisc.richtext.CaretNode;
import requests.appRequests.AppRequest;
import requests.peerRequests.SendPeerConnectionRequest;
import requests.peerRequests.StreamContentChangeRequest;
import requests.peerRequests.StreamContentSelectionRequest;
import requests.peerRequests.StreamCursorPositionRequest;
import utilities.RequestType;

import java.awt.*;
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
                    peer.setHasWritePermissions(connectionRequest.isHasWritePermissions());

                    Socket socket = new Socket(peer.getIpAddress(), peer.getPort());

                    peer.setSocket(connection);
                    peer.setOutputStream(new ObjectOutputStream(socket.getOutputStream()));
                    peer.setInputStream(new ObjectInputStream(socket.getInputStream()));

                    EditorConnection.connectedPeers.put(peer.getUser().getUserID(), peer);
                } else if (request.getRequestType() == RequestType.STREAM_CONTENT_CHANGES_REQUEST) {
                    StreamContentChangeRequest contentChangeRequest = (StreamContentChangeRequest) request;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            String insertedText = contentChangeRequest.getInsertedContent();
                            String removedText = contentChangeRequest.getRemovedContent();

                            int insertedStart = contentChangeRequest.getInsertedStart();
                            int removedEnd = contentChangeRequest.getRemovedEnd();
                            int removedStart = removedEnd - removedText.length();

                            EditorConnection.textArea.replaceText(removedStart, removedEnd, "");
                            EditorConnection.textArea.insertText(insertedStart, insertedText);
                        }
                    });
                } else if (request.getRequestType() == RequestType.STREAM_CONTENT_SELECTION_REQUEST) {
                    StreamContentSelectionRequest contentSelectionRequest = (StreamContentSelectionRequest) request;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            EditorConnection.textArea.selectRange(contentSelectionRequest.getStart(), contentSelectionRequest.getEnd());
                        }
                    });
                } else if (request.getRequestType() == RequestType.STREAM_CURSOR_POSITION_REQUEST) {
                    StreamCursorPositionRequest cursorPositionRequest = (StreamCursorPositionRequest) request;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            EditorConnection.textArea.setShowCaret(Caret.CaretVisibility.ON);
                            EditorConnection.textArea.moveTo(cursorPositionRequest.getPosition());
                        }
                    });
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