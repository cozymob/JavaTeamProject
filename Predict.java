import java.sql.*;
import java.util.*;

public class Predict extends NotifyFire {

    /* 3번 기능 */

    private String currentMountain;
    private String region;

    public void setCurrentMountain(String currentMountain) {
        this.currentMountain = currentMountain;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCurrentMountain() {
        return currentMountain;
    }

    public String getRegion() {
        return region;
    }

    @Override
    public void setInfo(Scanner sc) {
        System.out.println("산불 ");

        System.out.print("현재 위치한 구 또는 군을 입력하세요: ");

        boolean flag = false;

        while (!flag) {
            setRegion(sc.nextLine().trim());
            for (int i = 0; i < regions1.length; i++) {
                if (getRegion().equals(regions1[i])) {
                    flag = true;
                }
                if (region.equals(regions2[i])) {
                    flag = true;
                }
            }
            if (!flag) {
                region = null;
                System.out.print("다시 입력해주세요: ");
            }
        }
    }

    public double[] setLatLon() {
        double lat = 0.0;
        double lon = 0.0;
        double[] position = new double[2];

        if (region.equals("군위") || region.equals("군위군")) {
            lat = 36.239397;
            lon = 128.572818;
        } else if (region.equals("달성") || region.equals("달성군")) {
            lat = 35.774448;
            lon = 128.431704;
        } else if (region.equals("동") || region.equals("동구")) {
            lat = 35.886704;
            lon = 128.635004;
        } else if (region.equals("서") || region.equals("서구")) {
            lat = 35.872154;
            lon = 128.559096;
        } else if (region.equals("북") || region.equals("북구")) {
            lat = 35.904626;
            lon = 128.582760;
        } else if (region.equals("남") || region.equals("남구")) {
            lat = 35.845626;
            lon = 128.594663;
        } else if (region.equals("중") || region.equals("중구")) {
            lat = 35.869634;
            lon = 128.607042;
        } else if (region.equals("달서") || region.equals("달서구")) {
            lat = 35.829398;
            lon = 128.532611;
        } else if (region.equals("수성") || region.equals("수성구")) {
            lat = 35.856154;
            lon = 128.639360;
        }

        position[0] = lat;
        position[1] = lon;

        return position;
    }

    public void getInfo(String mountain) {
        GetWeather gw = new GetWeather();

        double[] latLon = setLatLon();
        gw.getWeather(latLon[0], latLon[1]);

        currentMountain = mountain;

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

        String sql = "SELECT nearby_village FROM " + tableName + " WHERE mountain_name = ? AND village_direction = ?";

        // 풍향 집합
        String[] directions = getDirections(gw.windDirection);

        Set<String> villageSet = new HashSet<>(); // 마을 이름 중복 제거를 위한 hashset

        try (Connection conn = DriverManager.getConnection(url, user, password)) {

            Map<String, String> wind =  Map.of("N","북풍","E","동풍","S","남풍","W",
                    "서풍","NW","북서풍","NE","북동풍","SE","남동풍","SW","남서풍");

            System.out.printf("현재 바람 방향은 [%s] 입니다.\n", wind.get(gw.windDirection));

            for (String dir : directions) {
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, currentMountain);
                    pstmt.setString(2, dir);

                    ResultSet rs = pstmt.executeQuery();

                    while (rs.next()) {
                        String village = rs.getString("nearby_village");
                        villageSet.add(village); // Set에 추가
                    }
                }
            }

            if (villageSet.isEmpty()) {
                System.out.println("산불 이동 예상 경로에 인접 마을이 없습니다.");
            } else {
                System.out.println("현재 풍향에 따라 산불 및 연기 이동 가능성이 높은 마을 목록:");
                for (String village : villageSet) {
                    System.out.printf("- %s", village);
                }
                System.out.println("에 거주하시는 주민 분들은 창문을 닫고 대피 준비하시길 바랍니다.");
                System.out.println();
            }

        } catch (SQLException e) {
            System.err.println("❌ 데이터베이스 오류:");
            e.printStackTrace();
        }
    }

    /* windDirection에 따른 풍향 집합 생성 메소드.
        NotifyFire 클래스의 getInfo() 메소드랑 비슷한 느낌
        바람 방향이 만약 E 즉 동풍이면 그 산 기준 동쪽, 북동쪽, 남동쪽 마을 모두가 피해를 입을 수 있음
        따라서 케이스 별로 쪼개서 getInfo()의 directions[] 에 넣는 메소드
     */
    private String[] getDirections(String windDirection) {
        if (windDirection.length() == 2) {
            String dir1 = windDirection;
            String dir2 = String.valueOf(windDirection.charAt(0));
            String dir3 = String.valueOf(windDirection.charAt(1));
            return new String[]{dir1, dir2, dir3};
        } else if (windDirection.length() == 1) {
            return switch (windDirection) {
                case "N" -> new String[]{"N", "NW", "NE"};
                case "S" -> new String[]{"S", "SW", "SE"};
                case "E" -> new String[]{"E", "NE", "SE"};
                case "W" -> new String[]{"W", "NW", "SW"};
                default -> new String[]{};
            };

        }
        return new String[]{windDirection};
    }
}

