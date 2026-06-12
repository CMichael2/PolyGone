package Project;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.File;

public class MusicSoundEffectsController {

    public static Clip backgroundMusicClip;
    private static String currentPlayingPath = "";
    public static double masterVolume = 1.0;
    public static double UXVolume = 1.0;
    public static double musicVolume = 0.05;

    public static void updateMusic(PolyGone game) {
        if (game.getCurrentState() == GameState.MAIN_MENU) {
            playBackgroundMusic("Assets/menu_theme.wav");
        }
        else if (game.getCurrentState() == GameState.PLAYING) {
            playBackgroundMusic("Assets/playing_theme.wav");
        }
        else if (game.getCurrentState() == GameState.PAUSED) {
            if (backgroundMusicClip != null && backgroundMusicClip.isRunning()) {
                backgroundMusicClip.stop();
            }
        }
        else if (game.getCurrentState() == GameState.DEATH_SCREEN) {
            stopBackgroundMusic("");
            playDeathSound();
        }
    }

    public static void playBackgroundMusic(String filePath) {
        //prevents track from replaying if already playing
        if (filePath.equals(currentPlayingPath) && backgroundMusicClip != null && backgroundMusicClip.isRunning()) return;

        //checks if music is playing to avoid overlays
        stopBackgroundMusic(filePath);

        try {
            File musicFile = new File(filePath);
            if (musicFile.exists()) {
                backgroundMusicClip = AudioSystem.getClip();
                backgroundMusicClip.open(AudioSystem.getAudioInputStream(musicFile));

                setClipVolume(backgroundMusicClip, (float) (musicVolume * masterVolume));
                backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
                backgroundMusicClip.start();
                currentPlayingPath = filePath;
                System.out.println("Started playing: " + filePath);
            } else {
                System.out.println("C418 Music file not found: " + filePath);
            }
        } catch (Exception e) {
            System.out.println("Error playing background music: " + e.getMessage());
        }
    }

    public static void stopBackgroundMusic(String filePath) {
        if (backgroundMusicClip != null) {
            try {
                if (backgroundMusicClip.isRunning()) {
                    backgroundMusicClip.stop();
                }
                backgroundMusicClip.close();
            } catch (Exception e) {
                System.out.println("Error closing clip: " + e.getMessage());
            }

            System.out.println("Stopped playing: " + currentPlayingPath);

            backgroundMusicClip = null;
            currentPlayingPath = "";
        }
    }

    private static void playDeathSound() {
        try {
            File soundFile = new File("Assets/player_died.wav");
            if (soundFile.exists()) {
                Clip clip = AudioSystem.getClip();
                clip.open(AudioSystem.getAudioInputStream(soundFile));

                setClipVolume(clip, (float) (UXVolume * masterVolume));
                clip.start();
            }
        } catch (Exception e) {
            System.out.println("Error playing death sound: " + e.getMessage());
        }
    }

    public static void playHoverSound() {
        try {
            File soundFile = new File("Assets/hover.wav");
            if (soundFile.exists()) {
                Clip clip = AudioSystem.getClip();
                clip.open(AudioSystem.getAudioInputStream(soundFile));

                setClipVolume(clip, (float) (UXVolume * masterVolume));
                clip.start();
            }
        } catch (Exception e) {
            System.out.println("Error playing hover sound: " + e.getMessage());
        }
    }

    public static void playClickSound() {
        try {
            File soundFile = new File("Assets/click.wav");
            if (soundFile.exists()) {
                Clip clip = AudioSystem.getClip();
                clip.open(AudioSystem.getAudioInputStream(soundFile));

                setClipVolume(clip, (float) (UXVolume * masterVolume));
                clip.start();
            }
        } catch (Exception e) {
            System.out.println("Error playing click sound: " + e.getMessage());
        }
    }

    public static void updateRunningMusicVolume() {
        if (backgroundMusicClip != null && backgroundMusicClip.isOpen()) {
            setClipVolume(backgroundMusicClip, (float) (musicVolume * masterVolume));
        }
    }

    public static void setClipVolume(Clip clip, float volume) {
        if (clip == null) return;

        try {
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

                if (volume < 0.0f) volume = 0.0f;
                if (volume > 1.0f) volume = 1.0f;

                //convert to db from linear scale
                float dB = (float) (Math.log(volume == 0 ? 0.0001 : volume) / Math.log(10.0) * 20.0);
                gainControl.setValue(dB);
            }
        } catch (Exception e) {
            System.out.println("Error adjusting volume: " + e.getMessage());
        }
    }
}
