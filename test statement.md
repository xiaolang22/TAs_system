# TA Recruitment System — Test Documentation

## 1. Test Framework

A lightweight, self-contained test framework (`TestRunner`) is provided at:

```
test-classes/com/group19/TestRunner.java
```

It provides JUnit-style assertion methods without requiring external dependencies:

| Method | Description |
|--------|-------------|
| `assertEquals(expected, actual)` | Asserts two values are equal |
| `assertTrue(condition)` | Asserts a condition is true |
| `assertFalse(condition)` | Asserts a condition is false |
| `assertNull(obj)` | Asserts an object is null |
| `assertNotNull(obj)` | Asserts an object is not null |
| `assertThrows(type, runnable)` | Asserts a block throws an expected exception |
| `fail(message)` | Unconditionally fails the test |

Every test class extends `TestRunner`. Test methods are discovered automatically — any `public void` method whose name starts with `test` is treated as a test case. The `setUp()` and `tearDown()` lifecycle hooks run before and after each test.

---

## 2. Test Directory Structure

```
test-classes/com/group19/
├── TestRunner.java                  (test framework)
├── AllTests.java                    (master test suite)
├── model/
│   ├── ApplicationTest.java         (10 tests)
│   ├── JobTest.java                 (12 tests)
│   ├── LoginUserTest.java           ( 8 tests)
│   ├── NotificationTest.java        ( 9 tests)
│   ├── SavedJobTest.java            ( 6 tests)
│   ├── TATest.java                  (11 tests)
│   ├── TimelineEventTest.java       ( 6 tests)
│   └── UserAccountTest.java         (14 tests)
├── dto/
│   ├── AdminFeedItemTest.java       (14 tests)
│   ├── CVExtractedInfoTest.java     (15 tests)
│   ├── CVUploadResultTest.java      ( 7 tests)
│   ├── CandidateMatchResultTest.java(14 tests)
│   ├── DeadlineReminderViewTest.java( 8 tests)
│   ├── ParsedCVDataTest.java        (10 tests)
│   ├── ServiceResultTest.java       (17 tests)
│   ├── TARecommendationTest.java    (22 tests)
│   ├── TaApplicationOverviewTest.java(10 tests)
│   └── TaWorkloadRowTest.java       (18 tests)
├── dao/
│   ├── ApplicationDaoTest.java      (16 tests)
│   ├── JobDaoTest.java              (10 tests)
│   ├── NotificationDaoTest.java     ( 8 tests)
│   ├── SavedJobDaoTest.java         ( 7 tests)
│   ├── TADaoTest.java               ( 7 tests)
│   ├── TimelineDaoTest.java         ( 8 tests)
│   └── UserAccountDaoTest.java      (12 tests)
├── service/
│   ├── ApplicationServiceTest.java  (17 tests)
│   ├── AuthServiceTest.java         (20 tests)
│   ├── JobServiceTest.java          (15 tests)
│   └── SavedJobServiceTest.java     ( 9 tests)
├── util/
│   ├── ApplicationServiceFactoryTest.java( 4 tests)
│   ├── CVParserUtilTest.java        (12 tests)
│   ├── DataPathResolverTest.java    ( 4 tests)
│   ├── FileUploadUtilTest.java      (37 tests)
│   ├── HtmlEscapeTest.java          (13 tests)
│   └── JsonFileUtilTest.java        (12 tests)
└── filter/
    └── AuthFilterTest.java          ( 7 tests)

Total: 38 files, ~400 individual test methods
```

---

## 3. How to Compile and Run

### Prerequisites

- **JDK 17+** (the project uses Jakarta EE 10 and `java.nio.file.Files` modern APIs)
- The project JAR libraries in `web/WEB-INF/lib/` (principally `gson-2.10.1.jar`)

### Compile All Tests

From the project root (`TA_Recruitment_System/`):

```bash
# Unix / Git Bash
javac -cp "web/WEB-INF/lib/*;src;test-classes" \
      -d out \
      $(find test-classes -name "*.java")

# Windows PowerShell
javac -cp "web/WEB-INF/lib/*;src;test-classes" `
      -d out `
      $(Get-ChildItem -Recurse -Path test-classes -Filter "*.java" | % { $_.FullName })
```

### Run Individual Test Class

```bash
java -cp "web/WEB-INF/lib/*;out" com.group19.model.ApplicationTest
```

Each test class prints results to stdout:

