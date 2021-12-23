package controllers;


import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import mainClasses.CodeDocsClient;
import response.appResponse.GetMeResponse;
import services.SceneService;
import services.StorageService;
import services.UserService;
import utilities.AppScreen;
import utilities.Status;

import java.io.File;
import java.io.IOException;

public class SplashScreenController {

    @FXML
    public void initialize() {
        Stage stage = CodeDocsClient.appStage;
        stage.addEventHandler(WindowEvent.WINDOW_SHOWN, new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent window) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            String token = StorageService.fetchJWT();
                            if (token.isEmpty()) {
                                SceneService.setScene(AppScreen.loginScreen);
                            } else {
                                GetMeResponse response = UserService.loadMyData(token);
                                if (response.getStatus() == Status.SUCCESS) {
                                    SceneService.setScene(AppScreen.mainScreen);

                                } else {
                                    SceneService.setScene(AppScreen.loginScreen);
                                }
                            }

                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}
