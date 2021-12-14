
package controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import services.AuthenticationService;
import services.SceneService;
import utilities.AppScreen;
import utilities.LoginStatus;

import java.io.IOException;

public class LoginScreenController {
    @FXML
    public TextField emailTF;
    @FXML
    public PasswordField passwordTF;

    private String email;
    private String password;

    private void getData() {
        email = emailTF.getText().trim().toLowerCase();
        password = passwordTF.getText();
    }

    private void loginUser() {
        try {
            LoginStatus status = AuthenticationService.loginUser(email, password);
            if(status == LoginStatus.SUCCESS) {
                SceneService.setScene(AppScreen.mainScreen);
                System.out.println("Login success!");
            } else if(status == LoginStatus.WRONG_CREDENTIALS) {
                // TODO: Display error
                System.out.println("Invalid email or password!");
            } else if(status == LoginStatus.SERVER_SIDE_ERROR) {
                // TODO: Display error
                System.out.println("Try again later!");
            } else if(status == LoginStatus.UNVERIFIED_USER) {
                // TODO: Display dialog
                System.out.println("Please verify your account!");
            }
        } catch (IOException | ClassNotFoundException e) {
            // TODO: Display error
            e.printStackTrace();
        }
    }
    @FXML
    public void onSignInClicked(ActionEvent actionEvent) {
        getData();
        loginUser();
    }

    public void onSignupClicked(ActionEvent actionEvent) {
        try {
            SceneService.setScene(AppScreen.signupScreen);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
