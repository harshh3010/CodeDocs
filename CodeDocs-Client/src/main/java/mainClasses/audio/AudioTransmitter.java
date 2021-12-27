package mainClasses.audio;

import mainClasses.editor.EditorConnection;
import models.Peer;

import javax.sound.sampled.*;
import java.io.IOException;

/**
 * AudioTransmitter runs on a separate thread, it reads the audio as bytes from microphone
 * and writes it to audio receiving servers of all the connected users
 */
public class AudioTransmitter extends Thread {

    private volatile boolean isActive = true; // Is transmitter still active

    private final EditorConnection editorConnection; // Reference to current editor connection

    public AudioTransmitter(EditorConnection editorConnection) {
        this.editorConnection = editorConnection;
    }

    @Override
    public void run() {

        System.out.println("Audio transmitter started!");

        AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);

        try {
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);

            int numBytesRead;
            int CHUNK_SIZE = 1024;
            byte[] data = new byte[microphone.getBufferSize() / 5];
            microphone.start();

            // While transmitter is active, write bytes from microphone to all the peers
            while (isActive) {
                numBytesRead = microphone.read(data, 0, CHUNK_SIZE);
                if (!editorConnection.isMute()) {
                    for (Peer peer : editorConnection.getConnectedPeers().values()) {
                        try {
                            peer.getAudioOutputStream().write(data, 0, numBytesRead);
                            peer.getAudioOutputStream().flush();
                        } catch (IOException e) {
                            System.out.println("Unable to transmit audio to a user.");
                        }
                    }
                }
            }

            microphone.stop();
            microphone.close();

        } catch (LineUnavailableException e) {
            System.out.println("Audio transmission shut down!");
        }

        System.out.println("Audio transmitter closed!");
    }

    public void stopTransmission() {
        isActive = false;
    }
}
