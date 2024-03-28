package com.iron.Test1_��������;

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
*   ���������� API
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

    // ��������������� Test3 �Ѿ�����

    //1. ��ѯ��ǰ���񣺰������̣��������������ġ�
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

        String assignee = "zhangsan";
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

    // ----------------------- ���������� API ----------------------------------------

    // ��ѯ���̶���
    @Test
    public void findProcessDefinitionList() {

        final List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                .orderByProcessDefinitionVersion()
                .desc()
                .list();

        //������̶�����Ϣ
        for (ProcessDefinition processDefinition : list) {
            System.out.println("���̶��� id="+processDefinition.getId());
            System.out.println("���̶��� name="+processDefinition.getName());
            System.out.println("���̶��� key="+processDefinition.getKey());
            System.out.println("���̶��� Version="+processDefinition.getVersion());
            System.out.println("���̲���ID ="+processDefinition.getDeploymentId());
            System.out.println("------------------------------------------------");
        }
    }

    // ɾ�����̶���
    @Test
    public void deleteDeployment() {

        // �������̲���id����ɾ��
        String deploymentId = "16a0910e-eb1b-11ee-b18b-005056c00008";
        // ɾ�����̶��壬��������̶�����������ʵ��������ɾ��ʱ����
        repositoryService.deleteDeployment(deploymentId);
        // ����true ����ɾ�����̶��壬��ʹ������������ʵ������Ҳ����ɾ��������Ϊfalse�Ǽ���ɾ����ʽ
        //repositoryService.deleteDeployment(deploymentId, true);
    }

    // ���𣬼�������ʵ����ֻ�е�������ʵ�������
    @Test
    public void suspendProcessInstance_single() {

        final ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("qingjia").singleResult();

        final boolean suspended = processDefinition.isSuspended();

        if (suspended) {
            // �ݶ�,�ǾͿ��Լ���
            // ����1:���̶����id  ����2:�Ƿ񼤻�    ����3:ʱ���
            repositoryService.activateProcessDefinitionById(processDefinition.getId(), true, null);
            System.out.println("���̶���:" + processDefinition.getId() + "����");
        } else {
            repositoryService.suspendProcessDefinitionById(processDefinition.getId(), true, null);
            System.out.println("���̶���:" + processDefinition.getId() + "����");
        }
    }

    // ���𣬼�������ʵ�����������ʵ�������
    @Test
    public void suspendProcessInstance_multi() {

        final List<ProcessDefinition> processDefinitionList = repositoryService.createProcessDefinitionQuery().processDefinitionKey("qingjia").list();

        for (ProcessDefinition processDefinition : processDefinitionList) {

            final boolean suspended = processDefinition.isSuspended();

            if (suspended) {
                // �ݶ�,�ǾͿ��Լ���
                // ����1:���̶����id  ����2:�Ƿ񼤻�    ����3:ʱ���
                repositoryService.activateProcessDefinitionById(processDefinition.getId(), true, null);
                System.out.println("���̶���:" + processDefinition.getId() + "����");
            } else {
                repositoryService.suspendProcessDefinitionById(processDefinition.getId(), true, null);
                System.out.println("���̶���:" + processDefinition.getId() + "����");
            }
        }

    }
}
