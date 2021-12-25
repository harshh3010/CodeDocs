package controllers;

import com.jfoenix.controls.JFXDrawer;
import controllers.activeUsers.ActiveUserTabController;
import controllers.chat.ChatTabController;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import mainClasses.editor.EditorConnection;
import models.CodeDoc;
import models.Peer;
import requests.peerRequests.SyncContentRequest;
import response.editorResponse.CompileCodeDocResponse;
import response.editorResponse.LoadEditorResponse;
import response.editorResponse.RunCodeDocResponse;
import response.editorResponse.SaveCodeDocResponse;
import services.EditorService;
import services.ScreenshotService;
import utilities.CodeEditor;
import utilities.Status;
import utilities.UserApi;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Controller class for the main code editing screen
 */
public class EditorController {

    public BorderPane borderPane; // Root pane
    public TextArea inputTextArea; // To provide input
    public TextArea outputTextArea; // To receive output
    public JFXDrawer activeUserDrawer; // Drawer to display active users in same codedoc
    public JFXDrawer chatDrawer; // Drawer for private and group chat
    public Button muteButton; // To mute ourselves
    public ImageView muteImageView;

    private CodeDoc codeDoc; // Current CodeDoc
    private CodeEditor codeEditor; // The main code editing region (Text area)
    private EditorConnection editorConnection; // Reference to current editor connection

    private ActiveUserTabController activeUserTabController; // Controller to manage active users drawer
    private ChatTabController chatTabController; // Controller to manage chat drawer

    private final Alert alert = new Alert(Alert.AlertType.ERROR);

    /**
     * This function initializes the UI
     *
     * @param codeDoc is the CodeDoc object currently being edited
     */
    public void setCodeDoc(CodeDoc codeDoc) throws IOException, ClassNotFoundException {
        this.codeDoc = codeDoc;

        // Start a new editor connection for specified CodeDoc
        editorConnection = new EditorConnection(codeDoc);

        // Set up the drawers
        setupActiveUsersDrawer();
        setupChatDrawer();

        // Initial state of the code editor
        String initialContent = "";
        boolean isEditable = false;

        if (editorConnection.getUserInControl() == null
                || editorConnection.getUserInControl().equals(UserApi.getInstance().getId())) {

            // If current user is the user in control of editor or there is no user in control
            // then the initial content needs to be loaded from the server

            // Send a request to the server
            LoadEditorResponse response = EditorService.loadEditorContent(codeDoc.getCodeDocId(), codeDoc.getLanguageType());

            if (response.getStatus() == Status.SUCCESS) {

                // Codedoc will be editable if the current user has control
                isEditable = editorConnection.getUserInControl() != null;
                initialContent = response.getContent();
            } else {
                throw new IOException();
            }

        } else {

            // If there is some user in control, then fetch initial content from that user

            // Sending synchronization request to user in control
            SyncContentRequest request = new SyncContentRequest();
            Peer peer = editorConnection.getConnectedPeers().get(editorConnection.getUserInControl());
            peer.getOutputStream().writeObject(request);
            peer.getOutputStream().flush();
        }

        // Setting up the code editor
        codeEditor = new CodeEditor(codeDoc.getLanguageType(), editorConnection, initialContent, isEditable);
        codeEditor.applyContentStyle(Objects.requireNonNull(getClass().getResource("/css/test.css")).toExternalForm(), "#690026");

        // Sending the reference of code editor to editor connection for updating changes by other
        // users
        editorConnection.setCodeEditor(codeEditor);

        // Displaying the code editor
        borderPane.setCenter(codeEditor);
    }

