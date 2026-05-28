# JavaFX-WeatherForecasting
## Multi-User JavaFX Weather Forecasting & Identity Persistence Application

This cross-platform desktop application connects a secure JavaFX user interface with real-time remote RESTful JSON enterprise meteorological streams via OpenWeatherMap API[cite: 1].

### 🛠️ Core Technical Features Include:
* **Decoupled Identity Subsystem:** Standalone controller views handling registration and secure login loops[cite: 1].
* **Advanced Cryptography:** PBKDF2WithHmacSHA256 password salting models (120,000 computation loops) and secure binary object serialization (`users.dat`)[cite: 1].
* **Session Isolation Ledger:** Character streaming tracking (`history.txt`) mapped strictly to personal authenticated dashboard views[cite: 1].
* **Dynamic Weather Grid:** Horizontally-aligned 5-day predictive forecasting carousel derived from nested JSON arrays[cite: 1].
  

## Installation & Execution Procedure

This project uses **Maven** for dependency automation and building framework pipelines. Follow these steps to execute the application locally:

1. **Prerequisites:** Ensure you have **Java 11 JDK** (or higher) and **Maven** installed on your system.
2. **Clone or Download:** Download this repository as a ZIP file and extract it to your computer.
3. **Open in IDE:** Launch **IntelliJ IDEA**, select "Open", and navigate to the root folder that contains the `pom.xml` file.
4. **Reload Maven Dependencies:** Allow the IDE to automatically import the dependencies declared in the `pom.xml` (such as `org.json` and JavaFX binaries).
5. **Run the Application:** Navigate through the project structure to `src/main/java/com/example/weatherappdemo/WeatherApplication.java`, right-click the file, and select **Run** to launch the login dashboard interface.
