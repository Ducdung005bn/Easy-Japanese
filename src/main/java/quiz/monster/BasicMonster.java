package quiz.monster;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.example.easyjapanese.Quiz;
import quiz.controller.MonsterHunterController;

import java.awt.*;
import java.util.List;

public class BasicMonster extends Monster {
    private static final double fireFrameWidth = (double) 949.0 / 9;
    private static final double fireFrameHeight = 280.0 / 2;
    private static final int fireTotalFrames = 18;
    private int fireCurrentFrame = 0;

    private static final double bloodFrameWidth = 500.0 / 4;
    private static final double bloodFrameHeight = 500.0 / 4;
    private static final int bloodTotalFrames = 16;
    private int bloodCurrentFrame = 0;

    private int movementSpeed;
    private static int timeBetweenTwoAttacks = 3;
    private Quiz quiz;
    private Point startingPoint;
    private Point targetPoint;

    private ImageView fireImageView = new ImageView(new Image(String.valueOf(getClass().getResource("/pictureContainer/fire-effect.png"))));
    private Timeline fireAnimation;

    private ImageView bloodImageView;
    private Timeline bloodAnimation;

    public BasicMonster(Quiz quiz, boolean isYellow, boolean isFromLeft, int startingX, int startingY) {
        super((double) 820 / 6,
                (double) 250 / 2,
                6,
                12,
                isYellow ? "yellow-monster.png" : "black-monster.png",
                quiz);

        this.quiz = quiz;
        this.isFromLeft = isFromLeft;
        movementSpeed = 2;
        monsterCurrentFrame = 0;
        startingPoint = new Point(startingX, startingY);
        targetPoint = new Point((int) (diamond.getLayoutX() + diamond.getFitWidth() / 2 - monsterFrameWidth / 2),
                (int) (diamond.getLayoutY() + diamond.getFitHeight() / 2 - monsterFrameHeight / 2));

        // Starting position; 1061 and 627 is the width and height of battleContainer
        monsterGroup.setLayoutX(startingX);
        monsterGroup.setLayoutY(startingY);

        updateMonsterTimeline();
    }

    public BasicMonster(Quiz quiz, boolean isYellow, boolean isFromLeft) {
        this(quiz, isYellow, isFromLeft, isFromLeft ? - 820 / 6 : 1061, random.nextInt(628));
    }

    public Quiz getQuiz() {
        return quiz;
    }

    Point getStartingPoint() {
        return startingPoint;
    }

    void setTargetPoint(Point targetPoint) {
        this.targetPoint = targetPoint;
    }

    ImageView getFireImageView() {
        return fireImageView;
    }

