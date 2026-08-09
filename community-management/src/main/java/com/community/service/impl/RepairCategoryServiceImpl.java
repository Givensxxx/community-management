package com.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.RepairCategory;
import com.community.mapper.RepairCategoryMapper;
import com.community.service.RepairCategoryService;
import org.springframework.stereotype.Service;

@Service
public class RepairCategoryServiceImpl extends ServiceImpl<RepairCategoryMapper, RepairCategory> implements RepairCategoryService {
}
