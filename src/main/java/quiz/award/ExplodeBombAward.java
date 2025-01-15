package quiz.award;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.example.easyjapanese.Quiz;
import quiz.monster.BasicMonster;
import quiz.monster.BlackHole;
import quiz.monster.Monster;
import quiz.monster.ShieldedMonster;

import java.util.List;

public class ExplodeBombAward extends Award {
    private final double explosionFrameWidth = (double) 1116 / 6;
    private final double explosionFrameHeight = (double) 1280 / 7;
    private final int explosionTotalFrames = 42;

    private int explosionCurrentFrame;
    private Timeline explosionTimeline;
    private ImageView explosionImageView;

    private List<Monster> monsterList;

    public ExplodeBombAward(Quiz quiz, List<Monster> monsterList) {
        super(quiz, "explodeBomb");
        this.monsterList = monsterList;
    }

    @Override
    public void activateAward() {
        super.activateAward();

        explosionCurrentFrame = 0;

        explosionImageView = new ImageView(String.valueOf(getClass().getResource("/pictureContainer/exploding.png")));

        explosionImageView.setFitWidth(explosionFrameWidth * 2);
        explosionImageView.setFitHeight(explosionFrameHeight * 2);

        explosionImageView.setLayoutX(getAwardGroup().getLayoutX() + getAwardGroup().getBoundsInLocal().getWidth() / 2 - explosionFrameWidth);
        explosionImageView.setLayoutY(getAwardGroup().getLayoutY() + getAwardGroup().getBoundsInLocal().getHeight() / 2 - explosionFrameHeight);

        explosionImageView.setViewport(new javafx.geometry.Rectangle2D(
                0, 0, explosionFrameWidth, explosionFrameHeight
        ));

        battleContainer.getChildren().add(explosionImageView);

        updateExplosionTimeline();

        shootMonsters();
    }

    private void updateExplosionTimeline() {
        explosionTimeline = new Timeline(
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
                            explosionTimeline.stop();
                            battleContainer.getChildren().remove(explosionImageView);
                        }
                    }
                })
        );

        explosionTimeline.setCycleCount(explosionTotalFrames);
        explosionTimeline.play();
    }

    private void shootMonsters() {
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
                if (monster instanceof ShieldedMonster shieldedMonster) {
                    if (shieldedMonster.getHasShield()) {
                        shieldedMonster.beShot(); //shield's got broken
                    } else {
                        shieldedMonster.beShot();
                        monsterList.remove(i);
                    }
                } else if (monsterList.get(i) instanceof BasicMonster basicMonster) {
                    basicMonster.beShot();
                    monsterList.remove(i);
                } else if (monsterList.get(i) instanceof BlackHole blackHole) {
                    blackHole.beShot();
                    monsterList.remove(i);
                }
            }
        }
    }
}
