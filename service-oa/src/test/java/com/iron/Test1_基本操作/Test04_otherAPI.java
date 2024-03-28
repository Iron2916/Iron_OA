package com.iron.Test1_基本操作;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/*
*   测试其他的 API
* */

@SpringBootTest
public class Test04_otherAPI {

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

    // 第三步：处理操作 Test3 已经操作

    //1. 查询当前任务：按照流程，先张三，后李四。
    @Test
    public void findPendingTaskList() {

        String assignee = "zhangsan";
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

        String assignee = "zhangsan";
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

    // ----------------------- 测试其他的 API ----------------------------------------

    // 查询流程定义
    @Test
    public void findProcessDefinitionList() {

        final List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                .orderByProcessDefinitionVersion()
                .desc()
                .list();

        //输出流程定义信息
        for (ProcessDefinition processDefinition : list) {
            System.out.println("流程定义 id="+processDefinition.getId());
            System.out.println("流程定义 name="+processDefinition.getName());
            System.out.println("流程定义 key="+processDefinition.getKey());
            System.out.println("流程定义 Version="+processDefinition.getVersion());
            System.out.println("流程部署ID ="+processDefinition.getDeploymentId());
            System.out.println("------------------------------------------------");
        }
    }

    // 删除流程定义
    @Test
    public void deleteDeployment() {

        // 根据流程部署id进行删除
        String deploymentId = "16a0910e-eb1b-11ee-b18b-005056c00008";
        // 删除流程定义，如果该流程定义已有流程实例启动则删除时出错
        repositoryService.deleteDeployment(deploymentId);
        // 设置true 级联删除流程定义，即使该流程有流程实例启动也可以删除，设置为false非级别删除方式
        //repositoryService.deleteDeployment(deploymentId, true);
    }

    // 挂起，激活流程实例（只有单个流程实例情况）
    @Test
    public void suspendProcessInstance_single() {

        final ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("qingjia").singleResult();

        final boolean suspended = processDefinition.isSuspended();

        if (suspended) {
            // 暂定,那就可以激活
            // 参数1:流程定义的id  参数2:是否激活    参数3:时间点
            repositoryService.activateProcessDefinitionById(processDefinition.getId(), true, null);
            System.out.println("流程定义:" + processDefinition.getId() + "激活");
        } else {
            repositoryService.suspendProcessDefinitionById(processDefinition.getId(), true, null);
            System.out.println("流程定义:" + processDefinition.getId() + "挂起");
        }
    }

    // 挂起，激活流程实例（多个流程实例情况）
    @Test
    public void suspendProcessInstance_multi() {

        final List<ProcessDefinition> processDefinitionList = repositoryService.createProcessDefinitionQuery().processDefinitionKey("qingjia").list();

        for (ProcessDefinition processDefinition : processDefinitionList) {

            final boolean suspended = processDefinition.isSuspended();

            if (suspended) {
                // 暂定,那就可以激活
                // 参数1:流程定义的id  参数2:是否激活    参数3:时间点
                repositoryService.activateProcessDefinitionById(processDefinition.getId(), true, null);
                System.out.println("流程定义:" + processDefinition.getId() + "激活");
            } else {
                repositoryService.suspendProcessDefinitionById(processDefinition.getId(), true, null);
                System.out.println("流程定义:" + processDefinition.getId() + "挂起");
            }
        }

    }
}
