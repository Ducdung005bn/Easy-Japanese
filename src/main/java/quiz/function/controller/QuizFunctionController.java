package quiz.function.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import quiz.controller.QuizController;
import utils.LanguageHandler;
import utils.SoundPlayer;

import java.io.File;
import java.io.IOException;
import org.example.easyjapanese.Quiz;

public class QuizFunctionController {
    public static Pane parentContainer;

    public static final String[] quizTypes = {"Fill in the blank"};

    private boolean shuffleOrder;
    private String meaningLanguage;
    private String soundName;
    private String soundFilePath;
    private String quizType;

    @FXML
    private CheckBox shuffleOrderCheckBox;

    @FXML
    private ChoiceBox<String> meaningLanguageChoiceBox;

    @FXML
    private ChoiceBox<String> soundChoiceBox;

    @FXML
    private ChoiceBox<String> quizTypeChoiceBox;

    @FXML
    private Pane specificQuizFunction;

    @FXML
    private Button startButton;

    public boolean getShuffleOrder() {
        return shuffleOrder;
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

    public String getQuizType() {
        return quizType;
    }

    @FXML
    public void initialize() {
        shuffleOrderCheckBox.setSelected(false);

        SoundPlayer.addValueToSoundChoiceBox(soundChoiceBox);
        LanguageHandler.addValueToMeaningLanguageChoiceBox(meaningLanguageChoiceBox);
        addValueToQuizTypeChoiceBox();

        updateSpecificQuizFunction(quizTypes[0]);
        quizTypeChoiceBox.setOnAction(e -> {
            String selectedQuizType = quizTypeChoiceBox.getValue();
            updateSpecificQuizFunction(selectedQuizType);
        });
    }

    private void updateSpecificQuizFunction(String selectedQuizType) {
        specificQuizFunction.getChildren().clear();

        Pane newPane = null;

        try {
            if (selectedQuizType.equals(quizTypes[0])) {
                newPane = new FXMLLoader((getClass().getResource("/org/example/easyjapanese/FillInTheBlankFunctionView.fxml"))).load();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        specificQuizFunction.getChildren().add(newPane);
    }

    private void addValueToQuizTypeChoiceBox() {
        ObservableList<String> quizTypes = FXCollections.observableArrayList();

        quizTypes.addAll(QuizFunctionController.quizTypes);

        quizTypeChoiceBox.setItems(quizTypes);
        quizTypeChoiceBox.setValue(QuizFunctionController.quizTypes[0]);
    }

    @FXML
    private void handleStartClick(MouseEvent event) {
        shuffleOrder = shuffleOrderCheckBox.isSelected();
        soundName = soundChoiceBox.getValue();
        meaningLanguage = meaningLanguageChoiceBox.getValue();
        quizType = quizTypeChoiceBox.getValue();

        if (!meaningLanguage.equals("English")) {
            try {
                LanguageHandler.handleMeaningLanguage(
                        meaningLanguage,
                        QuizController.class,
                        "quiz",
                        Quiz::getQuestion,
                        Quiz::setOtherInformation
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

        QuizController.quizFunctionController = this; //Must place there, before declaring loader

        try {
            String fxmlFilePath = null;
            if (quizType.equals(QuizFunctionController.quizTypes[0])) {
                fxmlFilePath = "/org/example/easyjapanese/FillInTheBlankView.fxml";
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