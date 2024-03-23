package com.iron.auth.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.iron.model.system.SysRole;
import com.iron.vo.system.AssginRoleVo;

import java.util.List;
import java.util.Map;

public interface SysRoleService extends IService<SysRole> {

    Map<String, Object> findRoleByAdminId(int userId);

    void doAssign(AssginRoleVo assginRoleVo);
}