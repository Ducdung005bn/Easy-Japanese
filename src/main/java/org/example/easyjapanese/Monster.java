package org.example.easyjapanese;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import quiz.MonsterHunterController;

import java.util.Random;

public class Monster {
    public static ImageView diamond;
    public static Pane battleContainer;

    private static final double monsterFrameWidth = (double) 820 / 6;
    private static final double monsterFrameHeight = (double) 250 / 2;
    private static final int monsterTotalFrames = 12;

    private static final double fireFrameWidth = (double) 949.0 / 9;
    private static final double fireFrameHeight = 280.0 / 2;
    private static final int fireTotalFrames = 18;
    private int fireCurrentFrame = 0;

    private static final double bloodFrameWidth = 500.0 / 4;
    private static final double bloodFrameHeight = 500.0 / 4;
    private static final int bloodTotalFrames = 16;
    private int bloodCurrentFrame = 0;

    private boolean isYellow; // if not, it's black
    private boolean isFromLeft;
    private int movementSpeed;
    private int timeBetweenTwoAttacks = 3;

    private ImageView monsterImageView;
    private int monsterCurrentFrame;
    private Quiz quiz;
    private Label questionLabel;
    private Group monsterGroup;
    private Timeline monsterTimeline;

    private ImageView fireImageView = new ImageView(new Image(String.valueOf(getClass().getResource("/pictureContainer/fire-effect.png"))));
    private Timeline fireAnimation;
    private Timeline attackTimeline;

    private ImageView bloodImageView;
    private Timeline bloodAnimation;

    public Monster(Quiz quiz) {
        Random random = new Random();

        isYellow = random.nextBoolean();
        isFromLeft = random.nextBoolean();
        movementSpeed = 5;
        monsterCurrentFrame = 0;

        // Initialize ImageView
        if (isYellow) {
            monsterImageView = new ImageView(new Image(String.valueOf(getClass().getResource("/pictureContainer/yellow-monster.png"))));
        } else {
            monsterImageView = new ImageView(new Image(String.valueOf(getClass().getResource("/pictureContainer/black-monster.png"))));
        }

        monsterImageView.setFitWidth(monsterFrameWidth);
        monsterImageView.setFitHeight(monsterFrameHeight);

        // Initialize Label
        questionLabel = new Label(quiz.getQuestion());
        questionLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-background-color: rgba(0, 0, 0, 0.5); -fx-padding: 4px;");
        questionLabel.setMinWidth(monsterFrameWidth);
        questionLabel.setAlignment(Pos.CENTER);

        // Create Group
        monsterGroup = new Group(monsterImageView, questionLabel);
        battleContainer.getChildren().add(monsterGroup);

        // Starting position; 1061 and 627 is the width and height of battleContainer
        monsterGroup.setLayoutX(isFromLeft ? -monsterFrameWidth : 1061);
        monsterGroup.setLayoutY(random.nextInt(628));

        this.quiz = quiz;

        updateMonsterAnimation();

        updateAttackTimeline();
    }

    private void updateAttackTimeline() {
        attackTimeline = new Timeline(
                new KeyFrame(Duration.seconds(timeBetweenTwoAttacks), event -> attack())
        );
        attackTimeline.setCycleCount(Timeline.INDEFINITE);
        attackTimeline.play();
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public Timeline getAttackTimeline() {
        return attackTimeline;
    }

    public Timeline getMonsterTimeline() {
        return monsterTimeline;
    }

    public Group getMonsterGroup() {
        return monsterGroup;
    }

    public boolean getIsFromLeft() {
        return isFromLeft;
    }

    public void beShot() {
        movementSpeed = 0;

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

    public void attack() {
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

                    if (fireCurrentFrame == 7) {
                        MonsterHunterController.power = Math.max(0, MonsterHunterController.power - 5);
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


    private void updateMonsterAnimation() {
        monsterTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0.01), e -> updateMonsterFrame()),
                new KeyFrame(Duration.seconds(0.1), e -> updateMonsterMovement())
        );

        monsterTimeline.setCycleCount(Timeline.INDEFINITE);
        monsterTimeline.play();
    }

    private void updateMonsterFrame() {
        int row = monsterCurrentFrame / 6;
        int col = monsterCurrentFrame % 6;

        monsterImageView.setViewport(
                new javafx.geometry.Rectangle2D(
                        col * monsterFrameWidth, row * monsterFrameHeight, monsterFrameWidth, monsterFrameHeight)
        );

        if (isFromLeft) {
            monsterImageView.setScaleX(1);
        } else {
            monsterImageView.setScaleX(-1);
        }

        monsterCurrentFrame = (monsterCurrentFrame + 1) % monsterTotalFrames;
    }

    private void updateMonsterMovement() {
        double xCenter = diamond.getLayoutX() + diamond.getFitWidth() / 2 - monsterFrameWidth / 2;
        double yCenter = diamond.getLayoutY() + diamond.getFitHeight() / 2 - monsterFrameHeight / 2;

        double currentX = monsterGroup.getLayoutX();
        double currentY = monsterGroup.getLayoutY();

        double deltaX = xCenter - currentX;
        double deltaY = yCenter - currentY;

        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        if (distance < 75) {
            return;
        }

        double ratioX = deltaX / distance;
        double ratioY = deltaY / distance;

        monsterGroup.setLayoutX(currentX + ratioX * movementSpeed);
        monsterGroup.setLayoutY(currentY + ratioY * movementSpeed);

        // Update label position relative to ImageView
        questionLabel.setLayoutX(0);
        questionLabel.setLayoutY(-questionLabel.getHeight() + 50); // Position label above the monster
    }
}