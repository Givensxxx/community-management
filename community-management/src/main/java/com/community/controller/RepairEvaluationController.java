package com.community.controller;

import com.community.common.Result;
import com.community.entity.RepairEvaluation;
import com.community.service.RepairEvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/repairEvaluation")
public class RepairEvaluationController {

    @Autowired
    private RepairEvaluationService repairEvaluationService;
}
