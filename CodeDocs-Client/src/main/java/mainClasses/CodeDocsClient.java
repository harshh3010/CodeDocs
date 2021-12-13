package mainClasses;

import javafx.application.Application;
import javafx.stage.Stage;
import services.SceneService;
import utilities.AppScreen;

import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Properties;

public class CodeDocsClient extends Application {

    public static Socket socket;
    public static ObjectInputStream inputStream;
    public static ObjectOutputStream outputStream;
    public static Stage appStage;

    public static void main(String[] args) {

        String hostname;
        int port;
        try {

            Properties properties = new Properties();
            FileReader fileReader = new FileReader("CodeDocs-Client/src/main/resources/configurations/server.properties");
            properties.load(fileReader);

            hostname = properties.getProperty("HOSTNAME");
            port = Integer.parseInt(properties.getProperty("PORT"));

        } catch (IOException e) {
            System.out.println("Unable to load server config file!");
            return;
        }

        try {
            socket = new Socket(hostname, port);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());

            inputStream.close();
            outputStream.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        appStage = stage;
        stage.setResizable(false);
        stage.setTitle("CodeDocs");
        SceneService.setScene(AppScreen.splashScreen);
        stage.show();
    }
}
