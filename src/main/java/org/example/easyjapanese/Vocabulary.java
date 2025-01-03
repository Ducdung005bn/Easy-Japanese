package org.example.easyjapanese;

public class Vocabulary {
    private int vocabularyID;
    private String vocabulary;
    private String hiragana;
    private String meaning;
    private int lessonID;

    public Vocabulary() {
        //To do
    }

    public Vocabulary(int vocabularyID,
                      String vocabulary,
                      String hiragana,
                      String meaning,
                      int lessonID) {
        this.vocabularyID = vocabularyID;
        this.vocabulary = vocabulary;
        this.hiragana = hiragana;
        this.meaning = meaning;
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

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public int getLessonID() {
        return lessonID;
    }

    public void setLessonID(int lessonID) {
        this.lessonID = lessonID;
    }
}
