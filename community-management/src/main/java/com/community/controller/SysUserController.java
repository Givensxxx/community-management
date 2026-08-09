package com.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.community.common.Result;
import com.community.entity.SysUser;
import com.community.security.JwtTokenProvider;
import com.community.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String getUsernameFromToken(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            return jwtTokenProvider.getUsernameFromToken(token);
        }
        return null;
    }

    @GetMapping("/info")
    public Result<SysUser> info(HttpServletRequest request) {
        String username = getUsernameFromToken(request);
        if (username == null) {
            return Result.error("用户未登录");
        }
        SysUser user = sysUserService.getOne(new QueryWrapper<SysUser>().eq("username", username));
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    @PostMapping("/update")
    public Result<String> update(@RequestBody SysUser user, HttpServletRequest request) {
        String username = getUsernameFromToken(request);
        if (username == null) {
            return Result.error("用户未登录");
        }
        SysUser exist = sysUserService.getOne(new QueryWrapper<SysUser>().eq("username", username));
        if (exist == null) {
            return Result.error("用户不存在");
        }
        exist.setNickname(user.getNickname());
        exist.setPhone(user.getPhone());
        exist.setBuilding(user.getBuilding());
        exist.setRoomNo(user.getRoomNo());
        sysUserService.updateById(exist);
        return Result.success("更新成功");
    }
}