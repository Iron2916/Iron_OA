package com.iron.TestActivity02_�������;

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
public class Test03_�������_���������� {

    @Autowired
    RepositoryService repositoryService;
    @Autowired
    RuntimeService runtimeService;

    @Autowired
    TaskService taskService;
    @Test
    public void deployProcess03() {
        // ���̲���
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("process/jiaban03.bpmn20.xml")
                .name("�Ӱ���������")
                .deploy();
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());
    }

    /**
     * ��������ʵ��
     */
    @Test
    public void startUpProcess03() {
        //��������ʵ��,������Ҫ֪�����̶����key
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("jiaban03");
        //���ʵ���������Ϣ
        System.out.println("���̶���id��" + processInstance.getProcessDefinitionId());
        System.out.println("����ʵ��id��" + processInstance.getId());
    }

    @Test
    public void findPendingTaskList() {

        String assignee = "zhangsan";
        final List<Task> list = taskService.createTaskQuery()
                .taskAssignee(assignee)
                .list();
        for (Task task : list) {

            System.out.println("����ID = " + task.getId());
            System.out.println("����ʵ��ID = " + task.getProcessInstanceId());
            System.out.println("�������� = " + task.getAssignee());
            System.out.println("�������� = " + task.getName());
        }
    }
}
