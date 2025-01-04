package org.example.easyjapanese;

import javafx.animation.FadeTransition;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class InterfaceHandler {
    public static void autoResizeText(Text text, int maxLength, int standardFontSize) {
        if (text.getText().length() <= maxLength) {
            text.setFont(new javafx.scene.text.Font(standardFontSize));
            return;
        }

        double fontSize = (double) (maxLength * standardFontSize) / text.getText().length();

        text.setFont(new javafx.scene.text.Font(fontSize));
    }

    public static void effectTransition(Pane container, Text text, String newText,
                                        int maxLength, int standardFontSize) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), container);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.25);

        fadeOut.setOnFinished(event -> {
            text.setText(newText);
            InterfaceHandler.autoResizeText(text, maxLength, standardFontSize);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(500), container);
            fadeIn.setFromValue(0.25);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });

        fadeOut.play();
    }
}
