package mainClasses;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import services.SceneService;
import utilities.AppScreen;
import utilities.configurations.FileConfigurations;
import utilities.configurations.ServerConfigurations;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * This class extends the JavaFX Application class, here the connection with the
 * CodeDocs server is established.
 */
public class CodeDocsClient extends Application {

    public static Socket socket; // Socket that connects to CodeDocs server
    public static ObjectInputStream inputStream; // For reading responses from server
    public static ObjectOutputStream outputStream; // For writing requests to the server

    public static Stage appStage; // Main app window that will be displayed on screen

    public static String screenshotDirectory;
    public static String notesDirectory;

    @Override
    public void start(Stage stage) throws Exception {

        Alert alert = new Alert(Alert.AlertType.ERROR);

        int port; // CodeDocs server port
        String hostname; // CodeDocs server hostname

        // Get the server details from configurations file
        try {
            ServerConfigurations serverConfigurations = new ServerConfigurations();
            hostname = serverConfigurations.getHostname();
            port = serverConfigurations.getPort();
        } catch (IOException e) {
            alert.setTitle("An error occurred!");
            alert.setContentText("Unable to load server configurations file!");
            alert.show();
            return;
        }

        // Get the local storage directory paths
        try {
            FileConfigurations fileConfigurations = new FileConfigurations();
            screenshotDirectory = fileConfigurations.getScreenshotDirectory();
            notesDirectory = fileConfigurations.getNotesDirectory();

            // Create directories if they don't exist
            File file = new File(CodeDocsClient.screenshotDirectory);
            if (!file.exists() && !file.mkdir()) {
                throw new IOException();
            }
            file = new File(CodeDocsClient.notesDirectory);
            if (!file.exists() && !file.mkdir()) {
                throw new IOException();
            }

        } catch (IOException e) {
            alert.setTitle("An error occurred!");
            alert.setContentText("Unable to load file configurations file!");
            alert.show();
            return;
        }

        // Establish connection with the server
        try {
            socket = new Socket(hostname, port);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            alert.setTitle("An error occurred!");
            alert.setContentText("Unable to establish connection with the server!");
            alert.show();
            return;
        }

        appStage = stage;
        stage.setResizable(false);
        stage.setTitle("CodeDocs");

        // Display splashscreen on main app window
        SceneService.setScene(AppScreen.splashScreen);
        stage.show();

        // Destroy all other application threads when main thread exits
        Platform.setImplicitExit(true);
        stage.setOnCloseRequest((actionEvent) -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
