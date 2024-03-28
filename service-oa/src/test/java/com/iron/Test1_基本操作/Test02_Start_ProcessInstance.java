package com.iron.Test1_基本操作;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/*
* 启动流程实例
* */
@SpringBootTest
public class Test02_Start_ProcessInstance {

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    RuntimeService runtimeService;

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

    // ------------------------  第二步：开启一个流程实例  --------------------------
    @Test
    public void startProcessInstance() {

        String businessKey = "1001"; // 业务标识，通常为业务的主键
        final ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("qingjia", businessKey);
        System.out.println("启动实例定义：" + processInstance.getProcessDefinitionId());
        System.out.println("启动实例ID：" + processInstance.getId());
        System.out.println("业务ID = " + processInstance.getBusinessKey());
    }
}
