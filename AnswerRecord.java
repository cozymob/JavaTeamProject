public class AnswerRecord {
    private Quiz quiz;
    private String userAnswer;

    public AnswerRecord(Quiz quiz, String userAnswer) {
        this.quiz = quiz;
        this.userAnswer = userAnswer;
    }
    
    //퀴즈 정답이 유저 답과 같은지 비교(답이 맞았는지 틀렸는지)
    public boolean isCorrect() {
        return quiz.getAnswer().equalsIgnoreCase(userAnswer);
    }
    //퀴즈 전체 들고오기
    public Quiz getQuiz() { 
    	return quiz; }
    
    //유저 답 들고오기
    public String getUserAnswer() { 
    	return userAnswer; }
}
