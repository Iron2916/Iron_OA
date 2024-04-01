package com.iron.auth.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iron.auth.service.SysUserService;
import com.iron.model.system.SysUser;
import com.iron.result.Result;
import com.iron.utils.MD5;
import com.iron.vo.system.SysUserQueryVo;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author iron
 * @since 2024-03-18
 */
@RestController
@CrossOrigin
@RequestMapping("/admin/system/sysUser")
public class SysUserController {

    @Autowired
    SysUserService service;

    //用户条件分页查询
    @PreAuthorize("hasAuthority('bnt.sysUser.list')")
    @ApiOperation("用户条件分页查询")
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable Long page, @PathVariable Long limit, SysUserQueryVo sysUserQueryVo) {

        IPage<SysUser> pages = new Page<SysUser>(page, limit);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();

        final String userName = sysUserQueryVo.getKeyword();
        final String createTimeBegin = sysUserQueryVo.getCreateTimeBegin();
        final String createTimeEnd = sysUserQueryVo.getCreateTimeEnd();

        wrapper.like(!StringUtils.isEmpty(userName), SysUser::getUsername, userName);
        wrapper.ge(!StringUtils.isEmpty(createTimeBegin), SysUser::getCreateTime, createTimeBegin);
        wrapper.le(!StringUtils.isEmpty(createTimeEnd), SysUser::getUpdateTime, createTimeEnd);

        final IPage<SysUser> reuslt = service.page(pages, wrapper);
        return Result.ok(reuslt);
    }

    @PreAuthorize("hasAuthority('bnt.sysUser.list')")
    @ApiOperation(value = "获取用户")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {

        final SysUser user = service.getById(id);

        if (user != null) {

            return Result.ok(user);
        }

        final Result<Object> fail = Result.fail();
        fail.setMessage("该用户不存在！");
        return fail;
    }

    @PreAuthorize("hasAuthority('btn.sysUser.add')")
    @ApiOperation(value = "保存用户")
    @PostMapping("save")
    public Result save(@RequestBody SysUser user) {

        user.setPassword(MD5.encrypt(user.getPassword()));  //  用MD5对用户密码进行加密
        final boolean save = service.save(user);

        if (save) {

            return Result.ok(save);
        }
        final Result<Object> fail = Result.fail();
        fail.setMessage("保存失败!");
        return Result.fail();
    }

    @PreAuthorize("hasAuthority('bnt.sysUser.update')")
    @ApiOperation(value = "更新用户")
    @PutMapping("update")
    public Result updateById(@RequestBody SysUser user) {

        // 注意这里使用的是 updateById
        final boolean result = service.updateById(user);
        if (result) {

            return Result.ok();
        }
        final Result<Object> fail = Result.fail();
        fail.setMessage("更新失败!");
        return fail;
    }

    @PreAuthorize("hasAuthority('bnt.sysUser.remove')")
    @ApiOperation(value = "删除用户")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {

        final boolean b = service.removeById(id);

        if (b) {

            return Result.ok();
        }
        final Result<Object> fail = Result.fail();
        fail.setMessage("删除失败!请检查用户的id！！！");
        return fail;
    }

    @PreAuthorize("hasAuthority('bnt.sysUser.update')")
    @ApiOperation(value = "更新状态")
    @GetMapping("updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable Long id, @PathVariable Integer status) {

        final SysUser user = service.getById(id);

        if (status.intValue() == 1) {

            user.setStatus(1);
        } else {

            user.setStatus(0);
        }

        service.updateById(user);
        return Result.ok();
    }

    @ApiOperation(value = "获取当前用户基本信息")
    @GetMapping("getCurrentUser")
    public Result getCurrentUser() {

        return Result.ok(service.getCurrentUser());
    }
}

