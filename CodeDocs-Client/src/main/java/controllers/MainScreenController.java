package controllers;

import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import models.CodeDoc;
import services.UserService;
import utilities.LanguageType;
import utilities.UserApi;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class MainScreenController implements Initializable {
    @FXML
    public Text firstNameLabel;
    @FXML
    public Text emailLabel;
    @FXML
    public JFXListView<CodeDoc> codeDocsListView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        firstNameLabel.setText(UserApi.getInstance().getFirstName());
        emailLabel.setText(UserApi.getInstance().getEmail());

        // TODO: Load codedocs from server
        List<CodeDoc> codeDocs = new ArrayList<>();
        codeDocsListView.setItems(FXCollections.observableArrayList(codeDocs));
        codeDocsListView.setCellFactory(new CodeDocCardFactory());
    }
    public void onClickCreate(ActionEvent actionEvent){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/create_codedoc.fxml"));
            Parent root1 = (Parent)fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Create CodeDoc");
            stage.setScene(new Scene(root1));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void onClickLogout(ActionEvent actionEvent) {
        try {
            UserService.logoutUser();
        } catch (IOException e) {
            System.out.println("Unable to logout!");
            e.printStackTrace();
        }
    }
}
