package com.study.collect.api.data;

// 数据对比分析

import com.study.collect.common.model.result.PageResult;
import com.study.collect.common.model.result.Response;
import com.study.collect.domain.entity.data.CollectData;
import com.study.collect.domain.entity.data.stats.DataStats;
import com.study.collect.domain.service.data.DataQueryService;
import com.study.collect.model.request.collect.CompareRequest;
import com.study.collect.model.request.query.DataQueryRequest;
import com.study.collect.model.request.query.StatsQueryRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 数据查询控制器
 */
@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
@Slf4j
public class DataQueryController {

    private final DataQueryService queryService;

    /**
     * 分页查询数据
     */
    @GetMapping("/page")
    public Response<PageResult<CollectData>> queryByPage(DataQueryRequest request) {
        try {
            PageResult<CollectData> result = queryService.queryByPage(request);
            return Response.success(result);
        } catch (Exception e) {
            log.error("Query data failed", e);
            return Response.error("QUERY_FAILED", e.getMessage());
        }
    }

    /**
     * 查询统计信息
     */
    @GetMapping("/stats")
    public Response<DataStats> queryStats(StatsQueryRequest request) {
        try {
            DataStats stats = queryService.queryStats(request);
            return Response.success(stats);
        } catch (Exception e) {
            log.error("Query stats failed", e);
            return Response.error("STATS_QUERY_FAILED", e.getMessage());
        }
    }

    /**
     * 数据对比
     */
    @PostMapping("/compare")
    public Response<CompareResult> compareData(@RequestBody CompareRequest request) {
        try {
            CompareResult result = queryService.compareData(request);
            return Response.success(result);
        } catch (Exception e) {
            log.error("Compare data failed", e);
            return Response.error("COMPARE_FAILED", e.getMessage());
        }
    }
}
