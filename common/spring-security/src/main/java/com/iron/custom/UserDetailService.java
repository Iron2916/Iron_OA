package com.iron.custom;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

// 第三步，自定义一个 DetailService 接口
public interface UserDetailService extends UserDetailsService {

    // 根据用户名获取用户对象(获取不到直接抛异常)
    UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException;
}
