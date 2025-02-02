package utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class LanguageHandler {
    //For translating from sourceLanguage to targetLanguage
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

    public static <T> void handleMeaningLanguage(
            String meaningLanguage,
            Class<?> clazz,
            String type,
            Function<T, String> getMeaning,
            BiConsumer<T, String> setOtherInformation
    ) throws NoSuchFieldException, IllegalAccessException {
        final int MAX_WORDS_PER_REQUEST = 60;
        final int REQUEST_DELAY_MS = 1000;

        LanguageHandler.targetLanguage = meaningLanguage;

        List<T> itemList = (List<T>) clazz.getField(type.equals("vocabulary") ? "vocabularyList" : "quizList").get(null);

        int totalItems = itemList.size();
        int numberOfRequests = (int) Math.ceil((double) totalItems / MAX_WORDS_PER_REQUEST);

        for (int i = 0; i < numberOfRequests; i++) {
            int start = i * MAX_WORDS_PER_REQUEST;
            int end = Math.min(start + MAX_WORDS_PER_REQUEST, totalItems);

            List<T> chunk = itemList.subList(start, end);
            String meaningsToTranslate = String.join("|", chunk.stream()
                    .map(getMeaning)
                    .toArray(String[]::new));

            new Thread(() -> {
                try {
                    String translatedMeanings = LanguageHandler.translate(meaningsToTranslate);

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
                        setOtherInformation.accept(chunk.get(j), meanings[j]);
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


    private static Map<String, String> setLanguageCodeMap() {
        Map<String, String> languageCodeMap = new HashMap<>();
        languageCodeMap.put("English", "en");
        languageCodeMap.put("Japanese", "ja");
        languageCodeMap.put("Vietnamese", "vi");

        return languageCodeMap;
    }
}
