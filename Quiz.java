public class Quiz {
    private String number;
    private String question;
    private String answer;
    private String explanation;

    public Quiz(String number, String question, String answer, String explanation) {
        this.number = number;
        this.question = question;
        this.answer = answer;
        this.explanation = explanation;
    }

    public String getNumber() { return number; }
    public String getQuestion() { return question; }
    public String getAnswer() { return answer; }
    public String getExplanation() { return explanation; }
}
