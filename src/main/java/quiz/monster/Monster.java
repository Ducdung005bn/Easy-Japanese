package quiz.monster;

import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.example.easyjapanese.Quiz;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class Monster {
    public static ImageView diamond;
    public static Pane battleContainer;

    static final Random random = new Random();

    final double monsterFrameWidth;
    final double monsterFrameHeight;
    final int monsterFrameColumn;
    final int monsterTotalFrames;

    int monsterCurrentFrame;

    ImageView monsterImageView;
    Label questionLabel;
    Group monsterGroup;
    List<Timeline> monsterTimeline;

    Monster(double monsterFrameWidth,
            double monsterFrameHeight,
            int monsterFrameColumn,
            int monsterTotalFrames,
            String filePath,
            Quiz quiz) {
        this.monsterFrameWidth = monsterFrameWidth;
        this.monsterFrameHeight = monsterFrameHeight;
        this.monsterFrameColumn = monsterFrameColumn;
        this.monsterTotalFrames = monsterTotalFrames;

        monsterTimeline = new ArrayList<>();

        monsterImageView = new ImageView(new Image(String.valueOf(getClass().getResource("/pictureContainer/") + filePath)));
        monsterImageView.setFitWidth(monsterFrameWidth);
        monsterImageView.setFitHeight(monsterFrameHeight);

        // Initialize Label
        questionLabel = new Label(quiz.getQuestion());
        questionLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-background-color: rgba(0, 0, 0, 0.5); -fx-padding: 4px;");
        questionLabel.setMinWidth(monsterFrameWidth);
        questionLabel.setAlignment(Pos.CENTER);

        questionLabel.setLayoutX(0);
        questionLabel.setLayoutY(-questionLabel.getHeight() + 25);

        // Create Group
        monsterGroup = new Group(monsterImageView, questionLabel);
        battleContainer.getChildren().add(monsterGroup);
    }

    public Group getMonsterGroup() {
        return monsterGroup;
    }

    public List<Timeline> getMonsterTimeline() {
        return monsterTimeline;
    }

    public void stopMonsterTimeline() {
        for (Timeline timeline : monsterTimeline) {
            timeline.stop();
        }
    }

    public void pauseMonsterTimeline() {
        for (Timeline timeline : monsterTimeline) {
            timeline.pause();
        }
    }

    public void playMonsterTimeline() {
        for (Timeline timeline : monsterTimeline) {
            timeline.play();
        }
    }

    abstract void updateMonsterTimeline();

    void updateMonsterFrame() {
        int row = monsterCurrentFrame / monsterFrameColumn;
        int col = monsterCurrentFrame % monsterFrameColumn;

        monsterImageView.setViewport(
                new javafx.geometry.Rectangle2D(
                        col * monsterFrameWidth, row * monsterFrameHeight, monsterFrameWidth, monsterFrameHeight)
        );

        monsterCurrentFrame = (monsterCurrentFrame + 1) % monsterTotalFrames;
    }

    abstract void updateMonsterMovement();

    public abstract void beShot();

    public void beFrozen(Map<Monster, ImageView> freezeImageViews, int awardActivationTime) {
        pauseMonsterTimeline();

        ImageView freezeImageView = new ImageView(String.valueOf(getClass().getResource("/pictureContainer/freezing.gif")));
        freezeImageView.setFitWidth((double) 601 / 3);
        freezeImageView.setFitHeight((double) 346 / 3);

        battleContainer.getChildren().add(freezeImageView);
        freezeImageViews.put(this, freezeImageView);

        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(awardActivationTime), freezeImageView);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0);
        fadeTransition.play();
    }

    public void unfreeze(Map<Monster, ImageView> freezeImageViews) {
        playMonsterTimeline();

        ImageView freezeImageView = freezeImageViews.get(this);
        if (freezeImageView != null) {
            battleContainer.getChildren().remove(freezeImageView);
        }
    }
}
