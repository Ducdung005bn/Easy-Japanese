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
import quiz.monster.*;
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
        Map<Integer, Function<Integer, Monster>> monsterFactory = Map.of(
                0, index -> new BasicMonster(quizList.get(index), random.nextBoolean(), random.nextBoolean()),
                1, index -> new ShieldedMonster(quizList.get(index), quizList.get(index + 1), random.nextBoolean(), random.nextBoolean()),
                2, index -> new BlackHole(quizList.get(index), monsterList, quizList, currentQuizIndex),
                3, index -> new CharmedMonster(quizList.get(index), monsterList)
        );

        quizTimeline = new Timeline(
                new KeyFrame(Duration.seconds(timeBetweenTwoQuizzes), event -> {
                    if (currentQuizIndex.getValue() < quizList.size()) {
                        int monsterType;

                        do {
                            //monsterType = random.nextInt(4);
                            monsterType = 3;
                        } while (monsterType == 1 && currentQuizIndex.getValue() + 1 >= quizList.size());

                        Monster monster = monsterFactory.get(monsterType).apply(currentQuizIndex.getValue());
                        monsterList.add(monster);
                        InterfaceHandler.objectList.addAll(monster.getMonsterTimeline());

                        if (monsterType == 1) {
                            currentQuizIndex.setValue(currentQuizIndex.getValue() + 2);
                        } else {
                            currentQuizIndex.setValue(currentQuizIndex.getValue() + 1);
                        }
                    } else {
                        quizTimeline.stop();
                    }
                })
        );

        quizTimeline.setCycleCount(Timeline.INDEFINITE);
        quizTimeline.play();

        InterfaceHandler.objectList.add(quizTimeline);
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
                        //int awardType = random.nextInt(3);
                        int awardType = 0;

                        Award award = awardFactory.get(awardType).apply(quizList.get(currentQuizIndex.getValue()));
                        awardList.add(award);

                        Collections.addAll(InterfaceHandler.objectList,
                                award.getGlowTimeline(), award.getAwardTimeline());

                        currentQuizIndex.setValue(currentQuizIndex.getValue() + 1);
                    } else {
                        awardTimeline.stop();
                    }
                })
        );

        awardTimeline.setCycleCount(Timeline.INDEFINITE);
        awardTimeline.play();

        InterfaceHandler.objectList.add(awardTimeline);
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

        InterfaceHandler.objectList.add(powerBarTimeline);
    }

    @Override
    void setQuizFunction() {
        //TO DO
    }

    @Override
    void handleSubmitButton() {
        String userAnswer = answerTextField.getText();

        for (int i = monsterList.size() - 1; i >= 0; i--) {
            monsterList.get(i).checkAnswer(monsterList, i, userAnswer);
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
