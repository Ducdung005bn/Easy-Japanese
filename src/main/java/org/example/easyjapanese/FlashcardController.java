package org.example.easyjapanese;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

import java.util.List;

public class FlashcardController {
    public static List<Vocabulary> vocabularyList;
    private int currentCardIndex;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private BorderPane flashcardContainer;

    @FXML
    private ImageView audioIcon;

    @FXML
    private Text progessText;

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
        if (vocabularyList != null && !vocabularyList.isEmpty()) {
            currentCardIndex = 0;

            flashcardText.setText(vocabularyList.get(currentCardIndex).getVocabulary());
            autoResizeText(flashcardText, flashcardContainer);

            updateProgess();
        }

        setEventHandlers();
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

        if (currentText.equals(vocabularyList.get(currentCardIndex).getVocabulary())) {
            String wordMeaning = vocabularyList.get(currentCardIndex).getMeaning();
            flashcardText.setText(wordMeaning);

            EnglishTextToSpeech.getInstance().speakEnglishWords(wordMeaning);
        } else {
            flashcardText.setText(vocabularyList.get(currentCardIndex).getVocabulary());
        }

        autoResizeText(flashcardText, flashcardContainer);
    }

    private void nextCard() {
        if (currentCardIndex < vocabularyList.size() - 1) {
            currentCardIndex++;
            flashcardText.setText(vocabularyList.get(currentCardIndex).getVocabulary());
            autoResizeText(flashcardText, flashcardContainer);

            updateProgess();
        }
    }

    private void previousCard() {
        if (currentCardIndex > 0) {
            currentCardIndex--;
            flashcardText.setText(vocabularyList.get(currentCardIndex).getVocabulary());
            autoResizeText(flashcardText, flashcardContainer);

            updateProgess();
        }
    }

    @FXML
    private void handleAudioIconClick(MouseEvent mouseEvent) {
        //To do
    }
}
