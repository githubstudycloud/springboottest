// FinanceDataController.java
package com.study.collect.business.finance.api.controller;

import com.study.collect.business.finance.api.model.request.FinanceDataGenerateRequest;
import com.study.collect.business.finance.api.model.request.FinanceDataQueryRequest;
import com.study.collect.business.finance.api.model.response.FinanceDataVO;
import com.study.collect.business.finance.service.FinanceCollectService;
import com.study.collect.business.finance.service.FinanceDataService;
import com.study.collect.common.model.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
public class FinanceDataController {

    private final FinanceCollectService collectService;
    private final FinanceDataService dataService;

    @PostMapping("/generate")
    public Response<String> generateData(@Valid @RequestBody FinanceDataGenerateRequest request) {
        String taskId = collectService.generateFinanceData(request);
        return Response.success(taskId);
    }

    @GetMapping("/query")
    public Response<Page<FinanceDataVO>> queryData(FinanceDataQueryRequest request) {
        Page<FinanceDataVO> result = dataService.queryFinanceData(request);
        return Response.success(result);
    }

    @GetMapping("/stats/{stockCode}")
    public Response<FinanceDataVO> getStockStats(
            @PathVariable String stockCode,
            @RequestParam(required = false) String statsType) {
        FinanceDataVO stats = dataService.getStockStats(stockCode, statsType);
        return Response.success(stats);
    }

    @GetMapping("/realtime/{stockCode}")
    public Response<FinanceDataVO> getRealtimeData(@PathVariable String stockCode) {
        FinanceDataVO data = dataService.getRealtimeData(stockCode);
        return Response.success(data);
    }

    @PostMapping("/sync/{stockCode}")
    public Response<Boolean> syncStockData(@PathVariable String stockCode) {
        collectService.syncStockData(stockCode);
        return Response.success(true);
    }
}