package quiz;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import utils.InterfaceHandler;
import utils.PixabayImageSearcher;
import utils.SoundPlayer;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class FillInTheBlankController extends QuizController {
    //QuizController stores shared functions, while FillInTheBlankController stores specific functions
    public static FillInTheBlankFunctionController fillInTheBlankFunctionController;

    private int currentQuizIndex;
    private boolean isShowingFeedback = false; //To handle no pressing submit button

    Timeline timeline;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private BorderPane quizContainer;

    @FXML
    private Text progressText;

    @FXML
    private ProgressBar timeBar;

    @FXML
    private Slider volumeSlider;

    @FXML
    private AnchorPane questionContainer;

    @FXML
    private TextField answerTextField;

    @FXML
    private Button submitButton;

    @FXML
    private Pane feedbackContainer;

    @FXML
    private Text feedbackText;

    @FXML
    public void initialize() {
        setQuizFunction();

        if (quizList != null && !quizList.isEmpty()) {
            currentQuizIndex = 0;

            feedbackText.setText("");
            feedbackContainer.setStyle("-fx-background-color: #e6e6e6;");

            showQuestion();

            updateProgess();
            updateTimeBar();
        }

        submitButton.setOnAction(event -> submitButtonHandler());

        if (quizFunctionController.getSoundName() != null) {
            volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                getSoundPlayer().setVolume(newValue.doubleValue() / 100);
            });
        }
    }

    private void setQuizFunction() {
        if (quizFunctionController.getShuffleOrder()) {
            Collections.shuffle(quizList);
        }

        if (quizFunctionController.getSoundFilePath() != null) {
            setSoundPlayer(new SoundPlayer(
                    quizFunctionController.getSoundFilePath(), true, false));
            getSoundPlayer().playSound();

            if (quizFunctionController.getSoundName() != null) {
                volumeSlider.setValue(getSoundPlayer().getVolume() * 100);
            }

            InterfaceHandler.soundPlayerList.add(getSoundPlayer());
        }
    }

    private void submitButtonHandler() {
        if (isShowingFeedback)
            return;

        timeline.stop();

        String answer = answerTextField.getText();
        String correctAnswer = null;

        String firstAnswer = quizList.get(currentQuizIndex).getAnswerFirstChoice();
        String secondAnswer = quizList.get(currentQuizIndex).getAnswerSecondChoice();

        if (!firstAnswer.equals(secondAnswer) && secondAnswer != null) {
            correctAnswer = firstAnswer + " / " + secondAnswer;
        } else {
            correctAnswer = firstAnswer;
        }

        if (quizList.get(currentQuizIndex).isCorrectAnswer(answer)) {
            showFeedback("Correct", true);
        } else {
            showFeedback("Incorrect. Correct answer: " + correctAnswer, false);
        }
    }

    private void showFeedback(String feedback, boolean isCorrect) {
        feedbackText.setText(feedback);
        feedbackContainer.setStyle("-fx-background-color: " + (isCorrect ? "green" : "red") + ";");
        isShowingFeedback = true;

        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), feedbackText);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), feedbackText);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        int timeDelay = 1500;
        if (!isCorrect) {
            timeDelay = fillInTheBlankFunctionController.getTimeDelay()*1000;
        }
        fadeOut.setDelay(Duration.millis(timeDelay));

        fadeIn.play();
        fadeOut.play();

        fadeOut.setOnFinished(event -> {
            feedbackContainer.setStyle("-fx-background-color: #e6e6e6;");
            isShowingFeedback = false;
            moveToNextQuiz();
        });
    }

    private void moveToNextQuiz() {
        if (currentQuizIndex < quizList.size() - 1) {
            currentQuizIndex++;
            answerTextField.setText("");

            showQuestion();

            updateProgess();
            updateTimeBar();
        }
        else {
            // Handle end of quiz here
        }
    }

    private void showQuestion() {
        if (fillInTheBlankFunctionController.getShowImages()) {
            showImage();
        }

        if (fillInTheBlankFunctionController.getShowMeanings()) {
            showMeaning();
        }
    }

    private void showImage() {
        List<String> imageUrlList = null;

        FutureTask<List<String>> futureTask = new FutureTask<>(() ->
                PixabayImageSearcher.getImage(quizList.get(currentQuizIndex).getQuestion()));

        new Thread(futureTask).start();

        try {
            imageUrlList = futureTask.get();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        String firstImageUrl = null;
        String secondImageUrl = null;

        try {
            firstImageUrl = imageUrlList.get(new Random().nextInt(imageUrlList.size()));
            do {
                secondImageUrl = imageUrlList.get(new Random().nextInt(imageUrlList.size()));
            } while (secondImageUrl.equals(firstImageUrl));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        double questionContainerWidth = 543.3333333333334; //questionContainer.getWidth()
        double questionContainerHeight = 175.33333333333334; //questionContainer.getHeight()

        if (firstImageUrl != null && secondImageUrl != null) {
            ImageView firstImageView = new ImageView(new Image(firstImageUrl));
            ImageView secondImageView = new ImageView(new Image(secondImageUrl));

            //change the ratio to fit the container if necessary
            firstImageView.setPreserveRatio(false);
            secondImageView.setPreserveRatio(false);

            firstImageView.setFitWidth(questionContainerWidth / 2);
            secondImageView.setFitWidth(questionContainerWidth / 2);

            firstImageView.setFitHeight(questionContainerHeight);
            secondImageView.setFitHeight(questionContainerHeight);

            SplitPane splitPane = new SplitPane();
            splitPane.getItems().addAll(firstImageView, secondImageView);

            questionContainer.getChildren().clear();
            questionContainer.getChildren().setAll(splitPane);
        }
    }

    private void showMeaning() {
        Text questionText = new Text();

        questionText.setText(quizList.get(currentQuizIndex).getOtherInformation());
        questionText.setFill(Color.RED);
        questionText.setFont(new Font(48.0));

        questionText.setLayoutX(25.0);
        questionText.setLayoutY(100.0);

        questionText.setStrokeType(javafx.scene.shape.StrokeType.OUTSIDE);
        questionText.setStrokeWidth(0.0);

        questionText.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        questionText.setWrappingWidth(493.21356201171875);

        InterfaceHandler.autoResizeText(questionText, 20, 48);

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.GREEN);
        shadow.setRadius(30);
        shadow.setOffsetX(2);
        shadow.setOffsetY(2);
        shadow.setSpread(0.25);

        questionText.setEffect(shadow);

        if (!fillInTheBlankFunctionController.getShowImages()) {
            questionContainer.getChildren().clear();
        }

        questionContainer.getChildren().add(questionText);
    }

    private void updateProgess() {
        double progress = (double) (currentQuizIndex + 1) / quizList.size();
        progressBar.setProgress(progress);

        progressText.setText("   " + String.valueOf(currentQuizIndex + 1) + "/" + quizList.size());
    }

    private void updateTimeBar() {
        timeBar.setProgress(1);
        int timeLimit = fillInTheBlankFunctionController.getTimeLimit();

        timeline = new Timeline(
                new KeyFrame(Duration.millis(100.0), event -> {
                    double progress = timeBar.getProgress() - (1.0 / timeLimit) * (100.0 / 1000.0);
                    if (progress <= 0) {
                        submitButtonHandler();
                    }
                    timeBar.setProgress(progress);
                })
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

}
