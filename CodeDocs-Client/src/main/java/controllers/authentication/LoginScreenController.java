package controllers.authentication;

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
import java.util.regex.Pattern;

public class LoginScreenController {

    public TextField emailTF;
    public PasswordField passwordTF;

    private String email;
    private String password;
    private final Pattern pattern = Pattern.compile("^(.+)@(.+)$");

    private final Alert alert = new Alert(Alert.AlertType.NONE);

    private void getData() {
        email = emailTF.getText().trim().toLowerCase();
        password = passwordTF.getText();
    }

    private boolean validateData() {
        if (email.isEmpty() || password.isEmpty()) {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setContentText("Enter complete details!");
            alert.show();
            return false;
        } else if (!pattern.matcher(email).matches()) {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setContentText("Enter valid email address!");
            alert.show();
            return false;
        }
        return true;
    }

    private void loginUser() {
        try {
            LoginStatus status = AuthenticationService.loginUser(email, password);
            if (status == LoginStatus.SUCCESS) {
                SceneService.setScene(AppScreen.mainScreen);
                System.out.println("Login success!");
            } else if (status == LoginStatus.WRONG_CREDENTIALS) {
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setContentText("Wrong Credentials");
                alert.show();
            } else if (status == LoginStatus.SERVER_SIDE_ERROR) {

                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setContentText("Cannot login at the moment. Try again later!");
                alert.show();
            } else if (status == LoginStatus.UNVERIFIED_USER) {
                TextInputDialog inputDialog = new TextInputDialog("Enter verification code");
                inputDialog.setHeaderText("Registration successful! Please verify your email address.");

                Optional<String> result = inputDialog.showAndWait();

                result.ifPresent(verificationCode -> {

                    try {
                        Status status1 = AuthenticationService.verifyUser(verificationCode, email);
                        if (status1 == Status.SUCCESS) {
                            SceneService.setScene(AppScreen.loginScreen);
                            alert.setAlertType(Alert.AlertType.INFORMATION);
                            alert.setContentText("Your account is verified. Please login again!");
                        } else {
                            System.out.println(verificationCode);
                            alert.setAlertType(Alert.AlertType.ERROR);
                            alert.setContentText("Please enter correct verification code");
                            alert.show();
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                });
            }
        } catch (IOException | ClassNotFoundException e) {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setContentText("Cannot login at the moment. Try again later.");
            alert.show();
            e.printStackTrace();
        }
    }

    @FXML
    public void onSignInClicked() {
        getData();
        if (!validateData())
            return;
        loginUser();
    }

    public void onSignupClicked() {
        try {
            SceneService.setScene(AppScreen.signupScreen);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
