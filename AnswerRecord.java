public class AnswerRecord {
    private Quiz quiz;
    private String userAnswer;

    public AnswerRecord(Quiz quiz, String userAnswer) {
        this.quiz = quiz;
        this.userAnswer = userAnswer;
    }

    public boolean isCorrect() {
        return quiz.getAnswer().equalsIgnoreCase(userAnswer);
    }

    public Quiz getQuiz() { return quiz; }
    public String getUserAnswer() { return userAnswer; }
}
