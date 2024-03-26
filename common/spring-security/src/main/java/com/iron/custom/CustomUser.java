package com.iron.custom;

import com.iron.model.system.SysUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

// 第二步，自定义自己的实体对象，调用用户信息的话直接获取这个对象
import java.util.Collection;


public class CustomUser extends User {  // 继承 Spring-security 里面的 User 对象，里面包含了很多方法,并且继承了 UserDetails 接口

    // 创建自己定义的 User 对象
    private SysUser sysUser;
    public CustomUser(SysUser sysUser, Collection<? extends GrantedAuthority> authorities) {    // 传入 sysUser

        super(sysUser.getUsername(), sysUser.getPassword(), authorities);       // 调用我们创建的方法进行验证
        this.sysUser = sysUser;
    }

    public void setSysUser(SysUser sysUser) {

        this.sysUser = sysUser;
    }

    public SysUser getSysUser() {

        return this.sysUser;
    }
}
