package flashcard;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import utils.LanguageHandler;
import utils.SoundPlayer;

import java.io.File;
import java.io.IOException;
import org.example.easyjapanese.Vocabulary;

public class FlashcardFunctionController {
    public static Pane parentContainer;

    private boolean shuffleOrder;
    private boolean speakMeaning;
    private boolean speakVocabulary;
    private boolean showKanji;
    private boolean showHiragana;
    private String meaningLanguage;
    private String soundName;
    private String soundFilePath;

    @FXML
    private CheckBox shuffleOrderCheckBox;

    @FXML
    private CheckBox speakVocabularyCheckBox;

    @FXML
    private CheckBox speakMeaningCheckBox;

    @FXML
    private CheckBox showKanjiCheckBox;

    @FXML
    private CheckBox showHiraganaCheckBox;

    @FXML
    private ChoiceBox<String> meaningLanguageChoiceBox;

    @FXML
    private ChoiceBox<String> soundChoiceBox;

    @FXML
    private Button startButton;

    public boolean getShuffleOrder() {
        return shuffleOrder;
    }

    public boolean getSpeakMeaning() {
        return speakMeaning;
    }

    public boolean getSpeakVocabulary() {
        return speakVocabulary;
    }

    public boolean getShowKanji() {
        return showKanji;
    }

    public boolean getShowHiragana() {
        return showHiragana;
    }

    public String getMeaningLanguage() {
        return meaningLanguage;
    }

    public String getSoundName() {
        return soundName;
    }

    public String getSoundFilePath() {
        return soundFilePath;
    }

    @FXML
    public void initialize() {
        shuffleOrderCheckBox.setSelected(false);
        speakVocabularyCheckBox.setSelected(false);
        speakMeaningCheckBox.setSelected(true);
        showKanjiCheckBox.setSelected(true);
        showHiraganaCheckBox.setSelected(false);

        showKanjiCheckBox.setOnAction(event -> {
            checkBoxHandler(true, false);
        });
        showHiraganaCheckBox.setOnAction(event -> {
            checkBoxHandler(false, true);
        });

        SoundPlayer.addValueToSoundChoiceBox(soundChoiceBox);
        LanguageHandler.addValueToMeaningLanguageChoiceBox(meaningLanguageChoiceBox);
    }

    private void checkBoxHandler(boolean changeShowKanjiCheckBox, boolean changeShowHiraganaCheckBox) {
        if (!showKanjiCheckBox.isSelected() && !showHiraganaCheckBox.isSelected()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("You must select at least one of the two: Show Kanji or Show Hiragana.");
            alert.showAndWait();

            if (changeShowKanjiCheckBox) {
                showKanjiCheckBox.setSelected(true);
            } else if (changeShowHiraganaCheckBox) {
                showHiraganaCheckBox.setSelected(true);
            }
        }
    }

    @FXML
    private void handleStartClick(MouseEvent event) {
        shuffleOrder = shuffleOrderCheckBox.isSelected();
        speakMeaning = speakMeaningCheckBox.isSelected();
        speakVocabulary = speakVocabularyCheckBox.isSelected();
        showKanji = showKanjiCheckBox.isSelected();
        showHiragana = showHiraganaCheckBox.isSelected();
        soundName = soundChoiceBox.getValue();
        meaningLanguage = meaningLanguageChoiceBox.getValue();

        if (!meaningLanguage.equals("English")) {
            speakMeaning = false;

            try {
                LanguageHandler.handleMeaningLanguage(
                        meaningLanguage,
                        FlashcardController.class,
                        "vocabulary",
                        Vocabulary::getEnglishMeaning,
                        Vocabulary::setOtherInformation
                );
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

        FlashcardController.flashcardFunctionController = this; //Must place there, before declaring loader

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/easyjapanese/FlashcardView.fxml"));
            VBox flashcardView = loader.load();

            //Replace flashcardFunctionView by flashcardView
            parentContainer.getChildren().clear();
            parentContainer.getChildren().add(flashcardView);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}