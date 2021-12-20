package services;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import mainClasses.CodeDocsClient;
import requests.appRequests.*;
import response.appResponse.*;
import utilities.CodeDocRequestType;
import utilities.UserApi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Optional;

public class CollaborationService {

    private static final ObjectInputStream inputStream = CodeDocsClient.inputStream;
    private static final ObjectOutputStream outputStream = CodeDocsClient.outputStream;

    public static InviteCollaboratorResponse inviteCollaborator(String codeDocID, String receiverID,int writePermissions) throws IOException, ClassNotFoundException {

        InviteCollaboratorRequest request = new InviteCollaboratorRequest();
        request.setSenderID(UserApi.getInstance().getId());
        request.setReceiverID(receiverID);
        request.setCodeDocID(codeDocID);
        request.setWritePermissions(writePermissions);

        outputStream.writeObject(request);
        outputStream.flush();

        return (InviteCollaboratorResponse) inputStream.readObject();

    }

    public static FetchInviteResponse fetchInvites(int rowCount, int offset) throws IOException, ClassNotFoundException {

        FetchInviteRequest request = new FetchInviteRequest();
        request.setRowcount(rowCount);
        request.setOffset(offset);
        request.setUserID(UserApi.getInstance().getId());

        outputStream.writeObject(request);
        outputStream.flush();

        return (FetchInviteResponse) inputStream.readObject();
    }

    public static AcceptInviteResponse acceptInvite(String codeDocID) throws IOException, ClassNotFoundException {

        AcceptInviteRequest request = new AcceptInviteRequest();
        request.setCodeDocID(codeDocID);
        request.setReceiverID(UserApi.getInstance().getId());

        outputStream.writeObject(request);
        outputStream.flush();

        return (AcceptInviteResponse) inputStream.readObject();

    }

    public static RejectInviteResponse rejectInvite(String codeDocID) throws IOException, ClassNotFoundException {

        RejectInviteRequest request = new RejectInviteRequest();
        request.setCodeDocID(codeDocID);
        request.setReceiverID(UserApi.getInstance().getId());

        outputStream.writeObject(request);
        outputStream.flush();

        return (RejectInviteResponse) inputStream.readObject();

    }

    public static FetchCollaboratorResponse fetchCollaborator(String codeDocID, int rowCount, int offset) throws IOException, ClassNotFoundException {

        FetchCollaboratorRequest request = new FetchCollaboratorRequest();
        request.setCodeDocID(codeDocID);
        request.setOwnerID(UserApi.getInstance().getId());
        request.setOffset(offset);
        request.setRowcount(rowCount);

        outputStream.writeObject(request);
        outputStream.flush();

        return (FetchCollaboratorResponse)inputStream.readObject();

    }

    public static RemoveCollaboratorResponse removeCollaborator(String codeDocID, String collaboratorID) throws IOException, ClassNotFoundException {

        ButtonType confirm = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION,"",confirm,cancel);
        confirmationAlert.setContentText("Are you sure?");
        Optional<ButtonType> pressedButton = confirmationAlert.showAndWait();

        if (pressedButton.orElse(cancel) == confirm) {
            RemoveCollaboratorRequest request = new RemoveCollaboratorRequest();
            request.setOwnerID(UserApi.getInstance().getId());
            request.setCodeDocID(codeDocID);
            request.setCollaboratorID(collaboratorID);

            outputStream.writeObject(request);
            outputStream.flush();

            return (RemoveCollaboratorResponse)inputStream.readObject();
        }
        return null;
    }

    public static ChangeCollaboratorRightsResponse changeCollaboratorRights(String codeDocID, String collaboratorID, int writePermissions) throws IOException, ClassNotFoundException {

        ChangeCollaboratorRightsRequest request = new ChangeCollaboratorRightsRequest();
        request.setWritePermissions(writePermissions);
        request.setOwnerID(UserApi.getInstance().getId());
        request.setCollaboratorID(collaboratorID);
        request.setCodeDocID(codeDocID);

        outputStream.writeObject(request);
        outputStream.flush();

        return (ChangeCollaboratorRightsResponse) inputStream.readObject();

    }



}
