package mainClasses;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
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

                            // TODO: Check
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
                            // TODO: Implement multiple cursors
                            String userId = cursorPositionRequest.getUserId();
                            int pos = cursorPositionRequest.getPosition();
                            int mxPos = EditorConnection.textArea.getLength();

                            if (pos <= mxPos) {
                                if (EditorConnection.cursors.get(userId) == null) {
                                    EditorConnection.cursors.put(userId, new CaretNode(userId, EditorConnection.textArea, 0));
                                    EditorConnection.cursors.get(userId).setVisible(true);
                                    EditorConnection.textArea.addCaret(EditorConnection.cursors.get(userId));

                                    EditorConnection.cursorLabels.put(userId, new Label());
                                    EditorConnection.cursorLabels.get(userId).setText(EditorConnection.connectedPeers.get(userId).getUser().getFirstName());
                                    EditorConnection.cursorLabels.get(userId).setVisible(true);
                                    EditorConnection.cursorLabels.get(userId).setStyle("-fx-text-fill: white;");
                                    EditorConnection.pane.getChildren().add(EditorConnection.cursorLabels.get(userId));
                                }
                                EditorConnection.cursors.get(userId).moveTo(pos);
                                double x = EditorConnection.cursors.get(userId).getCaretBounds().get().getCenterX();
                                double y = EditorConnection.cursors.get(userId).getCaretBounds().get().getCenterY();
                                double x1 = EditorConnection.pane.localToScreen(EditorConnection.pane.getBoundsInLocal()).getMinX();
                                double y1 = EditorConnection.pane.localToScreen(EditorConnection.pane.getBoundsInLocal()).getMinY();

//                                System.out.println(x + ":" + y);
//                                System.out.println(x1 + ":" + y1);

                                EditorConnection.cursorLabels.get(userId).setLayoutX(x - x1);
                                EditorConnection.cursorLabels.get(userId).setLayoutY(y - y1);

                            }
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