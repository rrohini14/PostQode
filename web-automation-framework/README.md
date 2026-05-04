# Web Automation Framework (Java + Selenium + TestNG)

Standard, scalable, and maintainable test automation framework for web application testing.

## Tech Stack
- Java 17
- Selenium WebDriver 4
- TestNG
- WebDriverManager
- Extent Reports
- Log4j2
- Owner (configuration management)

## Framework Highlights
- Page Object Model (POM)
- Thread-safe driver handling using `ThreadLocal`
- Centralized config management
- Reusable base test and base page
- Explicit wait utilities
- Screenshot capture on failure
- Extent HTML reporting
- TestNG listener integration
- Maven-based execution

## Project Structure
- `src/main/java/com/framework` - framework core components
- `src/test/java/com/framework` - tests, pages, listeners
- `src/main/resources` - framework configs
- `src/test/resources` - test data and suite configs

## Run Tests
From project root (`web-automation-framework`):

```bash
mvn clean test
```

Optional runtime params:

```bash
mvn clean test -Dbrowser=chrome -Dheadless=true
```

## Reports
- Extent report: `reports/AutomationReport.html`
- Logs: `logs/framework.log`
- Screenshots (on failure): `screenshots/`
