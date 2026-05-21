# US03 测试报告

**测试人**：韦嘉宁  
**测试日期**：2026-04-12  
**功能模块**：US03 - 从简历自动填写个人资料

---

## 一、功能实现情况

### 1.1 已实现功能

✅ **US03 核心功能已实现**：
- PDF 简历上传与解析
- 自动提取姓名、邮箱、学号、专业、技能、可用时间
- 表单自动填充（不覆盖已有内容）
- 用户可手动编辑所有字段

### 1.2 新增文件

**Java 源文件**：
- `src/com/group19/dto/ParsedCVData.java` - 简历解析数据传输对象
- `src/com/group19/service/CVParseService.java` - 简历解析服务
- `src/com/group19/servlet/ParseCVServlet.java` - 简历解析 Servlet

**前端文件**：
- `web/WEB-INF/jsp/profile.jsp` - 新增"Parse CV to Auto-Fill Profile"区域

**配置文件**：
- `web/WEB-INF/web.xml` - 新增 ParseCVServlet 配置
- `scripts/compile.bat` - 添加 PDFBox 相关依赖

**依赖库**：
- `web/WEB-INF/lib/pdfbox-2.0.29.jar`
- `web/WEB-INF/lib/fontbox-2.0.29.jar`
- `web/WEB-INF/lib/commons-logging-1.2.jar`

**测试文件**：
- `test_cv.pdf` - 测试用简历

---

## 二、测试发现的问题

### 2.1 问题一：申请岗位无法撤销

**问题描述**：
在测试申请岗位功能时，发现提交申请后没有撤销或撤回申请的功能。

**问题位置**：
- 申请页面：`/jobs` 或职位详情页
- 申请功能：`ApplyServlet`

**疑问**：
这是有意设计的功能限制，还是需要补充的功能？如果需要，建议添加"撤回申请"按钮。

---

### 2.2 问题二：US02 与 US03 功能重复

**问题描述**：
在个人资料页面（profile.jsp）发现两个类似的功能区域：

1. **US03 功能**（我开发的）：
   - 标题："Parse CV to Auto-Fill Profile"
   - 功能：独立的解析按钮，只解析不保存
   - 位置：页面上方

2. **US02 功能**（其他组员开发的）：
   - 标题："Upload CV"
   - 新增提示："US03: after upload, the system will try to prefill education, skills and experience fields below."
   - 功能：上传 CV 后自动解析并填充
   - 位置：页面中间

**结果**：
- 页面上有两个简历解析入口，功能重复
- 用户可能会困惑应该使用哪个

---

### 2.3 问题三：US02 解析 PDF 出现乱码

**问题描述**：
测试 US02 的"上传 CV 后自动解析"功能时，发现：
- 使用 test_cv.pdf 测试
- 解析结果出现乱码或无法正确提取信息
- 疑似编码问题或 PDF 解析方式不正确

---

### 2.4 问题四：功能合并疑问

**问题描述**：
我并未对 US02 的代码进行任何修改或合并操作，但发现：
- US02 的功能中包含了 US03 的解析逻辑
- profile.jsp 中 US02 区域新增了 US03 相关的提示文字
- 疑似是其他组员在合并分支时，将我的 US03 功能误合并到了 US02 中

**我的操作记录**：
1. 我独立开发了 US03 功能
2. 提交了两个 commit：
   - `US03: Implement CV auto-fill with PDF parsing`
   - `Add test_cv.pdf for US03 testing`
3. 我没有修改过 US02 相关的任何代码

---

## 三、建议

### 3.1 关于功能重复

建议明确两个功能的定位：
- **方案 A**：保留 US03 的独立解析按钮，移除 US02 中的自动解析逻辑
- **方案 B**：保留 US02 的上传即解析，移除 US03 的独立按钮
- **方案 C**：两个功能都保留，但在页面上明确区分用途

### 3.2 关于 PDF 解析

建议统一使用 US03 中采用的 Apache PDFBox 库进行解析，该库：
- 稳定可靠
- 支持中文
- 解析准确率高

### 3.3 关于撤销申请

建议确认产品需求：
- 如果需要撤销功能，建议在申请列表或申请详情页添加"撤回申请"按钮
- 如果不需要撤销，建议在 UI 上明确提示"申请提交后不可撤销"

---

## 四、US03 功能测试结果

✅ **测试通过**：
- PDF 上传正常
- 文本提取准确
- 表单自动填充正常
- 不覆盖已有内容
- 用户可手动编辑

**测试文件**：`test_cv.pdf`

---

## 五、总结

US03 功能已完整实现并测试通过。发现的主要问题是：
1. 功能重复（US02 与 US03）
2. US02 解析 PDF 有乱码
3. 疑似功能被误合并

建议团队开会讨论功能定位和统一解析方案。

---

**报告人**：韦嘉宁  
**日期**：2026-04-12
