package com.iron.filter;

import com.alibaba.fastjson.JSON;
import com.iron.custom.LoginUserInfoHelper;
import com.iron.jwt.JwtHelper;
import com.iron.result.ResponseUtil;
import com.iron.result.Result;
import com.iron.result.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

// 第六步，创建 认证解析token过滤器(除了登录认证之外其他的操作如果没有携带token，就进行拦截)

public class TokenAuthenticationFilter extends OncePerRequestFilter {


    RedisTemplate redisTemplate;

    public TokenAuthenticationFilter(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        logger.info("uri:" + request.getRequestURI());

        // 如果是登陆接口，直接放行
        if ("/admin/system/index/login".equals(request.getRequestURI())) {

            filterChain.doFilter(request, response);    // 放行
            return;
        }

        // 不是登录接口
        UsernamePasswordAuthenticationToken authentication =  getAuthentication(request);
        if (null != authentication) {
            // 带有 token 放行
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } else {
            // 不带有 token 认证，进行拦截
            ResponseUtil.out(response, Result.build(null, ResultCodeEnum.LOGIN_MOBLE_ERROR));
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {

        final String token = request.getHeader("token");    // 从requset里面获取到token
        logger.info("token:" + token);

        if (!StringUtils.isEmpty(token)) {

            final String username = JwtHelper.getUsername(token);
            logger.info("userName:" + username);
            if (!StringUtils.isEmpty(username)) {

                //通过ThreadLocal记录当前登录人信息
                LoginUserInfoHelper.setUserId(JwtHelper.getUserId(token));
                LoginUserInfoHelper.setUsername(username);

                // 返回对应的 usernamePasswordAuthenticationToken
                final String authoritiesString = (String)redisTemplate.opsForValue().get(username); // 得到 存入的JSONString
                final List<Map> maps = JSON.parseArray(authoritiesString, Map.class);
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                for (Map map : maps) {

                    authorities.add(new SimpleGrantedAuthority((String)map.get("authority")));  // 这里每个map的key值都是固定的 authority
                }

                return new UsernamePasswordAuthenticationToken(username, null, authorities);   // 将结果进行返回
            }
        }
        return null;
    }


}
