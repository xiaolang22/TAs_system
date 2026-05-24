# TA Recruitment System — Project Data Structure Description

## Top-Level Directory

| Directory / File | Description |
|------------------|-------------|
| `TA_Recruitment_System/` | Main application directory containing all Java source code, web frontend, data files, and test code |
| `docs/` | Project documentation including development guidelines and iteration test docs |
| `first_assignment/` | First-phase assignment materials |
| `TAHub_User_Manual.pdf` | TAHub user manual |
| `README.md` | Project overview, environment setup, and quick verification guide |
| `PROJECT_STRUCTURE_CN.md` | This file: project data structure description (Chinese) |
| `PROJECT_STRUCTURE_EN.md` | Project data structure description (English) |
| `test statement.md` | Test framework and test directory documentation |

---

## `TA_Recruitment_System/` Detailed Structure

### `src/com/group19/` — Java Source Code

| Subdirectory | Description |
|--------------|-------------|
| `dao/` | **Data Access Object layer**: Handles JSON file read/write operations and provides CRUD interfaces. The sole interaction channel between the system and persistent data. Includes ApplicationDao, JobDao, NotificationDao, SavedJobDao, TADao, TimelineDao, UserAccountDao |
| `dto/` | **Data Transfer Object layer**: Encapsulates data passed between frontend and backend. Contains page-specific view data aggregates such as AdminDashboardData, ApplicantReviewRow, MoTaCandidateCard, TARecommendation, etc. |
| `model/` | **Domain model layer**: Defines core business entities — Application, Job, TA, UserAccount, Notification, TimelineEvent, SavedJob, LoginUser |
| `service/` | **Business logic layer**: Implements all core business logic including authentication/authorisation, job management, application review, CV parsing, intelligent recommendations, workload monitoring, notification pushing, and matching algorithms |
| `servlet/` | **Controller layer (Servlet)**: Handles HTTP requests and responses, invokes the Service layer for business operations, then forwards to JSP pages for rendering. Covers login, registration, job listings, application management, admin dashboard, CV upload, and other workflow entry points |
| `filter/` | **Filter layer**: AuthFilter handles login state checks and permission interception, redirecting unauthenticated users to the login page |
| `util/` | **Utility layer**: Provides JSON file I/O (JsonFileUtil), HTML escaping for XSS prevention (HtmlEscape), CV file parsing (CVParserUtil), file upload handling (FileUploadUtil), data path resolution (DataPathResolver), and other common utilities |

### `web/` — Web Application Root

| Subdirectory / File | Description |
|---------------------|-------------|
| `WEB-INF/web.xml` | Deployment descriptor defining servlet mappings, filter chains, context parameters, and the welcome page |
| `WEB-INF/jsp/` | **JSP view templates** (protected directory, not directly URL-accessible): Role-specific pages — TA-side (home, job_list, job_detail, profile, ta_applications), MO-side (mo_home, mo_job_list, mo_job_detail, mo_ta_profile, candidate_review), Admin-side (admin_home, admin_login, workload_dashboard, applicant_review, manage_applications), and shared pages (login, profile, account_center, post_job) |
| `WEB-INF/lib/` | **Third-party dependencies**: Includes Gson (JSON parsing), PDF Box and related libraries (CV processing), and other JAR files |
| `css/` | **Stylesheets**: style.css covering global styles, authentication pages, role-specific dashboards, workload dashboard, and responsive adaptation |
| `js/` | JavaScript script files |
| `assets/` | Static assets (e.g., tahub-logo.svg) |
| `jsp/` | Directly accessible JSP pages (e.g., post_job.jsp) |
| `index.html` | Entry redirect page, automatically forwards to the login page |
| `uploads/` | Uploaded file storage directory (CVs, images, etc.) |

### `data/` — JSON Persistence Layer

| File | Content |
|------|---------|
| `users.json` | User accounts: username, password, role (TA/MO/ADMIN), display name |
| `tas.json` | TA profiles: personal details, skills, course experience, CV file paths |
| `jobs.json` | Job postings: title, requirements, status (open/closed), owning MO |
| `applications.json` | Application records: status, timestamps, CV paths, review notes |
| `timelines.json` | Timeline events: complete history trail of application status changes |
| `saved_jobs.json` | TA users' saved job records |
| `notifications.json` | Notification records: system notification messages for TA and MO roles |

### `test_classes/` — Unit Tests

| Subdirectory | Description |
|--------------|-------------|
| `com/group19/TestRunner.java` | Custom lightweight test framework providing JUnit-style assertion methods (no external dependencies) |
| `com/group19/AllTests.java` | Master test suite aggregating and running all test classes |
| `com/group19/dao/` | DAO layer tests: verifies CRUD operations using temporary JSON files |
| `com/group19/dto/` | DTO layer tests: verifies factory methods, null safety, and collection immutability |
| `com/group19/model/` | Model layer tests: verifies constructors, getters/setters, and immutability |
| `com/group19/service/` | Service layer tests: verifies business logic, state transitions, and input validation |
| `com/group19/util/` | Util layer tests: verifies pure functions, file I/O, and XSS prevention |
| `com/group19/filter/` | Filter layer tests: verifies lifecycle and interface compliance |
| `com/group19/fixtures/` | Test fixture data files |

### Other Directories

| Directory | Description |
|-----------|-------------|
| `scripts/` | Build and deployment scripts (compile.bat, deploy.bat) |
| `out/` | Compiled output directory (.class files) |
| `testing_files/` | Temporary test file directory |

---

## `docs/` — Project Documentation

| Subdirectory | Content |
|--------------|---------|
| `rules/` | Development guidelines: Git usage guide, MVC architecture guide, Tomcat development standards, first-phase task description |
| `test/iteration1/` | Iteration 1 test documentation (US02, US04, US08) |
| `test/iteration2/` | Iteration 2 test documentation and defence preparation materials (US03, US07, US10) |
| `test/iteration3/` | Iteration 3 test documentation (US13) |

## `first_assignment/` — First-Phase Assignment

Contains initial requirements analysis, design documents, and other project startup materials.