```
=== ApplicationTest ===
Running 10 test(s)...

  [PASS] testDefaultConstructorInitialisesAllFieldsToNull
  [PASS] testSetAndGetApplicationId
  ...

Results: 10 passed, 0 failed
```

### Run All Tests (Master Suite)

```bash
java -cp "web/WEB-INF/lib/*;out" com.group19.AllTests
```

This runs every test across all layers and produces a final aggregated summary.

### IntelliJ IDEA

1. Add `test-classes` as a test source root: Right-click → **Mark Directory as** → **Test Sources Root**
2. Add `web/WEB-INF/lib/*.jar` to the module classpath
3. Run any test class directly from the IDE

---

## 4. What Is Tested

### Model Layer (8 classes)
- Constructor behaviour (default and parameterised)
- Getter/setter round-trips for every field
- Immutability verification (LoginUser has no setters)
- Constructor delegation chains (UserAccount)

### DTO Layer (10 classes)
- Factory method correctness (ServiceResult.success/failure)
- Null safety in constructors (CVExtractedInfo, CVUploadResult)
- List field immutability (TaApplicationOverview, TaWorkloadRow)
- Boolean warning detection (TaWorkloadRow.isHasWorkloadWarning)
- Generic type support (ServiceResult<String>, ServiceResult<Integer>)

### DAO Layer (7 classes)
- CRUD operations using temporary JSON files (zero external dependencies)
- Case-insensitive lookups
- Empty/null parameter handling
- Duplicate prevention logic
- Seed data initialisation (UserAccountDao)

### Service Layer (4 classes)
- **AuthService**: login with valid/invalid credentials, frozen accounts, role mismatches, registration with validation
- **JobService**: job creation with field validation, status management, deadline parsing (ISO date/datetime), keyword filtering
- **ApplicationService**: application submission, duplicate prevention, status transitions (forward/backward/same), timeline recording
- **SavedJobService**: save/unsave, duplicate handling, saved job listing

### Util Layer (6 classes)
- **HtmlEscape**: XSS prevention (all 5 HTML entities), ordering correctness
- **FileUploadUtil**: file type validation (CV and image), safe filename generation, path extraction
- **JsonFileUtil**: read/write round-trips, null/empty handling, auto-directory creation
- **DataPathResolver**: structural verification (constructor accessibility)
- **ApplicationServiceFactory**: structural verification
- **CVParserUtil**: null safety in extract(), CVExtractedInfo edge cases

### Filter Layer (1 class)
- **AuthFilter**: instantiation, lifecycle (init/destroy), interface compliance

---

## 5. Test Design Principles

1. **Isolation**: Each test is independent. DAO/Service tests create temporary JSON files in `setUp()` and delete them in `tearDown()`.
2. **No mocking framework**: Tests use real file I/O with `java.nio.file.Files.createTempFile()`, avoiding external mocking libraries.
3. **British English**: All comments and assertion messages use UK spelling (programme, behaviour, organisation, initialise, normalise).
4. **One assertion per test**: Each test method verifies a single behaviour, making failures immediately clear.
5. **Descriptive naming**: Test method names describe exactly what they verify (e.g., `testApplyForJobPreventsDuplicateApplication`).

---

## 6. Test Coverage Summary

| Layer | Classes | Test Files | Approx. Tests | Key Focus |
|-------|---------|------------|---------------|-----------|
| Model | 8 | 8 | 76 | Getters/setters, constructors, immutability |
| DTO | 18 | 10 | 135 | Factory methods, null safety, list handling |
| DAO | 7 | 7 | 68 | CRUD with temp files, edge cases |
| Service | 19 | 4 | 61 | Business logic, validation, state transitions |
| Util | 6 | 6 | 82 | Pure functions, file I/O, XSS prevention |
| Filter | 1 | 1 | 7 | Lifecycle, interface compliance |
| Framework | — | 2 | — | TestRunner, AllTests suite |
| **Total** | **59** | **38** | **~429** | |

---

## 7. Notes

- **Servlet classes** are not unit-tested. They require a Servlet container (Tomcat) and are better suited for integration testing with tools like `HttpUnit` or manual verification.
- **CVParserUtil** parsing of actual PDF and DOCX files is not tested in unit tests, as it requires binary test fixtures. The utility's null-safety and structural properties are verified.
- **WorkloadService** and other more complex services could be expanded with additional tests covering edge cases as needed.
- All test data is ephemeral — temporary files are cleaned up automatically.

---


