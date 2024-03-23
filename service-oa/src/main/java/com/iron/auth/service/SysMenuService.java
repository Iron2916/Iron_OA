package com.iron.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iron.model.system.SysMenu;
import com.iron.vo.system.AssginMenuVo;
import com.iron.vo.system.RouterVo;

import java.util.List;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author iron
 * @since 2024-03-19
 */
public interface SysMenuService extends IService<SysMenu> {

    List<RouterVo> findUserMenuList(Long id);

    List<String> findUserPermsList(Long id);

    List<SysMenu> findSysMenuByRoleId(Long roleId);

    void doAssign(AssginMenuVo assignMenuVo);
}
