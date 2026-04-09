
## 文件功能说明
- `compile.bat`文件用于编译所有java文件，并将编译产生的class文件存放在%WEB_DIR%\WEB-INF\classes目录下
- `deploy.bat`文件用于一键将代码部署到Tomcat，通过调用compile.bat编译java文件，然后将对应代码粘贴到Tomcat的对应目录完成部署


## 一键部署使用说明：
- 需要设置CATALINA_HOME系统环境变量，变量值为Tomcat解压根目录的路径，例如 `D:\workspace\network_programming\Tomcat-10.1.49`
- 设置完成后运行`deploy.bat`即可完成一键部署
- 通过运行Tomcat的启动脚本，启动Tomcat
- 在浏览器输入对应网址即可测试

## 代码开发与部署工作流
代码开发与部署测试应独立进行，代码开发工作在本地工作空间文件夹进行，代码部署过程产生的文件变动仅涉及Tomcat文件夹

部署过程包含代码编译产生class文件和测试产生data的过程，会在data和classes目录产生不必要文件，这些文件无需上传到github。为了确保代码开发与部署保持独立，制定以下工作流：

1. 在本地工作空间开发代码，但不要直接在工作空间文件夹运行compile.bat脚本或进行任何编译
2. 在本地工作空间运行deploy.bat脚本，完成代码编译和部署，此步骤不会在工作空间文件夹产生任何多余的文件，所有编译产生的classes文件只会出现在Tomcat所在目录
3. 切换到Tomcat目录，启动Tomcat并测试代码，此过程可能在data文件夹产生新的文件
4. 代码测试无误后，即可把本地工作空间的代码commit并上传到github，注意上传前使用git status命令再次检查有没有多余的新增文件