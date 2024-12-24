package com.study.collect.business.enterprise.controller;

import com.study.collect.business.enterprise.model.Enterprise;
import com.study.collect.business.enterprise.service.EnterpriseService;
import com.study.collect.common.model.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enterprise")
@RequiredArgsConstructor
public class EnterpriseController {

    private final EnterpriseService enterpriseService;

    @GetMapping("/collect/{code}")
    public Response<Enterprise> collect(@PathVariable String code) {
        Enterprise enterprise = enterpriseService.collectAndProcess(code);
        return Response.success(enterprise);
    }

    @GetMapping("/full")
    public Response<List<Enterprise>> getFullData() {
        return Response.success(enterpriseService.getFullData());
    }

    @GetMapping("/increment")
    public Response<List<Enterprise>> getIncrementalData(
            @RequestParam String version) {
        return Response.success(enterpriseService.getIncrementalData(version));
    }
}


// 1. Controller - 增加全量/增量接口