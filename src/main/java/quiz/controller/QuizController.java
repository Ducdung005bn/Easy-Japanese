package quiz.controller;

import javafx.fxml.FXML;

import org.example.easyjapanese.Quiz;
import quiz.function.controller.QuizFunctionController;
import utils.SoundPlayer;

import java.util.List;
import java.util.Random;

public abstract class QuizController {
    public static List<Quiz> quizList;
    public static QuizFunctionController quizFunctionController;

    final Random random = new Random();
    SoundPlayer soundPlayer;
    int currentQuizIndex;

    abstract void setQuizFunction();

    @FXML
    abstract void handleSubmitButton();

    @FXML
    abstract void handleSpaceKey();
}
