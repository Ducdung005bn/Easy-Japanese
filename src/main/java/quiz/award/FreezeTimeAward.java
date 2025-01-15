package quiz.award;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.example.easyjapanese.Quiz;
import quiz.monster.Monster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FreezeTimeAward extends Award {
    private Map<Monster, ImageView> freezeImageViews = new HashMap<>();
    private Timeline unfreezeShotMonstersTimeline;
    private Timeline unfreezeAllMonstersTimeline;

    private Timeline quizTimeline;
    private List<Monster> monsterList;
    private List<Monster> frozenMonsterList;

    public FreezeTimeAward(Quiz quiz, Timeline quizTimeline, List<Monster> monsterList) {
        super(quiz, "freezeTime");
        this.quizTimeline = quizTimeline;
        this.monsterList = monsterList;
        this.frozenMonsterList = new ArrayList<>();
    }

    @Override
    public void activateAward() {
        super.activateAward();

        if (unfreezeAllMonstersTimeline != null && unfreezeAllMonstersTimeline.getStatus() == Timeline.Status.RUNNING) {
            unfreezeAllMonstersTimeline.setOnFinished(e -> {
                activateAward();
            });

            return;
        }

        //Freeze monsters
        quizTimeline.pause();

        for (Monster monster : monsterList) {
            freeze(monster);
            frozenMonsterList.add(monster);
        }

        //Unfreeze monsters
        unfreezeShotMonstersTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0.1), e -> {
                    for (int i = frozenMonsterList.size() - 1; i >= 0; i--) {
                        Monster monster = frozenMonsterList.get(i);
                        if (!monsterList.contains(monster)) {
                            unfreeze(monster);
                            frozenMonsterList.remove(monster);
                        }
                    }
                })
        );

        unfreezeShotMonstersTimeline.setCycleCount(Timeline.INDEFINITE);
        unfreezeShotMonstersTimeline.play();

        unfreezeAllMonstersTimeline = new Timeline(
                new KeyFrame(Duration.seconds(awardActivationTime), e -> {
                    quizTimeline.play();

                    for (Monster monster : frozenMonsterList) {
                        unfreeze(monster);
                    }

                    freezeImageViews.clear();
                    unfreezeShotMonstersTimeline.stop();
                })
        );

        unfreezeAllMonstersTimeline.setCycleCount(1);
        unfreezeAllMonstersTimeline.play();
    }

    private void freeze(Monster monster) {
        monster.pauseMonsterTimeline();

        ImageView freezeImageView = new ImageView(String.valueOf(getClass().getResource("/pictureContainer/freezing.gif")));
        freezeImageView.setFitWidth((double) 601 / 3);
        freezeImageView.setFitHeight((double) 346 / 3);

        if (!monster.getIsFromLeft()) {
            freezeImageView.setScaleX(-1);
            freezeImageView.setLayoutX(monster.getMonsterGroup().getLayoutX() - freezeImageView.getFitWidth() + 100);
        } else {
            freezeImageView.setLayoutX(monster.getMonsterGroup().getLayoutX() + monster.getMonsterGroup().getBoundsInLocal().getWidth() - 100);
        }
        freezeImageView.setLayoutY(monster.getMonsterGroup().getLayoutY() - 20);

        battleContainer.getChildren().add(freezeImageView);
        freezeImageViews.put(monster, freezeImageView);

        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(awardActivationTime), freezeImageView);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0);
        fadeTransition.play();
    }

    private void unfreeze(Monster monster) {
        ImageView freezeImageView = freezeImageViews.get(monster);
        if (freezeImageView != null) {
            battleContainer.getChildren().remove(freezeImageView);
        }

        for (Timeline timeline : monster.getMonsterTimeline()) {
            if (timeline.getStatus() != Timeline.Status.PAUSED) {
                return;
            }
        }
        monster.playMonsterTimeline();
    }
}
