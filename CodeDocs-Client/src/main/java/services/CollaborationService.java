package services;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import mainClasses.CodeDocsClient;
import requests.appRequests.*;
import response.appResponse.*;
import utilities.UserApi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Optional;

/**
 * This class has all the functions for handling collaborators of a codedoc
 */
public class CollaborationService {

    // IO streams for sending requests to server and reading responses from server
    private static final ObjectInputStream inputStream = CodeDocsClient.inputStream;
    private static final ObjectOutputStream outputStream = CodeDocsClient.outputStream;

    /**
     * Function to invite someone to collaborate
     *
     * @param codeDocID        id of codedoc
     * @param receiverEmail    email of the user to be invited
     * @param writePermissions write permissions with the user to be invited
     */
    public static InviteCollaboratorResponse inviteCollaborator(String codeDocID, String receiverEmail, int writePermissions) throws IOException, ClassNotFoundException {

        // Generate a request
        InviteCollaboratorRequest request = new InviteCollaboratorRequest();
        request.setSenderID(UserApi.getInstance().getId());
        request.setReceiverEmail(receiverEmail);
        request.setCodeDocID(codeDocID);
        request.setWritePermissions(writePermissions);

        // Write the request to the server
        outputStream.writeObject(request);
        outputStream.flush();

        // Return the response from the server
        return (InviteCollaboratorResponse) inputStream.readObject();
    }


    /**
     * Function to fetch the invites from the server
     *
     * @param rowCount for pagination
     * @param offset   for pagination
     */
    public static FetchInviteResponse fetchInvites(int rowCount, int offset) throws IOException, ClassNotFoundException {

        // Generate the request
        FetchInviteRequest request = new FetchInviteRequest();
        request.setRowcount(rowCount);
        request.setOffset(offset);
        request.setUserID(UserApi.getInstance().getId());

        // Write request to the server
        outputStream.writeObject(request);
        outputStream.flush();

        // Return the response
        return (FetchInviteResponse) inputStream.readObject();
    }

    /**
     * Function to accept an invitation
     *
     * @param codeDocID id of codedoc
     */
    public static AcceptInviteResponse acceptInvite(String codeDocID) throws IOException, ClassNotFoundException {

        // Generating the request
        AcceptInviteRequest request = new AcceptInviteRequest();
        request.setCodeDocID(codeDocID);
        request.setReceiverID(UserApi.getInstance().getId());

        // Writing the response
        outputStream.writeObject(request);
        outputStream.flush();

        // Returning the response
        return (AcceptInviteResponse) inputStream.readObject();
    }

    /**
     * Function to reject an invitation
     *
     * @param codeDocID
     */
    public static RejectInviteResponse rejectInvite(String codeDocID) throws IOException, ClassNotFoundException {

        RejectInviteRequest request = new RejectInviteRequest();
        request.setCodeDocID(codeDocID);
        request.setReceiverID(UserApi.getInstance().getId());

        outputStream.writeObject(request);
        outputStream.flush();

        return (RejectInviteResponse) inputStream.readObject();

    }

    /**
     * Function to fetch all collaborators in a codedoc
     *
     * @param codeDocID id of the codedoc
     * @param rowCount  for pagination
     * @param offset    for pagination
     */
    public static FetchCollaboratorResponse fetchCollaborator(String codeDocID, int rowCount, int offset) throws IOException, ClassNotFoundException {

        FetchCollaboratorRequest request = new FetchCollaboratorRequest();
        request.setCodeDocID(codeDocID);
        request.setOwnerID(UserApi.getInstance().getId());
        request.setOffset(offset);
        request.setRowcount(rowCount);

        outputStream.writeObject(request);
        outputStream.flush();

        return (FetchCollaboratorResponse) inputStream.readObject();

    }

    /**
     * Function to remove a collaborator
     *
     * @param codeDocID      id of codedoc
     * @param collaboratorID id of user to bew removed
     */
    public static RemoveCollaboratorResponse removeCollaborator(String codeDocID, String collaboratorID) throws IOException, ClassNotFoundException {

        ButtonType confirm = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "", confirm, cancel);
        confirmationAlert.setContentText("Are you sure?");
        Optional<ButtonType> pressedButton = confirmationAlert.showAndWait();

        if (pressedButton.orElse(cancel) == confirm) {
            RemoveCollaboratorRequest request = new RemoveCollaboratorRequest();
            request.setOwnerID(UserApi.getInstance().getId());
            request.setCodeDocID(codeDocID);
            request.setCollaboratorID(collaboratorID);

            outputStream.writeObject(request);
            outputStream.flush();

            return (RemoveCollaboratorResponse) inputStream.readObject();
        }
        return null;
    }

    /**
     * Function to change the rights of a collaborator
     * @param codeDocID id of codedoc
     * @param collaboratorID id of user
     * @param writePermissions new write permissions
     */
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
