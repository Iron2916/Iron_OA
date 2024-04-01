package com.iron.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iron.model.system.SysUser;

import java.util.Map;

public interface SysUserService extends IService<SysUser> {
    Map<String, Object> getUserInfo(String username);

    Map<String, Object> getCurrentUser();
}
