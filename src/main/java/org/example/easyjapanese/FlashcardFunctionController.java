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

public class FlashcardFunctionController {
    public static Pane parentContainer;
    private boolean reverseOrder;
    private boolean speakMeaning;
    private boolean speakVocabulary;
    private boolean showKanji;
    private boolean showHiragana;
    private String soundFileName;

    @FXML
    private CheckBox reverseOrderCheckBox;

    @FXML
    private CheckBox speakMeaningCheckBox;

    @FXML
    private CheckBox speakVocabularyCheckBox;

    @FXML
    private CheckBox showKanjiCheckBox;

    @FXML
    private CheckBox showHiraganaCheckBox;

    @FXML
    private ChoiceBox<String> soundChoiceBox;

    @FXML
    private Button startButton;

    public boolean getReverseOrder() {
        return reverseOrder;
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

    public String getSoundFileName() {
        return soundFileName;
    }

    @FXML
    public void initialize() {
        reverseOrderCheckBox.setSelected(false);
        speakMeaningCheckBox.setSelected(true);
        speakVocabularyCheckBox.setSelected(true);
        showKanjiCheckBox.setSelected(true);
        showHiraganaCheckBox.setSelected(false);
        addValueToSoundChoiceBox();
    }

    private void addValueToSoundChoiceBox() {
        String folderSoundPath = "C:\\Users\\Admin\\Documents\\EasyJapanese\\soundContainer\\when learning";

        ObservableList<String> soundFiles = FXCollections.observableArrayList();
        File folder = new File(folderSoundPath);

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".mp3"));

            if (files != null) {
                for (File file : files) {
                    soundFiles.add(file.getName());
                }
            }
        }

        soundFiles.add("No music");

        soundChoiceBox.setItems(soundFiles);
        soundChoiceBox.setValue("No music");
    }

    @FXML
    private void handleStartClick(MouseEvent event) {
        reverseOrder = reverseOrderCheckBox.isSelected();
        speakMeaning = speakMeaningCheckBox.isSelected();
        speakVocabulary = speakVocabularyCheckBox.isSelected();
        showKanji = showKanjiCheckBox.isSelected();
        showHiragana = showHiraganaCheckBox.isSelected();
        soundFileName = soundChoiceBox.getValue();

        //Handle choice exception
        if (!showKanji && !showHiragana) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("You must select at least one of the two: Show Kanji or Show Hiragana.");
            alert.showAndWait();

            return;
        }

        if (soundFileName.equals("No music")) {
            soundFileName = null;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FlashcardView.fxml"));
            BorderPane flashcardView = loader.load();

            //Set up flashcardView
            FlashcardController.flashcardFunctionController = this;

            //Replace flashcardFunctionView by flashcardView
            parentContainer.getChildren().clear();
            parentContainer.getChildren().add(flashcardView);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}