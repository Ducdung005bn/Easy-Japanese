package quiz.award;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.example.easyjapanese.Quiz;

import java.util.Random;

public class Award {
    public static Pane battleContainer;
    private final Random random = new Random();

    private final String[] typeList = new String[]{
            "freezeTime", "boostPower", "explodeBomb"
    };
    private final int awardSideLength = 30;
    private String type;
    private final int movementSpeed = 1;
    private int movementType;
    private int xCenter;
    private Timeline awardTimeline;

    private ImageView awardImageView;
    private Quiz quiz;
    private Label questionLabel;
    private Group awardGroup;
    private Timeline glowTimeline;

    private boolean isExisting = true;

    public Award(Quiz quiz) {
        this.quiz = quiz;

        //type = typeList[random.nextInt(typeList.length)];
        type = typeList[2];
        movementType = random.nextInt(4);

        awardImageView = new ImageView(new Image(String.valueOf(getClass().getResource("/pictureContainer/" + type + ".png"))));

        awardImageView.setFitWidth(awardSideLength);
        awardImageView.setFitHeight(awardSideLength);

        // Initialize Label
        questionLabel = new Label(quiz.getQuestion());
        questionLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-background-color: rgba(0, 0, 0, 0.5); -fx-padding: 4px;");
        questionLabel.setMinWidth(awardSideLength);
        questionLabel.setAlignment(Pos.CENTER);
        questionLabel.setLayoutY(-25);

        // Create Group
        awardGroup = new Group(awardImageView, questionLabel);
        battleContainer.getChildren().add(awardGroup);

        xCenter = random.nextInt(1000);
        awardGroup.setLayoutX(xCenter);
        awardGroup.setLayoutY(0);

        //Create glow effect
        createGlowTimeline();

        awardTimeline = new Timeline(new KeyFrame(
                Duration.millis(20),
                e -> updateAwardPosition()
        ));
        awardTimeline.setCycleCount(Timeline.INDEFINITE);
        awardTimeline.play();
    }

    public boolean getIsExisting() {
        return isExisting;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public String getType() {
        return type;
    }

    public Timeline getAwardTimeline() {
        return awardTimeline;
    }

    public Timeline getGlowTimeline() {
        return glowTimeline;
    }

    public Group getAwardGroup() {
        return awardGroup;
    }

    private void createGlowTimeline() {
        Glow glow = new Glow();
        glow.setLevel(0);
        awardGroup.setEffect(glow);

        glowTimeline = new Timeline(
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
    }

    private void updateAwardPosition() {
        double currentX = awardGroup.getLayoutX();
        double currentY = awardGroup.getLayoutY();

        switch (movementType) {
            case 0:
                awardGroup.setLayoutY(currentY + movementSpeed);
                break;
            case 1:
                awardGroup.setLayoutX(xCenter + 1.5 * awardSideLength * Math.cos(0.05 * currentY + Math.PI / 2));
                awardGroup.setLayoutY(currentY + movementSpeed);
                break;
            case 2:
                double scale = 1 + 0.2 * Math.sin(currentY / 50);
                awardGroup.setScaleX(scale);
                awardGroup.setScaleY(scale);
                awardGroup.setLayoutY(currentY + movementSpeed);
                break;
            case 3:
                double rollOffset = 360 * Math.sin(currentY / 60);
                awardGroup.setRotate(rollOffset);
                awardGroup.setLayoutY(currentY + movementSpeed);
                break;
        }

        if (currentY >= battleContainer.getHeight() - awardSideLength) {
            awardTimeline.stop();
            glowTimeline.stop();
            battleContainer.getChildren().remove(awardGroup);

            isExisting = false;
            battleContainer.getChildren().remove(awardGroup);
        }
    }
}
