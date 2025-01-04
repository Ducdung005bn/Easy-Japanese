package org.example.easyjapanese;

import java.util.List;

public class QuizController {
    public static List<Vocabulary> vocabularyList;
    public static QuizFunctionController quizFunctionController;

    private SoundPlayer soundPlayer;

    public SoundPlayer getSoundPlayer() {
        return soundPlayer;
    }

    public void setSoundPlayer(SoundPlayer soundPlayer) {
        this.soundPlayer = soundPlayer;
    }
}
