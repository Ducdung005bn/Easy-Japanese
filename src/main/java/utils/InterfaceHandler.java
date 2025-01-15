package utils;

import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class InterfaceHandler {
    public static List<Object> objectList = new ArrayList<>();

    public static void removeRemainings() {
        for (Object object : objectList) {
            if (object instanceof SoundPlayer soundPlayer) {
                soundPlayer.setStopPlayingSound(true);
            } else if (object instanceof Timeline timeline) {
                timeline.stop();
            }
        }

        objectList.clear();

        System.gc();
    }

    public static void autoResizeText(Text text, int maxLength, int standardFontSize) {
        if (text.getText().length() <= maxLength) {
            text.setFont(new javafx.scene.text.Font(standardFontSize));
            return;
        }

        double fontSize = (double) (maxLength * standardFontSize) / text.getText().length();

        text.setFont(new javafx.scene.text.Font(fontSize));
    }

    public static <T> void updateFadeTransition(T UI, int seconds, double statingValue, double endingValue, Runnable onFinished) {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(seconds), (Node) UI);

        fadeTransition.setFromValue(statingValue);
        fadeTransition.setToValue(endingValue);

        fadeTransition.setOnFinished(event -> {
            fadeTransition.stop();
            if (onFinished != null) {
                onFinished.run();  //callback if necessary
            }
        });

        fadeTransition.play();
    }
}
