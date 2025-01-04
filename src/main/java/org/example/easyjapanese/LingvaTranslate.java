package org.example.easyjapanese;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class LingvaTranslate {
    public static String sourceLanguage = "English";
    public static String targetLanguage = "English";

    public static final Map<String, String> languageCodeMap = setLanguageCodeMap();

    public static String translate(String textToTranslate) {
        try {
            String url = "https://lingva.ml/api/v1/"
                    + languageCodeMap.get(sourceLanguage) + "/"
                    + languageCodeMap.get(targetLanguage) + "/"
                    + textToTranslate;
            Document document = Jsoup.connect(url).ignoreContentType(true).get();

            String jsonResponse = document.body().text();
            JSONObject jsonObject = new JSONObject(jsonResponse);

            return jsonObject.getString("translation");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static void addValueToMeaningLanguageChoiceBox(ChoiceBox<String> meaningLanguageChoiceBox) {
        ObservableList<String> languageNames = FXCollections.observableArrayList();

        for (Map.Entry<String, String> entry : languageCodeMap.entrySet()) {
            if (!entry.getKey().equals("Japanese")) {
                languageNames.add(entry.getKey());
            }
        }

        meaningLanguageChoiceBox.setItems(languageNames);
        meaningLanguageChoiceBox.setValue("English");
    }

    private static Map<String, String> setLanguageCodeMap() {
        Map<String, String> languageCodeMap = new HashMap<>();
        languageCodeMap.put("English", "en");
        languageCodeMap.put("Japanese", "ja");
        languageCodeMap.put("Vietnamese", "vi");

        return languageCodeMap;
    }
}
