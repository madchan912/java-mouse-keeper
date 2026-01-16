# 🖱️ Java Mouse Keeper (Internal Tool)

> **Summary:** 폐쇄망(Internal Network) 환경의 보안 정책 준수와 업무 효율성을 동시에 확보하기 위해 개발한 **세션 유지 유틸리티**입니다.

---

### 💡 Motivation (Why I built this?)
제가 근무하는 인프라 운영 환경은 **외부망 접속이 차단된 내부망(Private Network)**입니다.
강력한 보안 정책으로 인해 화면 보호기가 5분마다 강제로 작동하여, 장시간 모니터링이 필요한 운영 업무에 비효율이 발생했습니다.

* **Constraint:** 외부 인터넷 접속 불가로 기존 유틸리티(Mouse Jiggler 등) 다운로드 및 반입 불가.
* **Solution:** JDK는 설치되어 있다는 점에 착안하여, **Java 표준 라이브러리(AWT)만을 활용해 직접 도구를 개발**하기로 결정.
* **Result:** 보안 규정을 위반하지 않으면서도(검증 가능한 소스 코드), 팀원들의 업무 피로도를 획기적으로 개선함.

---

### 🚀 Key Features
* **Smart Toggle:** 프로세스 ID(PID) 파일을 생성하여 **On/Off(토글)** 방식으로 제어합니다. (불필요한 리소스 낭비 방지)
* **OS Interaction:** `Runtime.exec`를 통해 윈도우 프로세스 리스트(`tasklist`)를 조회하고 제어합니다.
* **Safe Execution:** 외부 라이브러리(Dependency) 없이 **Pure Java**로만 구현하여, 내부망 환경에서도 즉시 빌드 및 실행이 가능합니다.

---

### 🛠️ Tech Stack
* **Language:** Java 8+
* **Core:** `java.awt.Robot` (Input Simulation), `java.io` (File Lock & PID)
* **Build:** Native CMD (Manual Compilation)

---

### 📦 How to Build (CMD)
IDE가 없는 서버/PC 환경에서도 빌드할 수 있도록 배치 스크립트를 구성했습니다.

```bat
# build.bat
javac -encoding UTF-8 MouseMover.java
jar cvfm MouseMover.jar META-INF/MANIFEST.MF MouseMover.class
