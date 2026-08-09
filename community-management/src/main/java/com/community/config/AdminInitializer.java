package com.community.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.community.entity.SysUser;
import com.community.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 初始化管理员
        SysUser admin = sysUserMapper.selectOne(new QueryWrapper<SysUser>().eq("username", "admin"));
        if (admin == null) {
            SysUser user = new SysUser();
            user.setUsername("admin");
            user.setPassword(passwordEncoder.encode("123456"));
            user.setNickname("系统管理员");
            user.setRole(3);
            user.setStatus(1);
            sysUserMapper.insert(user);
            System.out.println("管理员账号已初始化: admin / 123456");
        }
        // 初始化维修工测试账号
        SysUser maintainer = sysUserMapper.selectOne(new QueryWrapper<SysUser>().eq("username", "maintainer"));
        if (maintainer == null) {
            SysUser m = new SysUser();
            m.setUsername("maintainer");
            m.setPassword(passwordEncoder.encode("123456"));
            m.setNickname("维修工张师傅");
            m.setPhone("13800138002");
            m.setRole(2);
            m.setStatus(1);
            sysUserMapper.insert(m);
            System.out.println("维修工账号已初始化: maintainer / 123456");
        }
    }
}
