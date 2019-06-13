package data.controlers;

import pfg.config.Config;
import utils.container.Service;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class AudioPlayer implements Service {

    private AudioInputStream audioInputStream;
    private AudioFormat audioFormat;
    private SourceDataLine audioLine;

    private Properties audioNames;

    public AudioPlayer(){
        audioNames = new Properties();

        FileInputStream input = null;
        try {
            input = new FileInputStream("../config/audioFiles.config");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            audioNames.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFile(String name) {
        File file = new File(audioNames.getProperty(name));

        try{
            audioInputStream = AudioSystem.getAudioInputStream(file);

        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
            return;
        }

        audioFormat = audioInputStream.getFormat();

        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);

        try {
            audioLine = (SourceDataLine) AudioSystem.getLine(info);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void play(String name) {
        loadFile(name);
        Thread thread = new Thread(() -> {
            System.out.println("Audio Thread Running ("+name+")");
            try {
                audioLine.open(audioFormat);
            } catch (LineUnavailableException e) {
                e.printStackTrace();
                return;
            }

            audioLine.start();

            try {
                byte[] bytes = new byte[1024];
                int bytesRead;
                while (((bytesRead = audioInputStream.read(bytes, 0, bytes.length)) != -1)) {
                    audioLine.write(bytes, 0, bytesRead);
                }
            } catch (IOException io) {
                io.printStackTrace();
                return;
            }

            audioLine.close();
            System.out.println("Audio Thread Finished");
        });
        thread.setName("Audio Thread ("+name+")");

        thread.start();
    }

    @Override
    public void updateConfig(Config config) {

    }
}
