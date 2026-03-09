# 🔗 BrowserHolder — Why It Exists & How It Works

## What Is BrowserHolder

A simple `ThreadLocal<String>` that stores the browser name per thread, acting as a **bridge between TestNG and Cucumber
** in cross-browser parallel execution.

```java
public class BrowserHolder {
    private static final ThreadLocal<String> browserThread = new ThreadLocal<>();

    public static void set(String browser) {
        browserThread.set(browser);
    }

    public static String get() {
        return browserThread.get();
    }

    public static void clear() {
        browserThread.remove();
    }
}
```

---

## The Problem It Solves

### The Gap Between TestNG and Cucumber

```
TestNG World                          Cucumber World
────────────                          ──────────────

CrossBrowserRunner.java               Hooks.java
  this.browser = "firefox"              @Before setUp()
  (from suite XML parameter)              browser = ???

         │                                     │
         │          NO CONNECTION              │
         │◄───────────────────────────────────►│
         │                                     │
         │  Separate classes                   │
         │  Separate frameworks                │
         │  Running in spawned threads         │
```

### The Detailed Flow

```
Suite XML
  ├── <test> browser=chrome  → CrossBrowserRunner instance A
  │     │
  │     └── @BeforeTest → this.browser = "chrome"
  │           │
  │           └── DataProvider(parallel=true) → spawns threads
  │                 │
  │                 ├── Thread 32 → runScenario()
  │                 │                    │
  │                 │                    └── Cucumber @Before Hook
  │                 │                          │
  │                 │                          └── HOW DOES HOOK KNOW
  │                 │                              IT SHOULD LAUNCH CHROME?
  │                 │                              ❌ IT DOESN'T!
  │                 │
  │                 ├── Thread 34 → same problem ❌
  │                 └── Thread 36 → same problem ❌
  │
  └── <test> browser=firefox → CrossBrowserRunner instance B
        │
        └── @BeforeTest → this.browser = "firefox"
              │
              └── DataProvider(parallel=true) → spawns threads
                    │
                    ├── Thread 33 → same problem ❌
                    ├── Thread 35 → same problem ❌
                    └── Thread 37 → same problem ❌
```

---

## How BrowserHolder Fixes It

```
CrossBrowserRunner                      Hooks.java
──────────────────                      ──────────

runScenario()                           @Before setUp()
  │                                       │
  ├── BrowserHolder.set("firefox")        ├── BrowserHolder.get()
  │   ThreadLocal stores                  │   ThreadLocal reads
  │   "firefox" for Thread 33             │   "firefox" for Thread 33
  │                                       │
  └── super.runScenario() ──────────────► └── launches Firefox ✅

  SAME THREAD (Thread 33)
  ThreadLocal guarantees isolation
```

### In Code

```java
// CrossBrowserRunner.java — SETS the browser
@Override
public void runScenario(PickleWrapper pickle, FeatureWrapper feature) {
    BrowserHolder.set(this.browser);     // ← store for THIS thread
    super.runScenario(pickle, feature);  // ← triggers Cucumber @Before
}

// Hooks.java — READS the browser
@Before
public void setUp(Scenario scenario) {
    String browser = BrowserHolder.get(); // ← read for THIS thread
    WebDriver driver = new DriverFactory().createDriver(browser, headless);
}
```

### Why It Works

```
THE KEY INSIGHT:
────────────────

runScenario() is called ONCE per scenario.
It runs in the SAME THREAD as Cucumber's @Before/@After.

TestNG calls:
  Thread 33 → runScenario(pickle, feature)
                  │
                  ├── BrowserHolder.set("firefox")   ← we add this
                  │
                  └── super.runScenario()
                        │
                        ├── Cucumber @Before            ← SAME thread 33
                        │     └── BrowserHolder.get()   ← reads "firefox" ✅
                        │
                        ├── Step Definitions            ← SAME thread 33
                        │
                        └── Cucumber @After             ← SAME thread 33
                              └── BrowserHolder.clear()
```

---

## Thread Safety

```
Thread 32 (Chrome):
  BrowserHolder ThreadLocal → "chrome"     only Thread 32 sees this

Thread 33 (Firefox):
  BrowserHolder ThreadLocal → "firefox"    only Thread 33 sees this

Thread 34 (Chrome):
  BrowserHolder ThreadLocal → "chrome"     only Thread 34 sees this

Each thread has its OWN copy of the ThreadLocal variable.
They CANNOT see each other's values.
100% thread-safe by design.
```

---

## What Happens WITHOUT BrowserHolder

### ❌ Attempt 1: System.setProperty (Global — Race Condition)

```java
// CrossBrowserRunner.java
@BeforeTest
public void setUpBrowser(ITestContext context) {
    System.setProperty("browser", "firefox");  // GLOBAL!
}

// Hooks.java
@Before
public void setUp() {
    String browser = System.getProperty("browser");
}
```

