package org.example.easyjapanese;

public class Quiz {
    private String question;
    private String answerFirstChoice;
    private String answerSecondChoice;
    private String otherInformation;

    public Quiz() {

    }

    public Quiz(String question, String answerFirstChoice, String answerSecondChoice, String otherInformation) {
        this.question = question;
        this.answerFirstChoice = answerFirstChoice;
        this.answerSecondChoice = answerSecondChoice;
        this.otherInformation = otherInformation;
    }

    public boolean isCorrectAnswer(String answer) {
        return answer.equals(answerFirstChoice) || answer.equals(answerSecondChoice);
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswerFirstChoice() {
        return answerFirstChoice;
    }

    public void setAnswerFirstChoice(String answerFirstChoice) {
        this.answerFirstChoice = answerFirstChoice;
    }

    public String getAnswerSecondChoice() {
        return answerSecondChoice;
    }

    public void setAnswerSecondChoice(String answerSecondChoice) {
        this.answerSecondChoice = answerSecondChoice;
    }

    public String getOtherInformation() {
        return otherInformation;
    }

    public void setOtherInformation(String otherInformation) {
        this.otherInformation = otherInformation;
    }
}
