package com.iron.TestActivity03_global���̱���;

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
public class Test01_��������ʵ��ʱ������ {

    @Autowired
    RepositoryService repositoryService;
    @Autowired
    RuntimeService runtimeService;

    @Autowired
    TaskService taskService;

    @Test
    public void deployProcess01() {
        // ���̲���
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("process/jiaban01.bpmn20.xml")
                .name("�Ӱ���������")
                .deploy();
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());
    }

    /**
     * ------------------- ��������ʵ���� ע�����ڴ˴����õ����̱��� -------------------------------------
     */
    @Test
    public void startUpProcess01() {

        Map<String, Object> variables = new HashMap<>();    // �������̱�������
        variables.put("assignee1","zhangsan");
        variables.put("assignee2","lisi");

        //��������ʵ��,������Ҫ֪�����̶����key
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("jiaban", variables);     // �������̱���
        //���ʵ���������Ϣ
        System.out.println("���̶���id��" + processInstance.getProcessDefinitionId());
        System.out.println("����ʵ��id��" + processInstance.getId());
    }

    //1. ���ո��������Ʋ�ѯ����
    @Test
    public void findPendingTaskList() {

        String assignee = "zhangsan";
        final List<Task> list = taskService.createTaskQuery()
                .taskAssignee(assignee)
                .list();
        for (Task task : list) {

            System.out.println("assignee1��" + taskService.getVariable(task.getId(), "assignee1"));  // ��ȡ���̱���
            System.out.println("����ID = " + task.getId());
            System.out.println("����ʵ��ID = " + task.getProcessInstanceId());
            System.out.println("�������� = " + task.getAssignee());
            System.out.println("�������� = " + task.getName());
        }
    }
}
