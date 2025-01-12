package quiz.award;

import org.example.easyjapanese.Quiz;
import quiz.controller.MonsterHunterController;

public class BoostPowerAward extends Award {
    public BoostPowerAward(Quiz quiz) {
        super(quiz, "boostPower");
    }

    @Override
    public void activateAward() {
        super.activateAward();

        MonsterHunterController.power = Math.min(100, MonsterHunterController.power + 20);
    }
}
