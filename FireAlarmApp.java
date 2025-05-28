package hw11;
import java.util.Scanner;

public class FireAlarmApp {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        
        while(true) {
        	
        	System.out.println("---------------------------------------");
    		System.out.println("[대구 산불 예방 및 대처 애플리케이션]");
            System.out.println("1번: 대구 산불 발생 알리기");
            System.out.println("2번: 대구 산불 위험 현황보기");
            System.out.println("3번: 대구 산불 위험도 확인하기");
            System.out.println("4번: 우리 동네 대피소");
            System.out.println("5번: 산불 대처 방법 알아보기");
            System.out.println("6번: 산불 위험도 지도 보기");
            System.out.println("---------------------------------------");
            
            System.out.printf("메뉴를 선택해주세요: ");
            int answer=scanner.nextInt();
    	
            switch (answer) {
            
                case 1:
    		        NotifyFire notifyFire = new NotifyFire();
    		        notifyFire.getInfo(scanner); // 사용자에게 입력 받음 (지역,마을,방향)
                    break;
                    
                case 2:
                    System.out.println("");
                    break;
                case 3:
                    System.out.println("");
                    break;
                case 4:
                    System.out.println("");
                    break;
                case 5:
                    System.out.println("");
                    break;
                case 6:
                    DangerPoint.InputAndSwing();
                    break;
    		    case 0:
    				System.out.println("애플리케이션을 종료합니다.");
    				return;      

                default: //예외처리 해야함
                    System.out.println("잘못된 메뉴 선택입니다. 다시 입력해주세요(1~6)");
                    
            }
		
        }
    }
}
