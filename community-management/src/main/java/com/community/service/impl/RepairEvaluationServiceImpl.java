package com.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.RepairEvaluation;
import com.community.mapper.RepairEvaluationMapper;
import com.community.service.RepairEvaluationService;
import org.springframework.stereotype.Service;

@Service
public class RepairEvaluationServiceImpl extends ServiceImpl<RepairEvaluationMapper, RepairEvaluation> implements RepairEvaluationService {
}
