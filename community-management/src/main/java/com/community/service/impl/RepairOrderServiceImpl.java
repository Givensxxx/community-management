package com.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.RepairOrder;
import com.community.mapper.RepairOrderMapper;
import com.community.service.RepairOrderService;
import org.springframework.stereotype.Service;

@Service
public class RepairOrderServiceImpl extends ServiceImpl<RepairOrderMapper, RepairOrder> implements RepairOrderService {
}