```
FAILS:

  Thread 20 (@BeforeTest Chrome):  System.setProperty("browser", "chrome")
  Thread 21 (@BeforeTest Firefox): System.setProperty("browser", "firefox")
                                                                      ↑
                                                              OVERWRITES chrome!

  Thread 32 reads: System.getProperty("browser") → "firefox" ← WRONG!
  Thread 33 reads: System.getProperty("browser") → "firefox" ← correct by luck

  System.setProperty is GLOBAL — shared across ALL threads.
  Last write wins — race condition.
```

### ❌ Attempt 2: TestNG @Parameters in Hooks

```java
// Hooks.java
@Before
public void setUp() {
    // ❌ Cannot use @Parameters in Cucumber hooks
    // ❌ Cucumber hooks are NOT TestNG methods
    // ❌ No ITestContext available in Cucumber @Before
}
```

```
FAILS:

  Cucumber @Before is managed by Cucumber, not TestNG.
  It has no access to TestNG's ITestContext or @Parameters.
  These are two completely separate frameworks.
```

### ❌ Attempt 3: InheritableThreadLocal

```java
private static final InheritableThreadLocal<String> browser =
        new InheritableThreadLocal<>();
```

```
FAILS:

  DataProvider(parallel=true) creates a thread POOL.
  Pool threads are REUSED, not freshly spawned.
  InheritableThreadLocal only works for NEW child threads.
  Pool threads may inherit WRONG parent's value.
```

### ❌ Attempt 4: TestNG Listener (IInvokedMethodListener)

```java
public class Listener implements IInvokedMethodListener {
    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult result) {
        String browser = result.getTestContext()
                .getCurrentXmlTest()
                .getParameter("browser");
        // set for THIS thread
    }
}
```

```
FAILS:

  Listener fires for runScenario() method in TestNG's thread.
  BUT DataProvider spawns DIFFERENT threads for each scenario.
  Listener thread ≠ Cucumber @Before thread.
```

### ⚠️ Attempt 5: Scenario Tags

```gherkin
@chrome
Scenario: Test ONE
When I do something
```

```
WORKS but inflexible:

  ❌ Browser hardcoded in feature file
  ❌ Cannot change browser from suite XML
  ❌ Cannot run same feature on multiple browsers
  ❌ Feature files should be browser-agnostic
```

---

## Comparison Table

```
┌──────────────────────────┬────────┬──────────────────────────────────┐
│ Approach                 │ Works? │ Why                              │
├──────────────────────────┼────────┼──────────────────────────────────┤
│ System.setProperty       │ ❌     │ Global, race condition           │
│ @Parameters in Hooks     │ ❌     │ Hooks aren't TestNG methods      │
│ InheritableThreadLocal   │ ❌     │ Thread pool reuses threads       │
│ TestNG Listener          │ ❌     │ Different thread than Hooks      │
│ Scenario Tags            │ ⚠️     │ Works but inflexible             │
│ BrowserHolder (TL)       │ ✅     │ runScenario() = same thread      │
│ + runScenario()          │        │ as Cucumber @Before              │
└──────────────────────────┴────────┴──────────────────────────────────┘
```

---

## Full Flow Diagram

```
Suite XML: <parameter name="browser" value="firefox"/>
    │
    ▼
@BeforeTest (ITestContext)
    │
    └── this.browser = "firefox"     (instance variable)
            │
            ▼
DataProvider(parallel=true)
    │
    ├── Thread 33: runScenario()
    │       │
    │       ├── BrowserHolder.set("firefox")     ← ThreadLocal SET
    │       │
    │       └── super.runScenario()
    │             │
    │             ├── Cucumber @Before
    │             │     │
    │             │     ├── BrowserHolder.get()   ← ThreadLocal GET → "firefox"
    │             │     └── DriverFactory.createDriver("firefox")
    │             │
    │             ├── Step Definitions execute
    │             │
    │             └── Cucumber @After
    │                   │
    │                   ├── DriverManager.quitDriver()
    │                   └── BrowserHolder.clear()  ← ThreadLocal CLEAR
    │
    ├── Thread 35: runScenario()     ← same flow, isolated ThreadLocal
    │
    └── Thread 37: runScenario()     ← same flow, isolated ThreadLocal
```

---

## Summary

```
BrowserHolder exists because:

  1. TestNG and Cucumber are SEPARATE frameworks
  2. Suite XML parameter lives in TestNG world
  3. @Before hook lives in Cucumber world
  4. They meet ONLY inside runScenario()
  5. ThreadLocal bridges the gap safely

Without BrowserHolder:
  → All scenarios launch the default browser (chrome)
  → Cross-browser testing from suite XML is impossible
  → You'd have to hardcode browser in feature files

With BrowserHolder:
  → Browser controlled entirely from suite XML
  → Feature files stay browser-agnostic
  → Thread-safe parallel cross-browser execution
  → Zero code changes to add new browsers
```