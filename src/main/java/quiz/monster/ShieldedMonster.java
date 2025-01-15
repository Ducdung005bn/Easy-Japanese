package quiz.monster;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import org.example.easyjapanese.Quiz;
import quiz.controller.MonsterHunterController;

import java.util.List;

public class ShieldedMonster extends BasicMonster {
    private boolean hasShield;
    private Quiz shieldQuiz;
    private ImageView shieldImageView;
    private Label shieldQuestionLabel;
    private Group shieldGroup;

    public ShieldedMonster(Quiz monsterQuiz, Quiz shieldQuiz, boolean isYellow, boolean isFromLeft, int startingX, int startingY) {
        super(monsterQuiz, isYellow, isFromLeft, startingX, startingY);

        initializeShield(shieldQuiz);
    }

    public ShieldedMonster(Quiz monsterQuiz, Quiz shieldQuiz, boolean isYellow, boolean isFromLeft) {
        super(monsterQuiz, isYellow, isFromLeft);

        initializeShield(shieldQuiz);
    }

    public Quiz getShieldQuiz() {
        return shieldQuiz;
    }

    public boolean getHasShield() {
        return hasShield;
    }

    public Group getShieldGroup() {
        return shieldGroup;
    }

    @Override
    void updateMonsterMovement(Runnable onStandingStill) {
        super.updateMonsterMovement(null);

        if (hasShield) {
            shieldGroup.setLayoutX(monsterGroup.getLayoutX());
            shieldGroup.setLayoutY(monsterGroup.getLayoutY());
        }
    }

    @Override
    public void checkAnswer(List<Monster> monsterList, int index, String userAnswer) {
        if (hasShield && shieldQuiz.isCorrectAnswer(userAnswer)) {
            beShot();
        } else if (!hasShield && getQuiz().isCorrectAnswer(userAnswer)) {
            beShot();
            monsterList.remove(index);
            MonsterHunterController.power = Math.min(100, MonsterHunterController.power + 10);
        }
    }

    @Override
    public void beShot() {
        if (hasShield) {
            hasShield = false;
            battleContainer.getChildren().remove(shieldGroup);
        } else {
            super.beShot();
        }
    }

    private void initializeShield(Quiz shieldQuiz) {
        this.shieldQuiz = shieldQuiz;
        this.hasShield = true;

        shieldImageView = new ImageView(new Image(String.valueOf(getClass().getResource("/pictureContainer/shield.png"))));
        shieldImageView.setFitWidth(monsterFrameWidth);
        shieldImageView.setFitHeight(monsterFrameHeight);

        shieldQuestionLabel = new Label(shieldQuiz.getQuestion());
        shieldQuestionLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-background-color: rgba(0, 0, 0, 0.5); -fx-padding: 4px;");
        shieldQuestionLabel.setMinWidth(monsterFrameWidth);
        shieldQuestionLabel.setAlignment(Pos.CENTER);

        shieldQuestionLabel.setLayoutX(0);
        shieldQuestionLabel.setLayoutY(-shieldQuestionLabel.getHeight() + 25);

        shieldGroup = new Group(shieldImageView, shieldQuestionLabel);
        battleContainer.getChildren().add(shieldGroup);

        shieldGroup.setLayoutX(monsterGroup.getLayoutX());
        shieldGroup.setLayoutY(monsterGroup.getLayoutY());
    }
}
