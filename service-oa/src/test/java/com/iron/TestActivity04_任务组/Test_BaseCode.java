package com.iron.TestActivity04_任务组;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class Test_BaseCode {

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    TaskService taskService;

    @Test
    public void deploye() {

        // 部署
        final Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("process/jiaban04.bpmn20.xml")
                .name("请假流程")
                .deploy();

        System.out.println("deploy.getId() = " + deploy.getId());
        // 开启一个流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("jiaban04");
        System.out.println("processInstance.getId() = " + processInstance.getId());
        System.out.println(processInstance.getId());
    }

    // 进行部署
    // 注意，此时会通过springSecurity进行验证，保证sysUser里面有zhangsan01
    @Test
    public void findGroupTaskList() {
        //查询组任务
        List<Task> list = taskService.createTaskQuery()
                .taskCandidateUser("zhangsan01")//根据候选人查询
                .list();
        for (Task task : list) {
            System.out.println("--------------zhangsan01接受--------------");
            System.out.println("流程实例id：" + task.getProcessInstanceId());
            System.out.println("任务id：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        }

        List<Task> list2 = taskService.createTaskQuery()
                .taskCandidateUser("zhangsan02")//根据候选人查询
                .list();
        for (Task task : list2) {
            System.out.println("--------------zhangsan02接受--------------");
            System.out.println("流程实例id：" + task.getProcessInstanceId());
            System.out.println("任务id：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        }
    }

    // 拾取任务
     @Test
    public void claimTask() {

         final Task task = taskService.createTaskQuery()
                 .taskCandidateUser("zhangsan01")
                 .singleResult();

         if (task != null) {

             taskService.claim(task.getId(), "zhangsan01");
             System.out.println("任务拾取成功！");
         }
     }

     // 查询任务
    @Test
    public void findGroupPendingTaskList() {
        //任务负责人
        String assignee = "zhangsan02";
        List<Task> list = taskService.createTaskQuery()
                .taskAssignee(assignee)//只查询该任务负责人的任务
                .list();
        for (Task task : list) {
            System.out.println("流程实例id：" + task.getProcessInstanceId());
            System.out.println("任务id：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        }
    }

    // 归还任务
    @Test
    public void assigneeToGroupTask() {
        String taskId = "c92cbad0-ebd4-11ee-b206-744ca17a808e";
        // 任务负责人
        String userId = "zhangsan01";
        // 校验userId是否是taskId的负责人，如果是负责人才可以归还组任务
        Task task = taskService
                .createTaskQuery()
                .taskId(taskId)
                .taskAssignee(userId)
                .singleResult();
        if (task != null) {
            // 如果设置为null，归还组任务,该 任务没有负责人
            taskService.setAssignee(taskId, null);
        }
    }

    // 任务交接，任务负责人将任务交给其它候选人办理该任务
    @Test
    public void assigneeToCandidateUser() {
        // 当前待办任务
        String taskId = "c92cbad0-ebd4-11ee-b206-744ca17a808e";
        // 校验zhangsan01是否是taskId的负责人，如果是负责人才可以归还组任务
        Task task = taskService
                .createTaskQuery()
                .taskId(taskId)
                .taskAssignee("zhangsan01")
                .singleResult();
        if (task != null) {
            // 将此任务交给其它候选人zhangsan02办理该 任务
            taskService.setAssignee(taskId, "zhangsan02");
        }
    }

    // 完成任务
    @Test
    public void completGroupTask() {
        Task task = taskService.createTaskQuery()
                .taskAssignee("zhangsan01")  //要查询的负责人
                .singleResult();//返回一条
        taskService.complete(task.getId());
    }


}
