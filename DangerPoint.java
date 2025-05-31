import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.util.*;

// === 데이터 구조 ===
record Weather(double humidity, double windSpeed) {}
record FireHistory(String region, int recentFires) {}

public class DangerPoint {

    // 위험 점수 계산
    public static int computeRiskScore(Weather weather, FireHistory history) {
        int score = 0;

        // 습도 점수
        if (weather.humidity() < 20) score += 40;
        else if (weather.humidity() < 30) score += 30;
        else if (weather.humidity() < 40) score += 20;
        else score += 10;

        // 풍속 점수
        if (weather.windSpeed() > 10) score += 40;
        else if (weather.windSpeed() > 5) score += 30;
        else if (weather.windSpeed() > 2) score += 20;
        else score += 10;

        // 산불 발생 이력 점수
        if (history.recentFires() >= 5) score += 30;
        else if (history.recentFires() >= 3) score += 20;
        else if (history.recentFires() >= 1) score += 10;

        return score;
    }

    // 등급 분류
    public static String classifyRiskLevel(int score) {
        if (score >= 90) return "매우 높음";
        else if (score >= 70) return "높음";
        else if (score >= 50) return "보통";
        else return "낮음";
    }

    public static void InputAndSwing() {
        // 콘솔 입력
        Scanner sc = new Scanner(System.in);
        System.out.print("현재 지역명을 입력하세요: ");
        String region = sc.nextLine();

        System.out.print("현재 습도(%)를 입력하세요: ");
        double humidity = sc.nextDouble();

        System.out.print("현재 풍속(m/s)을 입력하세요: ");
        double windSpeed = sc.nextDouble();

        System.out.print("최근 산불 발생 횟수: ");
        int recentFires = sc.nextInt();

        Weather weather = new Weather(humidity, windSpeed);
        FireHistory history = new FireHistory(region, recentFires);

        int score = computeRiskScore(weather, history);
        String level = classifyRiskLevel(score);

        System.out.println("\n[ " + region + " ] 지역의 산불 위험도: " + level + " (" + score + "점)");

        // GUI 띄우기
        SwingUtilities.invokeLater(() -> showRiskWindow(region, level));
    }

    public static void showRiskWindow(String region, String riskLevel) {
        JFrame frame = new JFrame("산불 위험도 알림");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);

        JLabel label = new JLabel(region + " 지역의 위험도: " + riskLevel, SwingConstants.CENTER);
        label.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        frame.add(label, BorderLayout.CENTER);

        JButton button = new JButton("실시간 산불위험지도 열기");
        button.addActionListener((ActionEvent e) -> {
            try {
                Desktop.getDesktop().browse(new URI("http://forestfire.nifos.go.kr/menu.action?menuNum=1"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        frame.add(button, BorderLayout.SOUTH);
        frame.setVisible(true);
    }
}