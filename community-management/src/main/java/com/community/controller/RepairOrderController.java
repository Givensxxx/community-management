package com.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.Result;
import com.community.entity.RepairOrder;
import com.community.entity.SysUser;
import com.community.entity.RepairOrderLog;
import com.community.security.JwtTokenProvider;
import com.community.service.RepairOrderService;
import com.community.service.SysUserService;
import com.community.service.RepairOrderLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/order")
public class RepairOrderController {

    @Autowired
    private RepairOrderService repairOrderService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private RepairOrderLogService repairOrderLogService;
    @Autowired
    private com.community.service.RepairEvaluationService repairEvaluationService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String generateOrderNo() {
        String date = java.time.LocalDate.now().toString().replace("-", "");
        int random = new Random().nextInt(9000) + 1000;
        return "BX" + date + random;
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            String username = jwtTokenProvider.getUsernameFromToken(token);
            SysUser user = sysUserService.getOne(new QueryWrapper<SysUser>().eq("username", username));
            if (user != null) return user.getId();
        }
        return null;
    }

    @PostMapping("/create")
    public Result<String> create(@RequestBody RepairOrder order, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) return Result.error("用户未登录");
        order.setUserId(userId);
        order.setOrderNo(generateOrderNo());
        order.setStatus(0);
        repairOrderService.save(order);
        return Result.success("提交成功");
    }

    @GetMapping("/myList")
    public Result<Page<RepairOrder>> myList(@RequestParam(defaultValue = "1") Integer page,
                                            @RequestParam(defaultValue = "10") Integer size,
                                            @RequestParam(required = false) Integer status,
                                            HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) return Result.error("用户未登录");
        QueryWrapper<RepairOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        if (status != null) wrapper.eq("status", status);
        wrapper.orderByDesc("create_time");
        Page<RepairOrder> result = repairOrderService.page(new Page<>(page, size), wrapper);
        return Result.success(result);
    }

    @GetMapping("/detail/{id}")
    public Result<RepairOrder> detail(@PathVariable Long id) {
        RepairOrder order = repairOrderService.getById(id);
        if (order == null) return Result.error("工单不存在");
        return Result.success(order);
    }

    @PostMapping("/evaluate")
    public Result<String> evaluate(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) return Result.error("用户未登录");
        
        Long orderId = Long.valueOf(params.get("orderId").toString());
        Integer stars = Integer.valueOf(params.get("stars").toString());
        String content = (String) params.get("content");
        
        RepairOrder exist = repairOrderService.getById(orderId);
        if (exist == null) return Result.error("工单不存在");
        if (exist.getStatus() != 4) return Result.error("只有已完成的工单才能评价");
        
        QueryWrapper<com.community.entity.RepairEvaluation> evalWrapper = new QueryWrapper<>();
        evalWrapper.eq("order_id", orderId);
        com.community.entity.RepairEvaluation existingEval = repairEvaluationService.getOne(evalWrapper);
        if (existingEval != null) {
            return Result.error("该工单已评价");
        }
        
        com.community.entity.RepairEvaluation evaluation = new com.community.entity.RepairEvaluation();
        evaluation.setOrderId(orderId);
        evaluation.setUserId(userId);
        evaluation.setStars(stars);
        evaluation.setContent(content);
        repairEvaluationService.save(evaluation);
        
        exist.setStatus(6);
        repairOrderService.updateById(exist);
        
        return Result.success("评价成功");
    }

    @GetMapping("/pendingList")
    public Result<Page<RepairOrder>> pendingList(@RequestParam(defaultValue = "1") Integer page,
                                                  @RequestParam(defaultValue = "10") Integer size) {
        QueryWrapper<RepairOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 0);
        wrapper.orderByDesc("create_time");
        Page<RepairOrder> result = repairOrderService.page(new Page<>(page, size), wrapper);
        return Result.success(result);
    }

    @PostMapping("/take/{orderId}")
    public Result<String> takeOrder(@PathVariable Long orderId, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) return Result.error("用户未登录");
        RepairOrder order = repairOrderService.getById(orderId);
        if (order == null) return Result.error("工单不存在");
        if (order.getStatus() != 0) return Result.error("工单状态不是待接单");
        order.setMaintainerId(userId);
        order.setStatus(1);
        repairOrderService.updateById(order);
        RepairOrderLog log = new RepairOrderLog();
        log.setOrderId(orderId);
        log.setOperatorId(userId);
        log.setAction("接单");
        log.setRemark("维修工接单");
        repairOrderLogService.save(log);
        return Result.success("接单成功");
    }

    @PostMapping("/updateStatus")
    public Result<String> updateStatus(@RequestBody RepairOrder dto, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) return Result.error("用户未登录");
        RepairOrder order = repairOrderService.getById(dto.getId());
        if (order == null) return Result.error("工单不存在");
        if (!userId.equals(order.getMaintainerId())) return Result.error("无权操作此工单");
        int currentStatus = order.getStatus();
        int newStatus = dto.getStatus();
        if (newStatus <= currentStatus) return Result.error("状态不能后退或重复");
        if (newStatus - currentStatus > 1) return Result.error("状态不能跳跃");
        if (newStatus == 5 && (dto.getRejectReason() == null || dto.getRejectReason().isEmpty())) {
            return Result.error("驳回时必须填写驳回原因");
        }
        if (newStatus == 4) {
            if (dto.getResultDesc() == null || dto.getResultDesc().isEmpty()) {
                return Result.error("维修完成时必须填写维修结果");
            }
            order.setResultDesc(dto.getResultDesc());
            order.setMaterials(dto.getMaterials());
            order.setResultImages(dto.getResultImages());
        }
        if (newStatus == 5) {
            order.setRejectReason(dto.getRejectReason());
        }
        order.setStatus(newStatus);
        repairOrderService.updateById(order);
        String[] actions = {"", "接单", "上门中", "维修处理中", "维修完成", "无法维修"};
        RepairOrderLog log = new RepairOrderLog();
        log.setOrderId(dto.getId());
        log.setOperatorId(userId);
        log.setAction(actions[newStatus]);
        log.setRemark("状态更新为: " + actions[newStatus]);
        repairOrderLogService.save(log);
        return Result.success("状态更新成功");
    }

    @GetMapping("/maintainerOrders")
    public Result<Page<RepairOrder>> maintainerOrders(@RequestParam(defaultValue = "1") Integer page,
                                                       @RequestParam(defaultValue = "10") Integer size,
                                                       HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) return Result.error("用户未登录");
        QueryWrapper<RepairOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("maintainer_id", userId);
        wrapper.ge("status", 1);
        wrapper.orderByDesc("create_time");
        Page<RepairOrder> result = repairOrderService.page(new Page<>(page, size), wrapper);
        return Result.success(result);
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> statistics(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) return Result.error("用户未登录");
        QueryWrapper<RepairOrder> pendingWrapper = new QueryWrapper<>();
        pendingWrapper.eq("maintainer_id", userId).eq("status", 1);
        long pendingCount = repairOrderService.count(pendingWrapper);
        QueryWrapper<RepairOrder> doneWrapper = new QueryWrapper<>();
        doneWrapper.eq("maintainer_id", userId).eq("status", 4);
        long doneCount = repairOrderService.count(doneWrapper);
        QueryWrapper<RepairOrder> rejectWrapper = new QueryWrapper<>();
        rejectWrapper.eq("maintainer_id", userId).eq("status", 5);
        long rejectCount = repairOrderService.count(rejectWrapper);
        Map<String, Object> data = new HashMap<>();
        data.put("pending", pendingCount);
        data.put("done", doneCount);
        data.put("rejected", rejectCount);
        return Result.success(data);
    }
}

