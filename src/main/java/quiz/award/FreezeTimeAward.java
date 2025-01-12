package quiz.award;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.example.easyjapanese.Quiz;
import quiz.monster.Monster;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FreezeTimeAward extends Award {
    private Map<Monster, ImageView> freezeImageViews = new HashMap<>();
    private Timeline freezeTimeTimeline;

    private Timeline quizTimeline;
    private List<Monster> monsterList;

    public FreezeTimeAward(Quiz quiz, Timeline quizTimeline, List<Monster> monsterList) {
        super(quiz, "freezeTime");
        this.quizTimeline = quizTimeline;
        this.monsterList = monsterList;
    }

    @Override
    public void activateAward() {
        super.activateAward();

        if (freezeTimeTimeline != null && freezeTimeTimeline.getStatus() == Timeline.Status.RUNNING) {
            freezeTimeTimeline.setOnFinished(e -> {
                activateAward();
            });

            return;
        }

        freezeTimeTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> {
                    quizTimeline.pause();

                    for (Monster monster : monsterList) {
                        monster.beFrozen(freezeImageViews, awardActivationTime);
                    }
                }),
                new KeyFrame(Duration.seconds(awardActivationTime), e -> {
                    quizTimeline.play();

                    for (Monster monster : monsterList) {
                        monster.unfreeze(freezeImageViews);
                    }

                    freezeImageViews.clear();
                })
        );

        freezeTimeTimeline.setCycleCount(1);
        freezeTimeTimeline.play();
    }
}
