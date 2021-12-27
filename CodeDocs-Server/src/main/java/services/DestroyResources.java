package services;

import mainClasses.CodeDocsServer;
import utilities.DatabaseConstants;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * This function cleans up the inconsistent data from the database
 */
public class DestroyResources {

    /**
     * Functions to remove all active editor allocations to a user
     */
    public static void destroyAllocations(String userID) {

        String updateQuery = "UPDATE " + DatabaseConstants.CODEDOC_ACCESS_TABLE_NAME +
                " SET " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_IS_ACTIVE + " = FALSE" +
                " WHERE " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_USER_ID + "=?;";

        try {
            CodeDocsServer.databaseConnection.setAutoCommit(false);
            try {
                PreparedStatement preparedStatement = CodeDocsServer.databaseConnection.prepareStatement(updateQuery);
                preparedStatement.setString(1, userID);
                preparedStatement.executeUpdate();

                CodeDocsServer.databaseConnection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
                CodeDocsServer.databaseConnection.rollback();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    /**
     * Function to clear all active editor allocations foe every user in database
     */
    public static void cleanDB() {
        String updateQuery = "UPDATE " + DatabaseConstants.CODEDOC_ACCESS_TABLE_NAME +
                " SET " + DatabaseConstants.CODEDOC_ACCESS_TABLE_COL_IS_ACTIVE + " = FALSE;";


        String deleteQuery = "DELETE FROM " + DatabaseConstants.ACTIVE_EDITORS_TABLE_NAME + ";";
        try {
            CodeDocsServer.databaseConnection.setAutoCommit(false);
            try {
                PreparedStatement preparedStatement = CodeDocsServer.databaseConnection.prepareStatement(updateQuery);
                preparedStatement.executeUpdate();

                preparedStatement = CodeDocsServer.databaseConnection.prepareStatement(deleteQuery);
                preparedStatement.executeUpdate();

                CodeDocsServer.databaseConnection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
                CodeDocsServer.databaseConnection.rollback();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
