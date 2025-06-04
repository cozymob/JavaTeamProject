import java.io.*;
import java.util.*;

public class FireAlarmApp {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        OnFireMountain onFireMountain = new OnFireMountain();

        while(true){
            if (!onFireMountain.getOnFireMountain().isEmpty()) {
                System.out.print("\u001B[31m현재\u001B[0m ");
                // 중복 제거
                Set<String> firingMountains = new HashSet<>(onFireMountain.getOnFireMountain());

                for (String s : firingMountains) {
                    System.out.print("[" + s + "]" + " ");
                }

                System.out.println("\u001B[31m인근에서 산불 발생 위험!! 인근 주민은 대피 준비하시길 바랍니다.\u001B[0m");
            }

            System.out.println("--------------------------------------------------------------");
            System.out.println("[대구 산불 예방 및 대처 애플리케이션]\n");
            System.out.println("1. 대구 산불 발생 신고하기");
            System.out.println("2. 대구 산불 위험 현황 보기");
            System.out.println("3. 산불 진행 예측하기");
            System.out.println("4. 우리 동네 대피소 확인하기");
            System.out.println("5. 산불 대처 방법 알아보기");
            System.out.println("6. 산불 위험 지수 지도 보기");
            System.out.println("0. 시스템 종료");
            System.out.println("--------------------------------------------------------------");
            System.out.print("메뉴를 선택해 주세요: ");
            
            String answer = scanner.nextLine().trim();
            System.out.println();
            
            switch (answer) {
                case "1":
                    while(true){
                    System.out.println("1. 산불 발생 신고하기");
                    System.out.println("2. 메인메뉴로 돌아가기");
                    System.out.print("-> ");
                    String answer2 = scanner.nextLine().trim();
                    System.out.println();


                    if(answer2.equals("1")) {
                        while(true){
                            System.out.println("1. 산 이름을 아는 경우");
                            System.out.println("2. 산 이름을 모르는 경우");
                            System.out.println("3. 뒤로 가기");
                            System.out.print("-> ");
                            String answer3 = scanner.nextLine().trim();
                            System.out.println();

                            if(answer3.equals("1")) {
                                System.out.print("산불이 난 산의 이름을 입력하세요: ");
                                String mountain= scanner.nextLine().trim();
                                onFireMountain.setOnFireMountain(mountain);
                                System.out.println("신고가 접수되었습니다. 감사합니다.\n");
                                break;
                            }else if(answer3.equals("2")) {
                                NotifyFire notifyFire = new NotifyFire();
                                if(notifyFire.setInfo(scanner))
                                    notifyFire.getInfo();
                                if (notifyFire.getMountain() != null) {
                                    onFireMountain.setOnFireMountain(notifyFire.getMountain());
                                }
                            }else if(answer3.equals("3")) {
                                break;
                            }else
                                System.out.println("올바른 번호를 입력해주세요.");
                        }


                    }else if(answer2.equals("2")) {
                        break;
                    }else{
                        System.out.println("올바른 번호를 입력해주세요.");
                    }
                }
                    break;

                case "2":
                    while(true){
                        System.out.println("1. 현재 접수된 신고 현황");
                        System.out.println("2. 메인으로 돌아가기 ");
                        System.out.print("-> ");

                        String answer2 = scanner.nextLine().trim();

                        if(answer2.equals("1")) {
                            if(onFireMountain.getOnFireMountain().isEmpty()){
                                System.out.println("현재 접수된 신고가 없습니다.");
                            }else{
                                FireStatus fireStatus = new FireStatus();
                                fireStatus.runFireStatus(onFireMountain.getOnFireMountain(), scanner);
                            }
                        }else if(answer2.equals("2"))
                            break;
                        else
                            System.out.println("메뉴를 다시 입력해주세요");
                    }
                    break;

                case "3":
                    while(true){
                        System.out.println("1. 현재 풍향에 따른 위험지역 예측하기");
                        System.out.println("2. 메인으로 돌아가기 ");
                        System.out.print("-> ");

                        String answer2 = scanner.nextLine().trim();

                        if(answer2.equals("1")) {
                            if(onFireMountain.getOnFireMountain().isEmpty()) {
                                System.out.println("현재 접수된 신고가 없습니다.");
                            }else{
                                System.out.print("지역명을 입력하세요 (예: 북구, 동구 등): ");
                                String regionInput = scanner.nextLine().trim();

                                String tableName = switch (regionInput) {
                                    case "북구" -> "buk_mountains";
                                    case "동구" -> "dong_mountains";
                                    case "달성군" -> "dalseong_mountains";
                                    case "달서구" -> "dalseo_mountains";
                                    case "군위군" -> "gunwi_mountains";
                                    case "서구" -> "seo_mountains";
                                    case "남구" -> "nam_mountains";
                                    case "수성구" -> "suseong_mountains";
                                    case "중구" -> "jung_mountains";
                                    default -> {
                                        System.out.println("지원하지 않는 지역입니다.");
                                        yield null;
                                    }
                                };

                                if (tableName != null) {
                                    Predict predict = new Predict();
                                    List<String> mountainsOnFire = onFireMountain.getOnFireMountain();
                                    predict.checkMountains(mountainsOnFire, regionInput, tableName);
                                }
                            }
                        }else if(answer2.equals("2"))
                            break;
                        else
                            System.out.println("메뉴를 다시 입력해주세요");
                    }
                    break;
                    
                case "4":
                    String region;
                    while(true){
                        System.out.println("지역을 입력하세요.(종료: q 또는 Q 입력) ");
                        System.out.println("ex) 대구광역시, 북구, 비산동");
                        System.out.print("-> ");
                        region = scanner.nextLine().toUpperCase().trim();
                        if(!region.equals("Q")){
                            ShelterLocation shelterLocation = new ShelterLocation();
                            shelterLocation.printShelterLocation(region);
                        }else{
                            break;
                        }
                    }
                    break;

                case "5":
                    List<Quiz> quizzes = null;
                    try {
                        quizzes = QuizLoader.loadFromFile("ox_quiz.txt");
                    } catch (IOException e) {
                        System.err.println("에러 발생: " + e.getMessage());
                    }
                    QuizManager manager = new QuizManager(quizzes);
                    manager.run();
                    break;

                case "6":
                    DangerPointMap dpm = new DangerPointMap();
                    dpm.InputAndSwing();
                    break;

                case "0":
                	System.out.println("시스템을 종료합니다.");
                	return;
                	
                default:
                    System.out.println("유효한 번호를 입력해 주세요(0~6)");
            }
        }
    }
}