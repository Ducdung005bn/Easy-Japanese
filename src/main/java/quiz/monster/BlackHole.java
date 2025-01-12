package quiz.monster;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import org.apache.commons.lang3.mutable.MutableInt;
import org.example.easyjapanese.Quiz;
import quiz.award.Award;
import utils.InterfaceHandler;

import java.util.List;

public class BlackHole extends Monster {
    private Quiz quiz;
    private List<Monster> monsterList;
    private List<Award> awardList;
    private List<Quiz> quizList;
    private MutableInt currentQuizIndex;

    private boolean isFromLeft;

    public BlackHole(Quiz quiz, List<Monster> monsterList, List<Award> awardList, List<Quiz> quizList, MutableInt currentQuizIndex) {
        super (283, 283, 1, 1, "black-hole.gif", quiz);
        this.quiz = quiz;
        this.monsterList = monsterList;
        this.awardList = awardList;
        this.quizList = quizList;
        this.currentQuizIndex = currentQuizIndex;

        isFromLeft = random.nextBoolean();

        monsterGroup.setLayoutX(isFromLeft ? 0 : 1061 - (int) monsterFrameWidth);
        monsterGroup.setLayoutY(random.nextInt(628 - (int) monsterFrameHeight));

        updateMonsterTimeline();
    }

    @Override
    void updateMonsterTimeline() {
        Timeline blackHoleTimeline = new Timeline(
                new KeyFrame(Duration.seconds(5), e -> {
                    generateMonster();
                    attractAwards();
                })
        );
        blackHoleTimeline.setCycleCount(Timeline.INDEFINITE);
        blackHoleTimeline.play();

        monsterTimeline.add(blackHoleTimeline);
    }

    private void generateMonster() {
        if (currentQuizIndex.getValue() < quizList.size()) {
            int monsterType;

            double startingX = monsterGroup.getLayoutX() + monsterGroup.getBoundsInParent().getWidth() / 2 - (double) 820 / 12;
            double startingY = monsterGroup.getLayoutY() + monsterGroup.getBoundsInParent().getHeight() / 2 - (double) 250 / 4;

            //Handle exception
            do {
                monsterType = random.nextInt(2);
            } while (!((monsterType == 0)
                    || (monsterType == 1 && currentQuizIndex.getValue() < quizList.size() - 1)));

            switch (monsterType) {
                case 0: {
                    BasicMonster basicMonster = new BasicMonster(
                            quizList.get(currentQuizIndex.getValue()), random.nextBoolean(), isFromLeft, startingX, startingY);

                    monsterList.add(basicMonster);
                    InterfaceHandler.timelineList.addAll(basicMonster.getMonsterTimeline());

                    currentQuizIndex.setValue(currentQuizIndex.getValue() + 1);
                    break;
                }
                case 1: {
                    ShieldedMonster shieldedMonster = new ShieldedMonster(
                            quizList.get(currentQuizIndex.getValue()), quizList.get(currentQuizIndex.getValue() + 1), random.nextBoolean(), isFromLeft, startingX, startingY);

                    monsterList.add(shieldedMonster);
                    InterfaceHandler.timelineList.addAll(shieldedMonster.getMonsterTimeline());

                    currentQuizIndex.setValue(currentQuizIndex.getValue() + 2);
                    break;
                }
            }
        } else {
            stopMonsterTimeline();
        }
    }

    private void attractAwards() {
    }

    @Override
    void updateMonsterMovement() {
        //No movement
    }

    @Override
    public void beShot() {

    }
}
