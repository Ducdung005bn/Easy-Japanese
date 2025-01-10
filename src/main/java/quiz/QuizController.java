package quiz;

import org.example.easyjapanese.Quiz;
import org.example.easyjapanese.Vocabulary;
import utils.SoundPlayer;

import java.util.List;

public class QuizController {
    public static List<Quiz> quizList;
    public static QuizFunctionController quizFunctionController;

    private SoundPlayer soundPlayer;

    public SoundPlayer getSoundPlayer() {
        return soundPlayer;
    }

    public void setSoundPlayer(SoundPlayer soundPlayer) {
        this.soundPlayer = soundPlayer;
    }
}
