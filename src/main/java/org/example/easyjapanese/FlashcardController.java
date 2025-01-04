package org.example.easyjapanese;

import javafx.animation.FadeTransition;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Collections;
import java.util.List;

public class FlashcardController {
    public static List<Vocabulary> vocabularyList;
    public static FlashcardFunctionController flashcardFunctionController;

    private int currentCardIndex;
    private SoundPlayer soundPlayer;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private BorderPane flashcardContainer;

    @FXML
    private ImageView audioIcon;

    @FXML
    private Text progessText;

    @FXML
    private Slider volumeSlider;

    @FXML
    private Text flashcardText;

    @FXML
    private Button previousButton;

    @FXML
    private Button flipButton;

    @FXML
    private Button nextButton;

    @FXML
    public void initialize() {
        setFlashcardFunction();

        if (vocabularyList != null && !vocabularyList.isEmpty()) {
            currentCardIndex = 0;

            flashcardText.setText(getVocabularyText());
            autoResizeText(flashcardText, flashcardContainer);

            updateProgess();
        }

        setEventHandlers();

        if (flashcardFunctionController.getSoundName() != null) {
            volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                soundPlayer.setVolume(newValue.doubleValue() / 100);
            });
        }
    }

    private void setFlashcardFunction() {
        if (flashcardFunctionController.getReverseOrder()) {
            Collections.reverse(vocabularyList);
        }

        if (flashcardFunctionController.getSpeakVocabulary()) {
            //Not handle yet
        }

        if (flashcardFunctionController.getSoundFilePath() != null) {
            soundPlayer = new SoundPlayer(
                    flashcardFunctionController.getSoundFilePath(), true, false);
            soundPlayer.playSound();

            if (flashcardFunctionController.getSoundName() != null) {
                volumeSlider.setValue(soundPlayer.getVolume() * 100);
            }
        }
    }

    private String getVocabularyText() {
        String vocabularyText = "";

        if (flashcardFunctionController.getShowKanji()) {
            vocabularyText += vocabularyList.get(currentCardIndex).getVocabulary();

            if (flashcardFunctionController.getShowHiragana()) {
                vocabularyText += " (" + vocabularyList.get(currentCardIndex).getHiragana() + ")";
            }
        } else {
            vocabularyText += vocabularyList.get(currentCardIndex).getHiragana();
        }

        if (vocabularyList.get(currentCardIndex).getVocabulary().equals(
                vocabularyList.get(currentCardIndex).getHiragana())) {
            vocabularyText = vocabularyList.get(currentCardIndex).getHiragana();
        }

        return vocabularyText;
    }

    private void updateProgess() {
        double progress = (double) (currentCardIndex + 1) / vocabularyList.size();
        progressBar.setProgress(progress);

        progessText.setText("   " + String.valueOf(currentCardIndex + 1) + "/" + vocabularyList.size());
    }

    private void autoResizeText(Text text, BorderPane flashcardContainer) {
        final int maxLength = 50;
        final int standardFontSize = 48;
        if (text.getText().length() <= maxLength) {
            text.setFont(new javafx.scene.text.Font(standardFontSize));
            return;
        }

        double fontSize = (double) (maxLength * standardFontSize) / text.getText().length();

        text.setFont(new javafx.scene.text.Font(fontSize));
    }

    private void setEventHandlers() {
        flipButton.setOnAction(event -> flipCard());
        nextButton.setOnAction(event -> nextCard());
        previousButton.setOnAction(event -> previousCard());
    }

    private void flipCard() {
        String currentText = flashcardText.getText();

        if (currentText.equals(getVocabularyText())) {
            String wordMeaning = vocabularyList.get(currentCardIndex).getMeaning();
            flashcardText.setText(wordMeaning);

            if (flashcardFunctionController.getSpeakMeaning()) {
                EnglishTextToSpeech.getInstance().speakEnglishWords(wordMeaning);
            }
        } else {
            flashcardText.setText(getVocabularyText());
        }

        autoResizeText(flashcardText, flashcardContainer);
    }

    private void nextCard() {
        if (currentCardIndex < vocabularyList.size() - 1) {
            currentCardIndex++;

            updateProgess();

            cardEffectTransition();
        }
    }

    private void previousCard() {
        if (currentCardIndex > 0) {
            currentCardIndex--;

            updateProgess();

            cardEffectTransition();
        }
    }

    private void cardEffectTransition() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), flashcardContainer);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.25);

        fadeOut.setOnFinished(event -> {
            flashcardText.setText(getVocabularyText());
            autoResizeText(flashcardText, flashcardContainer);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(500), flashcardContainer);
            fadeIn.setFromValue(0.25);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });

        fadeOut.play();
    }


    @FXML
    private void handleAudioIconClick(MouseEvent mouseEvent) {
        //Not handle yet
    }
}
