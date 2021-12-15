package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.text.Text;
import services.UserService;
import utilities.UserApi;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainScreenController implements Initializable {
    public Text firstNameLabel;
    public Text emailLabel;
    private final Alert errorAlert = new Alert(Alert.AlertType.ERROR);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        firstNameLabel.setText(UserApi.getInstance().getFirstName());
        emailLabel.setText(UserApi.getInstance().getEmail());
    }

    public void onClickLogout(ActionEvent actionEvent) {
        try {
            UserService.logoutUser();
        } catch (IOException e) {
            errorAlert.setContentText("An error occurred! Please try again later.");
            errorAlert.show();
        }
    }
}
