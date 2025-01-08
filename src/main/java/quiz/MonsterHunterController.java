package quiz;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.example.easyjapanese.Vocabulary;

import java.util.Random;

public class MonsterHunterController {

    @FXML
    private ImageView backgroundImageView;

    @FXML
    private ImageView diamond;

    private int backgroundCurrentFrame = 1;
    private boolean isForward = true;
    private final String backgroundBasePath = String.valueOf(
            getClass().getResource("/pictureContainer/background/"));

    @FXML
    private Pane battleContainer;

    @FXML
    private TextField answerTextField;

    @FXML
    private Button shootButton;

    private final double monsterFrameWidth = (double) 1151 / 6;
    private final double monsterFrameHeight = (double) 351 / 2;
    private final int monsterTotalFrames = 12;

    @FXML
    public void initialize() {
        updateBackgroundAnimation();

        for (int i = 1; i <= 5; i++) {
            Monster monster = new Monster();
        }
    }

    private void updateBackgroundAnimation() {
        Glow glow = new Glow(0);
        diamond.setEffect(glow);

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
        backgroundTimeline.play();
    }

    private class Monster {
        private boolean isYellow; //if not, it's black
        private boolean isFromLeft;
        private int movementSpeed;
        private int monsterCurrentFrame;
        private ImageView monsterImageView;
        private Vocabulary vocabulary;

        public Monster() {
            Random random = new Random();

            isYellow = random.nextBoolean();

            isFromLeft = random.nextBoolean();

            movementSpeed = 5;

            monsterCurrentFrame = 0;

            if (isYellow) {
                monsterImageView = new ImageView(new Image(String.valueOf(getClass().getResource("/pictureContainer/yellow-monster.png"))));
            } else {
                monsterImageView = new ImageView(new Image(String.valueOf(getClass().getResource("/pictureContainer/black-monster.png"))));
            }

            monsterImageView.setFitWidth(monsterFrameWidth);
            monsterImageView.setFitHeight(monsterFrameHeight);

            battleContainer.getChildren().add(monsterImageView);

            //starting position; 1061 and 627 is the width and height of battleContainer
            monsterImageView.setLayoutX(isFromLeft ? - monsterFrameWidth : 1061);
            monsterImageView.setLayoutY(random.nextInt(628));

            updateMonsterAnimation();
        }

        private void updateMonsterAnimation() {
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(0.01), e -> updateMonsterFrame()),
                    new KeyFrame(Duration.seconds(0.1), e -> updateMonsterMovement())
            );

            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();
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
            double xCenter = diamond.getLayoutX() + diamond.getFitWidth() / 2  - monsterFrameWidth / 2;
            double yCenter = diamond.getLayoutY() + diamond.getFitHeight() / 2 - monsterFrameHeight / 2;

            double currentX = monsterImageView.getLayoutX();
            double currentY = monsterImageView.getLayoutY();

            double deltaX = xCenter - currentX;
            double deltaY = yCenter - currentY;

            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

            if (distance < 100) {
                return;
            }

            double ratioX = deltaX / distance;
            double ratioY = deltaY / distance;

            monsterImageView.setLayoutX(currentX + ratioX * movementSpeed);
            monsterImageView.setLayoutY(currentY + ratioY * movementSpeed);
        }

    }

    @FXML
    private void handleShootClick(MouseEvent event) {
        //TO DO
    }
}
