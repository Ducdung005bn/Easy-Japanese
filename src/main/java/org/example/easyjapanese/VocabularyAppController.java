package org.example.easyjapanese;

import flashcard.FlashcardController;
import flashcard.FlashcardFunctionController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.*;
import quiz.MonsterHunterController;
import quiz.QuizController;
import quiz.QuizFunctionController;
import utils.DatabaseConnector;
import utils.InterfaceHandler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class VocabularyAppController {
    private final Map<String, Map<Integer, List<Integer>>> menuData = createMenuData();
    private final Map<String, String> bookData = createBookData();

    @FXML
    private TreeView<String> itemMenu;

    @FXML
    private Pane contentPane;

    @FXML
    public void initialize() {
        TreeItem<String> rootItem = new TreeItem<>("Menu");
        rootItem.setExpanded(true);

        organizeMenu(rootItem);

        // Set rootItem as the top-level menu in the TreeView
        itemMenu.setRoot(rootItem);

        // Add a listener to handle item selection in the TreeView
        itemMenu.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            InterfaceHandler.removeAllRemainings(); //remove all sounds and timelines

            if (newVal.getValue().equals("Flashcard")) {
                showFlashcard(newVal);
            } else if (newVal.getValue().equals("Quiz")) {
                showQuiz(newVal);
            } else if (newVal.getValue().equals("Word List")) {


                List<Quiz> quizList = new ArrayList<>();
                quizList.add(new Quiz("男性", "だんせい", null, null));
                quizList.add(new Quiz("女性", "じょせい", null, null));
                quizList.add(new Quiz("高齢", "こうれい", null, null));
                quizList.add(new Quiz("年上", "としうえ", null, null));
                quizList.add(new Quiz("目上", "めうえ", null, null));
                quizList.add(new Quiz("先輩", "せんぱい", null, null));
                quizList.add(new Quiz("後輩", "こうはい", null, null));
                quizList.add(new Quiz("上司", "じょうし", null, null));
                quizList.add(new Quiz("相手", "あいて", null, null));
                quizList.add(new Quiz("知り合い", "しりあい", null, null));

                MonsterHunterController.quizList = quizList;
                MonsterHunterController.parentContainer = contentPane;


                //EXAMPLE
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/easyjapanese/MonsterHunterView.fxml"));
                    StackPane example = loader.load();

                    contentPane.getChildren().clear();
                    contentPane.getChildren().add(example);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //EXAMPLE

            }
        });

        //Set a front side for itemMenu
        itemMenu.setCellFactory(treeView -> new TreeCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-size: 16px;");
                }
            }
        });
    }

    private void organizeMenu(TreeItem<String> rootItem) {
        for (String bookISBN : menuData.keySet()) {
            TreeItem<String> bookItem = new TreeItem<>(getItemName(bookISBN, -1));
            rootItem.getChildren().add(bookItem);

            for (int unit : menuData.get(bookISBN).keySet()) {
                TreeItem<String> unitItem = new TreeItem<>(getItemName("unit", unit));
                bookItem.getChildren().add(unitItem);

                for (int functionality : menuData.get(bookISBN).get(unit)) {
                    TreeItem<String> funcItem = new TreeItem<>(getItemName("functionality", functionality));
                    unitItem.getChildren().add(funcItem);
                }
            }
        }
    }

    private String getItemName(String stringInput, int intInput) {
        if (stringInput.equals("unit")) {
            return "Unit " + intInput;
        } else if (stringInput.equals("functionality")) {
            switch(intInput) {
                case 0: return "Flashcard";
                case 1: return "Quiz";
                case 2: return "Word List";
                case 3: return "PDF";
                case 4: return "Question And Answer";
            }
        }

        if (bookData.containsKey(stringInput)) {
            return bookData.get(stringInput);
        }

        return null;
    }

    private void showFlashcard(TreeItem<String> newVal) {
        setVocabularyList(newVal);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/easyjapanese/FlashcardFunctionView.fxml"));
            GridPane flashcardFunctionView = loader.load();

            //To be able to clear FlashcardFunctionView to add FlashcardView
            FlashcardFunctionController.parentContainer = contentPane;

            contentPane.getChildren().clear();
            contentPane.getChildren().add(flashcardFunctionView);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void showQuiz (TreeItem<String> newVal) {
        setQuizList(newVal);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/easyjapanese/QuizFunctionView.fxml"));
            VBox quizFunctionView = loader.load();

            QuizFunctionController.parentContainer = contentPane;

            contentPane.getChildren().clear();
            contentPane.getChildren().add(quizFunctionView);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void setQuizList(TreeItem<String> newVal) {
        TreeItem<String> unit = newVal.getParent();
        TreeItem<String> bookName = unit.getParent();

        String[] parts = unit.getValue().split(" ");

        String bookISBN = null;
        for (Map.Entry<String, String> entry : bookData.entrySet()) {
            if (bookName.getValue().equals(entry.getValue())) {
                bookISBN = entry.getKey();
            }
        }

        List<Quiz> quizList = new ArrayList<Quiz>();

        ResultSet resultSet = DatabaseConnector.getInstance().executeQuery(
                "SELECT * FROM " + bookISBN + " WHERE lessonID = " +  (Integer.parseInt(parts[1]))  + ";");

        try {
            while (resultSet.next()) {
                Quiz quiz = new Quiz();

                quiz.setQuestion(resultSet.getString("meaning").replace("/", " or "));
                quiz.setOtherInformation(resultSet.getString("meaning").replace("/", " or "));
                quiz.setAnswerFirstChoice(resultSet.getString("japaneseWord"));
                quiz.setAnswerSecondChoice(resultSet.getString("hiragana"));

                quizList.add(quiz);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        QuizController.quizList = quizList;
    }

    private void setVocabularyList(TreeItem<String> newVal) {
        TreeItem<String> unit = newVal.getParent();
        TreeItem<String> bookName = unit.getParent();

        String[] parts = unit.getValue().split(" ");

        String bookISBN = null;
        for (Map.Entry<String, String> entry : bookData.entrySet()) {
            if (bookName.getValue().equals(entry.getValue())) {
                bookISBN = entry.getKey();
            }
        }

        List<Vocabulary> vocabularyList = new ArrayList<Vocabulary>();

        ResultSet resultSet = DatabaseConnector.getInstance().executeQuery(
                "SELECT * FROM " + bookISBN + " WHERE lessonID = " + (Integer.parseInt(parts[1])) + ";");

        try {
            while (resultSet.next()) {
                Vocabulary vocabulary = new Vocabulary();

                vocabulary.setVocabularyID(Integer.parseInt(resultSet.getString("vocabularyID")));
                vocabulary.setVocabulary(resultSet.getString("japaneseWord"));
                vocabulary.setHiragana(resultSet.getString("hiragana"));
                //Lingva translator will fail if there is "/".
                vocabulary.setEnglishMeaning(resultSet.getString("meaning").replace("/", " or "));
                vocabulary.setOtherInformation(resultSet.getString("meaning").replace("/", " or "));
                vocabularyList.add(vocabulary);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        FlashcardController.vocabularyList = vocabularyList;
    }

    private Map<String, Map<Integer, List<Integer>>> createMenuData() {
        Map<String, Map<Integer, List<Integer>>> menuData = new LinkedHashMap<>();
        String filePath = "C:\\Users\\Admin\\Documents\\EasyJapanese\\MenuData";

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            int numberOfBooks = Integer.parseInt(bufferedReader.readLine().trim());

            for (int i = 0; i < numberOfBooks; i++) {
                String isbn = bufferedReader.readLine().trim();
                Map<Integer, List<Integer>> unitMap = new HashMap<>();

                List<Integer> functions;
                int unitIndex = 1;

                while (bufferedReader.ready()) {
                    String line = bufferedReader.readLine().trim();
                    if (line.isEmpty()) break;

                    String[] values = line.split(" ");
                    functions = new ArrayList<>();

                    for (int j = 0; j < values.length; j++) {
                        if (values[j].equals("1")) {
                            functions.add(j);
                        }
                    }

                    unitMap.put(unitIndex++, functions);
                }

                menuData.put(isbn, unitMap);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return menuData;
    }

    private Map<String, String> createBookData() {
        Map<String, String> bookData = new LinkedHashMap<>();
        bookData.put("ISBN9784757418578", "耳から覚える N3 語彙");
        bookData.put("ISBN9784883196463", "皆の日本語 II 語彙");

        return bookData;
    }
}
