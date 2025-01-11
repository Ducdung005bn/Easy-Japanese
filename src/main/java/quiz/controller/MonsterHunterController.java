package quiz.controller;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import quiz.award.Award;
import quiz.monster.BasicMonster;
import quiz.monster.Monster;
import quiz.monster.ShieldedMonster;
import utils.InterfaceHandler;

import java.util.*;

public class MonsterHunterController extends QuizController {
    private Timeline quizTimeline;
    private int timeBetweenTwoQuizzes = 5;
    private Timeline awardTimeline;
    private int timeBetweenTwoAwards = 10;
    private final int awardActivationTime = 8;
    List<Monster> monsterList;
    List<Award> awardList;

    private Timeline freezeTimeTimeline;
    private Animation explosionAnimation;
    private int explosionCurrentFrame;

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
        currentQuizIndex = 0;
        power = 100;
        powerBar.setProgress(1);

        quizTimeline = updateQuizTimeline();
        quizTimeline.setCycleCount(Timeline.INDEFINITE);
        quizTimeline.play();

        awardTimeline = updateAwardTimeline();
        awardTimeline.setCycleCount(Timeline.INDEFINITE);
        awardTimeline.play();

        powerBarTimeline = updatePowerBarTimeline();
        powerBarTimeline.setCycleCount(Timeline.INDEFINITE);
        powerBarTimeline.play();

        InterfaceHandler.timelineList.add(quizTimeline);
        InterfaceHandler.timelineList.add(awardTimeline);
        InterfaceHandler.timelineList.add(powerBarTimeline);

