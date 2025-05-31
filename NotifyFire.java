import java.sql.*;
import java.util.*;

public class NotifyFire {

    /* 1번 기능 */

    private String region; // 구, 군 정보
    private String village; // 마을 정보
    private String direction; // 방향
    private String counterDirection; // 반대방향
    private boolean found;
    private double mostRisk; // 산불 가능성이 가장 높은 산의 위험지수
    private String mostRiskMountain; // 산불 가능성이 가장 높은 산 이름

    public String[] regions1 = {"군위", "달성", "동", "서", "남", "북", "수성", "달서"};
    public String[] regions2 = {"군위군", "달성군", "동구", "서구", "남구", "북구", "수성구", "달서구"};

    String url = "jdbc:mysql://localhost:3306/mountains_db?serverTimezone=UTC";
    String user = "root";
    String password = "";

    public String getMountain(){
        return mostRiskMountain;
    }

    public String getRegion() {
        return region;
    }

    public String getVillage() {
        return village;
    }

    public String getDirection() {
        return direction;
    }

    // 방향 변환 Map
    private static final Map<String, String> DIRECTION_MAP = Map.of(
            "N", "S", "S", "N", "E", "W", "W", "E",
            "NW", "SE", "NE", "SW", "SW", "NE", "SE", "NW"
    );

    public void setInfo(Scanner scanner) {
        System.out.println("--------------------------------------------------------------");
        System.out.println("신고 위치 기준 산불이 발생한 산과 산불 위험지수를 파악해드립니다.");
        System.out.println("산불 위험지수는 0~100 의 수치로 100에 가까울 수록 산불 발생 위험도가 높습니다.");
        System.out.println();
        System.out.print("현재 위치한 구 또는 군을 입력하세요: ");
        boolean flag = false;

        while (!flag) {
            region = scanner.nextLine().trim();
            for (int i = 0; i < regions1.length; i++) {
                if (region.equals(regions1[i]) || region.equals(regions2[i])) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                region = null;
                System.out.print("지원하지 않는 지역입니다. 다시 입력해주세요: ");
            }
        }

        System.out.print("인근 마을을 입력하세요 (예: 내의리, 대현동): ");
        village = scanner.nextLine();

        System.out.print("방향을 입력하세요 (예: N, S, E, W): ");
        direction = scanner.nextLine().toUpperCase();

        counterDirection = null;
        while (counterDirection == null) {
            if (DIRECTION_MAP.containsKey(direction)) {
                counterDirection = DIRECTION_MAP.get(direction);
            } else {
                System.out.print("잘못된 방향입니다. 다시 입력해주세요 (예: N, S, E, W): ");
                direction = scanner.nextLine().toUpperCase();
            }
        }

        System.out.println("신고 위치 : " + region + " " + village);
        System.out.println("신고가 접수되었습니다. 빠르게 대응하겠습니다.\n");
    }

