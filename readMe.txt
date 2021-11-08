#drop database activiti_learn;
# 创建数据库
create database `activiti_learn` default character set utf8mb4 collate utf8mb4_general_ci;
#==================
static/editor-app/app-cfg.js文件 contextRoot 修改


#==================




#==================
新增模型，post方式
http://localhost:8081/model/createModel
{
	"category": "测试分类",
	"name": "测试模型",
	"key": "TestModel",
	"desc": "测试描述"
}

进入模型流程图编辑页面，get方式
http://localhost:8081/modeler.html?modelId=1

进入模型流程图编辑页面，需要根据modelId获取model，get方式
http://localhost:8081/service/model/{modelId}/json

编辑好流程图，点击保存，跳到页面 http://localhost:8081/editor-app/popups/save-model.html?version=1552028405115
执行保存操作，调用接口保存编辑好的流程图，put方式
http://localhost:8081/service/model/{modelId}/save

根据modelId部署模型流程，post方式
http://localhost:8081/model/deploy/{id}

分页查询部署好的流程，get方式
http://localhost:8081/process/getProcessPage?descs=create_time&current=1&size=20

读取流程的 流程定义图 image/流程定义 xml，get方式
http://localhost:8081/process/resource/{proInsId}/{procDefId}/{resType}
http://localhost:8081/process/resource/7501/process:1:5004/image
http://localhost:8081/process/resource/10/process:1:9/xml

提交流程，post方式
http://localhost:8081/process/submit/{deploymentId}

分页查询代办，get方式
http://localhost:8081/task/todoPage?current=1&size=20

处理任务，post方式
http://localhost:8081/task/handleTask
{
    "taskId": "7504",
    "comment": "同意审批"
}