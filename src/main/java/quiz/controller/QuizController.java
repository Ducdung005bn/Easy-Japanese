package quiz.controller;

import javafx.fxml.FXML;

import org.apache.commons.lang3.mutable.MutableInt;
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
    MutableInt currentQuizIndex = new MutableInt();

    abstract void setQuizFunction();

    @FXML
    abstract void handleSubmitButton();

    @FXML
    void handleSpaceKey() {
        handleSubmitButton();
    }
}