    public void getInfo() {
        Map<String, String> map = new HashMap<>();
        map.put("군위", "gunwi_mountains");
        map.put("군위군", "gunwi_mountains");
        map.put("달성", "dalseong_mountains");
        map.put("달성군", "dalseong_mountains");
        map.put("북", "buk_mountains");
        map.put("북구", "buk_mountains");
        map.put("동", "dong_mountains");
        map.put("동구", "dong_mountains");
        map.put("남", "nam_mountains");
        map.put("남구", "nam_mountains");
        map.put("서", "seo_mountains");
        map.put("서구", "seo_mountains");
        map.put("중", "jung_mountains");
        map.put("중구", "jung_mountains");
        map.put("수성", "suseong_mountains");
        map.put("수성구", "suseong_mountains");
        map.put("달서", "dalseo_mountains");
        map.put("달서구", "dalseo_mountains");

        String tableName = map.get(region);
        if (tableName == null) {
            System.out.println("지원하지 않는 지역입니다.");
            return;
        }

        String sql = "SELECT mountain_name, forest_type FROM " + tableName +
                " WHERE nearby_village = ? AND village_direction = ?";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {

            found = false;

            // 1차 시도
            found = queryAndUpdate(conn, sql, village, counterDirection) || found;

            // 그 방향에 산이 없으면 2차시도
            if (!found) {
                System.out.println(" 조건에 맞는 산이 없습니다. 추가 방향 검색을 시도합니다...");

                if (counterDirection.length() == 2) { // 방향이 NW 인 경우 NW, N, W 세 방향 모두 조사
                    for (char dir : counterDirection.toCharArray()) {
                        found = queryAndUpdate(conn, sql, village, String.valueOf(dir)) || found;
                    }
                } else if (counterDirection.length() == 1) { // 방향이 N인 경우 N, NE, NW 세 방향 모두 조사
                    String[] newDirs = switch (counterDirection) {
                        case "N" -> new String[]{"NW", "NE"};
                        case "S" -> new String[]{"SW", "SE"};
                        case "E" -> new String[]{"NE", "SE"};
                        case "W" -> new String[]{"NW", "SW"};
                        default -> new String[]{};
                    };
                    for (String dir : newDirs) {
                        found = queryAndUpdate(conn, sql, village, dir) || found;
                    }
                }
            }

            if (!found) { // 2차 시도까지 했는데도 없으면 진짜 없는 것임
                System.out.println(" 추가 검색에도 조건에 맞는 산이 없습니다.");
                System.out.println("올바른 동 또는 리 이름을 입력했는 지 확인해보세요.");
                System.out.println();
            }

            if (mostRisk != 0.0 && mostRiskMountain != null) {
                System.out.printf("🚨 가장 위험한 산: %s (산불 위험지수: %.2f)%n", mostRiskMountain, mostRisk);
                System.out.println();
                System.out.printf("산불 발생지역이 [%s]일 가능성이 높습니다.\n", mostRiskMountain);
                System.out.println("인근 대피소는 [군위농업기술센터(지하 1층)] 입니다."); // TODO: 대피소 연동
                System.out.println();
                System.out.println();
            }

        } catch (SQLException e) {
            System.err.println(" 데이터베이스 오류:");
            e.printStackTrace();
        }
    }

    /* PreparedStatement 새로 열고 닫기
       원래는 위 코드의 2차시도에서 방향을 넣을 때마다 쿼리를 써줘야 함
       preparedstatement는 쿼리에서 물음표에 들어갈 값을 미리 정해 놓는 것
       따라서 setString() 메소드에 매개 값만 다르게 하면 중복코드 없이 쿼리를 여러번 쓸 수 있다
     */
    private boolean queryAndUpdate(Connection conn, String sql, String village, String direction) {
        boolean localFound = false;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, village);
            pstmt.setString(2, direction);

            try (ResultSet rs = pstmt.executeQuery()) {
                Map<Double, String> map = new HashMap<>();
                while (rs.next()) {
                    localFound = true;

                    String name = rs.getString("mountain_name");
                    int forest = rs.getInt("forest_type");

                    CalculateFFDRI calculateFFDRI = new CalculateFFDRI();
                    double risk = calculateFFDRI.calculate(forest, counterDirection);

                    map.put(risk, name);
                    /*멥에 위험지수와 그 산 이름을 모두 넣음(while 문)
                        산이 여러 개일 경우 위험지수 기준 내림차순으로 정렬
                        그럼 맨 위의 값이 위험지수가 가장 높은 산이 됨
                     */

                    // 1. entrySet을 리스트로 변환
                    List<Map.Entry<Double, String>> entryList = new ArrayList<>(map.entrySet());

                    // 2. key 기준 내림차순 정렬
                    entryList.sort(Map.Entry.<Double, String>comparingByKey().reversed());

                    // 3. 가장 첫 번째 원소가 key 최대값
                    if (!entryList.isEmpty()) {
                        Map.Entry<Double, String> maxEntry = entryList.get(0);
                        mostRisk=maxEntry.getKey();
                        mostRiskMountain=maxEntry.getValue();
                    }

                    System.out.printf("산 이름: %s | 산불 위험지수: %.2f%n", name, risk);
                    System.out.println();

                }
            }
        } catch (SQLException e) {
            System.err.println(" 쿼리 실행 중 오류 (village: " + village + ", direction: " + direction + ")");
            e.printStackTrace();
        }
        return localFound;
    }
}