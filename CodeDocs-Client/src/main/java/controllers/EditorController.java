package controllers;

import com.jfoenix.controls.JFXDrawer;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import mainClasses.EditorConnection;
import models.CodeDoc;
import response.editorResponse.CompileCodeDocResponse;
import response.editorResponse.LoadEditorResponse;
import response.editorResponse.RunCodeDocResponse;
import response.editorResponse.SaveCodeDocResponse;
import services.EditorService;
import utilities.CodeEditor;
import utilities.Status;
import utilities.UserApi;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EditorController implements Initializable {

    public BorderPane borderPane;
    public TextArea inputTextArea;
    public TextArea outputTextArea;
    public JFXDrawer activeUserDrawer;
    public JFXDrawer chatDrawer;
    private CodeDoc codeDoc;
    private CodeEditor codeEditor;
    private EditorConnection editorConnection;
    private ActiveUserTabController activeUserTabController;
    private ChatTabController chatTabController;
    private VBox activeUserBox, chatBox;
    Alert alert = new Alert(Alert.AlertType.ERROR);

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        activeUserDrawer.setOnDrawerOpening(event ->
        {
            AnchorPane.setRightAnchor(activeUserDrawer, 0.0);
            AnchorPane.setLeftAnchor(activeUserDrawer, 0.0);
            AnchorPane.setTopAnchor(activeUserDrawer, 0.0);
            AnchorPane.setBottomAnchor(activeUserDrawer, 0.0);
        });

        activeUserDrawer.setOnDrawerClosed(event ->
        {
            AnchorPane.clearConstraints(activeUserDrawer);
            AnchorPane.setLeftAnchor(activeUserDrawer, -400.0);
            AnchorPane.setTopAnchor(activeUserDrawer, 0.0);
            AnchorPane.setBottomAnchor(activeUserDrawer, 0.0);
        });
        chatDrawer.setOnDrawerOpening(event ->
        {
            AnchorPane.setRightAnchor(chatDrawer, 0.0);
            AnchorPane.setLeftAnchor(chatDrawer, 0.0);
            AnchorPane.setTopAnchor(chatDrawer, 0.0);
            AnchorPane.setBottomAnchor(chatDrawer, 0.0);
        });

        chatDrawer.setOnDrawerClosed(event ->
        {
            AnchorPane.clearConstraints(chatDrawer);
            AnchorPane.setRightAnchor(chatDrawer, -400.0);
            AnchorPane.setTopAnchor(chatDrawer, 0.0);
            AnchorPane.setBottomAnchor(chatDrawer, 0.0);
        });


    }

    public void setCodeDoc(CodeDoc codeDoc) throws IOException {
        this.codeDoc = codeDoc;

        try {
            editorConnection = new EditorConnection(codeDoc);

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/active_user_tab.fxml"));
                activeUserBox = loader.load();
                activeUserTabController = loader.getController();
                activeUserTabController.setActiveUserTab(editorConnection);
                activeUserDrawer.setSidePane(activeUserBox);

                FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/fxml/chat_tab.fxml"));
                chatBox = loader2.load();
                chatTabController = loader2.getController();
                chatTabController.setEditorConnection(editorConnection);
                chatDrawer.setSidePane(chatBox);
                chatDrawer.setDirection(JFXDrawer.DrawerDirection.RIGHT);
                activeUserDrawer.close();
                chatDrawer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // TODO: Fetch content from user in control
            LoadEditorResponse response = EditorService.loadEditorContent(codeDoc.getCodeDocId(), codeDoc.getLanguageType());
            if (response.getStatus() == Status.SUCCESS) {

                boolean isEditable = editorConnection.getUserInControl().equals(UserApi.getInstance().getId());
                codeEditor = new CodeEditor(codeDoc.getLanguageType(), editorConnection, response.getContent(), isEditable);
                codeEditor.applyContentStyle(getClass().getResource("/css/test.css").toExternalForm(), "#690026");
                editorConnection.setCodeEditor(codeEditor);

                borderPane.setCenter(codeEditor);

            } else {
                alert.setContentText("Cannot load at the moment");
                alert.show();
                Stage stage = (Stage) borderPane.getScene().getWindow();
                stage.close();
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            alert.setContentText("Cannot load at the moment");
            alert.show();
            Stage stage = (Stage) borderPane.getScene().getWindow();
            stage.close();
        }

    }

    public void saveContent() {

        codeDoc.setFileContent(codeEditor.getText());

        try {
            SaveCodeDocResponse response = EditorService.saveCodeDoc(codeDoc);
            if (response.getStatus() == Status.SUCCESS) {
                alert.setAlertType(Alert.AlertType.INFORMATION);
                alert.setContentText("Saved successfully");
            } else {
                alert.setContentText("Cannot save at the moment");
            }
            alert.show();
        } catch (IOException | ClassNotFoundException e) {
            alert.setContentText("Cannot load at the moment");
            alert.show();
            e.printStackTrace();
        }
    }

    public void exitEditor() {
        try {
            editorConnection.closeConnection();
            Stage stage = (Stage) borderPane.getScene().getWindow();
            stage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void compileCodeDoc() {
        try {
            saveContent();
            CompileCodeDocResponse response = EditorService.compileCodeDoc(codeDoc.getCodeDocId(), codeDoc.getLanguageType());
            if (response.getStatus() == Status.SUCCESS) {
                if (response.getError().isEmpty()) {
                    outputTextArea.setStyle("-fx-text-fill: black;");
                    outputTextArea.setText("Your code compiled successfully!");
                } else {
                    outputTextArea.setStyle("-fx-text-fill: red;");
                    outputTextArea.setText("ERROR: " + response.getError());
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Cannot compile codedoc at the moment!");
                alert.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void runCodeDoc() {
        try {
            saveContent();
            String input = inputTextArea.getText();
            RunCodeDocResponse response = EditorService.runCodeDoc(codeDoc.getCodeDocId(), codeDoc.getLanguageType(), input);
            if (response.getStatus() == Status.SUCCESS) {
                if (response.getError().isEmpty()) {
                    outputTextArea.setStyle("-fx-text-fill: black;");
                    outputTextArea.setText(response.getOutput());
                } else {
                    outputTextArea.setStyle("-fx-text-fill: red;");
                    outputTextArea.setText("ERROR: " + response.getError());

                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Cannot run codedoc at the moment!");
                alert.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onCompileClicked(ActionEvent actionEvent) {
        compileCodeDoc();
    }

    public void onRunClicked(ActionEvent actionEvent) {
        runCodeDoc();
    }

    public void onActiveUserClicked(ActionEvent actionEvent) {

        if (activeUserDrawer.isOpened()) {
            activeUserDrawer.close();
        } else {
            activeUserTabController.setActiveUsers();
            activeUserDrawer.open();
        }
    }

    public void onChatClicked(ActionEvent actionEvent) {

        if (chatDrawer.isOpened()) {
            chatDrawer.close();
        } else {
            //TODO : already sent msg is to be added in th view ... ig u need to maintain a list there .. usmein iss naye msg ko add krdena
           chatTabController.setChatDrawer();
            chatDrawer.open();
        }
    }
}
