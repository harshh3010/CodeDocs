package services.clientServices;

import mainClasses.CodeDocsServer;
import models.CodeDoc;
import models.Collaborator;
import models.User;
import requests.appRequests.*;
import response.appResponse.*;
import utilities.DatabaseConstants;
import utilities.Status;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CollaborationService {


    public static InviteCollaboratorResponse inviteCollaborator(InviteCollaboratorRequest inviteCollaboratorRequest){

        InviteCollaboratorResponse inviteCollaboratorResponse = new InviteCollaboratorResponse();
        //sender cannot send request to himself
        if(inviteCollaboratorRequest.getSenderID() == inviteCollaboratorRequest.getReceiverID()){
            inviteCollaboratorResponse.setStatus(Status.FAILED);
            return inviteCollaboratorResponse;
        }
       //TODO: check performance
        String checkAccessRightQuery = "SELECT " +DatabaseConstants.CODEDOC_TABLE_COL_CODEDOCID +
                " FROM " + DatabaseConstants.CODEDOC_TABLE_NAME +
                " WHERE " + DatabaseConstants.CODEDOC_TABLE_COL_OWNERID + " =? " +
                " AND " + DatabaseConstants.CODEDOC_TABLE_COL_CODEDOCID + " =?";

        String insertInviteQuery = "INSERT INTO " + DatabaseConstants.CODEDOC_ACCESS_TABLE_NAME
                + "(" + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_CODEDOC_ID
                + "," + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_USER_ID
                + "," + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_ACCESS_RIGHT
                + "," + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_HAS_WRITE_PERMISSIONS
                + ") values(?,?,?,?);";
        try {
            CodeDocsServer.databaseConnection.setAutoCommit(false);
            try {
                PreparedStatement preparedStatement = CodeDocsServer.databaseConnection.prepareStatement(checkAccessRightQuery);
                preparedStatement.setString(1, inviteCollaboratorRequest.getSenderID());
                preparedStatement.setString(2, inviteCollaboratorRequest.getCodeDocID());
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {

                    preparedStatement = CodeDocsServer.databaseConnection.prepareStatement(insertInviteQuery);
                    preparedStatement.setString(1, inviteCollaboratorRequest.getCodeDocID());
                    preparedStatement.setString(2, inviteCollaboratorRequest.getReceiverID());
                    preparedStatement.setString(3, "PENDING");
                    preparedStatement.setInt(4, inviteCollaboratorRequest.getWritePermissions());
                    preparedStatement.executeUpdate();
                    CodeDocsServer.databaseConnection.commit();
                    inviteCollaboratorResponse.setStatus(Status.SUCCESS);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                CodeDocsServer.databaseConnection.rollback();
                inviteCollaboratorResponse.setStatus(Status.FAILED);
            }

        }catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return inviteCollaboratorResponse;
    }



    public static AcceptInviteResponse acceptInvite(AcceptInviteRequest acceptInviteRequest){
        AcceptInviteResponse acceptInviteResponse = new AcceptInviteResponse();

        String acceptInviteQuery = "UPDATE " + DatabaseConstants.CODEDOC_ACCESS_TABLE_NAME +
                " SET " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_ACCESS_RIGHT + " =?" +
                " WHERE " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_CODEDOC_ID+ " =?" +
                " AND " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_ACCESS_RIGHT+ " =\"PENDING\"" +
                " AND " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_USER_ID+ " =?;";
         try {
            CodeDocsServer.databaseConnection.setAutoCommit(false);
            try {
                PreparedStatement preparedStatement = CodeDocsServer.databaseConnection.prepareStatement(acceptInviteQuery);
                preparedStatement.setString(1, "COLLABORATOR");
                preparedStatement.setString(2, acceptInviteRequest.getCodeDocID());
                preparedStatement.setString(3, acceptInviteRequest.getReceiverID());
                int result = preparedStatement.executeUpdate();

                CodeDocsServer.databaseConnection.commit();
                if(result !=0)
                    acceptInviteResponse.setStatus(Status.SUCCESS);
                else
                    acceptInviteResponse.setStatus(Status.FAILED);
            } catch (SQLException e) {
                acceptInviteResponse.setStatus(Status.FAILED);
                CodeDocsServer.databaseConnection.rollback();
                e.printStackTrace();
            }
        }catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return acceptInviteResponse;
    }

    public static RejectInviteResponse rejectInvite(RejectInviteRequest rejectInviteRequest){

        RejectInviteResponse rejectInviteResponse= new RejectInviteResponse();

        String rejectInviteQuery = "DELETE FROM " + DatabaseConstants.CODEDOC_ACCESS_TABLE_NAME +
                " WHERE " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_CODEDOC_ID + " =? " +
                " AND " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_USER_ID+ " =?" +
                " AND " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_ACCESS_RIGHT+ " =?";

        try {
            CodeDocsServer.databaseConnection.setAutoCommit(false);
            try {
                PreparedStatement preparedStatement = CodeDocsServer.databaseConnection.prepareStatement(rejectInviteQuery);
                preparedStatement.setString(1, rejectInviteRequest.getCodeDocID());
                preparedStatement.setString(2, rejectInviteRequest.getReceiverID());
                preparedStatement.setString(3, "PENDING");
                preparedStatement.executeUpdate();
                CodeDocsServer.databaseConnection.commit();
                rejectInviteResponse.setStatus(Status.SUCCESS);
            } catch (SQLException e) {
                rejectInviteResponse.setStatus(Status.FAILED);
                CodeDocsServer.databaseConnection.rollback();
                e.printStackTrace();
            }
        }catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rejectInviteResponse;
    }

    public static FetchInviteResponse fetchInvites(FetchInviteRequest fetchInviteRequest){
        FetchInviteResponse fetchInviteResponse = new FetchInviteResponse();

        String fetchInvitesQuery = " SELECT " +
                DatabaseConstants.CODEDOC_TABLE_NAME + "." +DatabaseConstants.CODEDOC_TABLE_COL_TITLE+ ", " +
                DatabaseConstants.CODEDOC_TABLE_NAME+ "." +DatabaseConstants.CODEDOC_TABLE_COL_DESCRIPTION+ ", " +
                DatabaseConstants.USER_TABLE_NAME+ "." +DatabaseConstants.USER_TABLE_COL_FIRSTNAME + ", " +
                DatabaseConstants.CODEDOC_TABLE_NAME+ "." +DatabaseConstants.CODEDOC_TABLE_COL_CODEDOCID +
                " FROM " +
                "(( " +DatabaseConstants.CODEDOC_TABLE_NAME +
                " INNER JOIN " +DatabaseConstants.CODEDOC_ACCESS_TABLE_NAME+ " ON " +
                DatabaseConstants.CODEDOC_TABLE_NAME + "." +DatabaseConstants.CODEDOC_TABLE_COL_CODEDOCID+ "= " +
                DatabaseConstants.CODEDOC_ACCESS_TABLE_NAME+ "." +DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_CODEDOC_ID +
                " ) " +
                " INNER JOIN " +DatabaseConstants.USER_TABLE_NAME+ " ON " +
                DatabaseConstants.CODEDOC_TABLE_NAME + "." +DatabaseConstants.CODEDOC_TABLE_COL_OWNERID+ "= " +
                DatabaseConstants.USER_TABLE_NAME+ "." +DatabaseConstants.USER_TABLE_COL_USERID +
                " ) " +
                " WHERE " +
                DatabaseConstants.CODEDOC_ACCESS_TABLE_NAME+"." + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_USER_ID +"=?"+
                " AND " +
                DatabaseConstants.CODEDOC_ACCESS_TABLE_NAME+"." + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_ACCESS_RIGHT +"=?" +
                " ORDER BY " + DatabaseConstants.CODEDOC_TABLE_COL_UPDATED_AT + " DESC "
                + " LIMIT " + fetchInviteRequest.getOffset() +
                " , " + fetchInviteRequest.getRowcount()
                + ";";
        List<CodeDoc> inviteList = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = CodeDocsServer.databaseConnection.prepareStatement(fetchInvitesQuery);
            preparedStatement.setString(1, fetchInviteRequest.getUserID());
            preparedStatement.setString(2, "PENDING");

            ResultSet resultSet = preparedStatement.executeQuery();
            CodeDoc invite = null;

            while (resultSet.next()) {
                invite = new CodeDoc();
                invite.setTitle(resultSet.getString(1));
                invite.setCodeDocId(resultSet.getString(4));
                invite.setDescription(resultSet.getString(2));
                invite.setOwnerName(resultSet.getString(3));
                inviteList.add(invite);
            }

            fetchInviteResponse.setStatus(Status.SUCCESS);

        } catch (SQLException e) {
            fetchInviteResponse.setStatus(Status.FAILED);
            e.printStackTrace();
        }
        fetchInviteResponse.setInvites(inviteList);
        return fetchInviteResponse;
    }

    public static RemoveCollaboratorResponse removeCollaborator(RemoveCollaboratorRequest request){

        RemoveCollaboratorResponse response = new RemoveCollaboratorResponse();
        //first check that owner is requesting to remove collaborator or not
        String checkOwnerQuery = "SELECT " +DatabaseConstants.CODEDOC_TABLE_COL_CODEDOCID +
                " FROM " + DatabaseConstants.CODEDOC_TABLE_NAME +
                " WHERE " + DatabaseConstants.CODEDOC_TABLE_COL_OWNERID + " =? " +
                " AND " + DatabaseConstants.CODEDOC_TABLE_COL_CODEDOCID + " =?";

        String deleteQuery = "DELETE FROM " + DatabaseConstants.CODEDOC_ACCESS_TABLE_NAME +
                " WHERE " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_CODEDOC_ID + " =? " +
                " AND " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_USER_ID+ " =?;" ;

        try {
            CodeDocsServer.databaseConnection.setAutoCommit(false);
            try {
                PreparedStatement preparedStatement = CodeDocsServer.databaseConnection.prepareStatement(checkOwnerQuery);
                preparedStatement.setString(1, request.getOwnerID());
                preparedStatement.setString(2, request.getCodeDocID());
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {

                    preparedStatement = CodeDocsServer.databaseConnection.prepareStatement(deleteQuery);
                    preparedStatement.setString(1, request.getCodeDocID());
                    preparedStatement.setString(2, request.getCollaboratorID());
                    preparedStatement.executeUpdate();
                    CodeDocsServer.databaseConnection.commit();
                    response.setStatus(Status.SUCCESS);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                CodeDocsServer.databaseConnection.rollback();
                response.setStatus(Status.FAILED);
            }

        }catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return response;

    }

    public static ChangeCollaboratorRightsResponse changeCollaboratorRights(ChangeCollaboratorRightsRequest request){

        ChangeCollaboratorRightsResponse response = new ChangeCollaboratorRightsResponse();
        //first check that owner is requesting to remove collaborator or not
        String checkOwnerQuery = "SELECT " +DatabaseConstants.CODEDOC_TABLE_COL_CODEDOCID +
                " FROM " + DatabaseConstants.CODEDOC_TABLE_NAME +
                " WHERE " + DatabaseConstants.CODEDOC_TABLE_COL_OWNERID + " =? " +
                " AND " + DatabaseConstants.CODEDOC_TABLE_COL_CODEDOCID + " =?";

        String updateQuery = "UPDATE " + DatabaseConstants.CODEDOC_ACCESS_TABLE_NAME +
                " SET " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_HAS_WRITE_PERMISSIONS + " =?" +
                " WHERE " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_CODEDOC_ID+ " =?" +
                " AND " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_USER_ID+ " =?;";

        try {
            CodeDocsServer.databaseConnection.setAutoCommit(false);
            try {
                PreparedStatement preparedStatement = CodeDocsServer.databaseConnection.prepareStatement(checkOwnerQuery);
                preparedStatement.setString(1, request.getOwnerID());
                preparedStatement.setString(2, request.getCodeDocID());
                ResultSet resultSet = preparedStatement.executeQuery();
                System.out.println("{{{{{{{{");
                if (resultSet.next()) {
                    System.out.println("hiiiii");
                    preparedStatement = CodeDocsServer.databaseConnection.prepareStatement(updateQuery);
                    preparedStatement.setInt(1,request.getWritePermissions());
                    preparedStatement.setString(2, request.getCodeDocID());
                    preparedStatement.setString(3, request.getCollaboratorID());
                    int result = preparedStatement.executeUpdate();
                    response.setStatus(Status.SUCCESS);
                    if(result !=0)
                        response.setStatus(Status.SUCCESS);
                    else
                        response.setStatus(Status.FAILED);
                    CodeDocsServer.databaseConnection.commit();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                CodeDocsServer.databaseConnection.rollback();
                response.setStatus(Status.FAILED);
            }

        }catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return response;
    }

    public static FetchCollaboratorResponse fetchCollaborators(FetchCollaboratorRequest request){

        FetchCollaboratorResponse response = new FetchCollaboratorResponse();
        //TODO: allow only accessor to fetch collaborators


        String fetchQuery = " SELECT " +
                DatabaseConstants.USER_TABLE_NAME+ "." +DatabaseConstants.USER_TABLE_COL_FIRSTNAME + ", " +
                DatabaseConstants.USER_TABLE_NAME+ "." +DatabaseConstants.USER_TABLE_COL_LASTNAME + ", " +
                DatabaseConstants.USER_TABLE_NAME+ "." +DatabaseConstants.USER_TABLE_COL_EMAIL+ ", " +
                DatabaseConstants.USER_TABLE_NAME+ "." +DatabaseConstants.USER_TABLE_COL_USERID+ ", " +
                DatabaseConstants.CODEDOC_ACCESS_TABLE_NAME+ "." +DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_HAS_WRITE_PERMISSIONS +
                " FROM " +
                "( " +DatabaseConstants.CODEDOC_ACCESS_TABLE_NAME +
                " INNER JOIN " +DatabaseConstants.USER_TABLE_NAME+ " ON " +
                DatabaseConstants.CODEDOC_ACCESS_TABLE_NAME + "." +DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_USER_ID+ "= " +
                DatabaseConstants.USER_TABLE_NAME+ "." +DatabaseConstants.USER_TABLE_COL_USERID +
                " ) " +
                " WHERE " +
                DatabaseConstants.CODEDOC_ACCESS_TABLE_NAME+"." + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_CODEDOC_ID +"=?"+
                " AND " +
                DatabaseConstants.CODEDOC_ACCESS_TABLE_NAME+"." + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_ACCESS_RIGHT +"=?" +
                " ORDER BY " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_CODEDOC_ID + " DESC "+
                " LIMIT " + request.getOffset() +
                " , " + request.getRowcount()
                + ";";

        List<Collaborator> collaboratorList = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = CodeDocsServer.databaseConnection.prepareStatement(fetchQuery);
            preparedStatement.setString(1,request.getCodeDocID());
            preparedStatement.setString(2,"COLLABORATOR");

            ResultSet resultSet = preparedStatement.executeQuery();
            User user = null;
            Collaborator collaborator = null;

            while (resultSet.next()) {
                user = new User();
                collaborator = new Collaborator();
                user.setFirstName(resultSet.getString(1));
                user.setLastName(resultSet.getString(2));
                user.setEmail(resultSet.getString(3));
                user.setUserID(resultSet.getString(4));
                collaborator.setUser(user);
                collaborator.setWritePermissions(resultSet.getInt(5));
                collaboratorList.add(collaborator);
            }
            response.setStatus(Status.SUCCESS);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(Status.FAILED);
        }

        response.setCollaborators(collaboratorList);
        return response;
    }
}
