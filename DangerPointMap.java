import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URI;

public class DangerPointMap {



    public void InputAndSwing() {
        // GUI 띄우기
        SwingUtilities.invokeLater(() -> showRiskWindow());
    }

    public void showRiskWindow() {
        JFrame frame = new JFrame("산불 위험도 알림");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300); // 조금 더 여유 있는 창 크기

        JButton button = new JButton("실시간 산불위험지도 열기");

        // 버튼 폰트 크기 키우기
        button.setFont(new Font("맑은 고딕", Font.BOLD, 20)); // 글꼴과 크기 설정
        button.setPreferredSize(new Dimension(300, 80)); // 버튼 크기 설정

        // 가운데 배치
        frame.setLayout(new GridBagLayout());
        frame.add(button);

        button.addActionListener((ActionEvent e) -> {
            try {
                Desktop.getDesktop().browse(new URI("http://forestfire.nifos.go.kr/menu.action?menuNum=1"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        frame.setVisible(true);
    }
}