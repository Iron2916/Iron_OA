package com.iron.Test1_基本操作;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class Test03_completeTask {

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    TaskService taskService;

    @Autowired
    HistoryService historyService;
    // 第一步：进行部署，Test1已经操作
    @Test
    public void deploy() {

        final Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("process/qingjia.bpmn20.xml")
                .addClasspathResource("process/qingjia.png")
                .name("请假申请流程")
                .deploy();
        System.out.println("部署的ID：" + deploy.getId());
        System.out.println("部署名称：" + deploy.getName());
    }

    // 第二步：开始一个进程实例，Test2已经讲解
    @Test
    public void startProcessInstance() {

        String businessKey = "1001"; // 业务标识，通常为业务的主键
        final ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("qingjia", businessKey);
        System.out.println("启动实例定义：" + processInstance.getProcessDefinitionId());
        System.out.println("启动实例ID：" + processInstance.getId());
    }

    // --------------------------  第三步：处理操作  ---------------------------------

    //1. 查询当前任务：按照流程，先张三，后李四。
    @Test
    public void findPendingTaskList() {

        String assignee = "lisi";
        final List<Task> list = taskService.createTaskQuery()
                .taskAssignee(assignee)
                .list();
        for (Task task : list) {

            System.out.println("任务ID = " + task.getId());
            System.out.println("流程实例ID = " + task.getProcessInstanceId());
            System.out.println("任务负责人 = " + task.getAssignee());
            System.out.println("任务名称 = " + task.getName());
        }
    }


    //2. 处理流程：根据流程来看，先是张三进行任务，完成任务后，任务自动传递到下一个节点(即李四节点)
    @Test
    public void completeTask() {

        String assignee = "lisi";

        /* 一次性完成所有任务 */
        final List<Task> list = taskService.createTaskQuery()
                .taskAssignee(assignee)
                .list();

        for (Task task : list) {

            taskService.complete(task.getId());
        }

        /* 只有一个任务，单此完成一个任务 */

/*        final Task task = taskService.createTaskQuery()
        .taskAssignee(assignee)
        .singleResult();
        // 根据获得的任务进行完成任务
        taskService.complete(task.getId());
        */
    }

    //3. 查询已经处理过的流程

    @Test
    public void findeCompletedTask() {

        String assignee = "lisi";
        final List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(assignee)
                .finished().list();

        for (HistoricTaskInstance historicTaskInstance : list) {

            System.out.println("流程实例ID = " + historicTaskInstance.getProcessInstanceId());
            System.out.println("任务ID = " + historicTaskInstance.getId());
            System.out.println("任务负责人 = " + historicTaskInstance.getAssignee());
            System.out.println("任务名称 = " + historicTaskInstance.getName());
        }
    }
}
