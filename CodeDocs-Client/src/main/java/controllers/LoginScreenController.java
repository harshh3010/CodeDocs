
package controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import services.AuthenticationService;
import services.SceneService;
import utilities.AppScreen;
import utilities.LoginStatus;
import utilities.Status;

import java.io.IOException;
import java.util.Optional;

public class LoginScreenController {
    @FXML
    public TextField emailTF;
    @FXML
    public PasswordField passwordTF;

    private String email;
    private String password;
    Alert a = new Alert(Alert.AlertType.NONE);
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
                a.setAlertType(Alert.AlertType.ERROR);
                a.setContentText("Wrong Credentials");
                a.show();
            } else if(status == LoginStatus.SERVER_SIDE_ERROR) {

                a.setAlertType(Alert.AlertType.ERROR);
                a.setContentText("Try again later");
                a.show();
            } else if(status == LoginStatus.UNVERIFIED_USER) {
                TextInputDialog inputDialog = new TextInputDialog("Enter verification code");
                inputDialog.setHeaderText("Registration successful! Please verify your email address.");

                Optional<String> result = inputDialog.showAndWait();

                result.ifPresent(verificationCode -> {

                    try {
                        Status status1 = AuthenticationService.verifyUser(verificationCode,email);
                        if(status1 == Status.SUCCESS){
                            System.out.println(verificationCode);
                            SceneService.setScene(AppScreen.loginScreen);
                            System.out.println("Account verified");
                        }else{
                            System.out.println(verificationCode);
                            a.setAlertType(Alert.AlertType.ERROR);
                            a.setContentText("Please enter correct verification code");
                            a.show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                });
            }
        } catch (IOException | ClassNotFoundException e) {
            a.setAlertType(Alert.AlertType.ERROR);
            a.setContentText("Cannot login at the moment. Try again later.");
            a.show();
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
