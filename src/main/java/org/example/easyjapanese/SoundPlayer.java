package org.example.easyjapanese;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class SoundPlayer {
    private MediaPlayer mediaPlayer;
    private String soundFileName;
    private boolean playRepeatedly;
    private boolean stopPlayingSound;

    public SoundPlayer() {
        //To do
    }

    public SoundPlayer(String soundFileName, boolean playRepeatedly, boolean stopPlayingSound) {
        Media media = new Media(new File(soundFileName).toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        this.soundFileName = soundFileName;
        this.playRepeatedly = playRepeatedly;
        this.stopPlayingSound = stopPlayingSound;
    }

    public String getSoundFileName() {
        return soundFileName;
    }

    public void setSoundFileName(String soundFileName) {
        this.soundFileName = soundFileName;
    }

    public boolean getPlayRepeatedly() {
        return playRepeatedly;
    }

    public void setPlayRepeatedly(boolean playRepeatedly) {
        this.playRepeatedly = playRepeatedly;
    }

    public boolean getStopPlayingSound() {
        return stopPlayingSound;
    }

    public void setStopPlayingSound(boolean stopPlayingSound) {
        this.stopPlayingSound = stopPlayingSound;
    }

    public void playSound() {
        new Thread(() -> {
            mediaPlayer.stop();

            mediaPlayer.setOnEndOfMedia(() -> {
                if (playRepeatedly) {
                    mediaPlayer.seek(mediaPlayer.getStartTime());
                    mediaPlayer.play();
                }
            });

            mediaPlayer.play();

            while (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                if (stopPlayingSound) {
                    mediaPlayer.stop();
                    break;
                }

                try {
                    Thread.sleep(100); // Wait for 100 millis till the next check
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
        }).start();
    }
}
