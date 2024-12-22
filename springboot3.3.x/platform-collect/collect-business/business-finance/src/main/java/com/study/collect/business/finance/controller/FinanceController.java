package com.study.collect.business.finance.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
public class FinanceController {

//    private final EnterpriseService enterpriseService;
//
//    @GetMapping("/collect/{code}")
//    public Response<Enterprise> collect(@PathVariable String code) {
//        Enterprise enterprise = enterpriseService.collectAndProcess(code);
//        return Response.success(enterprise);
//    }
}