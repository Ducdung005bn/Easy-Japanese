package quiz.controller;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.example.easyjapanese.Quiz;
import quiz.award.Award;
import quiz.award.BoostPowerAward;
import quiz.award.ExplodeBombAward;
import quiz.award.FreezeTimeAward;
import quiz.monster.BasicMonster;
import quiz.monster.BlackHole;
import quiz.monster.Monster;
import quiz.monster.ShieldedMonster;
import utils.InterfaceHandler;

import java.util.*;
import java.util.function.Function;

public class MonsterHunterController extends QuizController {
    private Timeline quizTimeline;
    private int timeBetweenTwoQuizzes = 5;
    private Timeline awardTimeline;
    private int timeBetweenTwoAwards = 10;

    List<Monster> monsterList;
    List<Award> awardList;

    @FXML
    private ImageView backgroundImageView;

    @FXML
    private ProgressBar powerBar;
    public static int power = 100;
    private Timeline powerBarTimeline;

    @FXML
    private ImageView diamond;

    @FXML
    private Pane battleContainer;

    @FXML
    private TextField answerTextField;

    @FXML
    private Button shootButton;

    @FXML
    public void initialize() {
        BasicMonster.battleContainer = battleContainer;
        BasicMonster.diamond = diamond;
        monsterList = new ArrayList<>();

        Award.battleContainer = battleContainer;
        awardList = new ArrayList<>();

        Collections.shuffle(quizList);
        currentQuizIndex.setValue(0);
        power = 100;
        powerBar.setProgress(1);

        updateQuizTimeline();

        updateAwardTimeline();

        updatePowerBarTimeline();

        updateBackgroundAnimation();
    }

    private void updateQuizTimeline() {
        quizTimeline = new Timeline(
                new KeyFrame(Duration.seconds(timeBetweenTwoQuizzes), event -> {
                    if (currentQuizIndex.getValue() < quizList.size()) {
                        int monsterType;

                        //Handle exception
                        do {
                            //monsterType = random.nextInt(2);
                            monsterType = 2;
                        } while (!((monsterType == 0)
                                || (monsterType == 1 && currentQuizIndex.getValue() < quizList.size() - 1)
                                || (monsterType == 2)));

                        switch (monsterType) {
                            case 0: {
                                BasicMonster basicMonster = new BasicMonster(quizList.get(currentQuizIndex.getValue()), random.nextBoolean(), random.nextBoolean());
                                monsterList.add(basicMonster);
                                InterfaceHandler.timelineList.addAll(basicMonster.getMonsterTimeline());

                                currentQuizIndex.setValue(currentQuizIndex.getValue() + 1);
                                break;
                            }
                            case 1: {
                                ShieldedMonster shieldedMonster = new ShieldedMonster(quizList.get(currentQuizIndex.getValue()), quizList.get(currentQuizIndex.getValue() + 1), random.nextBoolean(), random.nextBoolean());
                                monsterList.add(shieldedMonster);
                                InterfaceHandler.timelineList.addAll(shieldedMonster.getMonsterTimeline());

                                currentQuizIndex.setValue(currentQuizIndex.getValue() + 2);
                                break;
                            }
                            case 2: {
                                BlackHole blackHole = new BlackHole(quizList.get(currentQuizIndex.getValue()), monsterList, awardList, quizList, currentQuizIndex);
                                monsterList.add(blackHole);
                                InterfaceHandler.timelineList.addAll(blackHole.getMonsterTimeline());

                                currentQuizIndex.setValue(currentQuizIndex.getValue() + 1);
                                break;
                            }
                        }
                    } else {
                        quizTimeline.stop();
                    }
                })
        );

        quizTimeline.setCycleCount(Timeline.INDEFINITE);
        quizTimeline.play();

        InterfaceHandler.timelineList.add(quizTimeline);
    }

