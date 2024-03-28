package com.iron.Test1_��������;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/*
* ��������ʵ��
* */
@SpringBootTest
public class Test02_Start_ProcessInstance {

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    RuntimeService runtimeService;

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

    // ------------------------  �ڶ���������һ������ʵ��  --------------------------
    @Test
    public void startProcessInstance() {

        String businessKey = "1001"; // ҵ���ʶ��ͨ��Ϊҵ�������
        final ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("qingjia", businessKey);
        System.out.println("����ʵ�����壺" + processInstance.getProcessDefinitionId());
        System.out.println("����ʵ��ID��" + processInstance.getId());
        System.out.println("ҵ��ID = " + processInstance.getBusinessKey());
    }
}
