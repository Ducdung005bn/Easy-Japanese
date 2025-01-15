package quiz.monster;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import org.apache.commons.lang3.mutable.MutableInt;
import org.example.easyjapanese.Quiz;
import quiz.controller.MonsterHunterController;
import utils.InterfaceHandler;

import java.util.List;

public class BlackHole extends Monster {
    private Quiz quiz;
    private List<Monster> monsterList;
    private List<Quiz> quizList;
    private MutableInt currentQuizIndex;

    private int timeBetweenTwoQuizzes;

    public BlackHole(Quiz quiz, List<Monster> monsterList, List<Quiz> quizList, MutableInt currentQuizIndex) {
        super (283, 283, 1, 1, "black-hole.gif", quiz);
        this.quiz = quiz;
        this.monsterList = monsterList;
        this.quizList = quizList;
        this.currentQuizIndex = currentQuizIndex;

        isFromLeft = random.nextBoolean();
        timeBetweenTwoQuizzes = 3;

        monsterGroup.setLayoutX(isFromLeft ? 0 : 1061 - (int) monsterFrameWidth);
        monsterGroup.setLayoutY(random.nextInt(628 - (int) monsterFrameHeight));

        updateMonsterTimeline();
    }

    public Quiz getQuiz() {
        return quiz;
    }

    @Override
    void updateMonsterTimeline() {
        Timeline blackHoleTimeline = new Timeline(
                new KeyFrame(Duration.seconds(timeBetweenTwoQuizzes), e -> {
                    generateMonster();
                })
        );
        blackHoleTimeline.setCycleCount(Timeline.INDEFINITE);
        blackHoleTimeline.play();

        monsterTimeline.add(blackHoleTimeline);
    }

    @Override
    void updateMonsterMovement(Runnable onStandingStill) {
        //No movement
    }

    @Override
    public void checkAnswer(List<Monster> monsterList, int index, String userAnswer) {
        if (quiz.isCorrectAnswer(userAnswer)) {
            beShot();
            monsterList.remove(index);
            MonsterHunterController.power = Math.min(100, MonsterHunterController.power + 10);
        }
    }

    @Override
    public void beShot() {
        stopMonsterTimeline();

        InterfaceHandler.updateFadeTransition(monsterGroup,3, 1.0, 0.0, () -> {
            battleContainer.getChildren().remove(monsterGroup);
        });
    }

    private void generateMonster() {
        if (currentQuizIndex.getValue() < quizList.size()) {
            int monsterType;

            int startingX = (int) (monsterGroup.getLayoutX() + monsterGroup.getBoundsInParent().getWidth() / 2 - (double) 820 / 12);
            int startingY = (int) (monsterGroup.getLayoutY() + monsterGroup.getBoundsInParent().getHeight() / 2 - (double) 250 / 4);

            //Handle exception
            do {
                monsterType = random.nextInt(2);
            } while (monsterType == 1 && currentQuizIndex.getValue() + 1 >= quizList.size());

            switch (monsterType) {
                case 0: {
                    BasicMonster basicMonster = new BasicMonster(
                            quizList.get(currentQuizIndex.getValue()), random.nextBoolean(), isFromLeft, startingX, startingY);
                    InterfaceHandler.updateFadeTransition(basicMonster.getMonsterGroup(), 3, 0.0, 1.0, null);

                    monsterList.add(basicMonster);
                    InterfaceHandler.objectList.addAll(basicMonster.getMonsterTimeline());

                    currentQuizIndex.setValue(currentQuizIndex.getValue() + 1);
                    break;
                }
                case 1: {
                    ShieldedMonster shieldedMonster = new ShieldedMonster(
                            quizList.get(currentQuizIndex.getValue()), quizList.get(currentQuizIndex.getValue() + 1), random.nextBoolean(), isFromLeft, startingX, startingY);
                    InterfaceHandler.updateFadeTransition(shieldedMonster.getMonsterGroup(), 3, 0.0, 1.0, null);
                    InterfaceHandler.updateFadeTransition(shieldedMonster.getShieldGroup(), 3, 0.0, 1.0, null);

                    monsterList.add(shieldedMonster);
                    InterfaceHandler.objectList.addAll(shieldedMonster.getMonsterTimeline());

                    currentQuizIndex.setValue(currentQuizIndex.getValue() + 2);
                    break;
                }
            }
        } else {
            stopMonsterTimeline();
        }
    }
}
