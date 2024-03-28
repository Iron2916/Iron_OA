package com.iron.Test1_��������;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.InputStream;
import java.util.zip.ZipInputStream;

/*
* �������̶��壺������������ֲ���ʽ
* */
@SpringBootTest()
public class Test01_Deploy {

    @Autowired
    private RepositoryService repositoryService;

    // ���ڵ���в���
    @Test
    public void deployProcess_single() {

        final Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("process/qingjia.bpmn20.xml")
                .addClasspathResource("process/qingjia.png")
                .name("�����������")
                .deploy();
        System.out.println("�����ID��" + deploy.getId());
        System.out.println("�������ƣ�" + deploy.getName());
    }

    // zip ���в���
    @Test
    public void deployProcess_zip() {

        final InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("process/qingjia.zip");
        final ZipInputStream zipInputStream = new ZipInputStream(inputStream);

        final Deployment deploy = repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .name("�����������")
                .deploy();

        System.out.println("�����ID��" + deploy.getId());
        System.out.println("�������ƣ�" + deploy.getName());
    }
}
