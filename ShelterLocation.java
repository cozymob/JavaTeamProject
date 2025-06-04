import java.sql.*;

public class ShelterLocation {

    /* 4번 기능 */

    String url = "jdbc:mysql://localhost:3306/shelters_db?serverTimezone=UTC"; // 데이터베이스 주소
    String user = "root"; // MySQL 사용자 이름
    String password = ""; // MySQL 비밀번호

    public void printShelterLocation(String region) {

        try {
            Connection conn = DriverManager.getConnection(url, user, password);

            // SQL에서 입력한 주소가 포함된 항목 검색
            String query = "SELECT 이름, 주소, 전화번호 FROM 대피소 WHERE 주소 LIKE ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, "%" + region + "%");

            ResultSet rs = pstmt.executeQuery();

            if (!rs.isBeforeFirst()) { // 결과 X
                System.out.println("검색된 대피소가 없습니다.");
            } else {
                System.out.println("=====================================================================================================");
                System.out.printf("%-30s %-45s %-15s%n", "대피소 이름", "주소", "전화번호");
                System.out.println("=====================================================================================================");

                while (rs.next()) {
                    String name = rs.getString("이름");
                    String address = rs.getString("주소");
                    String number = rs.getString("전화번호");

                    System.out.printf("%-30s %-45s %-15s%n", name, address, number);
                }

                System.out.println("=====================================================================================================");
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

