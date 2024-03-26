package com.iron.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.iron.auth.service.SysMenuService;
import com.iron.auth.service.SysUserService;
import com.iron.custom.CustomUser;
import com.iron.custom.UserDetailService;
import com.iron.model.system.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
public class UserDetailServiceImpl implements UserDetailService {

    @Autowired
    SysUserService sysUserService;
    @Autowired
    SysMenuService sysMenuService;
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

        final SysUser sysUser = sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, userName));

        if (null == sysUser) {

            throw new UsernameNotFoundException("用户名不存在!");
        }

        if (sysUser.getStatus().intValue() == 0) {

            throw new RuntimeException("该账号已经停用");
        }

        //  查询到用户的权限， 然后进行分配
        final List<String> permsList = sysMenuService.findUserPermsList(sysUser.getId());
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for (String perm : permsList) {

            authorities.add(new SimpleGrantedAuthority(perm.trim()));
        }

        return new CustomUser(sysUser, authorities);    // 返回用户以及相关的权限
    }
}