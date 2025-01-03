package org.example.easyjapanese;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static java.lang.Math.abs;

public class VocabularyController {
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
            if (newVal.getValue().equals("Flashcard")) {
                showFlashcard(newVal);
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
                case 3: return "Word List PDF";
                case 4: return "Question And Answer";
            }
        }

        if (bookData.containsKey(stringInput)) {
            return bookData.get(stringInput);
        }

        return null;
    }

    private void showFlashcard(TreeItem<String> newVal) {
        TreeItem<String> unit = newVal.getParent();
        TreeItem<String> bookName = unit.getParent();

        String[] parts = unit.getValue().split(" ");

        String bookISBN = null;
        for (Map.Entry<String, String> entry : bookData.entrySet()) {
            if (bookName.getValue().equals(entry.getValue())) {
                bookISBN = entry.getKey();
            }
        }

        contentPane.getChildren().clear();

        FlashcardController.vocabularyList = getVocabularyList(bookISBN, Integer.parseInt(parts[1]));

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FlashcardView.fxml"));
            BorderPane flashcardView = loader.load();

            contentPane.getChildren().clear();
            contentPane.getChildren().add(flashcardView);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private List<Vocabulary> getVocabularyList(String bookISBN, int unit) {
        List<Vocabulary> vocabularyList = new ArrayList<Vocabulary>();

        ResultSet resultSet = DatabaseConnector.getInstance().executeQuery(
                "SELECT * FROM " + bookISBN + " WHERE lessonID = " + String.valueOf(unit) + ";");

        try {
            while (resultSet.next()) {
                Vocabulary vocabulary = new Vocabulary();

                vocabulary.setVocabularyID(Integer.parseInt(resultSet.getString("vocabularyID")));
                vocabulary.setVocabulary(resultSet.getString("japaneseWord"));
                vocabulary.setHiragana(resultSet.getString("hiragana"));
                vocabulary.setMeaning(resultSet.getString("meaning"));
                vocabularyList.add(vocabulary);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return vocabularyList;
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
