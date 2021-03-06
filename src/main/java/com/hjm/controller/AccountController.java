package com.hjm.controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hjm.common.lang.Result;
import com.hjm.dto.LoginDto;
import com.hjm.entity.User;
import com.hjm.service.UserService;

import com.hjm.utils.JwtUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class AccountController {
    @Autowired
    UserService userService;

    @Autowired
    JwtUtil jwtUtil;
    @PostMapping("/login")
    public Result login(@Validated @RequestBody LoginDto loginDto, HttpServletResponse httpServletResponse){
       User user= userService.getOne(new QueryWrapper<User>().eq("username",loginDto.getUsername()));
       Assert.notNull(user,"用户不存在");
       if (!user.getPassword().equals(SecureUtil.md5(loginDto.getPassword()))){
           return Result.fail("密码错误");
       }
    String jwt = jwtUtil.generateToken(user.getId());
        httpServletResponse.setHeader("Authorization",jwt);
        httpServletResponse.setHeader("Access-control-Expose-Headers","Authorization");
        return Result.succ(MapUtil.builder()
                .put("id",user.getId())
            .put("username",user.getUsername())
            .put("avatar",user.getAvatar())
            .put("email",user.getEmail()).map());
    }
    @GetMapping("/logout")
    public Result logout(){
        SecurityUtils.getSubject().logout();
        return Result.succ(null);
    }

}
