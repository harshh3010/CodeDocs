package controllers;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import mainClasses.CodeDocsClient;
import models.Screenshot;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MyNotesTabController implements Initializable {

    public GridPane notesGrid;
    private List<Screenshot> screenshotsList = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // notesGrid.setPadding(new Insets(,10,10,10));
        notesGrid.setVgap(10);
        notesGrid.setHgap(20);
        fetchScreenshots();
        int column = 0;
        int row = 1;
        try {
            for (int i = 0; i < screenshotsList.size(); i++) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/fxml/my_notes_item.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                MyNotesItemController itemController = fxmlLoader.getController();
                itemController.setNotesItem(screenshotsList.get(i));

                if (column == 3) {
                    column = 0;
                    row++;
                }

                notesGrid.add(anchorPane, column++, row); //(child,column,row)
                //set grid width
                notesGrid.setMinWidth(Region.USE_COMPUTED_SIZE);
                notesGrid.setPrefWidth(Region.USE_COMPUTED_SIZE);
                notesGrid.setMaxWidth(Region.USE_PREF_SIZE);

                //set grid height
                notesGrid.setMinHeight(Region.USE_COMPUTED_SIZE);
                notesGrid.setPrefHeight(Region.USE_COMPUTED_SIZE);
                notesGrid.setMaxHeight(Region.USE_PREF_SIZE);

                GridPane.setMargin(anchorPane, new Insets(10));
            }
        } catch ( IOException e) {
            e.printStackTrace();
        }
    }
    private void fetchScreenshots(){

        File screenshotDirectory = new File(CodeDocsClient.screenshotDirectory);
        String contents[] = screenshotDirectory.list();

        if(contents != null){
            for(String fileName : contents){
                if(fileName.endsWith(".png") ){
                    Screenshot screenshot = new Screenshot();
                    String[] parts = fileName.split("-", 2);
                    screenshot.setFileName(fileName.substring(0,fileName.length()-4));
                    screenshot.setTitle(parts[0]);
                    screenshotsList.add(screenshot);
                }
            }
        }

    }

}
