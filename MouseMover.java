import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.JOptionPane;

public class MouseMover {

    // PID를 저장할 파일명
    private static final String PID_FILE = "mouse_mover.pid";

    public static void main(String[] args) {
        // 1. 이미 실행 중인지 확인 (Toggle 기능)
        if (isAlreadyRunning()) {
            JOptionPane.showMessageDialog(null, "이미 실행 중인 프로그램을 감지했습니다. 기존 프로세스를 종료합니다.", "프로그램 종료", JOptionPane.INFORMATION_MESSAGE);
            terminateExistingProcess();
            System.exit(0);
        }

        // 2. 실행 시작 알림 및 PID 저장
        JOptionPane.showMessageDialog(null, "마우스 이동 프로그램 시작 (4분 간격)", "프로그램 시작", JOptionPane.INFORMATION_MESSAGE);
        savePid();

        // 3. 종료 시 PID 파일 삭제 (Shutdown Hook)
        Runtime.getRuntime().addShutdownHook(new Thread(MouseMover::deletePidFile));

        // 4. 마우스 흔들기 로직 (핵심)
        try {
            Robot robot = new Robot();
            while (true) {
                // 현재 마우스 위치 가져오기
                Point location = MouseInfo.getPointerInfo().getLocation();
                // 1픽셀 이동 (사용자 방해 최소화)
                robot.mouseMove(location.x + 1, location.y + 1);
                // 원래 위치로 복귀 (선택사항, 일단은 이동만 하게 둠)
                // robot.mouseMove(location.x, location.y);
                
                // 4분 대기 (240초 * 1000 = 240,000ms) - 화면보호기 5분 전 동작
                Thread.sleep(240_000);
            }
        } catch (InterruptedException ie) {
            // 종료 신호 발생 시 조용히 종료
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            deletePidFile();
        }
    }

    // 이미 실행 중인 프로세스가 있는지 확인 (파일 존재 여부 + 실제 프로세스 체크)
    private static boolean isAlreadyRunning() {
        if (!Files.exists(Paths.get(PID_FILE))) {
            return false;
        }

        try {
            String pid = new String(Files.readAllBytes(Paths.get(PID_FILE)), "UTF-8").trim();
            // 윈도우 tasklist 명령어로 실제 해당 PID가 살아있는지 이중 체크
            Process process = Runtime.getRuntime().exec("tasklist /FI \"PID eq " + pid + "\"");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(pid)) {
                    return true; // PID가 실제 실행 중임
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 파일은 있는데 프로세스가 없으면 (비정상 종료 등), 파일 지우고 false 리턴
        deletePidFile();
        return false;
    }

    // 기존 실행 중인 프로세스 강제 종료
    private static void terminateExistingProcess() {
        try {
            if (Files.exists(Paths.get(PID_FILE))) {
                String pid = new String(Files.readAllBytes(Paths.get(PID_FILE)), "UTF-8").trim();
                // 윈도우 taskkill 명령어로 강제 종료 (/F)
                Runtime.getRuntime().exec("taskkill /F /PID " + pid);
                Thread.sleep(1000); // 종료 대기
                deletePidFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 현재 프로세스 ID 저장
    private static void savePid() {
        try (FileWriter writer = new FileWriter(PID_FILE)) {
            writer.write(String.valueOf(getCurrentProcessId()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // PID 파일 삭제
    private static void deletePidFile() {
        try {
            Files.deleteIfExists(Paths.get(PID_FILE));
        } catch (IOException e) {
            // 무시
        }
    }

    // 현재 JVM의 PID 가져오기 (Java 8 호환 방식)
    private static long getCurrentProcessId() {
        // 이름이 "12345@hostname" 형식이므로 @ 앞부분을 파싱
        String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        return Long.parseLong(jvmName.split("@")[0]);
    }
}