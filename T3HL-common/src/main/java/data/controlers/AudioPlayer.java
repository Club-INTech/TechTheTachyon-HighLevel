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

    private AudioInputStream loadFile(String name) throws IOException, UnsupportedAudioFileException {
        File file = new File(audioNames.getProperty(name));

        return AudioSystem.getAudioInputStream(file);
    }

    public void play(String name) {
        Thread thread = new Thread(() -> {
            System.out.println("Audio Thread Running ("+name+")");
/*
            try {
                AudioInputStream audioInputStream = loadFile(name);
                Clip clip = (Clip)AudioSystem.getLine(new Line.Info(Clip.class));
                clip.open(audioInputStream);


                synchronized(clip){
                    clip.start();
                    Thread.sleep(1);
                    clip.drain();
                    try{
                        double clipLength = audioInputStream.getFrameLength() /
                                audioInputStream.getFormat().getFrameRate();
                        clip.wait(Math.round(clipLength +.5)*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    clip.stop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Audio Thread Finished");*/
            try {
                Runtime.getRuntime().exec(new String[]{"mplayer", audioNames.getProperty(name)}).waitFor();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.setName("Audio Thread ("+name+")");

        thread.start();
    }

    @Override
    public void updateConfig(Config config) {

    }
}
