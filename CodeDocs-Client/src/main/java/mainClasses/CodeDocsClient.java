package mainClasses;

import models.CodeDoc;
import models.User;
import requests.appRequests.*;
import response.appResponse.*;
import requests.editorRequests.LoadEditorRequest;
import response.appResponse.CreateCodeDocResponse;
import response.appResponse.FetchCodeDocResponse;
import response.appResponse.LoginResponse;
import response.appResponse.SignupResponse;
import response.editorResponse.LoadEditorResponse;
import utilities.CodeDocRequestType;
import utilities.LanguageType;
import utilities.SignupStatus;
import utilities.Status;

import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Properties;

public class CodeDocsClient {

    private static String hostname;
    private static int port;
    public static Socket socket;
    public static ObjectInputStream inputStream;
    public static ObjectOutputStream outputStream;

    public static void main(String[] args) {

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

            LoadEditorRequest loadEditorRequest = new LoadEditorRequest();
            loadEditorRequest.setCodeDocId("2db6e816-dab8-4c9d-812f-660a30466f83");
            loadEditorRequest.setUserId("7414f918-2624-4610-8c48-3988d433e385");
            loadEditorRequest.setLanguageType(LanguageType.JAVA);

            outputStream.writeObject(loadEditorRequest);
            outputStream.flush();

            LoadEditorResponse loadEditorResponse = (LoadEditorResponse) inputStream.readObject();
            System.out.println(loadEditorResponse.getStatus());
            System.out.println(loadEditorResponse.getContent());

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
