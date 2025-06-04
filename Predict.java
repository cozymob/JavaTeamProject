import java.sql.*;
import java.util.*;

public class Predict extends NotifyFire {

    /* 3번 기능 */

    public void checkMountains(List<String> mountainList, String region, String tableName) {
        try (Connection conn = DriverManager.getConnection(url, user, password)) {

            GetWeather gw = new GetWeather();

            Map<String, String> map = new HashMap<>();
            map.put("N", "북풍");
            map.put("NW", "북서풍");
            map.put("NE", "북동풍");
            map.put("S", "남풍");
            map.put("SE", "남동풍");
            map.put("SW", "남서풍");
            map.put("E", "동풍");
            map.put("W", "서풍");

            boolean weatherCalled = false;

            for (String mountain : mountainList) {
                if (!weatherCalled) { //날씨 정보는 한번만 가져와서 출력
                    double[] position=setLatLon(region); // 입력받은 구, 군기준 위, 경도 설정

                    if (position[0] != 0.0 && position[1] != 0.0) {

                        gw.getWeather(position[0], position[1]); // 해당 위경도로 날씨 정보 받아오기
                        weatherCalled = true;

                        String wind = map.get(gw.getWindDirection()); // 바람 방향만 가져오기
                        System.out.printf("현재 풍향은 [ %s ] 입니다.\n", wind);
                        System.out.println("현재 풍향에 따라 산불 및 연기 이동 가능성이 높은 마을 목록");
                        System.out.println();
                    }
                }

                String[] directions = getDirections(gw.getWindDirection()); // // 바람 방향 쪼개기
                Set<String> villageSet = new HashSet<>(); // 마을 이름 중복 제거를 위한 hashset
                String sql = "SELECT nearby_village FROM " + tableName + " WHERE mountain_name = ? AND village_direction = ?";


                for (String dir : directions) { // 쪼개진 풍향마다 쿼리문 실행
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, mountain);
                        pstmt.setString(2, dir);

                        ResultSet rs = pstmt.executeQuery();

                        while (rs.next()) {
                            String village = rs.getString("nearby_village");
                            villageSet.add(village); // Set에 추가(중복된 마을은 제거)
                        }
                    }catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                if(!villageSet.isEmpty()) {
                    System.out.println("[ "+mountain+" ]");
                    for (String village : villageSet) {
                        System.out.printf("< %s > ", village);
                    }
                    System.out.println("에 거주하시는 주민 분들은 창문을 닫고 대피 준비하시길 바랍니다.");
                    System.out.println();
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public double[] setLatLon(String currentRegion) {
        double lat = 0.0;
        double lon = 0.0;
        double[] position = new double[2];

        if (currentRegion.equals("군위군")) {
            lat = 36.239397;
            lon = 128.572818;
        } else if (currentRegion.equals("달성군")) {
            lat = 35.774448;
            lon = 128.431704;
        } else if (currentRegion.equals("동구")) {
            lat = 35.886704;
            lon = 128.635004;
        } else if (currentRegion.equals("서구")) {
            lat = 35.872154;
            lon = 128.559096;
        } else if (currentRegion.equals("북구")) {
            lat = 35.904626;
            lon = 128.582760;
        } else if (currentRegion.equals("남구")) {
            lat = 35.845626;
            lon = 128.594663;
        } else if (currentRegion.equals("중구")) {
            lat = 35.869634;
            lon = 128.607042;
        } else if (currentRegion.equals("달서구")) {
            lat = 35.829398;
            lon = 128.532611;
        } else if (currentRegion.equals("수성구")) {
            lat = 35.856154;
            lon = 128.639360;
        }

        position[0] = lat;
        position[1] = lon;

        return position;
    }

    /* windDirection에 따른 풍향 집합 생성 메소드.
        NotifyFire 클래스의 getInfo() 메소드랑 비슷한 느낌
        바람 방향이 만약 E 즉 동풍이면 그 산 기준 동쪽, 북동쪽, 남동쪽 마을 모두가 피해를 입을 수 있음
        따라서 케이스 별로 쪼개서 getInfo()의 directions[] 에 넣는 메소드
     */
    public String[] getDirections(String windDirection) {
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

