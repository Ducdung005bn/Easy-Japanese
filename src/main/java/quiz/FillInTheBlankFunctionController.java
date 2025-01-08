package quiz;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

public class FillInTheBlankFunctionController {
    @FXML
    private Spinner<Integer> timeLimitSpinner;

    @FXML
    private Spinner<Integer> timeDelaySpinner;

    @FXML
    private CheckBox showMeaningsCheckBox;

    @FXML
    private CheckBox showImagesCheckBox;

    @FXML
    private CheckBox retryCheckBox;

    public Integer getTimeLimit() {
        return timeLimitSpinner.getValue();
    }

    public Integer getTimeDelay() {
        return timeDelaySpinner.getValue();
    }

    public boolean getShowMeanings() {
        return showMeaningsCheckBox.isSelected();
    }

    public boolean getShowImages() {
        return showImagesCheckBox.isSelected();
    }

    public boolean getRetry() {
        return retryCheckBox.isSelected();
    }

    public void initialize() {
        //From 5 to 120 (s). Default value is 30. Step is 5.
        SpinnerValueFactory<Integer> timeLimitValueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 120, 30, 5);
        timeLimitSpinner.setValueFactory(timeLimitValueFactory);

        // From 1 to 30 (s). Default value is 5. Step is 1.
        SpinnerValueFactory<Integer> timeDelayValueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 60, 5, 1);
        timeDelaySpinner.setValueFactory(timeDelayValueFactory);

        showMeaningsCheckBox.setSelected(true);
        showImagesCheckBox.setSelected(true);

        showMeaningsCheckBox.setOnAction(event -> {
            checkBoxHandler(true, false);
        });
        showImagesCheckBox.setOnAction(event -> {
            checkBoxHandler(false, true);
        });

        retryCheckBox.setSelected(true);

        FillInTheBlankController.fillInTheBlankFunctionController = this;
    }

    private void checkBoxHandler(boolean changeShowMeaningsCheckBox, boolean changeShowImagesCheckBox) {
        if (!showMeaningsCheckBox.isSelected() && !showImagesCheckBox.isSelected()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("You must select at least one of the two: Show Meanings or Show Images.");
            alert.showAndWait();

            if (changeShowMeaningsCheckBox) {
                showMeaningsCheckBox.setSelected(true);
            } else if (changeShowImagesCheckBox) {
                showImagesCheckBox.setSelected(true);
            }
        }
    }
}
