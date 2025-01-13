package com.study.collect.business.testcase.controller;

import com.study.collect.business.testcase.model.Enterprise;
import com.study.collect.business.testcase.model.request.EnterpriseGenerateRequest;
import com.study.collect.business.testcase.model.request.EnterpriseQueryRequest;
import com.study.collect.business.testcase.model.response.EnterpriseQueryResponse;
import com.study.collect.business.testcase.service.EnterpriseService;
import com.study.collect.common.model.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated  // 添加此注解
@RestController
@RequestMapping("/api/enterprise")
@RequiredArgsConstructor
public class EnterpriseController {

    private final EnterpriseService enterpriseService;

    /**
     * 生成测试数据
     */
    @PostMapping("/generate")
    public Response<List<String>> generateData(@Valid @RequestBody EnterpriseGenerateRequest request) {
        List<String> codes = enterpriseService.generateEnterprises(
                request.getStartCode(),
                request.getCount(),
                request.getIndustry(),
                request.getRegAuthority()
        );
        return Response.success(codes);
    }

    /**
     * 触发数据采集
     */
    @PostMapping("/collect")
    public Response<String> collect(@RequestParam(required = false) String code) {
        String taskId = enterpriseService.startCollect(code);
        return Response.success(taskId);
    }

    /**
     * 分页查询数据
     */
    @GetMapping("/page")
    public Response<EnterpriseQueryResponse> queryPage(@Valid EnterpriseQueryRequest request) {
        EnterpriseQueryResponse response = enterpriseService.queryPage(request);
        return Response.success(response);
    }

    /**
     * 查询采集进度
     */
    @GetMapping("/progress/{taskId}")
    public Response<Object> queryProgress(@PathVariable String taskId) {
        Object progress = enterpriseService.queryProgress(taskId);
        return Response.success(progress);
    }

    /**
     * 获取某个版本之后的增量数据
     */
    @GetMapping("/increment")
    public Response<List<Enterprise>> getIncrementalData(
            @RequestParam(required = false) String version) {
        List<Enterprise> data = enterpriseService.getIncrementalData(version);
        return Response.success(data);
    }

    /**
     * 根据编码获取数据
     */
    @GetMapping("/{code}")
    public Response<Enterprise> getByCode(@PathVariable String code) {
        Enterprise enterprise = enterpriseService.getByCode(code);
        return Response.success(enterprise);
    }
}