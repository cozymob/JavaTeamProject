import java.util.*;

public class QuizManager {
    private List<Quiz> allQuizzes;
    private List<AnswerRecord> records;
    private Scanner scanner = new Scanner(System.in);
    private Random random = new Random();
    
    //퀴즈 리스트 객체, 기록
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
        	System.exit(0);
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
    
    //OX입력 받기 대소문자 상관 없이
    private String getOXInput() {
        while (true) {
        	//답 대문자로 변환해 받아오기
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("O") || input.equals("X")) {
            	return input;
            }
            //형식에 안 맞을 때
            System.out.println("O 또는 X만 입력해주세요.");
        }
    }

    //랜덤 문제 풀이
    private void randomSolve() {
        int count = 0;
        while (true) {
            try {
                System.out.print("몇 문제를 푸시겠습니까? (1~77): ");
                count = Integer.parseInt(scanner.nextLine().trim());
                if (count < 1 || count > 77) {
                    System.out.println("1~77 사이의 숫자를 입력해주세요.");
                } else {
                	break;
                }
                //숫자로 변환할 수 없는 문자가 입력됐을 때 예외처리
            } catch (NumberFormatException e) {
                System.out.println("숫자를 정확히 입력해주세요.");
            }
        }
        
        //퀴즈들 섞기
        Collections.shuffle(allQuizzes);
        //기록 전체 지우기
        records.clear();
        //입력된 횟수만큼 퀴즈 내기
        for (int i = 0; i < count; i++) {
        	//.get 인덱스 번호로 문제 불러오기
            Quiz q = allQuizzes.get(i);
            //1번부터 문제 불러오기
            System.out.println((i + 1) + ". " + q.getQuestion());
            //유저 답 받아오기
            String input = getOXInput();
            //기록에 저장 (문제,유저 답)
            records.add(new AnswerRecord(q, input));
        }
        //결과 출력
        showResult();
    }

    //대분류별로 한 문제씩 10문제 출력
    private void categorySolve() {
    	//기록 지우기
        records.clear();
        int index = 1;
        //1번 부터 10번까지 만들기
        for (int cat = 1; cat <= 10; cat++) {
            List<Quiz> group = new ArrayList<>();
            //1부터 10까지 대분류끼리 나눠서 그룹에 저장
            for (Quiz q : allQuizzes) {
                if (q.getNumber().startsWith(cat + ".")) group.add(q);
            }
            //그룹에 각 대분류마다 1개이상 문제가 들어가면
            if (!group.isEmpty()) {
            	//0~그룹사이즈까지 랜덤 숫자 불러와서 group에서 받아와 q에 저장
                Quiz q = group.get(random.nextInt(group.size()));
                System.out.println(index + ". " + q.getQuestion());
                String input = getOXInput();
                records.add(new AnswerRecord(q, input));
                //다음 문제로 인덱스 이동
                index++;
            }
        }
        //결과 보여주기
        showResult();
    }

    //결과 보여주는 기능
    private void showResult() {
        int correct = 0;
        //맞힌 문제 세기
        for (AnswerRecord r : records) {
            if (r.isCorrect()) {
                correct++;
            }
        }

        System.out.println("총 맞힌 개수: " + correct + " / " + records.size());
        System.out.printf("점수: %.1f점\n", correct * 100.0 / (double)records.size());
        
        //틀린 문제 있는지 있으면 4번 메뉴 추가
        boolean hasWrong = false;
        for (AnswerRecord r : records) {
            if (!r.isCorrect()) {
                hasWrong = true;
                break;
            }
        }

        while (true) {
        	 if(!hasWrong) {
             	System.out.println("오답이 없습니다!");
             }
            System.out.println("1. 전체 풀이 보기");
            if (hasWrong) {
            	System.out.println("2. 오답 풀이 보기");
            }
            System.out.println("3. 끝내기");
            if (hasWrong) {
                System.out.println("4. 다시 오답 퀴즈 풀기");
            }
            System.out.println("5. 메인으로 돌아가기");
          
            String sel = scanner.nextLine().trim();

            if (sel.equals("1")) {
                showReview(true);
            } 
            else if (sel.equals("2")&&hasWrong) {
            		showReview(false);
            		
                
            } else if (sel.equals("3")) {
                System.out.println("퀴즈 프로그램을 종료합니다.");
                System.exit(0);
                
                //오답 퀴즈 시작
            } else if (sel.equals("4") && hasWrong) {
                List<Quiz> wrongQuizzes = new ArrayList<>();
                for (AnswerRecord r : records) {
                    if (!r.isCorrect()) {
                        wrongQuizzes.add(r.getQuiz());
                    }
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
                return;
                // 오답 퀴즈 끝
            } else if (sel.equals("5")) {
                System.out.println("메인화면으로 돌아갑니다.\n");
                run();
                return;
            } else {
                System.out.println("1~5 중에서 다시 선택해주세요.");
            }
        }
    }
    
    //전체, 오답 문제 해설 보기
    private void showReview(boolean all) {
        int index = 1;
        //답 비교
        for (AnswerRecord r : records) {
        	//all이 true 전체보기, false 오답보기 / 문제 틀렸을 때
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
