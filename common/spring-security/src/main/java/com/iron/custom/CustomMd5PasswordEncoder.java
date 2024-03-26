package com.iron.custom;

import com.iron.utils.MD5;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// 第一步自己重新创建，passwordEncoder  解析器

@Component
public class CustomMd5PasswordEncoder implements PasswordEncoder {
    @Override
    // 进行编码(采用MD5方式进行编码)
    public String encode(CharSequence rawPassword) {

        return MD5.encrypt(rawPassword.toString());
    }

    @Override
    // 密码校验(采用MD5方式进行编码)
    public boolean matches(CharSequence rawPassword, String encodedPassword) {  // 用户密码 & 传递过来的认证密码

        return encodedPassword.equals(MD5.encrypt(rawPassword.toString()));
    }
}
