package org.example.easyjapanese;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Collections;

public class FillUsingMeaningsController extends QuizController {
    private int currentQuizIndex;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private BorderPane quizContainer;

    @FXML
    private Text progressText;

    @FXML
    private Slider volumeSlider;

    @FXML
    private Text questionText;

    @FXML
    private TextField answerTextField;

    @FXML
    private Button submitButton;

    @FXML
    public void initialize() {
        setQuizFunction();

        if (vocabularyList != null && !vocabularyList.isEmpty()) {
            currentQuizIndex = 0;

            questionText.setText(vocabularyList.getFirst().getMeaning());
            InterfaceHandler.autoResizeText(questionText, 50, 48);

            updateProgess();
        }

        submitButton.setOnAction(event -> submitButtonHandler());

        if (quizFunctionController.getSoundName() != null) {
            volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                getSoundPlayer().setVolume(newValue.doubleValue() / 100);
            });
        }
    }

    private void setQuizFunction() {
        if (quizFunctionController.getReverseOrder()) {
            Collections.reverse(vocabularyList);
        }

        if (quizFunctionController.getSoundFilePath() != null) {
            setSoundPlayer(new SoundPlayer(
                    quizFunctionController.getSoundFilePath(), true, false));
            getSoundPlayer().playSound();

            if (quizFunctionController.getSoundName() != null) {
                volumeSlider.setValue(getSoundPlayer().getVolume() * 100);
            }
        }
    }

    private void submitButtonHandler() {
        String answer = answerTextField.getText();
        String correctAnswer = null;

        String kanji = vocabularyList.get(currentQuizIndex).getVocabulary();
        String hiragana = vocabularyList.get(currentQuizIndex).getHiragana();

        if (!kanji.equals(hiragana)) {
            correctAnswer = kanji + "/" + hiragana;
        } else {
            correctAnswer = hiragana;
        }

        if (answer.equals(vocabularyList.get(currentQuizIndex).getVocabulary())
                || answer.equals(vocabularyList.get(currentQuizIndex).getHiragana())) {
            showFeedback("Correct", true);
        } else {
            showFeedback("Incorrect. Correct answer: " + correctAnswer, false);
        }
    }

    private void showFeedback(String feedbackText, boolean isCorrect) {
        Text feedback = new Text(feedbackText);
        feedback.setStyle("-fx-font-size: 24; -fx-fill: " + (isCorrect ? "green" : "red") + "; -fx-opacity: 0;");
        feedback.setLayoutX(10);
        feedback.setLayoutY(425);

        quizContainer.getChildren().add(feedback);

        // Animate the feedback
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), feedback);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(5000), feedback);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setDelay(Duration.millis(500));

        fadeIn.play();
        fadeOut.play();

        fadeOut.setOnFinished(event -> {
            quizContainer.getChildren().remove(feedback);
            moveToNextQuiz();
        });
    }

    private void moveToNextQuiz() {
        if (currentQuizIndex < vocabularyList.size() - 1) {
            currentQuizIndex++;
            answerTextField.setText("");

            updateProgess();

            InterfaceHandler.effectTransition(quizContainer,
                    questionText,
                    vocabularyList.get(currentQuizIndex).getMeaning(),
                    50,
                    48);
        } else {
            // Handle end of quiz here
        }
    }

    private void updateProgess() {
        double progress = (double) (currentQuizIndex + 1) / vocabularyList.size();
        progressBar.setProgress(progress);

        progressText.setText("   " + String.valueOf(currentQuizIndex + 1) + "/" + vocabularyList.size());
    }
}
