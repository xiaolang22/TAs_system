# Teaching Assistant Recruitment System

## 1. Overview

This is a Java JSP/Servlet web application for Teaching Assistant recruitment. It provides browser-based access for TA and MO users and stores application data in local JSON files.

The main application folder is `TA_Recruitment_System/`.

## 2. Environment Requirements

- JDK: JDK 17 is recommended.
- Servlet container: Apache Tomcat 10.1.x or another compatible Jakarta Servlet container.
- Development/deployment environment: a Java-capable IDE such as IntelliJ IDEA, or a manual servlet-container deployment environment.
- Web browser: Chrome, Edge, Firefox, or another modern browser.
- Database: no external SQL database is required. The project uses JSON files under `TA_Recruitment_System/data/`.

## 3. Project Structure

- `TA_Recruitment_System/src/`: Java source directory for servlets, services, DAOs, models, filters, DTOs, and utilities.
- `TA_Recruitment_System/web/`: web application root, including static files, JSP files, assets, and `WEB-INF`.
- `TA_Recruitment_System/web/WEB-INF/web.xml`: deployment descriptor with context parameters, filters, servlet definitions, and URL mappings.
- `TA_Recruitment_System/web/WEB-INF/lib/`: required third-party JAR files, including Gson and PDF/CV-related libraries.
- `TA_Recruitment_System/data/`: JSON data files used as the persistence layer.

## 4. Setup Instructions

1. Open or import the repository, or open/import the `TA_Recruitment_System/` application folder directly.
2. Configure a compatible JDK. JDK 17 is recommended.
3. Ensure `TA_Recruitment_System/src/` is treated as the Java source directory.
4. Ensure `TA_Recruitment_System/web/` is treated as the web application root.
5. Ensure `TA_Recruitment_System/web/WEB-INF/web.xml` is used as the deployment descriptor.
6. Ensure the required JAR files in `TA_Recruitment_System/web/WEB-INF/lib/` are included in the classpath if the IDE or deployment tool does not detect them automatically.
7. Ensure the servlet container provides the Jakarta Servlet API at runtime.

## 5. Servlet Container / Tomcat Configuration

1. Create a local server configuration for Apache Tomcat or another compatible Java servlet container.
2. Deploy the web application using `TA_Recruitment_System/web/` or an exploded web application artifact created from that folder.
3. Set the application context path, for example `/TA_Recruitment_System`.
4. Ensure `TA_Recruitment_System/web/WEB-INF/web.xml` is included in the deployment.
5. Start the server.

The optional Windows script `TA_Recruitment_System/scripts/deploy.bat` deploys to `%CATALINA_HOME%\webapps\TA_Recruitment`, which uses `/TA_Recruitment` as the context path.

## 6. Running the Software

1. Start the servlet container.
2. Open a web browser.
3. Visit the login page:

```text
http://localhost:8080/[context-path]/login
```

`[context-path]` depends on the local servlet container or IDE deployment configuration. For example, if the context path is `/TA_Recruitment_System`, open:

```text
http://localhost:8080/TA_Recruitment_System/login
```

The root `index.html` redirects to `login`, so opening the deployed application root should also reach the login page.

## 7. Test Accounts

The following accounts are defined in `TA_Recruitment_System/data/users.json`. Passwords are stored in plaintext in that file for testing.

| Username | Password | Role |
| --- | --- | --- |
| `ta001` | `ta123456` | TA |
| `ta002` to `ta058` | `ta123456` | TA |
| `ta099` | `ta123456` | TA |
| `ta100` | `ta123456` | TA |
| `mo001` to `mo020` | `mo123456` | MO |
| `admin001` | `admin123456` | ADMIN |

## 8. Data Files

The system uses JSON files under `TA_Recruitment_System/data/` as the persistence layer.

- `users.json`: user accounts, passwords, roles, display names, and user IDs.
- `tas.json`: TA profile data and CV file paths.
- `jobs.json`: TA job postings and job status data.
- `applications.json`: submitted job applications, application status, timestamps, CV path, and decision notes.
- `timelines.json`: application timeline events.
- `saved_jobs.json`: saved job records for TA users.
- `notifications.json`: notification records for TA and MO dashboard views.

## 9. Quick Verification

Use this checklist after deployment:

- The login page opens at `http://localhost:8080/[context-path]/login`.
- A TA account, such as `ta001`, can log in.
- An MO account, such as `mo001`, can log in.
- The jobs page opens for the appropriate role.
- Application and review pages can be accessed according to the logged-in user's role.
