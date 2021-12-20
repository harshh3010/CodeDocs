package services.clientServices;

import mainClasses.CodeDocsServer;
import models.Peer;
import models.User;
import requests.editorRequests.EditorCloseRequest;
import requests.editorRequests.EditorConnectionRequest;
import requests.editorRequests.LoadEditorRequest;
import requests.editorRequests.SaveCodeDocRequest;
import response.editorResponse.EditorConnectionResponse;
import response.editorResponse.LoadEditorResponse;
import response.editorResponse.SaveCodeDocResponse;
import utilities.DatabaseConstants;
import utilities.Status;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

public class EditorService {

    // TODO: Sync

    // TODO: Optimise queries
    public static EditorConnectionResponse establishConnection(EditorConnectionRequest editorConnectionRequest, String ipAddress) {
        EditorConnectionResponse response = new EditorConnectionResponse();

        String permissionsQuery = "SELECT " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_HAS_WRITE_PERMISSIONS
                + " FROM " + DatabaseConstants.CODEDOC_ACCESS_TABLE_NAME
                + " WHERE " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_USER_ID
                + " = ? AND " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_CODEDOC_ID
                + " = ?;";

        try {
            CodeDocsServer.databaseConnection.setAutoCommit(false);

            try {
                PreparedStatement preparedStatement = CodeDocsServer.databaseConnection.prepareStatement(permissionsQuery);
                preparedStatement.setString(1, editorConnectionRequest.getUserId());
                preparedStatement.setString(2, editorConnectionRequest.getCodeDocId());

                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    response.setHasWritePermissions(resultSet.getBoolean(1));

                    String activeQuery = "SELECT " + DatabaseConstants.ACTIVE_EDITORS_COL_USER_IN_CONTROL
                            + " FROM " + DatabaseConstants.ACTIVE_EDITORS_TABLE_NAME
                            + " WHERE " + DatabaseConstants.ACTIVE_EDITORS_COL_CODEDOC_ID
                            + " = ?;";

                    preparedStatement = CodeDocsServer.databaseConnection.prepareStatement(activeQuery);
                    preparedStatement.setString(1, editorConnectionRequest.getCodeDocId());
                    resultSet = preparedStatement.executeQuery();

                    if (resultSet.next()) {
                        response.setUserInControl(resultSet.getString(1));
                    } else {
                        if(response.isHasWritePermissions()) {
                            activeQuery = "INSERT INTO " + DatabaseConstants.ACTIVE_EDITORS_TABLE_NAME
                                    + " (" + DatabaseConstants.ACTIVE_EDITORS_COL_CODEDOC_ID
                                    + ", " + DatabaseConstants.ACTIVE_EDITORS_COL_USER_IN_CONTROL
                                    + ") VALUES (?, ?);";
                            preparedStatement = CodeDocsServer.databaseConnection.prepareStatement(activeQuery);
                            preparedStatement.setString(1, editorConnectionRequest.getCodeDocId());
                            preparedStatement.setString(2, editorConnectionRequest.getUserId());
                            preparedStatement.executeUpdate();
                            response.setUserInControl(editorConnectionRequest.getUserId());
                        } else {
                            response.setUserInControl(null);
                        }
                    }

                    String activeUsersQuery = "SELECT " + DatabaseConstants.USER_TABLE_NAME + "."+ DatabaseConstants.USER_TABLE_COL_USERID
                            + ", " + DatabaseConstants.USER_TABLE_COL_EMAIL
                            + ", " + DatabaseConstants.USER_TABLE_COL_FIRSTNAME
                            + ", " + DatabaseConstants.USER_TABLE_COL_LASTNAME
                            + ", " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_IP_ADDRESS
                            + ", " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_PORT
                            + ", " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_HAS_WRITE_PERMISSIONS
                            + ", " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_AUDIO_PORT
                            + " FROM " + DatabaseConstants.USER_TABLE_NAME
                            + " JOIN " + DatabaseConstants.CODEDOC_ACCESS_TABLE_NAME
                            + " ON " + DatabaseConstants.USER_TABLE_NAME + "." + DatabaseConstants.USER_TABLE_COL_USERID
                            + " = " + DatabaseConstants.CODEDOC_ACCESS_TABLE_NAME + "." + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_USER_ID
                            + " WHERE " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_CODEDOC_ID
                            + " = ? AND " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_IS_ACTIVE
                            + " = TRUE;";

                    preparedStatement = CodeDocsServer.databaseConnection.prepareStatement(activeUsersQuery);
                    preparedStatement.setString(1, editorConnectionRequest.getCodeDocId());
                    resultSet = preparedStatement.executeQuery();

                    ArrayList<Peer> activeUsers = new ArrayList<>();
                    while (resultSet.next()) {
                        User user = new User();
                        user.setUserID(resultSet.getString(1));
                        user.setEmail(resultSet.getString(2));
                        user.setFirstName(resultSet.getString(3));
                        user.setLastName(resultSet.getString(4));

                        Peer peer = new Peer();
                        peer.setUser(user);
                        peer.setIpAddress(resultSet.getString(5));
                        peer.setPort(resultSet.getInt(6));
                        peer.setHasWritePermissions(resultSet.getBoolean(7));
                        peer.setAudioPort(resultSet.getInt(8));

                        activeUsers.add(peer);
                    }

                    response.setActivePeers(activeUsers);

                    String updateQuery = "UPDATE " + DatabaseConstants.CODEDOC_ACCESS_TABLE_NAME
                            + " SET " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_IS_ACTIVE
                            + " = TRUE, " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_IP_ADDRESS
                            + " = ?, " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_PORT
                            + " = ?, " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_AUDIO_PORT
                            + " = ? WHERE " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_USER_ID
                            + " = ? AND " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_CODEDOC_ID
                            + " = ?;";

                    preparedStatement = CodeDocsServer.databaseConnection.prepareStatement(updateQuery);
                    preparedStatement.setString(1, ipAddress);
                    preparedStatement.setInt(2, editorConnectionRequest.getPort());
                    preparedStatement.setInt(3, editorConnectionRequest.getAudioPort());
                    preparedStatement.setString(4, editorConnectionRequest.getUserId());
                    preparedStatement.setString(5, editorConnectionRequest.getCodeDocId());
                    preparedStatement.executeUpdate();

                    CodeDocsServer.databaseConnection.commit();
                    response.setStatus(Status.SUCCESS);
                    return response;
                }
            } catch (SQLException throwables) {
                CodeDocsServer.databaseConnection.rollback();
                throwables.printStackTrace();
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        response.setStatus(Status.FAILED);
        return response;
    }

    public static void destroyConnection(EditorCloseRequest request){

        String updateQuery = "UPDATE " + DatabaseConstants.CODEDOC_ACCESS_TABLE_NAME
                + " SET " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_IS_ACTIVE + " = FALSE " +
                " WHERE " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_USER_ID+ " =? " +
                " AND " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_CODEDOC_ID + " = ?;";

        try{
            CodeDocsServer.databaseConnection.setAutoCommit(false);
            try{
                PreparedStatement preparedStatement = CodeDocsServer.databaseConnection.prepareStatement(updateQuery);
                preparedStatement.setString(1, request.getUserId());
                preparedStatement.setString(2, request.getCodeDocId());
                preparedStatement.executeUpdate();

                String query = "";
                if(request.getUserInControl() == null){
                    query = "DELETE FROM " + DatabaseConstants.ACTIVE_EDITORS_TABLE_NAME +
                            " WHERE " + DatabaseConstants.ACTIVE_EDITORS_COL_CODEDOC_ID+ " = ?;";
                    preparedStatement = CodeDocsServer.databaseConnection.prepareStatement(query);
                    preparedStatement.setString(1,request.getCodeDocId());

                }
                else {
                    query = "UPDATE " + DatabaseConstants.ACTIVE_EDITORS_TABLE_NAME +
                            " SET " + DatabaseConstants.ACTIVE_EDITORS_COL_USER_IN_CONTROL + " =? " +
                            " WHERE " + DatabaseConstants.ACTIVE_EDITORS_COL_CODEDOC_ID + " = ?;";
                    preparedStatement = CodeDocsServer.databaseConnection.prepareStatement(query);
                    preparedStatement.setString(1,request.getUserInControl());
                    preparedStatement.setString(1,request.getCodeDocId());
                }
                preparedStatement.executeUpdate();
                CodeDocsServer.databaseConnection.commit();

            } catch (SQLException throwables) {
                throwables.printStackTrace();
                CodeDocsServer.databaseConnection.rollback();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }



    public static LoadEditorResponse loadEditor(LoadEditorRequest loadEditorRequest) {
        LoadEditorResponse response = new LoadEditorResponse();

        String query = "SELECT " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_HAS_WRITE_PERMISSIONS
                + " FROM " + DatabaseConstants.CODEDOC_ACCESS_TABLE_NAME
                + " WHERE " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_USER_ID
                + " = ? AND " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_CODEDOC_ID
                + " = ?;";

        try {
            CodeDocsServer.databaseConnection.setAutoCommit(false);

            try {
                PreparedStatement preparedStatement = CodeDocsServer.databaseConnection.prepareStatement(query);
                preparedStatement.setString(1, loadEditorRequest.getUserId());
                preparedStatement.setString(2, loadEditorRequest.getCodeDocId());

                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    response.setHasWritePermissions(resultSet.getBoolean(1));

                    Properties properties = new Properties();
                    FileReader fileReader = new FileReader("CodeDocs-Server/src/main/resources/configurations/db.properties");
                    properties.load(fileReader);
                    String filePath = properties.getProperty("FILEPATH");

                    filePath += loadEditorRequest.getCodeDocId() + "/Solution" + loadEditorRequest.getLanguageType().getExtension();

                    File file = new File(filePath);
                    BufferedReader br = new BufferedReader(new FileReader(file));

                    String content = "", line;
                    while ((line = br.readLine()) != null) {
                        content = content + line + "\n";
                    }

                    CodeDocsServer.databaseConnection.commit();

                    response.setContent(content);
                    response.setStatus(Status.SUCCESS);
                    return response;
                }
            } catch (SQLException | IOException throwables) {
                CodeDocsServer.databaseConnection.rollback();
                throwables.printStackTrace();
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        response.setStatus(Status.FAILED);
        return response;
    }

    public static SaveCodeDocResponse saveCodeDoc(SaveCodeDocRequest saveCodeDocRequest) {

        SaveCodeDocResponse saveCodeDocResponse = new SaveCodeDocResponse();
        String query = "SELECT " +
                DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_USER_ID +
                " FROM " + DatabaseConstants.CODEDOC_ACCESS_TABLE_NAME +
                " WHERE " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_CODEDOC_ID + "=?" +
                " AND " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_USER_ID + " =? " +
                " AND " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_HAS_WRITE_PERMISSIONS + " =1;";

        try {
            PreparedStatement preparedStatement = CodeDocsServer.databaseConnection.prepareStatement(query);
            preparedStatement.setString(1, saveCodeDocRequest.getCodeDoc().getCodeDocId());
            preparedStatement.setString(2, saveCodeDocRequest.getUserId());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                Properties properties = new Properties();
                FileReader fileReader = new FileReader("CodeDocs-Server/src/main/resources/configurations/db.properties");
                properties.load(fileReader);
                String filePath = properties.getProperty("FILEPATH");

                filePath += saveCodeDocRequest.getCodeDoc().getCodeDocId() + "/Solution" + saveCodeDocRequest.getCodeDoc().getLanguageType().getExtension();
                BufferedWriter fileWriter = new BufferedWriter(new FileWriter(filePath));

                fileWriter.write(saveCodeDocRequest.getCodeDoc().getFileContent());
                fileWriter.close();
                saveCodeDocResponse.setStatus(Status.SUCCESS);
            } else {
                saveCodeDocResponse.setStatus(Status.FAILED);
            }
        } catch (SQLException | IOException e) {
            saveCodeDocResponse.setStatus(Status.FAILED);
            e.printStackTrace();
        }


        return saveCodeDocResponse;
    }

}
