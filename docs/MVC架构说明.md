# MVC架构说明

### 1. 整体开发架构

项目整体使用MVC架构（Model-View-Controller），在Model、Controller、View三层之外，添加DAO、Service层等辅助层，尽可能保证功能的模块化；使用json文件作为模拟数据库存储不同类型数据

数据端：
- **Model层（实体类）**：建立专门的实体类，规范不同类的各种属性（TA类、MO类）
- 模拟数据库：使用json存储不同类型的数据（TA、MO等）
- DAO层：负责数据的读取和写入

业务逻辑与视图端：
- **Controller 层（Servlet）**：接收 HTTP 请求，调用 Service，选择视图
- **View 层（JSP）**：用于显示页面的构建
- Service层：定义不同的业务逻辑，例如：验证必填字段、检查学号唯一性（编辑时除外）、更新档案等

### 2. 完整详细pipline示例

下面以**TA修改个人资料**的场景为例，展示包含各个组件的完整业务流程：

![[Pasted image 20260408184229.png]]




