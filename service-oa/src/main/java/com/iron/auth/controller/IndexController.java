package com.iron.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.iron.auth.service.SysUserService;
import com.iron.config.exception.myException.IronException;
import com.iron.jwt.JwtHelper;
import com.iron.model.system.SysRole;
import com.iron.model.system.SysUser;
import com.iron.result.Result;
import com.iron.utils.MD5;
import com.iron.vo.system.LoginVo;
import io.jsonwebtoken.Jwt;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api("用户登录管理")
@RestController
@CrossOrigin
@RequestMapping("/admin/system/index")
public class IndexController {

    @Autowired
    SysUserService sysUserService;

    @ApiOperation("用户登录")
    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo) { // 根据用户 Id 返回 token

        final SysUser user = sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, loginVo.getUsername()));
        if (user == null) { // 用户不存在

            throw new IronException(201, "该用户名不存在！");
        }

        final String encrypt = MD5.encrypt(loginVo.getPassword());
        if (!user.getPassword().equals(encrypt)) {

            throw new IronException(201, "输入密码错误！");
        }

        if (user.getStatus() == 0){

            throw new IronException(201, "该用户被禁用！");
        }

        Map<String, Object> map = new HashMap<>();
        final String token = JwtHelper.createToken(user.getId(), user.getUsername());
        map.put("token", token);

        return Result.ok(map);
    }


    @ApiOperation("退出登录")
    @PostMapping("logout")
    public Result logout(){
        return Result.ok();
    }

    @ApiOperation("获取用户信息")
    @GetMapping("info")
    public Result info(HttpServletRequest request) {

        final String token = request.getHeader("token");
        System.out.println(JwtHelper.getUsername(token));
        Map<String, Object> map = sysUserService.getUserInfo(JwtHelper.getUsername(token));

        return Result.ok(map);
    }
}
