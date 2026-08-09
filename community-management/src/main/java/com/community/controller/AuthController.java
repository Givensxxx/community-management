package com.community.controller;

import com.community.common.Result;
import com.community.dto.LoginDTO;
import com.community.dto.RegisterDTO;
import com.community.entity.SysUser;
import com.community.mapper.SysUserMapper;
import com.community.security.JwtTokenProvider;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public Result<String> register(@RequestBody RegisterDTO dto) {
        SysUser existing = sysUserMapper.selectOne(new QueryWrapper<SysUser>().eq("username", dto.getUsername()));
        if (existing != null) {
            return Result.error("用户名已存在");
        }
        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname());
        user.setPhone(dto.getPhone());
        user.setBuilding(dto.getBuilding());
        user.setRoomNo(dto.getRoomNo());
        user.setRole(1);
        user.setStatus(1);
        sysUserMapper.insert(user);
        return Result.success("注册成功");
    }

    @PostMapping("/login")
    public Result<java.util.Map<String, Object>> login(@RequestBody LoginDTO dto) {
        SysUser user = sysUserMapper.selectOne(new QueryWrapper<SysUser>().eq("username", dto.getUsername()));
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            return Result.error("用户名或密码错误");
        }
        if (user.getStatus() != 1) {
            return Result.error("账户已被禁用");
        }
        String token = jwtTokenProvider.generateToken(user.getUsername());
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("token", token);
        data.put("role", user.getRole());
        data.put("username", user.getUsername());
        data.put("nickname", user.getNickname());
        return Result.success(data);
    }

    @GetMapping("/info")
    public Result<SysUser> info(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtTokenProvider.getUsernameFromToken(token);
        SysUser user = sysUserMapper.selectOne(new QueryWrapper<SysUser>().eq("username", username));
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }
}
