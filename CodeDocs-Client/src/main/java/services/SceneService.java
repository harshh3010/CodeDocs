package services;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mainClasses.CodeDocsClient;

import java.io.IOException;
import java.util.Objects;

/**
 * This class provides functions for managing different scenes in UI
 */
public class SceneService {

    /**
     * Function to set a new scene on app stage
     *
     * @param appScreen the screen to be displayed
     */
    public static void setScene(String appScreen) throws IOException {
        Stage stage = CodeDocsClient.appStage;
        Parent root = FXMLLoader.load(Objects.requireNonNull(SceneService.class.getResource("/fxml/" + appScreen)));
        stage.setScene(new Scene(root));
    }

}
