package com.iron.TestActivity03_global流程变量;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class Test03_通过当前流程实例设置 {

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    TaskService taskService;

    @Test
    public void startProcessInstance() {

        String businessKey = "1007";
        final ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("qingjia", businessKey);
        System.out.println("流程实例ID：" + processInstance.getId());
    }


    // 给所有的张三任务进行,并且通过runtimeService 设置全局变量
    @Test 
    public void setGlobalValByProcessInstanceId() {

        String assignee = "zhangsan";
        final List<Task> list = taskService.createTaskQuery()
                .taskAssignee(assignee)
                .list();

        for (Task task : list) {

            Map map = new HashMap<>();
            map.put("text", task.getId());
            runtimeService.setVariables(task.getProcessInstanceId(), map);
            System.out.println(task.getProcessInstanceId());

            taskService.complete(task.getId()); // 将张三进行执行，流程变量传递到后面的李四中
        }
    }

    // 通过 lisi 获取 zhangsan 传递过来的流程变量
    @Test
    public void getGlobalVaraible() {

        String assignee = "lisi";
        final List<Task> list = taskService.createTaskQuery()
                .taskAssignee(assignee)
                .list();

        for (Task task : list) {

            System.out.println("taskID：" + task.getId());
            final Object text = runtimeService.getVariable(task.getProcessInstanceId(), "text");
            System.out.println("text：" + text);
        }

        // 通过 act_ru_variable 里面的excuteId
//        final Object text = runtimeService.getVariable("5097006b-eb6d-11ee-a1a4-744ca17a808e", "text");
//        System.out.println("text = " + text);
    }
}