    /**
     * Function to set up the active users drawer
     */
    private void setupActiveUsersDrawer() throws IOException {

        // Positioning the drawer when in closed state
        activeUserDrawer.setOnDrawerOpening(event ->
        {
            AnchorPane.setRightAnchor(activeUserDrawer, 0.0);
            AnchorPane.setLeftAnchor(activeUserDrawer, 0.0);
            AnchorPane.setTopAnchor(activeUserDrawer, 0.0);
            AnchorPane.setBottomAnchor(activeUserDrawer, 0.0);
        });

        // Positioning the drawer when in opened state
        activeUserDrawer.setOnDrawerClosed(event ->
        {
            AnchorPane.clearConstraints(activeUserDrawer);
            AnchorPane.setLeftAnchor(activeUserDrawer, -400.0);
            AnchorPane.setTopAnchor(activeUserDrawer, 0.0);
            AnchorPane.setBottomAnchor(activeUserDrawer, 0.0);
        });

        // Loading the contents of the drawer from fxml resource
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/active_user_tab.fxml"));
        VBox activeUserBox = loader.load();
        activeUserTabController = loader.getController();
        activeUserTabController.setActiveUserTab(editorConnection);
        activeUserDrawer.setSidePane(activeUserBox);
        activeUserDrawer.close();
    }

    /**
     * Function to set up the chat drawer
     */
    private void setupChatDrawer() throws IOException {

        // Positioning the drawer when in closed state
        chatDrawer.setOnDrawerOpening(event ->
        {
            AnchorPane.setRightAnchor(chatDrawer, 0.0);
            AnchorPane.setLeftAnchor(chatDrawer, 0.0);
            AnchorPane.setTopAnchor(chatDrawer, 0.0);
            AnchorPane.setBottomAnchor(chatDrawer, 0.0);
        });

        // Positioning the drawer when in opened state
        chatDrawer.setOnDrawerClosed(event ->
        {
            AnchorPane.clearConstraints(chatDrawer);
            AnchorPane.setRightAnchor(chatDrawer, -400.0);
            AnchorPane.setTopAnchor(chatDrawer, 0.0);
            AnchorPane.setBottomAnchor(chatDrawer, 0.0);
        });

        // Loading the contents of the drawer from fxml resource
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/chat_tab.fxml"));
        VBox chatBox = loader.load();
        chatTabController = loader.getController();
        chatTabController.setEditorConnection(editorConnection);
        chatDrawer.setSidePane(chatBox);
        chatDrawer.setDirection(JFXDrawer.DrawerDirection.RIGHT);
        chatDrawer.close();

        // Passing the reference of chat controller to editor connection for updating chats
        editorConnection.setChatController(chatTabController);
    }

    /**
     * Function to save the CodeDoc content on the server
     */
    public void saveContent() {

        // Setting the file content from code editor
        codeDoc.setFileContent(codeEditor.getText());

        try {

            // Sending a save request to the server
            SaveCodeDocResponse response = EditorService.saveCodeDoc(codeDoc);
            if (response.getStatus() == Status.SUCCESS) {
                alert.setAlertType(Alert.AlertType.INFORMATION);
                codeEditor.setDirty(false);
                alert.setContentText("Saved successfully");
                alert.show();
            } else {
                throw new IOException();
            }
        } catch (IOException | ClassNotFoundException e) {
            alert.setContentText("Cannot save at the moment! Please try later.");
            alert.show();
        }
    }

    /**
     * Function to exit the editor
     */
    public void exitEditor() {
        try {

            // Close the editor connection
            editorConnection.closeConnection();

            // Close the editor window
            Stage stage = (Stage) borderPane.getScene().getWindow();
            stage.close();

        } catch (IOException e) {
            alert.setContentText("Some problem occurred! Please restart the application.");
            alert.show();
        }
    }

    /**
     * Function to compile the codedoc
     */
    private void compileCodeDoc() {
        try {

            // Send compile request to server and fetch response
            CompileCodeDocResponse response = EditorService.compileCodeDoc(codeDoc.getCodeDocId(), codeDoc.getLanguageType());

            if (response.getStatus() == Status.SUCCESS) {

                // Display error or output of compilation
                if (response.getError().isEmpty()) {
                    outputTextArea.setStyle("-fx-text-fill: white;");
                    outputTextArea.setText("Your code compiled successfully!");
                } else {
                    outputTextArea.setStyle("-fx-text-fill: red;");
                    outputTextArea.setText("ERROR: " + response.getError());
                }
            } else {
                throw new IOException();
            }
        } catch (IOException | ClassNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Cannot compile codedoc at the moment!");
            alert.show();
        }
    }

