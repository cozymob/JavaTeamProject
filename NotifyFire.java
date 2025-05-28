import java.sql.*;
import java.util.Scanner;

public class NotifyFire {

    /* 1번 기능 */

    public String region;
    public String village;
    public String counter_direction;
    boolean found;
    public String[] regions1={"군위","달성","동","서","남","북","수성","달서"};
    public String[] regions2={"군위군","달성군","동구","서구","남구","북구","수성구","달서구"};
    public String mountain;

    String url = "jdbc:mysql://localhost:3306/mountains_db?serverTimezone=UTC";
    String user = "root";
    String password = "";

    String sql=null;


    public void getInfo(Scanner scanner) {
        System.out.print("현재 위치한 구 또는 군을 입력하세요: ");
        boolean flag = false;
        while(!flag) {
            region = scanner.nextLine();
            for(int i=0; i<regions1.length; i++){
                if(region.equals(regions1[i])){
                    flag = true;
                }
                if(region.equals(regions2[i])){
                    flag = true;
                }
            }
            if(!flag) {
                region=null;
                System.out.print("다시 입력해주세요: ");
            }
        }


        System.out.print("인근 마을을 입력하세요 (예: 내의리, 대현동): ");
        village = scanner.nextLine();

        System.out.print("방향을 입력하세요 (예: N, S, E, W): ");

        String direction = scanner.nextLine().toUpperCase();
        counter_direction = null;
        while(counter_direction==null) {
            if(direction.equals("N")) {
                counter_direction = "S";
                break;
            }else if(direction.equals("S")) {
                counter_direction = "N";
                break;
            }else if(direction.equals("E")) {
                counter_direction = "W";
                break;
            }else if(direction.equals("W")) {
                counter_direction = "E";
                break;
            }else if(direction.equals("NW")) {
                counter_direction = "SE";
                break;
            }else if (direction.equals("NE")) {
                counter_direction = "SW";
                break;
            }else if (direction.equals("SW")) {
                counter_direction = "NE";
                break;
            }else if (direction.equals("SE")) {
                counter_direction = "NW";
                break;
            } else{
                System.out.println("다시 입력해주세요: ");
            }
        }
    }

    public void setInfo() {
        if(region.equals("군위") || region.equals("군위군")) {
            sql = "SELECT mountain_name, forest_type " +
                    "FROM gunwi_mountains " +
                    "WHERE nearby_village = ? AND village_direction = ?";
        }else if(region.equals("달성") || region.equals("달성군")) {
            sql = "SELECT mountain_name, forest_type " +
                    "FROM dalseong_mountains " +
                    "WHERE nearby_village = ? AND village_direction = ?";
        }else if(region.equals("북") || region.equals("북구")) {
            sql = "SELECT mountain_name, forest_type " +
                    "FROM buk_mountains " +
                    "WHERE nearby_village = ? AND village_direction = ?";
        }else if(region.equals("동") || region.equals("동구")) {
            sql = "SELECT mountain_name, forest_type " +
                    "FROM dong_mountains " +
                    "WHERE nearby_village = ? AND village_direction = ?";
        }else if(region.equals("남") || region.equals("남구")) {
            sql = "SELECT mountain_name, forest_type " +
                    "FROM nam_mountains " +
                    "WHERE nearby_village = ? AND village_direction = ?";
        }else if(region.equals("서") || region.equals("서구")) {
            sql = "SELECT mountain_name, forest_type " +
                    "FROM seo_mountains " +
                    "WHERE nearby_village = ? AND village_direction = ?";
        }else if(region.equals("중") || region.equals("중구")) {
            sql = "SELECT mountain_name, forest_type " +
                    "FROM jung_mountains " +
                    "WHERE nearby_village = ? AND village_direction = ?";
        }else if(region.equals("수성") || region.equals("수성구")) {
            sql = "SELECT mountain_name, forest_type " +
                    "FROM buk_mountains " +
                    "WHERE nearby_village = ? AND village_direction = ?";
        }else if(region.equals("달서") || region.equals("달서구")) {
            sql = "SELECT mountain_name, forest_type " +
                    "FROM buk_mountains " +
                    "WHERE nearby_village = ? AND village_direction = ?";
        }

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, village);
            pstmt.setString(2, counter_direction);

            ResultSet rs = pstmt.executeQuery();

            found=false;
            while (rs.next()) {
                found = true;
                mountain = rs.getString("mountain_name");
                int forest = rs.getInt("forest_type");
                System.out.printf("🌄 산 이름: %s | 🌲 임상지수: %d%n", mountain, forest);
            }

            if(!found) {
                System.out.println("❌ 조건에 맞는 산이 없습니다.");
            }

        } catch (SQLException e) {
            System.err.println("❌ 데이터베이스 오류:");
            e.printStackTrace();
        }
    }
}