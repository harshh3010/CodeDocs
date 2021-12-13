package services.clientServices;

import mainClasses.CodeDocsServer;
import requests.editorRequests.LoadEditorRequest;
import response.editorResponse.LoadEditorResponse;
import utilities.DatabaseConstants;
import utilities.Status;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class EditorService {

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
                if(resultSet.next()){
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

}
