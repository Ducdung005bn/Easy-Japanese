package quiz;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.example.easyjapanese.Award;
import org.example.easyjapanese.Monster;
import utils.InterfaceHandler;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class MonsterHunterController extends QuizController {
    public static Pane parentContainer;
    private static final Random random = new Random();

    private int backgroundCurrentFrame = 1;
    private boolean isForward = true;
    private final String backgroundBasePath = String.valueOf(
            getClass().getResource("/pictureContainer/background/"));

    private Timeline quizTimeline;
    private int timeBetweenTwoQuizzes = 5;
    private Timeline awardTimeline;
    private int timeBetweenTwoAwards = 10;
    private final int awardActivationTime = 8;
    private int currentQuiz;
    List<Monster> monsterList;
    List<Award> awardList;

    private Timeline freezeTimeTimeline;

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
        updateBackgroundAnimation();

        Monster.battleContainer = battleContainer;
        Monster.diamond = diamond;
        monsterList = new ArrayList<>();

        Award.battleContainer = battleContainer;
        awardList = new ArrayList<>();

        Collections.shuffle(quizList);
        currentQuiz = 0;
        power = 100;
        powerBar.setProgress(1);

        quizTimeline = new Timeline(
                new KeyFrame(Duration.seconds(timeBetweenTwoQuizzes), event -> {
                    if (currentQuiz < quizList.size()) {
                        Monster monster = new Monster(quizList.get(currentQuiz));
                        monsterList.add(monster);
                        InterfaceHandler.timelineList.add(monster.getAttackTimeline());

                        currentQuiz++;
                    } else {
                        quizTimeline.stop();
                    }
                })
        );
        quizTimeline.setCycleCount(Timeline.INDEFINITE);
        quizTimeline.play();

        awardTimeline = new Timeline(
                new KeyFrame(Duration.seconds(timeBetweenTwoAwards), event -> {
                    if (currentQuiz < quizList.size()) {
                        Award award = new Award(quizList.get(random.nextInt(quizList.size())));
                        awardList.add(award);

                        InterfaceHandler.timelineList.add(award.getGlowTimeline());
                        InterfaceHandler.timelineList.add(award.getAwardTimeline());
                    } else {
                        awardTimeline.stop();
                    }
                })
        );
        awardTimeline.setCycleCount(Timeline.INDEFINITE);
        awardTimeline.play();

        powerBarTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0.1), event -> {
                    powerBar.setProgress((double) power /100);

                    if (power == 0) {
                        powerBarTimeline.stop();
                    }
                })
        );
        powerBarTimeline.setCycleCount(Timeline.INDEFINITE);
        powerBarTimeline.play();

        InterfaceHandler.timelineList.add(quizTimeline);
        InterfaceHandler.timelineList.add(awardTimeline);
    }

    private void updateBackgroundAnimation() {
        Glow glow = new Glow(0);
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.YELLOW);
        shadow.setOffsetX(0);
        shadow.setOffsetY(0);
        shadow.setRadius(10);
        shadow.setSpread(0.5);

        shadow.setInput(glow);
        diamond.setEffect(shadow);

        Timeline glowTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0.0),
                        new KeyValue(glow.levelProperty(), 0)
                ),
                new KeyFrame(Duration.seconds(0.25),
                        new KeyValue(glow.levelProperty(), 0.5)
                ),
                new KeyFrame(Duration.seconds(0.5),
                        new KeyValue(glow.levelProperty(), 0)
                )
        );
        glowTimeline.setCycleCount(Timeline.INDEFINITE);
        glowTimeline.play();

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


        Timeline backgroundTimeline = getBackgroundTimeline();
        backgroundTimeline.play();
    }

    private Timeline getBackgroundTimeline() {
        Timeline backgroundTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0.1), e -> {
                    String imagePath = backgroundBasePath + String.valueOf(backgroundCurrentFrame) + ".png";

                    backgroundImageView.setImage(new Image(imagePath));

                    if (isForward) {
                        backgroundCurrentFrame++;
                        if (backgroundCurrentFrame > 19) {
                            backgroundCurrentFrame = 18;
                            isForward = false;
                        }
                    } else {
                        backgroundCurrentFrame--;
                        if (backgroundCurrentFrame < 1) {
                            backgroundCurrentFrame = 2;
                            isForward = true;
                        }
                    }
                })
        );

        backgroundTimeline.setCycleCount(Timeline.INDEFINITE);
        return backgroundTimeline;
    }

    @FXML
    private void handleShootClick(MouseEvent event) {
        String userAnswer = answerTextField.getText();

        for (int i = 0; i < monsterList.size(); i++) {
            Monster monster = monsterList.get(i);

            if (monster.getQuiz().isCorrectAnswer(userAnswer)) {
                monster.beShot();
                monster.getAttackTimeline().stop();
                monsterList.remove(i);
                power = Math.min(100, power + 10);
                break;
            }
        }

        for (int i = 0; i < awardList.size(); i++) {
             Award award = awardList.get(i);

            if (award.getIsExisting() && award.getQuiz().isCorrectAnswer(userAnswer)) {
                activateAward(award);

                awardList.remove(i);
                break;
            }
        }

        answerTextField.setText("");
    }

    private void activateAward(Award award) {
        award.getGlowTimeline().stop();
        award.getAwardTimeline().stop();
        battleContainer.getChildren().remove(award.getAwardGroup());

        String type = award.getType();
        switch (type) {
            case "freezeTime":
            {
                createFreezeTimeTimeline();
            }
        }
    }

    private void createFreezeTimeTimeline() {
        Map<Monster, ImageView> freezeImageViews = new HashMap<>();

        if (freezeTimeTimeline != null && freezeTimeTimeline.getStatus() == Timeline.Status.RUNNING) {
            freezeTimeTimeline.setOnFinished(e -> {
                createFreezeTimeTimeline();
            });

            return;
        }

        freezeTimeTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> {
                    quizTimeline.pause();

                    for (Monster monster : monsterList) {
                        monster.getMonsterTimeline().pause();
                        monster.getAttackTimeline().pause();

                        ImageView freezeImageView = new ImageView(String.valueOf(getClass().getResource("/pictureContainer/freezing.gif")));
                        freezeImageView.setFitWidth((double) 601 / 3);
                        freezeImageView.setFitHeight((double) 346 / 3);

                        if (!monster.getIsFromLeft()) {
                            freezeImageView.setScaleX(-1);
                            freezeImageView.setLayoutX(monster.getMonsterGroup().getLayoutX() - (double) 601 / 3 + 50);
                        } else {
                            freezeImageView.setLayoutX(monster.getMonsterGroup().getLayoutX() + (double) 820 / 6 - 50);
                        }
                        freezeImageView.setLayoutY(monster.getMonsterGroup().getLayoutY() - 20);

                        battleContainer.getChildren().add(freezeImageView);
                        freezeImageViews.put(monster, freezeImageView);

                        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(awardActivationTime), freezeImageView);
                        fadeTransition.setFromValue(1.0);
                        fadeTransition.setToValue(0);
                        fadeTransition.play();
                    }
                }),
                new KeyFrame(Duration.seconds(awardActivationTime), e -> {
                    quizTimeline.play();

                    for (Monster monster : monsterList) {
                        monster.getMonsterTimeline().play();
                        monster.getAttackTimeline().play();

                        ImageView freezeImageView = freezeImageViews.get(monster);
                        if (freezeImageView != null) {
                            battleContainer.getChildren().remove(freezeImageView);
                        }
                    }

                    freezeImageViews.clear();
                })
        );

        freezeTimeTimeline.setCycleCount(1);
        freezeTimeTimeline.play();
    }

    @FXML
    private void handleSpaceKey(KeyEvent keyEvent) {
        handleShootClick(null);
    }
}
