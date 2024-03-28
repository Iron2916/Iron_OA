package com.iron.TestActivity03_global流程变量;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class Test01_启动流程实例时候设置 {

    @Autowired
    RepositoryService repositoryService;
    @Autowired
    RuntimeService runtimeService;

    @Autowired
    TaskService taskService;

    @Test
    public void deployProcess01() {
        // 流程部署
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("process/jiaban01.bpmn20.xml")
                .name("加班申请流程")
                .deploy();
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());
    }

    /**
     * ------------------- 启动流程实例， 注意是在此处设置的流程变量 -------------------------------------
     */
    @Test
    public void startUpProcess01() {

        Map<String, Object> variables = new HashMap<>();    // 定义流程变量参数
        variables.put("assignee1","zhangsan");
        variables.put("assignee2","lisi");

        //创建流程实例,我们需要知道流程定义的key
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("jiaban", variables);     // 设置流程变量
        //输出实例的相关信息
        System.out.println("流程定义id：" + processInstance.getProcessDefinitionId());
        System.out.println("流程实例id：" + processInstance.getId());
    }

    //1. 按照负责人名称查询任务。
    @Test
    public void findPendingTaskList() {

        String assignee = "zhangsan";
        final List<Task> list = taskService.createTaskQuery()
                .taskAssignee(assignee)
                .list();
        for (Task task : list) {

            System.out.println("assignee1：" + taskService.getVariable(task.getId(), "assignee1"));  // 获取流程变量
            System.out.println("任务ID = " + task.getId());
            System.out.println("流程实例ID = " + task.getProcessInstanceId());
            System.out.println("任务负责人 = " + task.getAssignee());
            System.out.println("任务名称 = " + task.getName());
        }
    }
}
