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
import java.util.List;
import java.util.Map;

public class FlashcardFunctionController {
    public static Pane parentContainer;

    private boolean reverseOrder;
    private boolean speakMeaning;
    private boolean speakVocabulary;
    private boolean showKanji;
    private boolean showHiragana;
    private String meaningLanguage;
    private String soundName;
    private String soundFilePath;

    @FXML
    private CheckBox reverseOrderCheckBox;

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

    public String getSoundName() {
        return soundName;
    }

    public String getSoundFilePath() {
        return soundFilePath;
    }

    @FXML
    public void initialize() {
        reverseOrderCheckBox.setSelected(false);
        speakVocabularyCheckBox.setSelected(false);
        speakMeaningCheckBox.setSelected(true);
        showKanjiCheckBox.setSelected(true);
        showHiraganaCheckBox.setSelected(false);

        SoundPlayer.addValueToSoundChoiceBox(soundChoiceBox);
        LingvaTranslate.addValueToMeaningLanguageChoiceBox(meaningLanguageChoiceBox);
    }

    @FXML
    private void handleStartClick(MouseEvent event) {
        reverseOrder = reverseOrderCheckBox.isSelected();
        speakMeaning = speakMeaningCheckBox.isSelected();
        speakVocabulary = speakVocabularyCheckBox.isSelected();
        showKanji = showKanjiCheckBox.isSelected();
        showHiragana = showHiraganaCheckBox.isSelected();
        soundName = soundChoiceBox.getValue();
        meaningLanguage = meaningLanguageChoiceBox.getValue();

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
            speakMeaning = false;

            try {
                handleMeaningLanguage(meaningLanguage, FlashcardController.class);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FlashcardView.fxml"));
            BorderPane flashcardView = loader.load();

            //Replace flashcardFunctionView by flashcardView
            parentContainer.getChildren().clear();
            parentContainer.getChildren().add(flashcardView);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void handleMeaningLanguage(String meaningLanguage, Class<?> clazz) throws NoSuchFieldException, IllegalAccessException {
        final int MAX_WORDS_PER_REQUEST = 60;
        final int REQUEST_DELAY_MS = 1000;

        LingvaTranslate.targetLanguage = meaningLanguage;

        List<Vocabulary> vocabularyList = (List<Vocabulary>) clazz.getField("vocabularyList").get(null);

        int totalWords = vocabularyList.size();
        int numberOfRequests = (int) Math.ceil((double) totalWords / MAX_WORDS_PER_REQUEST);

        for (int i = 0; i < numberOfRequests; i++) {
            int start = i * MAX_WORDS_PER_REQUEST;
            int end = Math.min(start + MAX_WORDS_PER_REQUEST, totalWords);

            List<Vocabulary> chunk = vocabularyList.subList(start, end);
            String meaningsToTranslate = String.join("|", chunk.stream()
                    .map(Vocabulary::getMeaning)
                    .toArray(String[]::new));

            new Thread(() -> {
                try {
                    String translatedMeanings = LingvaTranslate.translate(meaningsToTranslate);

                    String[] meanings = translatedMeanings.split("\\|");
                    for (int j = 0; j < meanings.length; j++) {
                        if (meanings[j].contains(",")) {
                            String[] parts = meanings[j].split(",");
                            String firstPart = parts[0].trim();
                            String secondPart = parts[1].trim();

                            if (firstPart.equalsIgnoreCase(secondPart)) {
                                meanings[j] = firstPart;
                            }
                        }

                        vocabularyList.get(start + j).setMeaning(meanings[j]);
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }).start();

            try {
                Thread.sleep(REQUEST_DELAY_MS);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}