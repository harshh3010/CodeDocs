package mainClasses;

import javafx.application.Application;
import javafx.stage.Stage;
import models.CodeDoc;
import requests.appRequests.CreateCodeDocRequest;
import requests.appRequests.FetchInviteRequest;
import requests.appRequests.InviteCollaboratorRequest;
import requests.appRequests.RunCodeDocRequest;
import response.appResponse.CreateCodeDocResponse;
import response.appResponse.FetchInviteResponse;
import response.appResponse.InviteCollaboratorResponse;
import response.appResponse.RunCodeDocResponse;
import services.SceneService;
import utilities.AppScreen;
import utilities.LanguageType;

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
            /**InviteCollaboratorRequest request = new InviteCollaboratorRequest();
            request.setCodeDocID("8556a73d-c261-4519-b9af-265a8b9c7327");
            request.setReceiverID("954cb74b-2f4b-4431-8d14-42d86e639b73");
            request.setSenderID("dc6d9b08-b53d-4199-88c8-5b64c40290c0");
            request.setWritePermissions(1);
            outputStream.writeObject(request);
            outputStream.flush();

            InviteCollaboratorResponse response = (InviteCollaboratorResponse) inputStream.readObject();
            System.out.println(response.getStatus());
            FetchInviteRequest fetchInviteRequest = new FetchInviteRequest();
            fetchInviteRequest.setUserID("f93993c8-7dc1-4710-b204-291a6efb5219");
            fetchInviteRequest.setOffset(1);
            fetchInviteRequest.setRowcount(10);
            outputStream.writeObject(fetchInviteRequest);
             FetchInviteResponse response = (FetchInviteResponse) inputStream.readObject();
             for( CodeDoc i : response.getInvites()) {
             System.out.println(i.getTitle()+"  "+i.getOwnerName());
             }
             */

            RunCodeDocRequest runCodeDocRequest = new RunCodeDocRequest();
            runCodeDocRequest.setCodeDocID("ab49db3b-1584-4bba-8f52-4678a2211706");
            runCodeDocRequest.setLanguageType(LanguageType.JAVA);
            runCodeDocRequest.setUserID("dc6d9b08-b53d-4199-88c8-5b64c40290c0");
            outputStream.writeObject(runCodeDocRequest);
            outputStream.flush();
            RunCodeDocResponse runCodeDocResponse = (RunCodeDocResponse) inputStream.readObject();
            System.out.println(runCodeDocResponse.getError()+" *********\n"+runCodeDocResponse.getOutput());

            //inputStream.close();
            //outputStream.close();
            //socket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        appStage = stage;
        stage.setResizable(false);
        stage.setTitle("CodeDocs");
        SceneService.setScene(AppScreen.signupScreen);
        stage.show();
    }
}
