import java.util.*;

public class QuizManager implements QuizService {
    private List<Quiz> allQuizzes;
    private List<AnswerRecord> records;
    private Scanner scanner = new Scanner(System.in);
    private Random random = new Random();

    public QuizManager(List<Quiz> quizzes) {
        this.allQuizzes = quizzes;
        this.records = new ArrayList<>();
    }

    public void run() {
    	//퀴즈 시작 Y(y)입력
    	String ans;
    	while(true) {
        System.out.print("퀴즈를 시작하려면 Y를, 끝내려면 Q를 입력하세요: ");
        ans=scanner.nextLine().trim();
        if (!ans.equalsIgnoreCase("Y")&&!ans.equalsIgnoreCase("Q")) {
        	System.out.println("올바르게 입력해주세요.");
        	}
        else if(ans.equalsIgnoreCase("Q")) {
        	System.out.println("퀴즈 프로그램을 종료합니다.");
        	return;
        }
        else if(ans.equalsIgnoreCase("Y")) {
        	System.out.println("퀴즈 프로그램을 시작합니다.");
        	break;
        }
    	}
    	
    	//퀴즈 메뉴 선택
        while (true) {
            System.out.println("1. 랜덤 문제 풀기");
            System.out.println("2. 분야별로 풀어보기 (10문제)");
            System.out.println("3. 끝내기");
            String choice = scanner.nextLine().trim();
            if (choice.equals("1")) {
                randomSolve();
            } else if (choice.equals("2")) {
                categorySolve();
            } else if (choice.equals("3")) {
                System.out.println("퀴즈 프로그램을 종료합니다.");
                System.exit(0);
            } else {
                System.out.println("올바른 숫자를 입력해주세요.(1~3)");
            }
        }
    }

    private void randomSolve() {
        int count = 0;
        while (true) {
            try {
                System.out.print("몇 문제를 푸시겠습니까? (1~77): ");
                count = Integer.parseInt(scanner.nextLine().trim());
                if (count < 1 || count > 77) {
                    System.out.println("1~77 사이의 숫자를 입력해주세요.");
                } else break;
            } catch (NumberFormatException e) {
                System.out.println("숫자를 정확히 입력해주세요.");
            }
        }

        Collections.shuffle(allQuizzes);
        records.clear();
        for (int i = 0; i < count && i < allQuizzes.size(); i++) {
            Quiz q = allQuizzes.get(i);
            System.out.println((i + 1) + ". " + q.getQuestion());
            String input = getOXInput();
            records.add(new AnswerRecord(q, input));
        }
        showResult();
    }

    private void categorySolve() {
        records.clear();
        int index = 1;
        for (int cat = 1; cat <= 10; cat++) {
            List<Quiz> group = new ArrayList<>();
            for (Quiz q : allQuizzes) {
                if (q.getNumber().startsWith(cat + ".")) group.add(q);
            }
            if (!group.isEmpty()) {
                Quiz q = group.get(random.nextInt(group.size()));
                System.out.println(index + ". " + q.getQuestion());
                String input = getOXInput();
                records.add(new AnswerRecord(q, input));
                index++;
            }
        }
        showResult();
    }

    private String getOXInput() {
        while (true) {
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("O") || input.equals("X")) return input;
            System.out.println("O 또는 X만 입력해주세요.");
        }
    }

    private void showResult() {
        long correct = records.stream().filter(AnswerRecord::isCorrect).count();
        System.out.println("총 맞힌 개수: " + correct + " / " + records.size());
        System.out.printf("점수: %.1f점\n", correct * 100.0 / records.size());

        while (true) {
            System.out.println("1. 전체 풀이 보기");
            System.out.println("2. 오답 풀이 보기");
            System.out.println("3. 끝내기");
            if (records.stream().anyMatch(r -> !r.isCorrect()))
                System.out.println("4. 다시 오답 퀴즈 풀기");
            System.out.println("5. 메인으로 돌아가기");
            String sel = scanner.nextLine().trim();
            if (sel.equals("1")) {
                showReview(true);
            } else if (sel.equals("2")) {
                showReview(false);
            } else if (sel.equals("3")) {
                System.out.println("퀴즈가 끝났습니다.");
                System.exit(0);
            } else if (sel.equals("4") && records.stream().anyMatch(r -> !r.isCorrect())) {
                List<Quiz> wrongQuizzes = new ArrayList<>();
                for (AnswerRecord r : records) {
                    if (!r.isCorrect()) wrongQuizzes.add(r.getQuiz());
                }
                records.clear();
                System.out.println("오답 퀴즈를 다시 풉니다.");
                int index = 1;
                for (Quiz q : wrongQuizzes) {
                    System.out.println(index + ". " + q.getQuestion());
                    String input = getOXInput();
                    records.add(new AnswerRecord(q, input));
                    index++;
                }
                showResult();
            } else if (sel.equals("5")) {
                System.out.println("메인화면으로 돌아갑니다.\n");
                run();
                return;
            } else {
                System.out.println("1~5 중에서 다시 선택해주세요.");
            }
        }
    }

    private void showReview(boolean all) {
        int index = 1;
        for (AnswerRecord r : records) {
            if (all || !r.isCorrect()) {
                System.out.println(index + ". 문제: " + r.getQuiz().getQuestion());
                System.out.println("   내 답: " + r.getUserAnswer() + " / 정답: " + r.getQuiz().getAnswer());
                System.out.println("   해설: " + r.getQuiz().getExplanation());
                System.out.println();
            }
            index++;
        }
    }
}
