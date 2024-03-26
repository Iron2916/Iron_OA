package com.iron.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iron.auth.service.SysRoleService;
import com.iron.model.system.SysRole;
import com.iron.result.Result;
import com.iron.result.ResultCodeEnum;
import com.iron.vo.system.AssginRoleVo;
import com.iron.vo.system.SysRoleQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api("角色管理")
@RestController
@CrossOrigin
@RequestMapping("/admin/system/sysRole")
public class SysRoleController {

    @Autowired
    SysRoleService sysRoleService;

    // 返回数据结果
    @PreAuthorize("hasAuthority('bnt.sysRole.list')")
    @ApiOperation("获得所有的角色")
    @GetMapping("/findAll")
    public Result findeAll(SysRoleQueryVo role) {

        System.out.println(role.getRoleName());
        LambdaQueryWrapper<SysRole> wrapper = null;
        if (role != null) {

            wrapper = new LambdaQueryWrapper<>();
            wrapper.like(role.getRoleName() != null, SysRole::getRoleName, role.getRoleName());
        }

        final List<SysRole> list = sysRoleService.list(wrapper);
        return Result.ok(list);
    }

    // 分页查询
    //条件分页查询
    //page 当前页  limit 每页显示记录数
    //SysRoleQueryVo 条件对象
    @ApiOperation("条件分页查询")
    @PreAuthorize("hasAuthority('bnt.sysRole.list')")
    @GetMapping("{page}/{limit}")
    public Result pageQueryRole(@PathVariable Long page, @PathVariable Long limit, SysRoleQueryVo sysRoleQueryVo) {

        Page<SysRole> pageParam = new Page<>(page,limit);

        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        String roleName = sysRoleQueryVo.getRoleName();
        wrapper.like(!StringUtils.isEmpty(roleName), SysRole::getRoleName, roleName);

        //3 调用方法实现
        IPage<SysRole> pageModel = sysRoleService.page(pageParam, wrapper);
        return Result.ok(pageModel);
    }

    // 添加角色
    @PreAuthorize("hasAuthority('bnt.sysRole.add')")
    @ApiOperation("添加角色")
    @PostMapping("save")
    public Result save(@RequestBody SysRole role) {

        final boolean result = sysRoleService.save(role);
        if (result) {

            return Result.ok();
        } else {

            return Result.fail();
        }
    }

    // 根据id进行查询
    @PreAuthorize("hasAuthority('bnt.sysRole.list')")
    @ApiOperation("根据Id进行查询角色")
    @GetMapping("get/{id}")
    public Result get(@PathVariable int id) {

        final SysRole byId = sysRoleService.getById(id);

        if (byId != null) {

            return Result.ok(byId);
        } else {

            final Result<Object> result = Result.fail();
            result.setMessage("id角色不存在！");
            return result; // 不存在
        }

    }

    // 修改角色
    @PreAuthorize("hasAuthority('bnt.sysRole.update')")
    @ApiOperation("修改角色")
    @PutMapping("update")
    public Result update(@RequestBody SysRole role) {

        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getId, role.getId());
        final boolean result = sysRoleService.update(role, wrapper);

        if (result) return Result.ok(); else return Result.fail();
    }

    // 根据 id 进行删除
    @PreAuthorize("hasAuthority('bnt.sysRole.remove')")
    @ApiOperation("根据Id删除角色")
    @DeleteMapping("remove/{id}")
    public Result remvoe(@PathVariable int id) {

        final boolean result = sysRoleService.removeById(id);
        if (result) return Result.ok(); else return Result.fail();
    }

    // 根据 id 数组进行删除
    @ApiOperation("根据Id数组进行删除")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Integer> list) {

        final boolean result = sysRoleService.removeByIds(list);

        if (result) return Result.ok(); else return Result.fail();
    }

    @PreAuthorize("hasAuthority('bnt.sysRole.remove')")
    @ApiOperation(value = "根据用户角色id获取用户所拥有的角色")
    @GetMapping("/toAssign/{userId}")
    public Result toAssgin(@PathVariable int userId) {

        Map<String, Object> result = sysRoleService.findRoleByAdminId(userId);
        return Result.ok(result);
    }

    @PreAuthorize("bnt.sysUser.assignRole") // 给用户分配角色
    @ApiOperation(value = "给用户分配角色")
    @PostMapping("/doAssign")
    public Result doAssign(@RequestBody AssginRoleVo assginRoleVo) {
        sysRoleService.doAssign(assginRoleVo);
        return Result.ok();
    }
}
