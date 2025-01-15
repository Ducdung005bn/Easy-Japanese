package quiz.monster;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.easyjapanese.Quiz;

import java.util.List;

public class CharmedMonster extends BasicMonster {
    private Boolean isCharmed;
    private ImageView charmImageView;

    private List<Monster> monsterList;

    public CharmedMonster(Quiz quiz, List<Monster> monsterList) {
        super(quiz, random.nextBoolean(), random.nextBoolean());
        isCharmed = false;
        this.monsterList = monsterList;
    }

    @Override
    void updateMonsterMovement(Runnable onStandingStill) {
        if (charmImageView == null) {
            charmImageView = new ImageView(new Image(String.valueOf(getClass().getResource("/pictureContainer/charm.gif"))));
            charmImageView.setFitWidth(monsterFrameWidth);
            charmImageView.setFitHeight(monsterFrameHeight);
            setCharmImageViewLayout();

            battleContainer.getChildren().add(charmImageView);

            questionLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white; -fx-background-color: rgba(0, 0, 0, 0.5); -fx-padding: 4px;");
        }

        super.updateMonsterMovement(() -> {
            if (isCharmed) {
                stopMonsterTimeline();
                battleContainer.getChildren().remove(monsterGroup);
                battleContainer.getChildren().remove(charmImageView);
            }
        });
        setCharmImageViewLayout();
    }

    @Override
    public void beShot() {
        isCharmed = true;
        setTargetPoint(getStartingPoint());

        getMonsterGroup().getChildren().remove(questionLabel);
        isFromLeft = !isFromLeft;
    }

    @Override
    void attack(Runnable onFinished) {
        if (!isCharmed) {
            super.attack(onFinished);
        } else {
            super.attack(attackOtherMonsters());
        }
    }

    private Runnable attackOtherMonsters() {
        return () -> {
            for (int i = monsterList.size() - 1; i >= 0; i--) {
                Monster monster = monsterList.get(i);

                double fireCenterX = getFireImageView().getLayoutX() + getFireImageView().getFitWidth() / 2;
                double fireCenterY = getFireImageView().getLayoutY() + getFireImageView().getFitHeight() / 2;

                double monsterCenterX = monster.getMonsterGroup().getLayoutX() + monster.getMonsterGroup().getBoundsInLocal().getWidth() / 2;
                double monsterCenterY = monster.getMonsterGroup().getLayoutY() + monster.getMonsterGroup().getBoundsInLocal().getHeight() / 2;

                double distance = Math.sqrt(Math.pow(fireCenterX - monsterCenterX, 2) + Math.pow(fireCenterY - monsterCenterY, 2));

                if (distance < 200) {
                    if ((fireCenterX < monsterCenterX && isFromLeft) || (fireCenterX > monsterCenterX && !isFromLeft)) {
                        monster.beShot();
                        monsterList.remove(monster);
                    }
                }
            }
        };
    }

    private void setCharmImageViewLayout() {
        charmImageView.setLayoutX(getMonsterGroup().getLayoutX());
        charmImageView.setLayoutY(getMonsterGroup().getLayoutY());
    }

}