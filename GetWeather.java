import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class GetWeather {

    /* 3번 기능에 필요한 클래스
    * 대구 행정구역별로 날씨 받아옴*/

    String windDirection;
    int humidity;
    double windSpeed;

    public void getWeather(double lat, double lon) { //군, 구청 위,경도로 행정구역별 날씨 받아옴
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

            if(degree >= 22.5 && degree < 67.5) windDirection = "NE";
            else if(degree >= 67.5 && degree < 112.5) windDirection = "E";
            else if(degree >= 112.5 && degree < 157.5) windDirection = "SE";
            else if(degree >= 157.5 && degree < 202.5) windDirection = "S";
            else if(degree >= 202.5 && degree < 247.5) windDirection = "SW";
            else if(degree >= 247.5 && degree < 292.5) windDirection = "W";
            else if(degree >= 292.5 && degree < 337.5) windDirection = "NW";
            else windDirection = "N";

            System.out.println("습도: " + humidity + "%");
            System.out.print("풍속: " + windSpeed + " m/s");
            if (windSpeed < 4) System.out.println(" 약한 바람");
            else if (windSpeed < 9) System.out.println(" 약간 강한 바람");
            else if (windSpeed < 14) System.out.println(" 강한 바람");
            else System.out.println(" 매우 강한 바람");
            System.out.println("풍향: " + windDirection);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}