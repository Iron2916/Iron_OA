package com.iron.TestActivity02_任务分配;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

public class MyTaskListener implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {

        if (delegateTask.getName().equals("经理审批")) {

            delegateTask.setAssignee("zhangsan");
        } else if (delegateTask.getName().equals("人事审批")) {

            delegateTask.setAssignee("lisi");
        }

    }
}
