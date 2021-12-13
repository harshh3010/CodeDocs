package services;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mainClasses.CodeDocsClient;

import java.io.IOException;
import java.util.Objects;

public class SceneService {

    public static void setScene(String appScreen) throws IOException {
        Stage stage = CodeDocsClient.appStage;
        Parent root = FXMLLoader.load(Objects.requireNonNull(SceneService.class.getResource("/fxml/" + appScreen)));
        stage.setScene(new Scene(root));
    }

}
