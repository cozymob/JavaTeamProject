import java.io.*;
import java.util.*;

public class QuizLoader {
    public static List<Quiz> loadFromFile(String filename) throws IOException {
        List<Quiz> quizzes = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        String number = "", question = "", answer = "", explanation = "";

        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("[") && line.endsWith("]")) {
                number = line.substring(1, line.length() - 1);
            } else if (line.startsWith("Q:")) {
                question = line.substring(2).trim();
            } else if (line.startsWith("A:")) {
                answer = line.substring(2).trim();
            } else if (line.startsWith("E:")) {
                explanation = line.substring(2).trim();
                quizzes.add(new Quiz(number, question, answer, explanation));
            }
        }
        br.close();
        return quizzes;
    }
}
