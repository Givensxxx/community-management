package com.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.RepairOrderLog;
import com.community.mapper.RepairOrderLogMapper;
import com.community.service.RepairOrderLogService;
import org.springframework.stereotype.Service;

@Service
public class RepairOrderLogServiceImpl extends ServiceImpl<RepairOrderLogMapper, RepairOrderLog> implements RepairOrderLogService {
}
