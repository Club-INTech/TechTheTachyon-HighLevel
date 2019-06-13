package data.controlers;

import kotlin.Triple;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.audiokode.Source;
import org.jglrxavpok.audiokode.ThreadedSoundEngine;
import org.jglrxavpok.audiokode.decoders.DirectWaveDecoder;
import org.jglrxavpok.audiokode.decoders.StreamingWaveDecoder;
import org.jglrxavpok.audiokode.filters.AudioFilter;
import org.jglrxavpok.audiokode.filters.AudioFilterKt;
import org.jglrxavpok.audiokode.finders.AudioFinder;
import org.jglrxavpok.audiokode.finders.AudioInfo;
import pfg.config.Config;
import utils.Log;
import utils.communication.CopyIOThread;
import utils.container.Service;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class AudioPlayer implements Service {

    private static final Triple<Float, Float, Float> ZERO = new Triple<>(0f,0f,0f);
    private final ThreadedSoundEngine engine;

    private Properties audioNames;

    public AudioPlayer() {
        audioNames = new Properties();

        engine = new ThreadedSoundEngine();
        engine.initWithDefaultOpenAL();
        engine.addFinder(new AudioFinder() {
            @NotNull
            @Override
            public AudioInfo findAudio(@NotNull String s) {
                return new AudioInfo(() -> {
                    try {
                        return new FileInputStream(s);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    return null;
                }, DirectWaveDecoder.INSTANCE, StreamingWaveDecoder.INSTANCE);
            }
        });

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
           /* try {
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
            }*/
            engine.quickplayMusic(audioNames.getProperty(name), false, AudioFilterKt.getNoFilter(), ZERO, ZERO, 1f, 1f);
            System.out.println("Audio Thread Finished");
/*            try {
                ProcessBuilder builder = new ProcessBuilder("mplayer", audioNames.getProperty(name));
                Process process = builder.start();
                Runtime.getRuntime().addShutdownHook(new Thread(process::destroyForcibly));
                new CopyIOThread(process, Log.STDOUT).start();
                process.waitFor();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }*/
        });
        thread.setName("Audio Thread ("+name+")");

        thread.start();
    }

    @Override
    public void updateConfig(Config config) {

    }
}
