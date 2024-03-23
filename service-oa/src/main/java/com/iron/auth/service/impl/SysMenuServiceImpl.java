package com.iron.auth.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.iron.auth.mapper.SysMenuMapper;
import com.iron.auth.mapper.SysRoleMenuMapper;
import com.iron.auth.service.SysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iron.helper.MenuHelper;
import com.iron.model.system.SysMenu;
import com.iron.model.system.SysRoleMenu;
import com.iron.vo.system.AssginMenuVo;
import com.iron.vo.system.MetaVo;
import com.iron.vo.system.RouterVo;


import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author iron
 * @since 2024-03-19
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    SysRoleMenuMapper sysRoleMenuMapper;
    @Override
    public List<RouterVo> findUserMenuList(Long userId) {
        // 根据 用户id 查询菜单可以访问的路由

        List<SysMenu> menuList = null;
        if (userId == 1) {  // 超级管理员，全部进行返回

            menuList = this.list(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getStatus, 1).orderByAsc(SysMenu::getSortValue));
        } else {    // 普通用户根据 id 联合查询

            menuList = baseMapper.findByUserId(userId);     // 联合查询
        }

        // 构建树形结构
        final List<SysMenu> sysMenuTreeList = MenuHelper.buildTree(menuList);

        // 根据构建的树形结构，构建需要返回的对象

        List<RouterVo> routerVoList = this.buildMenus(sysMenuTreeList);
        return routerVoList;
    }

    public List<RouterVo> buildMenus(List<SysMenu> menus) {

        List<RouterVo> routers = new LinkedList<RouterVo>();
        for (SysMenu menu : menus) {
            RouterVo router = new RouterVo();
            router.setHidden(false);
            router.setAlwaysShow(false);
            router.setPath(getRouterPath(menu));
            router.setComponent(menu.getComponent());
            router.setMeta(new MetaVo(menu.getName(), menu.getIcon()));
            List<SysMenu> children = menu.getChildren();

            if (menu.getType() == 1) {  // 如果是菜单， 另外判断其按钮等是否存在隐藏的路由

                final List<SysMenu> hiddenMenuList = children.stream().filter(item -> !StringUtils.isNullOrEmpty(item.getComponent())).collect(Collectors.toList());
                for (SysMenu hiddenMenu : hiddenMenuList) {
                    RouterVo hiddenRouter = new RouterVo();
                    hiddenRouter.setHidden(true);
                    hiddenRouter.setAlwaysShow(false);
                    hiddenRouter.setPath(getRouterPath(hiddenMenu));
                    hiddenRouter.setComponent(hiddenMenu.getComponent());
                    hiddenRouter.setMeta(new MetaVo(hiddenMenu.getName(), hiddenMenu.getIcon()));
                    routers.add(hiddenRouter);
                }

            } else {

                if (!CollectionUtils.isEmpty(children)) {   // 排除是按钮的menu(按钮的children为0)
                    if(children.size() > 0) {
                        router.setAlwaysShow(true);
                    }
                    router.setChildren(buildMenus(children));   // 进行递归,即词menu是一个目录
                }

            }

            routers.add(router);
        }

        return routers;
    }

    public String getRouterPath(SysMenu menu) {
        String routerPath = "/" + menu.getPath();   // 此时为目录，前面加一个/
        if(menu.getParentId().intValue() != 0) {
            routerPath = menu.getPath();    // 判断此时不为目录
        }
        return routerPath;
    }

    @Override
    public List<String> findUserPermsList(Long id) {
        // 根据用户 id 查询 button 按钮可以访问的操作
        List<SysMenu> list = null;
        if (id == 1) {
            // 管理员,直接查询所有的按钮
            list = this.list(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getStatus, 1));
        } else {
            // 非管理员，根据userId进行查询菜单里面的按钮
            list = baseMapper.findByUserId(id);
        }

        // 获得按钮里面的操作权限
        final List<String> collect = list.stream().filter(item -> item.getType() == 2).map(item -> item.getPerms()).collect(Collectors.toList());

        return collect;
    }

    @Override
    public List<SysMenu> findSysMenuByRoleId(Long roleId) {
        // 获得 role 的菜单

        // 获取所有菜单选项
        final List<SysMenu> allSysMenuList = this.list(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getStatus, 1));

        //根据角色id获取角色权限(即拥有的id)
        List<SysRoleMenu> sysRoleMenuList = sysRoleMenuMapper.selectList(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        //转换给角色id与角色权限对应Map对象
        List<Long> menuIdList = sysRoleMenuList.stream().map(e -> e.getMenuId()).collect(Collectors.toList());

        allSysMenuList.forEach(permission -> {
            if (menuIdList.contains(permission.getId())) {
                permission.setSelect(true);
            } else {
                permission.setSelect(false);
            }
        });

        List<SysMenu> sysMenuList = MenuHelper.buildTree(allSysMenuList);   // 将修改后的
        return sysMenuList;
    }

    @Override
    public void doAssign(AssginMenuVo assginMenuVo) {
        // 添加 role 的管理权限

        // 添加之前，先进行删除
        sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, assginMenuVo.getRoleId()));

        for (Long menuId : assginMenuVo.getMenuIdList()) { // 遍历 menu list
            if (menuId == null) continue;

            SysRoleMenu rolePermission = new SysRoleMenu();
            rolePermission.setRoleId(assginMenuVo.getRoleId());
            rolePermission.setMenuId(menuId);
            sysRoleMenuMapper.insert(rolePermission);
        }
    }

}
