package quiz.monster;

import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Monster {
    public static ImageView diamond;
    public static Pane battleContainer;

    final Random random = new Random();

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
            int monsterTotalFrames) {
        this.monsterFrameWidth = monsterFrameWidth;
        this.monsterFrameHeight = monsterFrameHeight;
        this.monsterFrameColumn = monsterFrameColumn;
        this.monsterTotalFrames = monsterTotalFrames;

        monsterTimeline = new ArrayList<>();
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

    public abstract void beShot();

    void updateMonsterFrame() {
        int row = monsterCurrentFrame / monsterFrameColumn;
        int col = monsterCurrentFrame % monsterFrameColumn;

        monsterImageView.setViewport(
                new javafx.geometry.Rectangle2D(
                        col * monsterFrameWidth, row * monsterFrameHeight, monsterFrameWidth, monsterFrameHeight)
        );

        monsterCurrentFrame = (monsterCurrentFrame + 1) % monsterTotalFrames;
    }
}
