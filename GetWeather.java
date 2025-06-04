import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class GetWeather {

    /* 날씨 가져오는 api 연동 클래스 */

    private String windDirection; // 풍향 (8방위)
    private int humidity; // 습도 (%)
    private double windSpeed; // 풍속 (m/s)
    private double rainfall; // 강우량 (mm)

    public String getWindDirection(){
        return windDirection;
    }

    public int getHumidity(){
        return humidity;
    }

    public double getRainfall(){
        return rainfall;
    }

    public void getWeather(double lat, double lon) { // 군, 구청 위,경도로 행정구역별 날씨 받아옴
        String apiKey = "c502f3364872e84c7cb7f9b861287218"; // 발급받은 API 키
        String urlString = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat +
                "&lon=" + lon + "&appid=" + apiKey + "&units=metric";

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder content = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            conn.disconnect();

            JSONObject obj = new JSONObject(content.toString());

            humidity = obj.getJSONObject("main").getInt("humidity");
            windSpeed = obj.getJSONObject("wind").getDouble("speed");
            int degree = obj.getJSONObject("wind").getInt("deg");

            // 풍향 계산
            if(degree >= 22.5 && degree < 67.5) windDirection = "NE";
            else if(degree >= 67.5 && degree < 112.5) windDirection = "E";
            else if(degree >= 112.5 && degree < 157.5) windDirection = "SE";
            else if(degree >= 157.5 && degree < 202.5) windDirection = "S";
            else if(degree >= 202.5 && degree < 247.5) windDirection = "SW";
            else if(degree >= 247.5 && degree < 292.5) windDirection = "W";
            else if(degree >= 292.5 && degree < 337.5) windDirection = "NW";
            else windDirection = "N";

            // 강우량 가져오기 (1시간 기준)
            if (obj.has("rain")) {
                JSONObject rainObj = obj.getJSONObject("rain");
                if (rainObj.has("1h")) {
                    rainfall = rainObj.getDouble("1h");
                } else if (rainObj.has("3h")) {
                    rainfall = rainObj.getDouble("3h") / 3.0; // 3시간 강우량을 1시간 평균으로
                } else {
                    rainfall = 0.0; // rain은 있지만 세부 정보 없으면 0 처리
                }
            } else {
                rainfall = 0.0; // rain 필드 없으면 강우량 0
            }

            // 결과 출력
            System.out.println();
            System.out.println("습도: " + humidity + "%");
            System.out.print("풍속: " + windSpeed + " m/s");
            if (windSpeed < 4) System.out.println(" 의 약한 바람");
            else if (windSpeed < 9) System.out.println(" 의 약간 강한 바람");
            else if (windSpeed < 14) System.out.println(" 의 강한 바람");
            else System.out.println(" 의 매우 강한 바람");  //  바람세기 기준은 기상청 기준

            System.out.println("풍향: " + windDirection);
            System.out.println("강우량: " + rainfall + " mm");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}