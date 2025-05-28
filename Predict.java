import java.sql.*;
import java.util.Scanner;

public class Predict extends NotifyFire{

    /* 3번 기능 */

    GetWeather gw = new GetWeather();

    public String currentMountain;
    public String region;

    public void run(Scanner scanner) {
        System.out.println("[대구 산불 발생 예측]");
    }

    @Override
    public void getInfo(Scanner sc){
        System.out.print("현재 위치한 구 또는 군을 입력하세요: ");
        boolean flag = false;
        while(flag == false) {
            region = sc.nextLine();
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
    }

    public double[] getLatLon(){
        double lat=0.0;
        double lon=0.0;
        double[] position = new double[2];

        if(region.equals("군위") || region.equals("군위군")){
            lat=36.239397;
            lon=128.572818;
        }else if(region.equals("달성") || region.equals("달성군")){
            lat=35.774448;
            lon=128.431704;
        }else if(region.equals("동") || region.equals("동구")){
            lat=35.886704;
            lon=128.635004;
        }else if(region.equals("서") || region.equals("서구")){
            lat=35.872154;
            lon=128.559096;
        }else if(region.equals("북") || region.equals("북구")){
            lat=35.904626;
            lon=128.582760;
        }else if(region.equals("남") || region.equals("남구")){
            lat=35.845626;
            lon=128.594663;
        }else if(region.equals("중") || region.equals("중구")){
            lat=35.869634;
            lon=128.607042;
        }else if(region.equals("달서") || region.equals("달서구")){
            lat=35.829398;
            lon=128.532611;
        }else if(region.equals("수성") || region.equals("수성구")){
            lat=35.856154;
            lon=128.639360;
        }

        position[0]=lat;
        position[1]=lon;
        return position;
    }

    public void predictPath(String mountain) {

        double[] latLon = getLatLon();
        gw.getWeather(latLon[0], latLon[1]);

        currentMountain = mountain;

        if(region.equals("군위") || region.equals("군위군")) {
            sql = "SELECT nearby_village " +
                    "FROM gunwi_mountains " +
                    "WHERE mountain_name = ? AND village_direction = ?";
        }else if (region.equals("달성") || region.equals("달성군")) {
            sql = "SELECT nearby_village " +
                    "FROM dalseong_mountains " +
                    "WHERE mountain_name = ? AND village_direction = ?";
        }else if (region.equals("북") || region.equals("북구")) {
            sql = "SELECT nearby_village " +
                    "FROM buk_mountains " +
                    "WHERE mountain_name = ? AND village_direction = ?";
        }else if (region.equals("동") || region.equals("동구")) {
            sql = "SELECT nearby_village " +
                    "FROM dong_mountains " +
                    "WHERE mountain_name = ? AND village_direction = ?";
        }else if (region.equals("남") || region.equals("남구")) {
            sql = "SELECT nearby_village " +
                    "FROM nam_mountains " +
                    "WHERE mountain_name = ? AND village_direction = ?";
        }else if (region.equals("서") || region.equals("서구")) {
            sql = "SELECT nearby_village " +
                    "FROM seo_mountains " +
                    "WHERE mountain_name = ? AND village_direction = ?";
        }else if (region.equals("중") || region.equals("중구")) {
            sql = "SELECT nearby_village " +
                    "FROM seo_mountains " +
                    "WHERE mountain_name = ? AND village_direction = ?";
        }else if (region.equals("수성") || region.equals("수성구")) {
            sql = "SELECT nearby_village " +
                    "FROM suseong_mountains " +
                    "WHERE mountain_name = ? AND village_direction = ?";
        }else if (region.equals("달서") || region.equals("달서구")) {
            sql = "SELECT nearby_village " +
                    "FROM dalseo_mountains " +
                    "WHERE mountain_name = ? AND village_direction = ?";
        }

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, currentMountain);
            pstmt.setString(2, gw.windDirection );

            ResultSet rs = pstmt.executeQuery();

            found=false;
            String village=null;
            while (rs.next()) {
                found = true;
                village = rs.getString("nearby_village");
            }

            System.out.printf("현재 바람이 %s 방향으로 불고 있습니다.\n", gw.windDirection);

            if(!found) {
                System.out.println("산불 이동 예상 경로에 인접 마을이 없습니다.");
            }else{
                System.out.printf("현재 풍향에 따라 산불 이동 가능성이 높으니\n %s에 거주하시는 주민 분들은 창문을 닫고 대피 준비 하시길 바랍니다.\n",village);
            }

        } catch (SQLException e) {
            System.err.println("❌ 데이터베이스 오류:");
            e.printStackTrace();
        }
    }

    /*
    public static double calculateFFDRI(int DWI, int FMI, int TMI, double weight) {
        return (7*DWI+1.5*FMI+1.5*TMI)*weight;
    }

    // 일가중치 weight 계산
    public static double getDayWeight(LocalDate date) {
        int month=date.getMonthValue();
        int day=date.getDayOfMonth();

        return switch(month) {
            case 1, 2, 5 -> 0.85;
            case 3 -> (day <= 10) ? 0.9 : (day <= 20 ? 0.95 : 1.0);
            case 4 -> (day <= 10) ? 1.0 : (day <= 20 ? 0.95 : 0.9);
            case 6 -> 0.8;
            case 7, 8 -> 0.33;
            case 9 -> 0.5;
            case 10 -> 0.61;
            case 11 -> 0.78;
            case 12 -> 0.83;
            default -> 1.0;
        };
    }

    // 지형지수(TMI) 계산 : 일단 방위만
    public static double getTMI(String location) {
        return switch (location) {
            case "E" -> 1.5;
            case "N", "W" -> 2.5;
            case "SE", "S" -> 4.0;
            case "NW", "NE" -> 4.5;
            default -> 5.0;
        };
    }

    // 위험 등급 반환
    public static String getRiskLevel(double ffdIndex) {
        if(ffdIndex <=20) return "낮음";
        else if (ffdIndex<=40) return "보통";
        else if (ffdIndex<=60) return "위험";
        else if (ffdIndex<=80) return "매우 위험";
        else return "발생";
    }

     */
}

