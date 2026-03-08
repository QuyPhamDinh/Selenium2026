# 🚀 Selenium Automation Framework

A scalable, maintainable, and production-ready test automation framework built with **Selenium 4**, **Cucumber BDD**, *
*TestNG**, and **Maven**.

---

## 📋 Table of Contents

- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Setup](#setup)
- [Running Tests](#running-tests)
- [Parallel Execution](#parallel-execution)
- [Cross-Browser Testing](#cross-browser-testing)
- [Environments](#environments)
- [Reporting](#reporting)
- [Rerun Failed Tests](#rerun-failed-tests)
- [CI/CD Integration](#cicd-integration)
- [Design Patterns](#design-patterns)
- [Contributing](#contributing)

---

## 🛠 Tech Stack

| Technology    | Version | Purpose                          |
|---------------|---------|----------------------------------|
| Java          | 17+     | Programming language             |
| Selenium      | 4.20.0  | Browser automation               |
| Cucumber      | 7.15.0  | BDD framework (Gherkin syntax)   |
| TestNG        | 7.10.2  | Test runner & parallel execution |
| Maven         | 3.9+    | Build & dependency management    |
| Allure        | 2.27.0  | Test reporting                   |
| Log4j2        | 2.23.1  | Logging                          |
| PicoContainer | 7.15.0  | Dependency injection for steps   |
| Apache POI    | 5.2.5   | Excel test data reader           |

---

## 📁 Project Structure

```
Selenium2026/
├── pom.xml
├── README.md
├── .gitignore
│
├── src/
│   ├── main/
│   │   └── java/
│   │       ├── core/
│   │       │   ├── DriverManager.java          # ThreadLocal WebDriver
│   │       │   ├── DriverFactory.java          # Browser factory
│   │       │   └── BrowserType.java            # Browser enum
│   │       ├── config/
│   │       │   └── ConfigReader.java           # Environment config reader
│   │       ├── pages/
│   │       │   ├── BasePage.java               # Common page actions
│   │       │   ├── LoginPage.java
│   │       │   ├── DashboardPage.java
│   │       │   └── components/
│   │       │       └── HeaderComponent.java
│   │       ├─�� utils/
│   │       │   └── ScreenshotUtils.java
│   │       ├── constants/
│   │       │   └── FrameworkConstants.java
│   │       └── exceptions/
│   │           └── FrameworkException.java
│   │
│   └── test/
│       ├── java/
│       │   ├── context/
│       │   │   └── ScenarioContext.java        # Shared state between steps
│       │   ├── stepdefinitions/
│       │   │   ├── LoginSteps.java
│       │   │   └── CommonSteps.java
│       │   ├── hooks/
│       │   │   ├── Hooks.java                  # Before/After lifecycle
│       │   │   └── TestNGParameterListener.java
│       │   └── runners/
│       │       ├── TestNGRunner.java            # Main runner
│       │       ├── SmokeRunner.java             # @smoke tag runner
│       │       ├── RegressionRunner.java        # @regression tag runner
│       │       └── FailedRerunRunner.java       # Rerun failed scenarios
│       │
│       └── resources/
│           ├── features/
│           │   ├── login/
│           │   │   └── login.feature
│           │   └── dashboard/
│           │       └── dashboard.feature
│           ├── config/
│           │   ├── qa.properties
│           │   ├── staging.properties
│           │   └── prod.properties
│           └── testng-suites/
│               ├── smoke-suite.xml
│               ├── regression-suite.xml
│               ├── parallel-suite.xml
│               └── cross-browser-suite.xml
```

---

## ✅ Prerequisites

| Tool   | Required Version | Check Command             |
|--------|------------------|---------------------------|
| Java   | 17+              | `java -version`           |
| Maven  | 3.9+             | `mvn -version`            |
| Chrome | Latest           | `google-chrome --version` |
| Allure | 2.x (optional)   | `allure --version`        |

### Install (Mac)

```bash
# Java 17
brew install openjdk@17
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc
source ~/.zshrc

# Maven
brew install maven

# Allure (for reports)
brew install allure
```

### Install (Windows)

```bash
# Use Scoop
scoop install openjdk17
scoop install maven
scoop install allure
```

---

## 🔧 Setup

```bash
# 1. Clone the repository
git clone https://github.com/QuyPhamDinh/Selenium2026.git
cd Selenium2026

# 2. Download all dependencies
mvn clean install -U

# 3. Verify build
mvn validate

# 4. Verify dependencies
mvn dependency:tree | grep selenium
```

---

## 🚀 Running Tests

### Basic Commands

```bash
# ──────────────────────────────────────────
# Run ALL tests (default: chrome, qa env)
# ──────────────────────────────────────────
mvn clean test

# ──────────────────────────────────────────
# Run Smoke Tests
# ──────────────────────────────────────────
mvn clean test -Dsuite=smoke-suite

# ──────────────────────────────────────────
# Run Regression Tests
# ──────────────────────────────────────────
mvn clean test -Dsuite=regression-suite

# ──────────────────────────────────────────
# Run with specific browser
# ──────────────────────────────────────────
mvn clean test -Dsuite=smoke-suite -Dbrowser=firefox
mvn clean test -Dsuite=smoke-suite -Dbrowser=chrome
mvn clean test -Dsuite=smoke-suite -Dbrowser=edge

# ──────────────────────────────────────────
# Run with specific environment
# ──────────────────────────────────────────
mvn clean test -Dsuite=smoke-suite -Denv=qa
mvn clean test -Dsuite=smoke-suite -Denv=staging
mvn clean test -Dsuite=smoke-suite -Denv=prod

# ──────────────────────────────────────────
# Run headless (no browser UI)
# ──────────────────────────────────────────
mvn clean test -Dsuite=smoke-suite -Dheadless=true

# ──────────────────────────────────────────
# Combine multiple options
# ──────────────────────────────────────────
mvn clean test \
    -Dsuite=regression-suite \
    -Denv=staging \
    -Dbrowser=chrome \
    -Dheadless=true
```

### Filter by Cucumber Tags

```bash
# Run only @smoke tagged scenarios
mvn clean test -Dcucumber.filter.tags="@smoke"

# Run @regression but NOT @wip
mvn clean test -Dcucumber.filter.tags="@regression and not @wip"

# Run specific feature
mvn clean test -Dcucumber.filter.tags="@login"

# Run multiple tags
mvn clean test -Dcucumber.filter.tags="@smoke or @critical"
```

---

## ⚡ Parallel Execution

### Scenarios in Parallel (Single Browser)

```bash
# 4 scenarios run simultaneously in Chrome
mvn clean test -Dsuite=parallel-suite
```

```xml
<!-- parallel-suite.xml -->
<suite name="Parallel" parallel="methods"
       thread-count="4" data-provider-thread-count="4">
    <test name="Parallel Scenarios">
        <classes>
            <class name="runners.TestNGRunner"/>
        </classes>
    </test>
</suite>
```

### Adjust Thread Count

```bash
# 2 threads (slow machine)
mvn clean test -Dsuite=parallel-suite -Dparallel.count=2

# 8 threads (powerful machine / CI server)
mvn clean test -Dsuite=parallel-suite -Dparallel.count=8
```

---

## 🌐 Cross-Browser Testing

### All Browsers in Parallel

```bash
mvn clean test -Dsuite=cross-browser-suite
```

```xml
<!-- cross-browser-suite.xml -->
<suite name="Cross Browser" parallel="tests" thread-count="3">
    <test name="Chrome">
        <parameter name="browser" value="chrome"/>
        <classes>
            <class name="runners.SmokeRunner"/>
        </classes>
    </test>
    <test name="Firefox">
        <parameter name="browser" value="firefox"/>
        <classes>
            <class name="runners.SmokeRunner"/>
        </classes>
    </test>
    <test name="Edge">
        <parameter name="browser" value="edge"/>
        <classes>
            <class name="runners.SmokeRunner"/>
        </classes>
    </test>
</suite>
```

### Parallel Modes Explained

```
parallel="tests"
  → Each <test> block (browser) runs in its own thread
  → Scenarios within each browser run sequentially
  → 3 browsers × sequential scenarios

parallel="tests" + DataProvider(parallel=true)
  → Each browser runs in its own thread
  → Scenarios WITHIN each browser also run in parallel
  → 3 browsers × parallel scenarios = MAXIMUM SPEED
```

---

## 🌍 Environments

### Configuration Files

```
src/test/resources/config/
├── qa.properties        # QA environment
├── staging.properties   # Staging environment
└── prod.properties      # Production environment
```

### Example: qa.properties

```properties
base.url=https://qa.myapp.com
implicit.wait=10
explicit.wait=20
headless=false
browser=chrome
screenshot.on.failure=true
retry.count=1
grid.url=http://localhost:4444/wd/hub
```

### Switch Environment

```bash
# QA (default)
mvn clean test -Denv=qa

# Staging
mvn clean test -Denv=staging

# Production
mvn clean test -Denv=prod

# Override specific config property
mvn clean test -Denv=qa -Dbase.url=https://custom.url.com
```

---

## 📊 Reporting

### Allure Report (Recommended)

```bash
# Run tests first
mvn clean test -Dsuite=smoke-suite

# Generate and open report in browser
allure serve target/allure-results

# OR generate static HTML report
allure generate target/allure-results -o target/allure-report --clean
allure open target/allure-report
```

### Cucumber HTML Report

```
Generated automatically after each run:
  target/cucumber-reports/cucumber.html

Open in browser:
  open target/cucumber-reports/cucumber.html        # Mac
  start target/cucumber-reports/cucumber.html       # Windows
```

---

## 🔄 Rerun Failed Tests

### Automatic Rerun

```bash
# Step 1: Run tests (failures saved to target/failed_scenarios.txt)
mvn clean test -Dsuite=regression-suite

# Step 2: Rerun only failed scenarios
mvn test -Dcucumber.features=@target/failed_scenarios.txt
```

### Using Rerun Runner

```bash
# Dedicated runner for failed scenarios
mvn test -Dtest=FailedRerunRunner
```

---

## 🔄 CI/CD Integration

### Jenkins Pipeline

```groovy
pipeline {
    agent any

    parameters {
        choice(name: 'ENV', choices: ['qa', 'staging', 'prod'])
        choice(name: 'SUITE', choices: ['smoke-suite', 'regression-suite'])
        choice(name: 'BROWSER', choices: ['chrome', 'firefox', 'edge'])
    }

    stages {
        stage('Test') {
            steps {
                sh """
                    mvn clean test \
                      -Dsuite=${params.SUITE} \
                      -Denv=${params.ENV} \
                      -Dbrowser=${params.BROWSER} \
                      -Dheadless=true
                """
            }
        }

        stage('Rerun Failed') {
            when { expression { fileExists('target/failed_scenarios.txt') } }
            steps {
                sh 'mvn test -Dcucumber.features=@target/failed_scenarios.txt'
            }
        }
    }

    post {
        always {
            allure results: [[path: 'target/allure-results']]
        }
    }
}
```

### GitHub Actions

```yaml
name: Automation Tests

on:
  push:
    branches: [ main, develop ]
  schedule:
    - cron: '0 6 * * *'    # Daily at 6 AM

jobs:
  test:
    runs-on: ubuntu-latest
    strategy:
      matrix:
        browser: [ chrome, firefox ]

    steps:
      - uses: actions/checkout@v4

      - name: Setup Java 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Run Tests
        run: |
          mvn clean test \
            -Dsuite=smoke-suite \
            -Denv=qa \
            -Dbrowser=${{ matrix.browser }} \
            -Dheadless=true

      - name: Allure Report
        uses: simple-elf/allure-report-action@master
        if: always()
        with:
          allure_results: target/allure-results

      - name: Upload Report
        uses: actions/upload-artifact@v4
        if: always()
        with:
          name: test-report-${{ matrix.browser }}
          path: target/cucumber-reports/
```

---

## 🏗 Design Patterns

| Pattern              | Where Used                              |
|----------------------|-----------------------------------------|
| Page Object Model    | `pages/` — UI logic encapsulation       |
| Factory Pattern      | `DriverFactory` — browser creation      |
| Singleton            | `ConfigReader` — one-time config load   |
| ThreadLocal          | `DriverManager` — parallel-safe drivers |
| Fluent Interface     | Page methods return next page object    |
| Component Object     | `components/` — reusable UI sections    |
| Dependency Injection | PicoContainer — shared step state       |

---

## 📝 Quick Reference

```bash
# ─── MOST USED COMMANDS ───

# Smoke test on QA
mvn clean test -Dsuite=smoke-suite -Denv=qa

# Regression headless on Staging
mvn clean test -Dsuite=regression-suite -Denv=staging -Dheadless=true

# Cross-browser
mvn clean test -Dsuite=cross-browser-suite

# Specific tag
mvn clean test -Dcucumber.filter.tags="@login"

# Parallel (4 threads)
mvn clean test -Dsuite=parallel-suite

# Rerun failures
mvn test -Dcucumber.features=@target/failed_scenarios.txt

# Generate Allure report
allure serve target/allure-results
```

---

## 🤝 Contributing

```bash
# 1. Create feature branch
git checkout -b feature/your-feature

# 2. Write tests
# 3. Run locally
mvn clean test -Dsuite=smoke-suite

# 4. Commit
git add .
git commit -m "Add: your feature description"

# 5. Push
git push origin feature/your-feature

# 6. Create Pull Request on GitHub
```

### Branch Naming Convention

```
feature/login-tests
feature/dashboard-validation
bugfix/flaky-search-test
refactor/driver-factory
```

---

## 📄 License

MIT License — feel free to use and modify.