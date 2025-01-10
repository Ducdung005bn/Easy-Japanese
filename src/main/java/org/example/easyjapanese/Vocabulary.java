package org.example.easyjapanese;

public class Vocabulary {
    private int vocabularyID;
    private String vocabulary;
    private String hiragana;
    private String englishMeaning;
    private String otherInformation;
    private int lessonID;

    public Vocabulary() {
        //To do
    }

    public Vocabulary(int vocabularyID,
                      String vocabulary,
                      String hiragana,
                      String englishMeaning,
                      String otherInformation,
                      int lessonID) {
        this.vocabularyID = vocabularyID;
        this.vocabulary = vocabulary;
        this.hiragana = hiragana;
        this.englishMeaning = englishMeaning;
        this.otherInformation = otherInformation;
        this.lessonID = lessonID;
    }

    public int getVocabularyID() {
        return vocabularyID;
    }

    public void setVocabularyID(int vocabularyID) {
        this.vocabularyID = vocabularyID;
    }

    public String getVocabulary() {
        return vocabulary;
    }

    public void setVocabulary(String vocabulary) {
        this.vocabulary = vocabulary;
    }

    public String getHiragana() {
        return hiragana;
    }

    public void setHiragana(String hiragana) {
        this.hiragana = hiragana;
    }

    public String getEnglishMeaning() {
        return englishMeaning;
    }

    public void setEnglishMeaning(String englishMeaning) {
        this.englishMeaning = englishMeaning;
    }

    public int getLessonID() {
        return lessonID;
    }

    public void setLessonID(int lessonID) {
        this.lessonID = lessonID;
    }

    public String getOtherInformation() {
        return otherInformation;
    }

    public void setOtherInformation(String otherInformation) {
        this.otherInformation = otherInformation;
    }
}
