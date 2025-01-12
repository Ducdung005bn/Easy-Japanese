package quiz.monster;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.example.easyjapanese.Quiz;
import quiz.controller.MonsterHunterController;

import java.util.Map;

public class BasicMonster extends Monster {
    private static final double fireFrameWidth = (double) 949.0 / 9;
    private static final double fireFrameHeight = 280.0 / 2;
    private static final int fireTotalFrames = 18;
    private int fireCurrentFrame = 0;

    private static final double bloodFrameWidth = 500.0 / 4;
    private static final double bloodFrameHeight = 500.0 / 4;
    private static final int bloodTotalFrames = 16;
    private int bloodCurrentFrame = 0;

    private final boolean isYellow; // if not, it's black
    private final boolean isFromLeft;
    private int movementSpeed;
    private static int timeBetweenTwoAttacks = 3;

    private Quiz quiz;

    private ImageView fireImageView = new ImageView(new Image(String.valueOf(getClass().getResource("/pictureContainer/fire-effect.png"))));
    private Timeline fireAnimation;

    private ImageView bloodImageView;
    private Timeline bloodAnimation;

    public BasicMonster(Quiz quiz, boolean isYellow, boolean isFromLeft, double startingX, double startingY) {
        super((double) 820 / 6,
                (double) 250 / 2,
                6,
                12,
                isYellow ? "yellow-monster.png" : "black-monster.png",
                quiz);

        this.quiz = quiz;
        this.isYellow = isYellow;
        this.isFromLeft = isFromLeft;
        movementSpeed = 2;
        monsterCurrentFrame = 0;

        // Starting position; 1061 and 627 is the width and height of battleContainer
        monsterGroup.setLayoutX(startingX);
        monsterGroup.setLayoutY(startingY);

        updateMonsterTimeline();
    }

    public BasicMonster(Quiz quiz, boolean isYellow, boolean isFromLeft) {
        this(quiz, isYellow, isFromLeft, isFromLeft ? - (double) 820 / 6 : 1061, random.nextInt(628));
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public boolean getIsFromLeft() {
        return isFromLeft;
    }

    @Override
    void updateMonsterTimeline() {
        Timeline animateTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0.05), e -> updateMonsterFrame())
        );
        animateTimeline.setCycleCount(Timeline.INDEFINITE);
        animateTimeline.play();

        Timeline moveTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0.05), e -> updateMonsterMovement())
        );
        moveTimeline.setCycleCount(Timeline.INDEFINITE);
        moveTimeline.play();

        Timeline attackTimeline = new Timeline(
                new KeyFrame(Duration.seconds(timeBetweenTwoAttacks), e -> attack())
        );
        attackTimeline.setCycleCount(Timeline.INDEFINITE);
        attackTimeline.play();

        monsterTimeline.add(animateTimeline);
        monsterTimeline.add(moveTimeline);
        monsterTimeline.add(attackTimeline);
    }

    @Override
    public void beShot() {
        stopMonsterTimeline();

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

    @Override
    public void beFrozen(Map<Monster, ImageView> freezeImageViews, int awardActivationTime) {
        super.beFrozen(freezeImageViews, awardActivationTime);

        ImageView freezeImageView = freezeImageViews.get(this);

        if (!isFromLeft) {
            freezeImageView.setScaleX(-1);
            freezeImageView.setLayoutX(getMonsterGroup().getLayoutX() - (double) 601 / 3 + 50);
        } else {
            freezeImageView.setLayoutX(getMonsterGroup().getLayoutX() + (double) 820 / 6 - 50);
        }
        freezeImageView.setLayoutY(getMonsterGroup().getLayoutY() - 20);
    }

    @Override
    void updateMonsterFrame() {
        super.updateMonsterFrame();

        if (isFromLeft) {
            monsterImageView.setScaleX(1);
        } else {
            monsterImageView.setScaleX(-1);
        }
    }

    void updateMonsterMovement() {
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
    }

    private void attack() {
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
}