package com.iron.auth.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.iron.auth.service.SysMenuService;
import com.iron.helper.MenuHelper;
import com.iron.model.system.SysMenu;
import com.iron.result.Result;
import com.iron.vo.system.AssginMenuVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 菜单表 前端控制器
 * </p>
 *
 * @author iron
 * @since 2024-03-19
 */
@RestController
@CrossOrigin
@RequestMapping("/admin/system/sysMenu")
public class SysMenuController {

    @Autowired
    SysMenuService service;

    @ApiOperation("获得菜单的树形结构节点")
    @GetMapping("findNodes")
    public Result findNodes() {

        final List<SysMenu> list = service.list();
        final List<SysMenu> sysMenus = MenuHelper.buildTree(list);

        return Result.ok(sysMenus);
    }

    @ApiOperation("保存菜单选项")
    @PostMapping("save")
    public Result save(@RequestBody SysMenu sysMenu) {

        service.save(sysMenu);
        return Result.ok();
    }

    @ApiOperation("更新菜单选项")
    @PutMapping("update")
    public Result updateById(@RequestBody SysMenu sysMenu) {

        service.updateById(sysMenu);
        return Result.ok();
    }

    @ApiOperation("删除菜单选项")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable long id) {

        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId, id);
        final int count = service.count(wrapper);

        if (count > 0) {    // 说明为目录

            final Result<Object> fail = Result.fail();
            fail.setMessage("不能删除目录");
        }

        service.removeById(id);
        return Result.ok();
    }

    @ApiOperation(value = "根据角色获取菜单")
    @GetMapping("toAssign/{roleId}")
    public Result toAssign(@PathVariable Long roleId) {
        List<SysMenu> list = service.findSysMenuByRoleId(roleId);
        return Result.ok(list);
    }

    @ApiOperation(value = "给角色分配权限")
    @PostMapping("/doAssign")
    public Result doAssign(@RequestBody AssginMenuVo assignMenuVo) {
        service.doAssign(assignMenuVo);
        return Result.ok();
    }
}

