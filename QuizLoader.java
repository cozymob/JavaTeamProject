import java.io.*;
import java.util.*;

public class QuizLoader {
    public static List<Quiz> loadFromFile(String filename)  {
        //리스트 만들기
    	List<Quiz> quizzes = new ArrayList<>();

    	try {
    		//줄 단위로 파일 읽기
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;
            String number = "", question = "", answer = "", explanation = "";
            //한 줄씩 line으로 받기
            while ((line = br.readLine()) != null) {
                line = line.trim(); // 앞뒤 공백 제거
                //문제 번호 [1.1] -> 1.1
                if (line.startsWith("[") && line.endsWith("]")) {
                    number = line.substring(1, line.length() - 1); 
                 // Q:문제내용
                } else if (line.startsWith("Q:")) {
                    question = line.substring(2).trim(); 
                 // A:O 또는 X
                } else if (line.startsWith("A:")) {
                    answer = line.substring(2).trim(); 
                 // E:해설
                } else if (line.startsWith("E:")) {
                    explanation = line.substring(2).trim(); 
                    //퀴즈에 번호, 문제, 답, 해설 저장
                    quizzes.add(new Quiz(number, question, answer, explanation));
                }
            }
            //파일 닫기
            br.close();

        } catch (IOException e) {
            System.out.println("파일 오류 발생" + e.getMessage());
        }
   
        return quizzes;
    }
}
