import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FireAlarmApp {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        OnFireMountain onFireMountain = new OnFireMountain();

        while(true){
            if(!onFireMountain.getOnFireMountain().isEmpty()){
                System.out.print("\u001B[31m현재\u001B[0m ");
                for(String s : onFireMountain.getOnFireMountain()){
                    System.out.print("["+s+"]"+" ");
                }
                System.out.println("\u001B[31m에서 산불 발생!! 인근 주민은 대피하시길 바랍니다.\u001B[0m");
            }

            System.out.println("--------------------------------");
            System.out.println("[대구 산불 예방 및 대처 애플리케이션]");
            System.out.println();
            System.out.println("1. 대구 산불 발생 신고하기");
            System.out.println("2. 대구 산불 위험 현황 보기");
            System.out.println("3. 산불 진행 예측하기");
            System.out.println("4. 우리 동네 대피소 확인하기");
            System.out.println("5. 산불 대처 방법 알아보기");
            System.out.println("6. 산불 위험 지수 지도 보기");
            System.out.println("0. 시스템 종료");
            System.out.println("--------------------------------");
            System.out.print("메뉴를 선택해 주세요: ");
            
            int answer = scanner.nextInt();
            scanner.nextLine();
            
            switch (answer) {
                case 1:
                	NotifyFire notifyFire = new NotifyFire();
                    notifyFire.setInfo(scanner);
                    notifyFire.getInfo();
                    if(notifyFire.getMountain()!=null){
                        onFireMountain.setOnFireMountain(notifyFire.getMountain());
                    }
                    break;
                    
                case 2:
                    System.out.println("");
                    break;
                    
                case 3:
                	Predict predict = new Predict();
                    predict.setInfo(scanner);
                    for(int i=0; i<onFireMountain.getOnFireMountain().size(); i++ ){
                        predict.getInfo(onFireMountain.getOnFireMountain().get(i));
                    }
                    break;
                    
                case 4:
                    System.out.println("");
                    break;
                    
                case 5:
                    List<Quiz> quizzes = null;
                    try {
                        quizzes = QuizLoader.loadFromFile("ox_quiz.txt");
                    } catch (IOException e) {
                        System.err.println("에러 발생: " + e.getMessage());
                    }
                    QuizManager manager = new QuizManager(quizzes);
                    manager.run();
                    break;
                case 6:
                    DangerPoint.InputAndSwing();
                    break;
                case 0:
                	System.out.println("시스템을 종료합니다.");
                	return;
                	
                default:
                    System.out.println("유효한 번호를 입력해 주세요(0~6)");
            }
        }
    }
}