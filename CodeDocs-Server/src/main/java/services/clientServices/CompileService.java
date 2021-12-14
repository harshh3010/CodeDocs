package services.clientServices;

import mainClasses.CodeDocsServer;
import requests.appRequests.RunCodeDocRequest;
import response.appResponse.RunCodeDocResponse;
import utilities.CompileUtility;
import utilities.DatabaseConstants;
import utilities.ExecuteResponse;
import utilities.Status;

import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class CompileService {
    public static String compileCommand[] = {"javac ","g++ -o Solution ","IDK","gcc -o Solution "};
    public static String runCommand[] = {"java Solution "," Solution.exe ","IDK","Solution "};

    public static RunCodeDocResponse runCodeDoc(RunCodeDocRequest runCodeDocRequest){

        RunCodeDocResponse runCodeDocResponse = new RunCodeDocResponse();
        //to allow only collaborators to conpile the codedoc
        String checkAccessQuery = " SELECT * " +
                " FROM " + DatabaseConstants.CODEDOC_ACCESS_TABLE_NAME +
                " WHERE " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_CODEDOC_ID + " =?" +
                " AND " +DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_USER_ID+ " =?";
        try {
            PreparedStatement preparedStatement = CodeDocsServer.databaseConnection.prepareStatement(checkAccessQuery);
            preparedStatement.setString(1, runCodeDocRequest.getCodeDocID());
            preparedStatement.setString(2, runCodeDocRequest.getUserID());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                Properties properties = new Properties();
                FileReader fileReader = new FileReader("CodeDocs-Server/src/main/resources/configurations/db.properties");
                properties.load(fileReader);
                String filePath = properties.getProperty("FILEPATH");
                filePath += runCodeDocRequest.getCodeDocID() + "\\";
                String fileName = "Solution" + runCodeDocRequest.getLanguageType().getExtension();
                int index = runCodeDocRequest.getLanguageType().getIndex();
                System.out.println(filePath);
                ExecuteResponse executeResponse = CompileUtility.runProcess(
                        "cd /d "+filePath
                                +" && " + compileCommand[index]
                                + " " + fileName
                                + " && " + runCommand[index]

                );
                runCodeDocResponse.setOutput(executeResponse.getOutput());
                runCodeDocResponse.setError(executeResponse.getError());
                runCodeDocResponse.setExitStatus(executeResponse.getExitStatus());
                runCodeDocResponse.setStatus(Status.SUCCESS);
                return runCodeDocResponse;
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        runCodeDocResponse.setOutput("");
        runCodeDocResponse.setError("");
        //runCodeDocResponse.setExitStatus(executeResponse.getExitStatus());
        runCodeDocResponse.setStatus(Status.FAILED);
        return runCodeDocResponse;
    }
}
