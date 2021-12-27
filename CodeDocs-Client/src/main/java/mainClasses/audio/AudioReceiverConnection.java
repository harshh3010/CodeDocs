package mainClasses.audio;

import mainClasses.editor.EditorConnection;

import javax.sound.sampled.*;
import java.io.*;
import java.net.Socket;

/**
 * AudioReceiverConnection handles the audio received from a user on a separate thread,
 * audio is received as bytes from a DataInputStream and these bytes are written to speakers
 * to generate output
 */
public class AudioReceiverConnection extends Thread {

    private volatile boolean isActive = true; // Is the connection still active

    private final Socket connection;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;
    private final EditorConnection editorConnection; // Reference to current editor connection

    public AudioReceiverConnection(Socket connection, EditorConnection editorConnection) throws IOException {
        this.editorConnection = editorConnection;

        this.connection = connection;
        this.outputStream = new DataOutputStream(connection.getOutputStream());
        this.inputStream = new DataInputStream(connection.getInputStream());
    }

    @Override
    public void run() {

        AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);

        try {
            SourceDataLine speakers;

            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
            speakers = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            speakers.open(format);
            speakers.start();

            int CHUNK_SIZE = 1024;
            byte[] data = new byte[CHUNK_SIZE];
            int numBytesRead;

            // Read data from input stream and write to the speakers
            try {
                while (isActive) {
                    numBytesRead = inputStream.read(data, 0, CHUNK_SIZE);
                    if (numBytesRead > 0)
                        speakers.write(data, 0, numBytesRead);
                }
            } catch (IOException e) {
                System.out.println("Audio receiving line disconnected!");
            } finally {
                speakers.stop();
                speakers.close();

                inputStream.close();
                outputStream.close();
                connection.close();
            }

        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        isActive = false;
    }
}
