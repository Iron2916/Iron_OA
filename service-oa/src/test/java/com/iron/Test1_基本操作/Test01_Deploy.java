package com.iron.Test1_基本操作;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.InputStream;
import java.util.zip.ZipInputStream;

/*
* 部署流程定义：这里介绍了两种部署方式
* */
@SpringBootTest()
public class Test01_Deploy {

    @Autowired
    private RepositoryService repositoryService;

    // 单节点进行部署
    @Test
    public void deployProcess_single() {

        final Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("process/qingjia.bpmn20.xml")
                .addClasspathResource("process/qingjia.png")
                .name("请假申请流程")
                .deploy();
        System.out.println("部署的ID：" + deploy.getId());
        System.out.println("部署名称：" + deploy.getName());
    }

    // zip 进行部署
    @Test
    public void deployProcess_zip() {

        final InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("process/qingjia.zip");
        final ZipInputStream zipInputStream = new ZipInputStream(inputStream);

        final Deployment deploy = repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .name("请假申请流程")
                .deploy();

        System.out.println("部署的ID：" + deploy.getId());
        System.out.println("部署名称：" + deploy.getName());
    }
}
