# 基于Tomcat的开发规范

## 一、工具链规范

- 代码编辑器：VSCode等
- 浏览器：Google等
- JDK：21.0.2
- Tomcat：10.1.49

## 二、开发与部署指南

整个开发与部署测试过程我们不使用IDE，采用代码开发与部署测试分离的策略。

### 1. 代码开发

在 GitHub 仓库中维护的是 **开发态的源代码和静态资源**，包括：
- `src/` 目录下的所有 `.java` 源文件
- `web/` 目录下的 JSP、CSS、JS、`WEB-INF/web.xml`、第三方 jar 等
- `data/` 目录下的 JSON 文件（初始空数组模板，便于每个成员拉取后直接运行）
- 脚本文件（编译脚本、部署脚本等，需要在部署时根据本机更改路径等参数）
- `README.md`、`.gitignore` 等
注意：`web/WEB-INF/classes/` 目录（编译后的 `.class` 文件）**不提交**到 Git，因为它是编译产物，每个成员自己编译生成。同样，`web/uploads/`（用户上传的文件）也不提交。

github仓库的文件结构示例如下：
```text
TA_Recruitment_System/                     # 项目根目录（也是 GitHub 仓库根目录）
│
├── src/                                   # Java 源代码根目录
│   └── com/group19/
│       ├── model/                         # 实体类（Model）
│       │   ├── TA.java
│       │   ├── MO.java
│       │   ├── Job.java
│       │   ├── Application.java
│       │   └── TimelineEvent.java
│       │
│       ├── dao/                           # 数据访问对象（DAO）
│       │   ├── TADao.java
│       │   ├── MODao.java
│       │   ├── JobDao.java
│       │   └── ApplicationDao.java
│       │
│       ├── service/                       # 业务逻辑层（Service）
│       │   ├── ProfileService.java
│       │   ├── CVService.java
│       │   ├── JobService.java
│       │   ├── ApplicationService.java
│       │   └── MatchingService.java
│       │
│       ├── servlet/                       # 控制器层（Controller）
│       │   ├── ProfileServlet.java
│       │   ├── UploadCVServlet.java
│       │   ├── JobListServlet.java
│       │   ├── ApplyServlet.java
│       │   ├── PostJobServlet.java
│       │   ├── ApplicationStatusServlet.java
│       │   ├── ReviewApplicantsServlet.java
│       │   └── UpdateApplicationServlet.java
│       │
│       ├── util/                          # 工具类
│       │   ├── JsonFileUtil.java          # 通用 JSON 读写
│       │   ├── FileUploadUtil.java        # 处理文件上传
│       │   ├── IdGenerator.java           # 生成唯一 ID
│       │   └── CVParserUtil.java          # 模拟 CV 文本提取
│       │
│       └── dto/                           # 数据传输对象（可选）
│           └── ServiceResult.java         # 封装 Service 返回结果
│
├── web/                                   # Web 应用根目录（部署时整个复制到 Tomcat）
│   ├── WEB-INF/
│   │   ├── web.xml                        # 部署描述符
│   │   ├── lib/                           # 第三方 JAR 包
│   │   │   └── gson-2.10.1.jar            # Google Gson（处理 JSON）
│   │   └── classes/                       # 编译后的 .class 文件（由 javac 生成，自动放置）
│   │
│   ├── jsp/                               # 所有 JSP 视图（推荐放在 WEB-INF 下，禁止直接访问）
│   │   ├── profile.jsp
│   │   ├── jobs.jsp
│   │   ├── job_detail.jsp
│   │   ├── my_applications.jsp
│   │   ├── post_job.jsp
│   │   ├── review_applicants.jsp
│   │   └── common/                        # 公共组件（如 header, footer）
│   │       ├── header.jsp
│   │       └── footer.jsp
│   │
│   ├── css/                               # 样式表
│   │   └── style.css
│   │
│   ├── js/                                # 前端 JavaScript
│   │   └── main.js
│   │
│   ├── uploads/                           # 上传的 CV 文件存储目录
│   │   └── (存放用户上传的 PDF/DOC 文件)
│   │
│   └── index.html                         # 入口页面（重定向到登录或主页）
│
├── data/                                  # JSON 数据文件（模拟数据库）
│   ├── tas.json
│   ├── mos.json
│   ├── jobs.json
│   ├── applications.json
│   └── timelines.json
│
├── scripts/                               # 辅助脚本（可选）
│   ├── compile.bat                        # Windows 编译脚本
│   ├── compile.sh                         # Linux/Mac 编译脚本
│   └── deploy.bat                         # 自动复制到 Tomcat 脚本
│
├── README.md                              # 项目说明文档
└── .gitignore                             # Git 忽略文件（如 classes, uploads, data/*.json 初始为空）
```

### 2. 部署测试

在本机安装了tomcat之后，会有一个对应的tomcat文件夹，部署代码的过程本质上就是编译github中的java代码，并把github中保存的源代码、静态资源以及编译生成的class文件放到tomcat文件夹的正确位置。

具体部署实现可参考部署脚本。


## 三、其他必须遵守的规则

1. 避免在jsp中写java代码

## 附：Tomcat使用极简指南
### 1. 目录结构

解压 Tomcat 后，会看到以下核心目录：
- **`/bin`**：存放启动 (`startup.bat`)、关闭 (`shutdown.bat`) 等脚本文件。
- **`/conf`**：存放配置文件，核心是 `server.xml`（配置端口、主机等）和 `web.xml`（全局部署描述符）。
- **`/lib`**：存放 Tomcat 运行所需的 JAR 包（如 `servlet-api.jar`）。
- **`/logs`**：存放运行日志文件。
- **`/webapps`**：**最重要**， Web 应用需要放置在此目录下才能被 Tomcat 加载。
- **`/work`**：存放 JSP 编译后产生的 Servlet 文件。

### 2. 启动与停止

在 `%CATALINA_HOME%\bin\` 目录下：
- **启动**：双击 `startup.bat`（Windows）或运行 `./startup.sh`（Linux/macOS）。
- **停止**：双击 `shutdown.bat`（Windows）或运行 `./shutdown.sh`（Linux/macOS）。

### 3. 访问测试

启动成功后，打开浏览器访问 `http://localhost:8080/`，如果能看到 Tomcat 的欢迎页面，说明服务器运行正常。

### 4. 部署 Web 应用（手动方式）

Tomcat中，`/webapps`目录下每一个子文件夹都是一个web应用，初始阶段只有root，这是tomcat的默认欢迎界面。

在tomcat中部署web应用只需要在`/webapps`目录下添加子文件夹，并且子文件夹符合tomcat规范即可，文件夹添加后tomcat可以自动识别并部署。


启动 Tomcat 后，即可在浏览器访问 `http://localhost:8080/<web应用名>` 

> **提示**：对于手动编译 Servlet 项目，你需要将编译后的 `.class` 文件放在 `WEB-INF/classes` 目录下，并将 `web.xml` 放在 `WEB-INF` 目录中。