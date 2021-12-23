package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import mainClasses.CodeDocsClient;
import response.appResponse.GetMeResponse;
import services.SceneService;
import services.StorageService;
import services.UserService;
import utilities.AppScreen;
import utilities.Status;

import java.io.IOException;

/**
 * To display a splashscreen to the user while user details are fetched from the server
 */
public class SplashScreenController {

    @FXML
    public void initialize() {
        Stage stage = CodeDocsClient.appStage;
        stage.addEventHandler(WindowEvent.WINDOW_SHOWN, window -> Platform.runLater(() -> {

            try {
                // Fetch the login token from local storage
                String token = StorageService.fetchJWT();

                if (token.isEmpty()) {

                    // Navigate to login screen if token is not found
                    SceneService.setScene(AppScreen.loginScreen);
                } else {

                    // Fetch user details corresponding to the token
                    GetMeResponse response = UserService.loadMyData(token);

                    if (response.getStatus() == Status.SUCCESS) {

                        // Navigate to main screen on success
                        SceneService.setScene(AppScreen.mainScreen);
                    } else {

                        // Navigate to login screen if token was invalid
                        SceneService.setScene(AppScreen.loginScreen);
                    }
                }

            } catch (IOException | ClassNotFoundException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("An error occurred!");
                alert.setContentText("Unable to connect to the server. Please try again later!");
                alert.showAndWait();
                System.exit(1);
            }
        }));
    }
}
