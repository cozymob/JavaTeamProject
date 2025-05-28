import java.util.*;
import java.io.*;

public class QuizApp {
    public static void main(String[] args) throws IOException {
        List<Quiz> quizzes = QuizLoader.loadFromFile("ox_quiz.txt");
        QuizService manager = new QuizManager(quizzes);
        manager.run();
    }
}
