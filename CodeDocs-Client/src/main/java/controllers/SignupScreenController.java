package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import models.User;
import services.AuthenticationService;
import services.SceneService;
import utilities.AppScreen;
import utilities.SignupStatus;
import utilities.Status;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;

public class SignupScreenController {
    @FXML
    public TextField emailTF;
    @FXML
    public PasswordField passwordTF;
    @FXML
    public TextField fullNameTF;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    Alert a = new Alert(Alert.AlertType.NONE);
    private final Pattern pattern = Pattern.compile("^(.+)@(.+)$");

    private void getData() {

        email = emailTF.getText().trim().toLowerCase();
        password = passwordTF.getText();
        String[] names = fullNameTF.getText().trim().split("\\s+");
        firstName = names[0];
        lastName = "";
        for (int i = 1; i < names.length; i++) {
            lastName += names[i];
            if (i != names.length - 1) {
                lastName += " ";
            }
        }
    }

    private boolean validateData() {
        if (!(2 <= firstName.length() && firstName.length() <= 20)) {
            a.setAlertType(Alert.AlertType.WARNING);
            a.setContentText("Enter your first name");
            a.show();
            return false;
        } else if (!(8 <= password.length() && password.length() <= 40)) {
            a.setAlertType(Alert.AlertType.WARNING);
            a.setContentText("Enter a strong password");
            a.show();
            return false;
        } else if (!(2 <= lastName.length() && lastName.length() <= 20)) {
            a.setAlertType(Alert.AlertType.WARNING);
            a.setContentText("Enter your last name");
            a.show();
            return false;
        } else if (!pattern.matcher(email).matches()) {
            a.setAlertType(Alert.AlertType.WARNING);
            a.setContentText("Enter valid email");
            a.show();
            return false;
        }
        return true;
    }

    private void signupUser() {
        try {
            SignupStatus signupStatus = AuthenticationService.signupUser(new User(
                    email,
                    password,
                    firstName,
                    lastName
            ));
            if (signupStatus == SignupStatus.SUCCESS) {

                System.out.println("Signup successful!");

                TextInputDialog inputDialog = new TextInputDialog("Enter verification code");
                inputDialog.setHeaderText("Registration successful! Please verify your email address.");

                Optional<String> result = inputDialog.showAndWait();

                result.ifPresent(verificationCode -> {
                    // TODO: Send verification request
                    try {
                        Status status = AuthenticationService.verifyUser(verificationCode,email);
                        if(status == Status.SUCCESS){
                            SceneService.setScene(AppScreen.loginScreen);
                            System.out.println("Account verified");
                        }else{
                            a.setAlertType(Alert.AlertType.ERROR);
                            a.setContentText("Please enter correct verification code");
                            a.show();
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    System.out.println(verificationCode);
                });


            } else {
                a.setAlertType(Alert.AlertType.ERROR);
                a.setContentText("Cannot create at the moment. Try again later");
                a.show();
                System.out.println("Signup failed!");
            }
        } catch (IOException | ClassNotFoundException e) {
            a.setAlertType(Alert.AlertType.ERROR);
            a.setContentText("Cannot create at the moment. Try again later");
            a.show();
            e.printStackTrace();
        }
    }
    @FXML
    public void onSignupClicked(ActionEvent actionEvent) {
        getData();
        System.out.println(email);
        if (!validateData()) return;
        signupUser();
    }

    public void onLoginClicked(ActionEvent actionEvent) {
        try {
            SceneService.setScene(AppScreen.loginScreen);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}