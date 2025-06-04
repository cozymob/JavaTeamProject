import java.util.ArrayList;

public class OnFireMountain {

    /* 현재 산불 발생 중인 산 목록 저장 */

    private ArrayList<String> onFireMountain=new ArrayList<>(); // 2번 기능 신고 횟수를 count하기 위해 중복제거 X

    public void setOnFireMountain(String mountain) {
        onFireMountain.add(mountain);
    }

    public ArrayList<String> getOnFireMountain(){
        return onFireMountain;
    }
}
