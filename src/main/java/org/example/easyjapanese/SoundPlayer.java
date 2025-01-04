package org.example.easyjapanese;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class SoundPlayer {
    public static final String folderSoundPath = "C:\\Users\\Admin\\Documents\\EasyJapanese\\src\\main\\resources\\soundContainer\\when learning";
    private MediaPlayer mediaPlayer;
    private boolean playRepeatedly;
    private boolean stopPlayingSound;

    public SoundPlayer() {
        //To do
    }

    public SoundPlayer(String soundFilePath, boolean playRepeatedly, boolean stopPlayingSound) {
        Media media = new Media(new File(soundFilePath).toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        this.playRepeatedly = playRepeatedly;
        this.stopPlayingSound = stopPlayingSound;
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

    public double getVolume() {
        return mediaPlayer.getVolume();
    }

    public void setVolume(double volume) {
        mediaPlayer.setVolume(volume);
    }

    public static void addValueToSoundChoiceBox(ChoiceBox<String> soundChoiceBox) {
        ObservableList<String> soundNames = FXCollections.observableArrayList();
        File folder = new File(folderSoundPath);

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".mp3"));

            if (files != null) {
                for (File file : files) {
                    soundNames.add(file.getName().replace(".mp3", ""));
                }
            }
        }

        soundNames.add("No music");

        soundChoiceBox.setItems(soundNames);
        soundChoiceBox.setValue("No music");
    }

    public void playSound() {
        mediaPlayer.play();

        mediaPlayer.setOnEndOfMedia(() -> {
            if (playRepeatedly) {
                mediaPlayer.seek(mediaPlayer.getStartTime());
                mediaPlayer.play();
            }
        });

        //Only use a new thread when the status is changed to PLAYING
        mediaPlayer.statusProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == MediaPlayer.Status.PLAYING) {
                new Thread(() -> {
                    while (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                        if (stopPlayingSound) {
                            mediaPlayer.stop();
                            break;
                        }

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }).start();
            }
        });
    }

}
