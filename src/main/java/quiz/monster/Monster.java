package quiz.monster;

import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.example.easyjapanese.Quiz;

import java.util.ArrayList;
import java.util.List;
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
    boolean isFromLeft;

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

    public boolean getIsFromLeft() {
        return isFromLeft;
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

    abstract void updateMonsterMovement(Runnable onStandingStill);

    public abstract void checkAnswer(List<Monster> monsterList, int index, String userAnswer);

    public abstract void beShot();
}