    @Override
    void updateMonsterTimeline() {
        Timeline animateTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0.05), e -> updateMonsterFrame())
        );
        animateTimeline.setCycleCount(Timeline.INDEFINITE);
        animateTimeline.play();

        Timeline moveTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0.05), e -> updateMonsterMovement(null))
        );
        moveTimeline.setCycleCount(Timeline.INDEFINITE);
        moveTimeline.play();

        Timeline attackTimeline = new Timeline(
                new KeyFrame(Duration.seconds(timeBetweenTwoAttacks), e -> attack(() -> {
                    MonsterHunterController.power = Math.max(0, MonsterHunterController.power - 3);
                }))
        );
        attackTimeline.setCycleCount(Timeline.INDEFINITE);
        attackTimeline.play();

        monsterTimeline.add(animateTimeline);
        monsterTimeline.add(moveTimeline);
        monsterTimeline.add(attackTimeline);
    }

    void updateMonsterMovement(Runnable onStandingStill) {
        double currentX = monsterGroup.getLayoutX();
        double currentY = monsterGroup.getLayoutY();

        double deltaX = targetPoint.x - currentX;
        double deltaY = targetPoint.y - currentY;

        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        if (distance < 75) {
            if (onStandingStill != null) {
                onStandingStill.run();
            }

            return;
        }

        double ratioX = deltaX / distance;
        double ratioY = deltaY / distance;

        monsterGroup.setLayoutX(currentX + ratioX * movementSpeed);
        monsterGroup.setLayoutY(currentY + ratioY * movementSpeed);
    }

    @Override
    public void checkAnswer(List<Monster> monsterList, int index, String userAnswer) {
        if (quiz.isCorrectAnswer(userAnswer)) {
            beShot();
            monsterList.remove(index);
            MonsterHunterController.power = Math.min(100, MonsterHunterController.power + 5);
        }
    }

    @Override
    public void beShot() {
        stopMonsterTimeline();

        Image bloodImage = new Image(String.valueOf(getClass().getResource("/pictureContainer/blood-effect.png")));
        bloodImageView = new ImageView(bloodImage);

        bloodImageView.setFitWidth(monsterFrameWidth);
        bloodImageView.setFitHeight(monsterFrameHeight);
        bloodImageView.setLayoutX(monsterGroup.getLayoutX());
        bloodImageView.setLayoutY(monsterGroup.getLayoutY());

        bloodImageView.setViewport(new javafx.geometry.Rectangle2D(
                0, 0, bloodFrameWidth, bloodFrameHeight
        ));

        battleContainer.getChildren().add(bloodImageView);

        bloodAnimation = new Timeline(
                new KeyFrame(Duration.seconds(0.1), new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        int row = bloodCurrentFrame / 4;
                        int col = bloodCurrentFrame % 4;

                        bloodImageView.setViewport(new javafx.geometry.Rectangle2D(
                                col * bloodFrameWidth, row * bloodFrameHeight,
                                bloodFrameWidth, bloodFrameHeight
                        ));

                        bloodCurrentFrame++;

                        if (bloodCurrentFrame == 5) {
                            battleContainer.getChildren().remove(monsterGroup);
                        }

                        if (bloodCurrentFrame >= bloodTotalFrames) {
                            bloodAnimation.stop();
                            battleContainer.getChildren().remove(bloodImageView);
                        }
                    }
                })
        );

        bloodAnimation.setCycleCount(bloodTotalFrames);
        bloodAnimation.play();
    }

    void attack(Runnable onFinished) {
        fireImageView.setFitWidth(fireFrameWidth);
        fireImageView.setFitHeight(fireFrameHeight);

        if (isFromLeft) {
            fireImageView.setRotate(90);
            fireImageView.setLayoutX(monsterGroup.getLayoutX() + monsterGroup.getLayoutBounds().getWidth());
        } else {
            fireImageView.setRotate(-90);
            fireImageView.setLayoutX(monsterGroup.getLayoutX() - fireFrameWidth);
        }
        fireImageView.setLayoutY(monsterGroup.getLayoutY() + (monsterGroup.getLayoutBounds().getHeight() - fireFrameHeight) / 2 + 10);

        fireImageView.setViewport(new javafx.geometry.Rectangle2D(
                0, 0, fireFrameWidth, fireFrameHeight
        ));

        if (!battleContainer.getChildren().contains(fireImageView)) {
            battleContainer.getChildren().add(fireImageView);
        }

        fireAnimation = new Timeline(
                new KeyFrame(Duration.seconds(0.1), e -> {
                    if (isFromLeft) {
                        fireImageView.setLayoutX(monsterGroup.getLayoutX() + monsterGroup.getLayoutBounds().getWidth());
                    } else {
                        fireImageView.setLayoutX(monsterGroup.getLayoutX() - fireFrameWidth);
                    }
                    fireImageView.setLayoutY(monsterGroup.getLayoutY() + (monsterGroup.getLayoutBounds().getHeight() - fireFrameHeight) / 2 + 10);

                    int row = fireCurrentFrame / 9;
                    int col = fireCurrentFrame % 9;

                    fireImageView.setViewport(new javafx.geometry.Rectangle2D(
                            col * fireFrameWidth, row * fireFrameHeight,
                            fireFrameWidth, fireFrameHeight
                    ));

                    fireCurrentFrame++;

                    if (fireCurrentFrame == 7 && onFinished != null) {
                        onFinished.run();
                    }

                    if (fireCurrentFrame >= fireTotalFrames) {
                        fireCurrentFrame = 0;
                        fireAnimation.stop();
                        battleContainer.getChildren().remove(fireImageView);
                    }
                })
        );

        fireAnimation.setCycleCount(fireTotalFrames);
        fireAnimation.play();
    }

    private void updateMonsterFrame() {
        int row = monsterCurrentFrame / monsterFrameColumn;
        int col = monsterCurrentFrame % monsterFrameColumn;

        monsterImageView.setViewport(
                new javafx.geometry.Rectangle2D(
                        col * monsterFrameWidth, row * monsterFrameHeight, monsterFrameWidth, monsterFrameHeight)
        );

        monsterCurrentFrame = (monsterCurrentFrame + 1) % monsterTotalFrames;

        if (isFromLeft) {
            monsterImageView.setScaleX(1);
        } else {
            monsterImageView.setScaleX(-1);
        }
    }
}