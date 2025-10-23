package kutuOyunu;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundPlayer {

    public void playSound(String soundFileName) {
        try {
            File soundFile = new File(soundFileName);
            if (!soundFile.exists()) {
                System.err.println("Sound file not found: " + soundFileName);
                return;
            }
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}
