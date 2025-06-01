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
    //문제 번호
    public String getNumber() { 
    	return number; }
    //문제
    public String getQuestion() { 
    	return question; }
    //답
    public String getAnswer() { 
    	return answer; }
    //해설
    public String getExplanation() { 
    	return explanation; }
}
