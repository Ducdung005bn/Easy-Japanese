package org.example.easyjapanese;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.io.File;
import java.io.IOException;

public class QuizFunctionController {
    public static Pane parentContainer;

    private static final String[] quizTypes = {"Fill in the blank using meanings"
            , "Fill in the blank using pictures"};

    private boolean reverseOrder;
    private boolean showKanji;
    private boolean showHiragana;
    private String meaningLanguage;
    private String soundName;
    private String soundFilePath;
    private String quizType;

    @FXML
    private CheckBox reverseOrderCheckBox;

    @FXML
    private CheckBox showKanjiCheckBox;

    @FXML
    private CheckBox showHiraganaCheckBox;

    @FXML
    private ChoiceBox<String> meaningLanguageChoiceBox;

    @FXML
    private ChoiceBox<String> soundChoiceBox;

    @FXML
    private ChoiceBox<String> quizTypeChoiceBox;

    @FXML
    private Button startButton;

    public boolean getReverseOrder() {
        return reverseOrder;
    }

    public boolean getShowKanji() {
        return showKanji;
    }

    public boolean getShowHiragana() {
        return showHiragana;
    }

    public String getSoundName() {
        return soundName;
    }

    public String getSoundFilePath() {
        return soundFilePath;
    }

    public String getQuizType() {
        return quizType;
    }

    @FXML
    public void initialize() {
        reverseOrderCheckBox.setSelected(false);
        showKanjiCheckBox.setSelected(true);
        showHiraganaCheckBox.setSelected(false);

        SoundPlayer.addValueToSoundChoiceBox(soundChoiceBox);
        LingvaTranslate.addValueToMeaningLanguageChoiceBox(meaningLanguageChoiceBox);
        addValueToQuizTypeChoiceBox();
    }

    private void addValueToQuizTypeChoiceBox() {
        ObservableList<String> quizTypes = FXCollections.observableArrayList();

        quizTypes.addAll(QuizFunctionController.quizTypes);

        quizTypeChoiceBox.setItems(quizTypes);
        quizTypeChoiceBox.setValue(QuizFunctionController.quizTypes[0]);
    }

    @FXML
    private void handleStartClick(MouseEvent event) {
        reverseOrder = reverseOrderCheckBox.isSelected();
        showKanji = showKanjiCheckBox.isSelected();
        showHiragana = showHiraganaCheckBox.isSelected();
        soundName = soundChoiceBox.getValue();
        meaningLanguage = meaningLanguageChoiceBox.getValue();
        quizType = quizTypeChoiceBox.getValue();

        //Handle choice exception
        if (!showKanji && !showHiragana) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("You must select at least one of the two: Show Kanji or Show Hiragana.");
            alert.showAndWait();

            return;
        }

        if (!meaningLanguage.equals("English")) {
            try {
                FlashcardFunctionController.handleMeaningLanguage(meaningLanguage, QuizController.class);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                System.out.println(e.getMessage());
            }
        }

        if (soundName.equals("No music")) {
            soundName = null;
            soundFilePath = null;
        } else {
            File folder = new File(SoundPlayer.folderSoundPath);

            if (folder.exists() && folder.isDirectory()) {
                File[] files = folder.listFiles((dir, name) -> name.endsWith(".mp3"));

                if (files != null) {
                    for (File file : files) {
                        if (file.getName().replace(".mp3", "").equals(soundName)) {
                            soundFilePath = file.getAbsolutePath();
                        }
                    }
                }
            }
        }

        QuizController.quizFunctionController = this; //Must place there, before declaring loader

        try {
            String fxmlFilePath = null;
            if (quizType.equals(QuizFunctionController.quizTypes[0])) {
                fxmlFilePath = "FillUsingMeanings.fxml";
            } else if (quizType.equals(QuizFunctionController.quizTypes[1])) {
                fxmlFilePath = "FillUsingPictures.fxml";
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFilePath));

            //Replace QuizFunctionView by quiz view
            parentContainer.getChildren().clear();
            parentContainer.getChildren().add(loader.load());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}