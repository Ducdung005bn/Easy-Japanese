package org.example.easyjapanese;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EnglishTextToSpeech {
    private static EnglishTextToSpeech instance;
    private Voice voice;

    private EnglishTextToSpeech() {
        System.setProperty("freetts.voices",
                "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");

        voice = VoiceManager.getInstance().getVoice("kevin16");
        voice.allocate();
        voice.setPitch(120.0f);
    }

    public static EnglishTextToSpeech getInstance() {
        if (instance == null) {
            instance = new EnglishTextToSpeech();
        }
        return instance;
    }

    public void speakEnglishWords(String englishWords) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            voice.speak(englishWords);
        });
        executorService.shutdown();
    }
}