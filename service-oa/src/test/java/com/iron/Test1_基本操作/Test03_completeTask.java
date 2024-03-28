package com.iron.Test1_��������;

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
    // ��һ�������в���Test1�Ѿ�����
    @Test
    public void deploy() {

        final Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("process/qingjia.bpmn20.xml")
                .addClasspathResource("process/qingjia.png")
                .name("�����������")
                .deploy();
        System.out.println("�����ID��" + deploy.getId());
        System.out.println("�������ƣ�" + deploy.getName());
    }

    // �ڶ�������ʼһ������ʵ����Test2�Ѿ�����
    @Test
    public void startProcessInstance() {

        String businessKey = "1001"; // ҵ���ʶ��ͨ��Ϊҵ�������
        final ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("qingjia", businessKey);
        System.out.println("����ʵ�����壺" + processInstance.getProcessDefinitionId());
        System.out.println("����ʵ��ID��" + processInstance.getId());
    }

    // --------------------------  ���������������  ---------------------------------

    //1. ��ѯ��ǰ���񣺰������̣��������������ġ�
    @Test
    public void findPendingTaskList() {

        String assignee = "lisi";
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


    //2. �������̣��������������������������������������������Զ����ݵ���һ���ڵ�(�����Ľڵ�)
    @Test
    public void completeTask() {

        String assignee = "lisi";

        /* һ��������������� */
        final List<Task> list = taskService.createTaskQuery()
                .taskAssignee(assignee)
                .list();

        for (Task task : list) {

            taskService.complete(task.getId());
        }

        /* ֻ��һ�����񣬵������һ������ */

/*        final Task task = taskService.createTaskQuery()
        .taskAssignee(assignee)
        .singleResult();
        // ���ݻ�õ���������������
        taskService.complete(task.getId());
        */
    }

    //3. ��ѯ�Ѿ������������

    @Test
    public void findeCompletedTask() {

        String assignee = "lisi";
        final List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(assignee)
                .finished().list();

        for (HistoricTaskInstance historicTaskInstance : list) {

            System.out.println("����ʵ��ID = " + historicTaskInstance.getProcessInstanceId());
            System.out.println("����ID = " + historicTaskInstance.getId());
            System.out.println("�������� = " + historicTaskInstance.getAssignee());
            System.out.println("�������� = " + historicTaskInstance.getName());
        }
    }
}
