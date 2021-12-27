package utilities.configurations;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * This class is used for reading the file paths for notes as specified in config file
 */
public class FileConfigurations {

    private final String notesDirectory;
    private final String screenshotDirectory;

    public FileConfigurations() throws IOException {
        Properties properties = new Properties();
        FileReader fileReader = new FileReader("CodeDocs-Client/src/main/resources/configurations/file.properties");
        properties.load(fileReader);

        notesDirectory = properties.getProperty("NOTES_FILEPATH");
        screenshotDirectory = properties.getProperty("SCREENSHOT_FILEPATH");
    }

    public String getNotesDirectory() {
        return notesDirectory;
    }

    public String getScreenshotDirectory() {
        return screenshotDirectory;
    }
}
