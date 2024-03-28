package com.iron.TestActivity04_������;

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

        // ����
        final Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("process/jiaban04.bpmn20.xml")
                .name("�������")
                .deploy();

        System.out.println("deploy.getId() = " + deploy.getId());
        // ����һ������ʵ��
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("jiaban04");
        System.out.println("processInstance.getId() = " + processInstance.getId());
        System.out.println(processInstance.getId());
    }

    // ���в���
    // ע�⣬��ʱ��ͨ��springSecurity������֤����֤sysUser������zhangsan01
    @Test
    public void findGroupTaskList() {
        //��ѯ������
        List<Task> list = taskService.createTaskQuery()
                .taskCandidateUser("zhangsan01")//���ݺ�ѡ�˲�ѯ
                .list();
        for (Task task : list) {
            System.out.println("--------------zhangsan01����--------------");
            System.out.println("����ʵ��id��" + task.getProcessInstanceId());
            System.out.println("����id��" + task.getId());
            System.out.println("�������ˣ�" + task.getAssignee());
            System.out.println("�������ƣ�" + task.getName());
        }

        List<Task> list2 = taskService.createTaskQuery()
                .taskCandidateUser("zhangsan02")//���ݺ�ѡ�˲�ѯ
                .list();
        for (Task task : list2) {
            System.out.println("--------------zhangsan02����--------------");
            System.out.println("����ʵ��id��" + task.getProcessInstanceId());
            System.out.println("����id��" + task.getId());
            System.out.println("�������ˣ�" + task.getAssignee());
            System.out.println("�������ƣ�" + task.getName());
        }
    }

    // ʰȡ����
     @Test
    public void claimTask() {

         final Task task = taskService.createTaskQuery()
                 .taskCandidateUser("zhangsan01")
                 .singleResult();

         if (task != null) {

             taskService.claim(task.getId(), "zhangsan01");
             System.out.println("����ʰȡ�ɹ���");
         }
     }

     // ��ѯ����
    @Test
    public void findGroupPendingTaskList() {
        //��������
        String assignee = "zhangsan02";
        List<Task> list = taskService.createTaskQuery()
                .taskAssignee(assignee)//ֻ��ѯ���������˵�����
                .list();
        for (Task task : list) {
            System.out.println("����ʵ��id��" + task.getProcessInstanceId());
            System.out.println("����id��" + task.getId());
            System.out.println("�������ˣ�" + task.getAssignee());
            System.out.println("�������ƣ�" + task.getName());
        }
    }

    // �黹����
    @Test
    public void assigneeToGroupTask() {
        String taskId = "c92cbad0-ebd4-11ee-b206-744ca17a808e";
        // ��������
        String userId = "zhangsan01";
        // У��userId�Ƿ���taskId�ĸ����ˣ�����Ǹ����˲ſ��Թ黹������
        Task task = taskService
                .createTaskQuery()
                .taskId(taskId)
                .taskAssignee(userId)
                .singleResult();
        if (task != null) {
            // �������Ϊnull���黹������,�� ����û�и�����
            taskService.setAssignee(taskId, null);
        }
    }

    // ���񽻽ӣ��������˽����񽻸�������ѡ�˰��������
    @Test
    public void assigneeToCandidateUser() {
        // ��ǰ��������
        String taskId = "c92cbad0-ebd4-11ee-b206-744ca17a808e";
        // У��zhangsan01�Ƿ���taskId�ĸ����ˣ�����Ǹ����˲ſ��Թ黹������
        Task task = taskService
                .createTaskQuery()
                .taskId(taskId)
                .taskAssignee("zhangsan01")
                .singleResult();
        if (task != null) {
            // �������񽻸�������ѡ��zhangsan02����� ����
            taskService.setAssignee(taskId, "zhangsan02");
        }
    }

    // �������
    @Test
    public void completGroupTask() {
        Task task = taskService.createTaskQuery()
                .taskAssignee("zhangsan01")  //Ҫ��ѯ�ĸ�����
                .singleResult();//����һ��
        taskService.complete(task.getId());
    }


}
