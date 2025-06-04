import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.sql.*;

public class FireStatus extends Predict {

    /* 2번 기능 */

    private final String url = "jdbc:mysql://localhost:3306/mountains_db?serverTimezone=UTC";
    private final String user = "root";
    private final String password = "";
    private String region;

    public void runFireStatus(ArrayList<String> onFireMountain, Scanner scanner) {
        HashMap<String, Integer> reportCount = countDuplicates(onFireMountain);
        statistics(reportCount);

        for (String mountain : reportCount.keySet()) {
            int count = reportCount.get(mountain);
            String statusMessage;
            if (count == 1) {
                statusMessage = "-> 의심";
            } else if (count <= 3) {
                statusMessage = "-> 확실";
            } else {
                statusMessage = "-> 즉시 대응 필요";
            }
            System.out.println("[ "+mountain+" ]" + " (" + count + "회) " + statusMessage);
        }

        boolean validMountain = false;
        while (!validMountain) {
            System.out.println();
            System.out.print("산불에 따른 위험지역 확인을 위한 산이름을 입력해주세요: ");
            String mountain = scanner.nextLine().trim();
            System.out.println("--------------------------------------------------------------");
            validMountain = searchMountain(mountain);
            if (!validMountain) {
                System.out.println("해당 산 이름으로 검색된 결과가 없습니다. 다시 입력해주세요.\n");
            }
        }
    }

    public HashMap<String, Integer> countDuplicates(ArrayList<String> onFireMountain) {
        HashMap<String, Integer> countMap = new HashMap<>();
        for (String item : onFireMountain) {
            countMap.put(item, countMap.getOrDefault(item, 0) + 1);
        }
        return countMap;
    }

    public void statistics(HashMap<String, Integer> mountains) {
        String maxCountMountain = "";
        int maxCount = 0;
        int mtCount = mountains.size();
        int rpCount = 0;

        for (String key : mountains.keySet()) {
            int count = mountains.get(key);
            rpCount += count;
            if (count > maxCount) {
                maxCount = count;
                maxCountMountain = key;
            }
        }
        System.out.println("--------------------------------------------------------------");
        System.out.println("총 신고건수: " + rpCount);
        System.out.println("가장 많이 신고된 산: " + "[ "+maxCountMountain+" ]" + " (" + maxCount + "회)");
        System.out.println("신고된 산의 개수: " + mtCount + "\n");
    }

    public boolean searchMountain(String mountain) {
        boolean found = false;

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql1 = "SELECT TABLE_NAME, COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS " +
                    "WHERE TABLE_SCHEMA = 'mountains_db' AND DATA_TYPE IN ('varchar', 'char', 'text')";

            try (PreparedStatement ps = conn.prepareStatement(sql1);
                 ResultSet rs1 = ps.executeQuery()) {

                while (rs1.next()) {
                    String tableName = rs1.getString("TABLE_NAME");
                    String columnName = rs1.getString("COLUMN_NAME");
                    region = switch (tableName) {
                        case "gunwi_mountains" -> "군위군";
                        case "dalseo_mountains" -> "달서구";
                        case "dalseong_mountains" -> "달성군";
                        case "buk_mountains" -> "북구";
                        case "dong_mountains" -> "동구";
                        case "seo_mountains" -> "서구";
                        case "suseong_mountains" -> "수성구";
                        case "nam_mountains" -> "남구";
                        case "jung_mountains" -> "중구";
                        default -> "없음";
                    };

                    String searchQuery = "SELECT 1 FROM `" + tableName + "` WHERE `" + columnName + "` LIKE ? LIMIT 1";
                    try (PreparedStatement searchStmt = conn.prepareStatement(searchQuery)) {
                        searchStmt.setString(1, "%" + mountain + "%");

                        try (ResultSet rs2 = searchStmt.executeQuery()) {
                            if (rs2.next()) {
                                found = true;

                                String sql2 = "SELECT nearby_village FROM `" + tableName + "` " +
                                        "WHERE mountain_name = ? AND village_direction = ?";

                                GetWeather gw = new GetWeather();
                                double[] latLon = setLatLon(region);
                                System.out.println("선택한 산: " + "["+ mountain +"]");
                                System.out.printf("선택한 산이 위치한 지역 [ %s ] 날씨 \n",region);
                                gw.getWeather(latLon[0], latLon[1]);
                                String[] directions = getDirections(gw.getWindDirection());
                                System.out.println();
                                System.out.println("풍향에 따른 위험 지역");

                                for (String dir : directions) {
                                    try (PreparedStatement pstmt = conn.prepareStatement(sql2)) {
                                        pstmt.setString(1, mountain);
                                        pstmt.setString(2, dir);

                                        try (ResultSet rs3 = pstmt.executeQuery()) {
                                            while (rs3.next()) {
                                                String village = rs3.getString("nearby_village");
                                                System.out.print("< " + village + " >");
                                            }
                                        }
                                    }
                                }
                                System.out.println();
                                System.out.println("--------------------------------------------------------------");
                                System.out.println();
                                break;
                            }
                        }
                    }
                }

            }
        } catch (SQLException e) {
            System.err.println("[DB 오류] " + e.getMessage());
        }

        return found;
    }
}