import java.sql.*;
import java.util.Scanner;

public class ShelterLocation {
    public static void main(String[] args) {
        String url = "jdbc:mysql://127.0.0.1:3306/mydatabase1"; // 데이터베이스 주소
        String user = "root"; // MySQL 사용자 이름
        String password = "jdk1834zz!!"; // MySQL 비밀번호

        Scanner scanner = new Scanner(System.in);

        while (true) { // 2를 받을 때까지 반복
            System.out.print("검색할 주소를 입력하세요 (숫자 2 : 종료): ");
            String inputAddress = scanner.nextLine(); // 주소 입력

            // 숫자 2를 입력하면 종료
            if (inputAddress.equals("2")) {
                System.out.println("프로그램을 종료합니다.");
                break;
            }

            try {
                Connection conn = DriverManager.getConnection(url, user, password);

                // SQL에서 입력한 주소가 포함된 항목 검색
                String query = "SELECT 이름, 주소, 전화번호 FROM 대피소 WHERE 주소 LIKE ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, "%" + inputAddress + "%");

                ResultSet rs = pstmt.executeQuery();

                if (!rs.isBeforeFirst()) { // 결과 X
                    System.out.println("이 지역에는 대피소가 없습니다.");
                    System.out.println("다시 입력해주세요.");
                } else {
                    System.out.println("=====================================================================");
                    System.out.printf("%-30s %-45s %-15s%n", "대피소 이름", "주소", "전화번호");
                    System.out.println("=====================================================================");

                    while (rs.next()) {
                        String name = rs.getString("이름");
                        String address = rs.getString("주소");
                        String number = rs.getString("전화번호");

                        System.out.printf("%-30s %-45s %-15s%n", name, address, number);
                    }

                    System.out.println("=====================================================================");
                }

                rs.close();
                pstmt.close();
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        scanner.close();
    }
}