        updateBackgroundAnimation();
    }

    private Timeline updateQuizTimeline() {
        return new Timeline(
                new KeyFrame(Duration.seconds(timeBetweenTwoQuizzes), event -> {
                    if (currentQuizIndex < quizList.size()) {
                        int monsterType;

                        //Handle exception
                        do {
                            //monsterType = random.nextInt(2);
                            monsterType = 0;
                        } while (!((monsterType == 0) || (monsterType == 1 && currentQuizIndex < quizList.size() - 1)));

                        switch (monsterType) {
                            case 0: {
                                BasicMonster basicMonster = new BasicMonster(quizList.get(currentQuizIndex));
                                monsterList.add(basicMonster);
                                InterfaceHandler.timelineList.addAll(basicMonster.getMonsterTimeline());

                                currentQuizIndex++;
                                break;
                            }
                            case 1: {
                                ShieldedMonster shieldedMonster = new ShieldedMonster(quizList.get(currentQuizIndex), quizList.get(currentQuizIndex + 1));
                                monsterList.add(shieldedMonster);
                                InterfaceHandler.timelineList.addAll(shieldedMonster.getMonsterTimeline());

                                currentQuizIndex += 2;
                                break;
                            }
                        }
                    } else {
                        quizTimeline.stop();
                    }
                })
        );
    }

    private Timeline updateAwardTimeline() {
        return new Timeline(
                new KeyFrame(Duration.seconds(timeBetweenTwoAwards), event -> {
                    if (currentQuizIndex < quizList.size()) {
                        Award award = new Award(quizList.get(random.nextInt(quizList.size())));
                        awardList.add(award);

                        InterfaceHandler.timelineList.add(award.getGlowTimeline());
                        InterfaceHandler.timelineList.add(award.getAwardTimeline());
                    } else {
                        awardTimeline.stop();
                    }
                })
        );
    }

    private Timeline updatePowerBarTimeline() {
        return new Timeline(
                new KeyFrame(Duration.seconds(0.1), event -> {
                    powerBar.setProgress((double) power /100);

                    if (power == 0) {
                        powerBarTimeline.stop();
                    }
                })
        );
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
                    shieldedMonster.stopMonsterTimeline();
                    monsterList.remove(i);
                    power = Math.min(100, power + 15);
                }
            } else if (monsterList.get(i) instanceof BasicMonster) {
                BasicMonster basicMonster = (BasicMonster) monsterList.get(i);

                if (basicMonster.getQuiz().isCorrectAnswer(userAnswer)) {
                    basicMonster.beShot();
                    basicMonster.stopMonsterTimeline();
                    monsterList.remove(i);
                    power = Math.min(100, power + 10);
                }
            }
        }

        for (int i = awardList.size() - 1; i >= 0; i--) {
             Award award = awardList.get(i);

            if (award.getIsExisting() && award.getQuiz().isCorrectAnswer(userAnswer)) {
                activateAward(award);

                awardList.remove(i);
            }
        }

        answerTextField.setText("");
    }

    @Override
    void handleSpaceKey() {
        handleSubmitButton();
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

    private void activateAward(Award award) {
        award.getGlowTimeline().stop();
        award.getAwardTimeline().stop();
        battleContainer.getChildren().remove(award.getAwardGroup());

        String type = award.getType();
        switch (type) {
            case "freezeTime": freezeTimeHandler(); break;
            case "boostPower": power = Math.min(100, power + 20); break;
            case "explodeBomb": explodeBombHandler(award); break;
        }
    }

    private void freezeTimeHandler() {
        Map<BasicMonster, ImageView> freezeImageViews = new HashMap<>();

        if (freezeTimeTimeline != null && freezeTimeTimeline.getStatus() == Timeline.Status.RUNNING) {
            freezeTimeTimeline.setOnFinished(e -> {
                freezeTimeHandler();
            });

            return;
        }

        freezeTimeTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> {
                    quizTimeline.pause();

                    for (Monster monster : monsterList) {
                        if (monster instanceof BasicMonster basicMonster) {
                            freezeBasicMonster(basicMonster, freezeImageViews);
                        }
                    }
                }),
                new KeyFrame(Duration.seconds(awardActivationTime), e -> {
                    quizTimeline.play();

                    for (Monster monster : monsterList) {
                        if (monster instanceof BasicMonster basicMonster) {
                            unfreezeBasicMonster(basicMonster, freezeImageViews);
                        }
                    }

                    freezeImageViews.clear();
                })
        );

        freezeTimeTimeline.setCycleCount(1);
        freezeTimeTimeline.play();
    }

    private void freezeBasicMonster(BasicMonster basicMonster, Map<BasicMonster, ImageView> freezeImageViews) {
        basicMonster.pauseMonsterTimeline();

        ImageView freezeImageView = new ImageView(String.valueOf(getClass().getResource("/pictureContainer/freezing.gif")));
        freezeImageView.setFitWidth((double) 601 / 3);
        freezeImageView.setFitHeight((double) 346 / 3);

        if (!basicMonster.getIsFromLeft()) {
            freezeImageView.setScaleX(-1);
            freezeImageView.setLayoutX(basicMonster.getMonsterGroup().getLayoutX() - (double) 601 / 3 + 50);
        } else {
            freezeImageView.setLayoutX(basicMonster.getMonsterGroup().getLayoutX() + (double) 820 / 6 - 50);
        }
        freezeImageView.setLayoutY(basicMonster.getMonsterGroup().getLayoutY() - 20);

        battleContainer.getChildren().add(freezeImageView);
        freezeImageViews.put(basicMonster, freezeImageView);

        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(awardActivationTime), freezeImageView);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0);
        fadeTransition.play();
    }

    private void unfreezeBasicMonster(BasicMonster basicMonster, Map<BasicMonster, ImageView> freezeImageViews) {
        basicMonster.playMonsterTimeline();

        ImageView freezeImageView = freezeImageViews.get(basicMonster);
        if (freezeImageView != null) {
            battleContainer.getChildren().remove(freezeImageView);
        }
    }

    private void explodeBombHandler(Award award) {
        double explosionFrameWidth = (double) 1116 / 6;
        double explosionFrameHeight = (double) 1280 / 7;
        int explosionTotalFrames = 42;
        explosionCurrentFrame = 0;

        ImageView explosionImageView = new ImageView(String.valueOf(getClass().getResource("/pictureContainer/exploding.png")));

        explosionImageView.setFitWidth(explosionFrameWidth * 2);
        explosionImageView.setFitHeight(explosionFrameHeight * 2);

        explosionImageView.setLayoutX(award.getAwardGroup().getLayoutX() + award.getAwardGroup().getBoundsInLocal().getWidth() / 2 - explosionFrameWidth);
        explosionImageView.setLayoutY(award.getAwardGroup().getLayoutY() + award.getAwardGroup().getBoundsInLocal().getHeight() / 2 - explosionFrameHeight);

        explosionImageView.setViewport(new javafx.geometry.Rectangle2D(
                0, 0, explosionFrameWidth, explosionFrameHeight
        ));

        battleContainer.getChildren().add(explosionImageView);

        explosionAnimation = new Timeline(
                new KeyFrame(Duration.seconds(0.025), new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        int row = explosionCurrentFrame / 6;
                        int col = explosionCurrentFrame % 6;

                        explosionImageView.setViewport(new javafx.geometry.Rectangle2D(
                                col * explosionFrameWidth + col * 5, row * explosionFrameHeight + row * 5,
                                explosionFrameWidth, explosionFrameHeight
                        ));

                        explosionCurrentFrame++;

                        if (explosionCurrentFrame >= explosionTotalFrames) {
                            explosionAnimation.stop();
                            battleContainer.getChildren().remove(explosionImageView);
                        }
                    }
                })
        );

        explosionAnimation.setCycleCount(explosionTotalFrames);
        explosionAnimation.play();

        double explosionCenterX = explosionImageView.getLayoutX() + explosionImageView.getBoundsInLocal().getWidth() / 2;
        double explosionCenterY = explosionImageView.getLayoutY() + explosionImageView.getBoundsInLocal().getHeight() / 2;
        double explosionRadius = Math.sqrt(
                Math.pow(explosionImageView.getBoundsInLocal().getWidth() / 2, 2) + Math.pow(explosionImageView.getBoundsInLocal().getHeight() / 2, 2));

        for (int i = monsterList.size() - 1; i >= 0; i--) {
            Monster monster = monsterList.get(i);

            double monsterCenterX = monster.getMonsterGroup().getLayoutX() + monster.getMonsterGroup().getBoundsInLocal().getWidth() / 2;
            double monsterCenterY = monster.getMonsterGroup().getLayoutY() + monster.getMonsterGroup().getBoundsInLocal().getHeight() / 2;

            double distance = Math.sqrt(Math.pow(monsterCenterX - explosionCenterX, 2) + Math.pow(monsterCenterY - explosionCenterY, 2));

            if (distance <= explosionRadius) {
                monster.beShot();
                monster.stopMonsterTimeline();
                monsterList.remove(i);
            }
        }
    }
}