    private void updateAwardTimeline() {
        Map<Integer, Function<Quiz, Award>> awardFactory = Map.of(
                0, quiz -> new FreezeTimeAward(quiz, quizTimeline, monsterList),
                1, quiz -> new BoostPowerAward(quiz),
                2, quiz -> new ExplodeBombAward(quiz, monsterList)
        );

        awardTimeline = new Timeline(
                new KeyFrame(Duration.seconds(timeBetweenTwoAwards), event -> {
                    if (currentQuizIndex.getValue() < quizList.size()) {
                        int awardType = random.nextInt(3);

                        Award award = awardFactory.get(awardType).apply(quizList.get(currentQuizIndex.getValue()));
                        awardList.add(award);

                        Collections.addAll(InterfaceHandler.timelineList,
                                award.getGlowTimeline(), award.getAwardTimeline());

                        currentQuizIndex.setValue(currentQuizIndex.getValue() + 1);
                    } else {
                        awardTimeline.stop();
                    }
                })
        );

        awardTimeline.setCycleCount(Timeline.INDEFINITE);
        awardTimeline.play();

        InterfaceHandler.timelineList.add(awardTimeline);
    }

    private void updatePowerBarTimeline() {
        powerBarTimeline =  new Timeline(
                new KeyFrame(Duration.seconds(0.1), event -> {
                    powerBar.setProgress((double) power /100);

                    if (power == 0) {
                        powerBarTimeline.stop();
                    }
                })
        );

        powerBarTimeline.setCycleCount(Timeline.INDEFINITE);
        powerBarTimeline.play();

        InterfaceHandler.timelineList.add(powerBarTimeline);
    }

    @Override
    void setQuizFunction() {
        //TO DO
    }

    @Override
    void handleSubmitButton() {
        String userAnswer = answerTextField.getText();

        for (int i = monsterList.size() - 1; i >= 0; i--) {
            if (monsterList.get(i) instanceof ShieldedMonster) {
                ShieldedMonster shieldedMonster = (ShieldedMonster) monsterList.get(i);

                if (shieldedMonster.getHasShield() && shieldedMonster.getShieldQuiz().isCorrectAnswer(userAnswer)) {
                    shieldedMonster.beShot(); //shield's got broken
                } else if (!shieldedMonster.getHasShield() && shieldedMonster.getQuiz().isCorrectAnswer(userAnswer)) {
                    shieldedMonster.beShot();
                    monsterList.remove(i);
                    power = Math.min(100, power + 15);
                }
            } else if (monsterList.get(i) instanceof BasicMonster) {
                BasicMonster basicMonster = (BasicMonster) monsterList.get(i);

                if (basicMonster.getQuiz().isCorrectAnswer(userAnswer)) {
                    basicMonster.beShot();
                    monsterList.remove(i);
                    power = Math.min(100, power + 10);
                }
            }
        }

        for (int i = awardList.size() - 1; i >= 0; i--) {
             Award award = awardList.get(i);

            if (award.getIsExisting() && award.getQuiz().isCorrectAnswer(userAnswer)) {
                award.activateAward();

                awardList.remove(i);
            }
        }

        answerTextField.setText("");
    }

    private void updateBackgroundAnimation() {
        Glow glow = new Glow(0);

        backgroundImageView.setEffect(glow);

        Timeline glowTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0.0),
                        new KeyValue(glow.levelProperty(), 0)
                ),
                new KeyFrame(Duration.seconds(1.5),
                        new KeyValue(glow.levelProperty(), 0.5)
                ),
                new KeyFrame(Duration.seconds(3),
                        new KeyValue(glow.levelProperty(), 0)
                )
        );
        glowTimeline.setCycleCount(Timeline.INDEFINITE);
        glowTimeline.play();

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.YELLOW);
        shadow.setOffsetX(0);
        shadow.setOffsetY(0);
        shadow.setRadius(10);
        shadow.setSpread(0.5);

        diamond.setEffect(shadow);

        Timeline shadowTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0.0),
                        new KeyValue(shadow.radiusProperty(), 10),
                        new KeyValue(shadow.offsetXProperty(), 0),
                        new KeyValue(shadow.offsetYProperty(), 0)
                ),
                new KeyFrame(Duration.seconds(0.5),
                        new KeyValue(shadow.radiusProperty(), 20),
                        new KeyValue(shadow.offsetXProperty(), 5),
                        new KeyValue(shadow.offsetYProperty(), 5)
                ),
                new KeyFrame(Duration.seconds(1.0),
                        new KeyValue(shadow.radiusProperty(), 10),
                        new KeyValue(shadow.offsetXProperty(), 0),
                        new KeyValue(shadow.offsetYProperty(), 0)
                )
        );
        shadowTimeline.setCycleCount(Timeline.INDEFINITE);
        shadowTimeline.setAutoReverse(true);
        shadowTimeline.play();
    }
}
