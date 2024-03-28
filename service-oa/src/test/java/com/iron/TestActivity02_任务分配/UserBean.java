package com.iron.TestActivity02_任务分配;

import org.springframework.stereotype.Component;

// 这是 测试使用的 bean
@Component
public class UserBean {

    public String getUsername(int id) {
        if(id == 1) {
            return "zhangsan";
        }
        if(id == 2) {
            return "lisi";
        }
        return "admin";
    }
}
