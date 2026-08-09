package com.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.community.common.Result;
import com.community.entity.RepairCategory;
import com.community.service.RepairCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class RepairCategoryController {

    @Autowired
    private RepairCategoryService repairCategoryService;

    @GetMapping("/list")
    public Result<List<RepairCategory>> list() {
        QueryWrapper<RepairCategory> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("sort_order");
        return Result.success(repairCategoryService.list(wrapper));
    }
}