    /**
     * Function to run the codedoc code
     */
    private void runCodeDoc() {
        try {

            // Fetch the input and send run request to the server
            String input = inputTextArea.getText();
            RunCodeDocResponse response = EditorService.runCodeDoc(codeDoc.getCodeDocId(), codeDoc.getLanguageType(), input);

            if (response.getStatus() == Status.SUCCESS) {

                // Display the output of run
                if (response.getError().isEmpty()) {
                    outputTextArea.setStyle("-fx-text-fill: white;");
                    outputTextArea.setText(response.getOutput());
                } else {
                    outputTextArea.setStyle("-fx-text-fill: red;");
                    outputTextArea.setText("ERROR: " + response.getError());
                }
            } else {
                throw new IOException();
            }
        } catch (IOException | ClassNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Cannot run codedoc at the moment!");
            alert.show();
        }
    }

    /**
     * Action to be performed when compile button is clicked
     */
    public void onCompileClicked() {

        // Display warning if there are unsaved changes
        if (codeEditor.isDirty()) {
            alert.setAlertType(Alert.AlertType.WARNING);
            alert.setContentText("You have unsaved changes, please save the CodeDoc first!");
            alert.show();
        } else {
            compileCodeDoc();
        }
    }

    /**
     * Action to be performed when run button is clicked
     */
    public void onRunClicked() {

        // Display warning if there are unsaved changes
        if (codeEditor.isDirty()) {
            alert.setAlertType(Alert.AlertType.WARNING);
            alert.setContentText("You have unsaved changes, please save the CodeDoc first!");
            alert.show();
        } else {
            runCodeDoc();
        }
    }

    /**
     * Action to be performed when active users button is clicked
     */
    public void onActiveUserClicked() {
        if (activeUserDrawer.isOpened()) {
            activeUserDrawer.close();
        } else {
            activeUserTabController.setActiveUsers();
            activeUserDrawer.open();
        }
    }

    /**
     * Action to be performed when chat button is clicked
     */
    public void onChatClicked() {

        if (chatDrawer.isOpened()) {
            chatDrawer.close();
        } else {
            chatDrawer.open();
        }
    }

    /**
     * Action to be performed when mute button is clicked
     */
    public void onMuteClicked() {
        if (editorConnection.isMute()) {
            editorConnection.setMute(false);
            Image image = new Image(getClass().getResource("/images/unmute.png").toExternalForm());
            muteImageView.setImage(image);
            muteButton.setGraphic(muteImageView);
        } else {
            editorConnection.setMute(true);
            Image image = new Image(getClass().getResource("/images/mute.png").toExternalForm());
            muteImageView.setImage(image);
            muteButton.setGraphic(muteImageView);
        }
    }

    /**
     * Action to be performed when screenshot button is clicked
     */
    public void onScreenshotClicked() {
        Status status = ScreenshotService.takeScreenshot(codeEditor);
        Alert alert;
        if(status == null){
            return;
        }
        if (status == Status.SUCCESS) {
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Notes taken");
        } else {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Cannot take notes at the moment. Try again later");
        }
        alert.show();
    }

    /**
     * Action to be performed when font size from preferences menu clicked
     */
    public void onFontSizeClicked() {
        final double[] fontSize = new double[1];

        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.getEditor().setPromptText("font size");
        inputDialog.setHeaderText("Set font size");

        // force the field to be numeric only
        final Pattern pattern = Pattern.compile("^(?:[1-9]\\d*|0)?(?:\\.\\d+)?$");
        inputDialog.getEditor().setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), 0.0, change -> {
            final Matcher matcher = pattern.matcher(change.getControlNewText());
            return (matcher.matches() || matcher.hitEnd()) ? change : null;
        }));

        Optional<String> result = inputDialog.showAndWait();

        result.ifPresent(size -> {
            if(size!=""){
                fontSize[0] = Double.parseDouble(size);
                codeEditor.setStyle("-fx-font-size: " + fontSize[0] + "px;");
            }
        });


    }
}
