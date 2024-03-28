package com.iron.TestActivity03_local流程变量;

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
public class Test1_任务遍历时设置 {

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    TaskService taskService;

    @Test
    public void deploy() {

        String assginee = "zhangsan";
        final Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("process/qingjia.bpmn20.xml")
                .name("请假流程测试办理任务时候设置全局流程变量")
                .deploy();
        System.out.println("deploy.getId() = " + deploy.getId());
        System.out.println("deploy.getName() = " + deploy.getName());
    }

    @Test
    public void startProcessInstance() {

        String businessKey = "1002";
        final ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("qingjia", businessKey);

        System.out.println("processInstance.getBusinessKey() = " + processInstance.getBusinessKey());
        System.out.println("processInstance.getId() = " + processInstance.getId());
    }


    // ---------------------- 再此执行过程中进行设置流程变量  ----------------------------------

    // 步骤一：张三进行流程变量的设置
    @Test
    public void completeTask() {

        String assignee = "zhangsan";
        final List<Task> list = taskService.createTaskQuery()
                .taskAssignee(assignee)
                .list();

        for (Task task : list) {

            Map map = new HashMap<>();
            map.put(task.getAssignee(), task.getId());

            taskService.setVariablesLocal(task.getId(), map);
            taskService.complete(task.getId());
//            System.out.println("task.getId() = " + task.getId());
        }
    }

    // 步骤二：李四获得张三设置的流程变量
    @Test
    public void completeTask_lisi() {

        String assignee = "lisi";
        final List<Task> list = taskService.createTaskQuery()
                .taskAssignee(assignee)
                .list();

        for (Task task : list) {

            final Object zhangsan = taskService.getVariableLocal(task.getId(), "zhangsan");
//            taskService.complete(task.getId());
            System.out.println("TaskID：" + zhangsan);
        }
    }
}
