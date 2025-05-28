package hw11;

import java.sql.*;
import java.util.List;
import java.util.Scanner;
import java.sql.SQLException;

public class NotifyFire { 
	
	private String region; // 지역명
	private String village; // 읍면동
    private String direction; // 방향
    private String counterDirection; //반대 방향
    
    public void getInfo(Scanner scanner) { // 사용자에게 입력 받음 (지역,마을,방향)
    	
    	System.out.println("[대구 산불 발생 신고]");
    	
        System.out.print("현재 위치한 구 또는 군을 입력해주세요 (예: 군위군): ");
        scanner.nextLine(); //버퍼 클리어용
        region = scanner.nextLine().trim();
        //trim() : 문자열 앞뒤 공백 제거 (사용자가 실수로 공백 넣을 수 있음)
        
        System.out.print("인근 마을을 입력하세요 (예: 내의리, 대현동): ");
        village = scanner.nextLine().trim();
        
        System.out.print("방향을 입력하세요 (예: N, S, E, W): ");
        counterDirection = null;
        
        while (counterDirection == null) {
            direction = scanner.nextLine().trim().toUpperCase();
            switch (direction) {
                case "N": counterDirection = "S"; break;
                case "S": counterDirection = "N"; break;
                case "E": counterDirection = "W"; break;
                case "W": counterDirection = "E"; break;
                default:
                    System.out.print("잘못된 방향입니다. 다시 입력해주세요 (N, S, E, W): ");
            }
        }
        
        System.out.println("신고 위치: "+ village + " " + direction);
		System.out.println("신고가 접수되었습니다. 빠르게 대응하겠습니다.");
      

        setInfo();
       
    }

    public void setInfo() { //DB에서 해당 위치/방향에 맞는 산을 검색
        
    	String url = "jdbc:mysql://localhost:3306/mountains_db?serverTimezone=UTC";
        String user = "root";
        String password = "123456789";
        
        String regionForTable = region.replaceAll("군$", "").replaceAll("구$", "").trim();
        
        String table = switch (regionForTable) {
        case "군위" -> "gunwi_mountains";
        case "북" -> "buk_mountains";
        case "동" -> "dong_mountains";
        case "서" -> "seo_mountains";
        case "중" -> "jung_mountains";
        case "수성" -> "suseong_mountains";
        case "남" -> "nam_mountains";
        case "달서" -> "dalseo_mountains";
        case "달성" -> "dalseong_mountains";
        default -> null;
        };
        
        if (table == null) {
            System.out.println("❌ 지원되지 않는 지역입니다.");
            return;
        }
        
        String sql = String.format(
                "SELECT mountain_name, forest_type FROM %s WHERE nearby_village = ? AND village_direction = ?",
                table
            );

            try (Connection conn = DriverManager.getConnection(url, user, password);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, village);
                pstmt.setString(2, counterDirection);

                ResultSet rs = pstmt.executeQuery();

                if (!rs.next()) {
                    System.out.println("❌ 조건에 맞는 산이 없습니다.");
                    return;
                }

                // 결과 출력
                do {
                    String mountain = rs.getString("mountain_name");
                    int forest = rs.getInt("forest_type");
                    System.out.printf("🌄 산 이름: %s | 🌲 임상지수: %d%n", mountain, forest);
                } while (rs.next());

            } catch (SQLException e) {
                System.err.println("❌ 데이터베이스 오류:");
                e.printStackTrace();
        }
    }
}
