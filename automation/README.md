# NouriBiome Selenium UI Automation

A maintainable Java 21 + Selenium 4 + TestNG automation framework for the NouriBiome storefront.

## Framework design
- Page Object Model (POM): page locators and actions are isolated from test assertions.
- DriverFactory: centralized Chrome/Firefox creation; Selenium Manager resolves browser drivers.
- ThreadLocal WebDriver: supports parallel TestNG classes without sharing browser instances.
- Explicit waits only: implicit wait is set to zero to avoid mixed-wait issues.
- ConfigReader: properties can be overridden by JVM system properties.
- TestNG: suite, test grouping, data providers and lifecycle hooks.
- Allure: failure screenshot attachments; screenshots are also saved under `target/screenshots`.
- Maven Surefire: standard test execution and reports.

## Requirements
- JDK 21
- Maven 3.9+
- Google Chrome (recommended) or Firefox
- NouriBiome Spring Boot app running at the configured base URL

Selenium 4.52.0 uses Selenium Manager to manage browser drivers automatically; no manual chromedriver download is normally required. See the [official Selenium downloads](https://www.selenium.dev/downloads/).

## Start the application
Start PostgreSQL and the Spring Boot backend first. From the repository's `backend` folder:
```bash
mvn spring-boot:run
```
The default test URL is `http://localhost:8080`.

## Run UI tests
From this `automation` folder:
```bash
mvn clean test
```

Run headed Chrome:
```bash
mvn clean test -Dheadless=false
```

Run Firefox:
```bash
mvn clean test -Dbrowser=firefox
```

Run against another environment:
```bash
mvn clean test -DbaseUrl=https://your-test-host.example
```

## Reports and diagnostics
- TestNG/Maven report: `target/surefire-reports/index.html`
- Failure screenshots: `target/screenshots/`
- Allure results: `target/allure-results/`

Generate an Allure HTML report if Allure CLI is installed:
```bash
allure serve target/allure-results
```

## Current UI coverage
1. Homepage title, brand and hero heading.
2. FAQ accordion.
3. Quantity selection and price summary for 1, 2 and 3 units.
4. Quantity lower and upper bounds (1 and 10).
5. Checkout modal and required customer fields.
6. Mobile navigation breakpoint.

## Important test boundary
The suite does not submit a real order or trigger Razorpay payment. Use Razorpay TEST mode and a dedicated test environment for payment end-to-end tests. Never run payment tests against live customer traffic.

## Adding tests
- Put locators and user actions in a Page Object under `pages/`.
- Put assertions and scenario intent in `tests/`.
- Reuse `BaseTest`, `DriverFactory`, `WaitUtils` and `ConfigReader`.
- Prefer accessible attributes and stable IDs over brittle absolute XPath.
- Do not add Thread.sleep; use explicit waits.
- Keep test data synthetic; never store customer PII or secrets in source control.
