package mainClasses;

import requests.appRequests.AppRequest;
import requests.appRequests.LoginRequest;
import requests.appRequests.SignupRequest;
import requests.appRequests.VerifyUserRequest;
import services.clientServices.AuthenticationService;
import utilities.RequestType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientConnection extends Thread{

    private final String ipAddress;
    private final Socket client;
    private final ObjectInputStream inputStream;
    private final ObjectOutputStream outputStream;

    public ClientConnection(Socket client) throws IOException {

        ipAddress = client.getInetAddress().getCanonicalHostName();
        System.out.println("Client connected " + ipAddress);

        this.client = client;
        this.outputStream = new ObjectOutputStream(client.getOutputStream());
        this.inputStream = new ObjectInputStream(client.getInputStream());
    }

    @Override
    public void run() {

        while (true) {
            try {
                AppRequest request = (AppRequest) inputStream.readObject();

                if(request.getRequestType() == RequestType.SIGNUP_REQUEST) {
                    System.out.println("Client wants to signup!");
                    outputStream.writeObject(AuthenticationService.registerUser((SignupRequest) request));
                    outputStream.flush();
                }else if(request.getRequestType() == RequestType.LOGIN_REQUEST) {
                    System.out.println("Client wants to login!");
                    outputStream.writeObject(AuthenticationService.loginUser((LoginRequest) request));
                    outputStream.flush();
                }else if(request.getRequestType() == RequestType.VERIFY_USER_REQUEST) {
                    System.out.println("Client wants to verify his account!");
                    outputStream.writeObject(AuthenticationService.verifyUser((VerifyUserRequest) request));
                    outputStream.flush();
                }

            } catch (IOException | ClassNotFoundException e) {
                try {
                    inputStream.close();
                    outputStream.close();
                    client.close();
                } catch (IOException e1) {
                    System.out.println("Error: Unable to close connection!");
                    e1.printStackTrace();
                }
                System.out.println("Client disconnected!");
                break;
            }
        }
    }


}
