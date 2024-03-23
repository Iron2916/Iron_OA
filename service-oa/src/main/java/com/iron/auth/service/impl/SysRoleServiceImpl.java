package com.iron.auth.service.impl;

import com.iron.auth.mapper.SysRoleMapper;
import com.iron.auth.mapper.SysUserMapper;
import com.iron.auth.mapper.SysUserRoleMapper;
import com.iron.auth.service.SysRoleService;
import com.iron.auth.service.SysUserRoleService;
import com.iron.model.system.SysRole;
import com.iron.model.system.SysUser;

import com.iron.model.system.SysUserRole;
import com.iron.vo.system.AssginRoleVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
   @Autowired
    SysUserRoleService sysUserRoleService;
    @Override
    public Map<String, Object> findRoleByAdminId(int userId) {
        // 根据用户的id查询用户的角色

        // 查询所有的角色信息
        final List<SysRole> sysRoles = baseMapper.selectList(null);

        // 根据userId查询User_role表里面对应的角色ID数组
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        final List<SysUserRole> sysUserRoles = sysUserRoleService.list(wrapper);
        final List<Long> roleIds = sysUserRoles.stream().map(i -> i.getRoleId()).collect(Collectors.toList());

        // 根据 roleIds 和 sysRoles 创建该用户的角色列表
        List<SysRole> roleList = new ArrayList<>();
        for (SysRole sysRole : sysRoles) {

            if (roleIds.contains(sysRole.getId().longValue())) {    // 该用户拥有此角色

                roleList.add(sysRole);
            }
        }

        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("assginRoleList", roleList);
        roleMap.put("allRolesList", sysRoles);
        return roleMap;
    }

    @Override
    public void doAssign(AssginRoleVo assginRoleVo) {
        // 根据 用户id 角色id列表 更新用户角色

        final Long userId = assginRoleVo.getUserId();
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        sysUserRoleService.remove(wrapper);

        for (Long roleId : assginRoleVo.getRoleIdList()) {  // 遍历加入的数组进行加入

            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setRoleId(roleId);
            sysUserRole.setUserId(userId);

            sysUserRoleService.save(sysUserRole);
        }
    }
}
