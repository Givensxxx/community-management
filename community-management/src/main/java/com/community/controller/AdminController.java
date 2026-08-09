package com.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.Result;
import com.community.entity.*;
import com.community.security.JwtTokenProvider;
import com.community.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private RepairOrderService repairOrderService;
    @Autowired
    private RepairCategoryService repairCategoryService;
    @Autowired
    private NoticeService noticeService;
    @Autowired
    private RepairOrderLogService repairOrderLogService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private boolean isAdmin(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            String username = jwtTokenProvider.getUsernameFromToken(token.substring(7));
            SysUser user = sysUserService.getOne(new QueryWrapper<SysUser>().eq("username", username));
            return user != null && user.getRole() == 3;
        }
        return false;
    }

    // ========== User Management ==========
    @GetMapping("/userList")
    public Result<Page<SysUser>> userList(@RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer size,
                                          @RequestParam(required = false) Integer role,
                                          @RequestParam(required = false) String username,
                                          HttpServletRequest request) {
        if (!isAdmin(request)) return Result.error("No permission");
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        if (role != null) wrapper.eq("role", role);
        if (username != null && !username.isEmpty()) wrapper.like("username", username);
        wrapper.orderByDesc("create_time");
        Page<SysUser> result = sysUserService.page(new Page<>(page, size), wrapper);
        for (SysUser user : result.getRecords()) {
            user.setPassword(null);
        }
        return Result.success(result);
    }

    @PostMapping("/userStatus")
    public Result<String> userStatus(@RequestBody SysUser user, HttpServletRequest request) {
        if (!isAdmin(request)) return Result.error("No permission");
        SysUser exist = sysUserService.getById(user.getId());
        if (exist == null) return Result.error("User not found");
        exist.setStatus(user.getStatus());
        sysUserService.updateById(exist);
        return Result.success("Success");
    }

    @PostMapping("/userDelete")
    public Result<String> userDelete(@RequestBody Map<String, Long> params, HttpServletRequest request) {
        if (!isAdmin(request)) return Result.error("No permission");
        sysUserService.removeById(params.get("id"));
        return Result.success("Deleted");
    }

    @PostMapping("/userAdd")
    public Result<String> userAdd(@RequestBody SysUser user, HttpServletRequest request) {
        if (!isAdmin(request)) return Result.error("No permission");
        SysUser existing = sysUserService.getOne(new QueryWrapper<SysUser>().eq("username", user.getUsername()));
        if (existing != null) return Result.error("Username exists");
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        sysUserService.save(user);
        return Result.success("Added");
    }

    // ========== Order Management ==========
    @GetMapping("/orderList")
    public Result<Page<RepairOrder>> orderList(@RequestParam(defaultValue = "1") Integer page,
                                               @RequestParam(defaultValue = "10") Integer size,
                                               @RequestParam(required = false) Integer status,
                                               @RequestParam(required = false) String building,
                                               HttpServletRequest request) {
        if (!isAdmin(request)) return Result.error("No permission");
        QueryWrapper<RepairOrder> wrapper = new QueryWrapper<>();
        if (status != null) wrapper.eq("status", status);
        if (building != null && !building.isEmpty()) wrapper.like("address", building);
        wrapper.orderByDesc("create_time");
        return Result.success(repairOrderService.page(new Page<>(page, size), wrapper));
    }

    @PostMapping("/orderAssign")
    public Result<String> orderAssign(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        if (!isAdmin(request)) return Result.error("No permission");
        Long orderId = Long.valueOf(params.get("orderId").toString());
        Long maintainerId = Long.valueOf(params.get("maintainerId").toString());
        RepairOrder order = repairOrderService.getById(orderId);
        if (order == null) return Result.error("Order not found");
        order.setMaintainerId(maintainerId);
        order.setStatus(1);
        repairOrderService.updateById(order);
        return Result.success("Assigned");
    }

    @PostMapping("/orderClose")
    public Result<String> orderClose(@RequestBody Map<String, Long> params, HttpServletRequest request) {
        if (!isAdmin(request)) return Result.error("No permission");
        RepairOrder order = repairOrderService.getById(params.get("id"));
        if (order == null) return Result.error("Order not found");
        order.setStatus(4);
        repairOrderService.updateById(order);
        return Result.success("Closed");
    }

    // ========== Category Management ==========
    @GetMapping("/categoryList")
    public Result<List<RepairCategory>> categoryList(HttpServletRequest request) {
        if (!isAdmin(request)) return Result.error("No permission");
        return Result.success(repairCategoryService.list());
    }

    @PostMapping("/categorySave")
    public Result<String> categorySave(@RequestBody RepairCategory category, HttpServletRequest request) {
        if (!isAdmin(request)) return Result.error("No permission");
        if (category.getId() == null) {
            category.setCreateTime(LocalDateTime.now());
        }
        repairCategoryService.saveOrUpdate(category);
        return Result.success("Saved");
    }

    @PostMapping("/categoryDelete/{id}")
    public Result<String> categoryDelete(@PathVariable Long id, HttpServletRequest request) {
        if (!isAdmin(request)) return Result.error("No permission");
        repairCategoryService.removeById(id);
        return Result.success("Deleted");
    }

    // ========== Notice Management ==========
    @GetMapping("/noticeList")
    public Result<Page<Notice>> noticeList(@RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer size,
                                           HttpServletRequest request) {
        if (!isAdmin(request)) return Result.error("No permission");
        return Result.success(noticeService.page(new Page<>(page, size), new QueryWrapper<Notice>().orderByDesc("create_time")));
    }

    @PostMapping("/noticeSave")
    public Result<String> noticeSave(@RequestBody Notice notice, HttpServletRequest request) {
        if (!isAdmin(request)) return Result.error("No permission");
        if (notice.getId() == null) {
            notice.setCreateTime(LocalDateTime.now());
        }
        noticeService.saveOrUpdate(notice);
        return Result.success("Saved");
    }

    @PostMapping("/noticeDelete")
    public Result<String> noticeDelete(@RequestBody Map<String, Long> params, HttpServletRequest request) {
        if (!isAdmin(request)) return Result.error("No permission");
        noticeService.removeById(params.get("id"));
        return Result.success("Deleted");
    }
}