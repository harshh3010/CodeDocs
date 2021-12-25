package services;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import mainClasses.CodeDocsClient;
import utilities.CodeEditor;
import utilities.Status;

import javax.imageio.ImageIO;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;
import java.util.function.Consumer;

public class ScreenshotService {

    private static String getDateTime() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd_hh:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("PST"));
        return df.format(new Date());
    }

    public static Status takeScreenshot(CodeEditor codeEditor) {

        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Take Notes");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();

        TextField titleTF = new TextField();
        titleTF.setPromptText("Enter title for your notes");

        TextArea notesTA = new TextArea();
        notesTA.setPromptText("Notes..");

        gridPane.add(new Label("Title"), 0, 0);
        gridPane.add(titleTF, 1, 0);

        gridPane.add(new Label("Description"), 0, 1);
        gridPane.add(notesTA, 1, 1);

        gridPane.setVgap(8);
        dialog.getDialogPane().setContent(gridPane);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new Pair<>(titleTF.getText(), notesTA.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        final String[] title = {""};
        final String[] notes = {""};
        final boolean[] isCancelled = {true};
        result.ifPresent(new Consumer<Pair<String, String>>() {
            @Override
            public void accept(Pair<String, String> stringStringPair) {
                title[0] = titleTF.getText().trim();
                notes[0] = notesTA.getText().trim();
                isCancelled[0] = false;
            }
        });

        if (isCancelled[0]) {
            return null;
        }

        if (title[0].isEmpty() || notes[0].isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Fields cannot be empty!");
            alert.show();
            return null;
        }
        return saveScreenshotNotes(title[0], notes[0], codeEditor);
    }

    private static Status saveScreenshotNotes(String title, String notes, CodeEditor codeEditor) {

        String fileName = title + "-" + UUID.randomUUID().toString();
        //TODO: take unique filename
        try {
            BufferedWriter notesWriter = new BufferedWriter(new FileWriter(CodeDocsClient.notesDirectory + fileName + ".txt"));
            notesWriter.write(notes);
            notesWriter.close();
            WritableImage screenshot = codeEditor.snapshot(null, null);
            File temp = new File(CodeDocsClient.screenshotDirectory + fileName + ".png");
            ImageIO.write(SwingFXUtils.fromFXImage(screenshot, null),
                    "png", temp);
            return Status.SUCCESS;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Status.FAILED;
    }
}
