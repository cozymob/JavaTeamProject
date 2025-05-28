import java.util.ArrayList;
import java.util.Scanner;

public class FireAlarmApp {
    public static void main(String[] args) {
        NotifyFire notifyFire = new NotifyFire();
        Predict predict = new Predict();

        Scanner sc = new Scanner(System.in);

        ArrayList<String> onFireMountain = new ArrayList<>();

        int answer=1;

        while(answer!=0){
            System.out.println("--------------------------------");
            System.out.println("<원하는 항목을 고르세요> ");
            System.out.println();

            System.out.println("1번: 산불 발생 알리기");
            System.out.println("2번: 산불 현황보기");
            System.out.println("3번: 산불 예측하기");
            System.out.println("4번: 우리 동네 대피소");
            System.out.println("5번: 산불 대처 방법 알아보기");
            System.out.println("6번: 산불 위험 지수 맵");
            System.out.println("0번: 종료");
            System.out.print("-> ");
            answer = sc.nextInt();
            sc.nextLine();

            switch (answer) {
                case 1:
                    notifyFire.getInfo(sc);
                    notifyFire.setInfo();
                    onFireMountain.add(notifyFire.mountain);
                    break;
                case 2:
                    System.out.println("");
                case 3:
                    predict.getInfo(sc);
                    for(int i=0; i<onFireMountain.size(); i++ ){
                        predict.predictPath(onFireMountain.get(i));
                    }
                    break;
                case 4:
                    System.out.println("");
                case 5:
                    System.out.println("");
                case 6:
                    DangerPoint.InputAndSwing();
                    break;
                default:
                    System.out.println("error");
            }
        }
    }
}
