package com.study.collect.business.testcase.controller;

import com.study.collect.business.testcase.entity.UriEntity;
import com.study.collect.business.testcase.model.param.CollectParam;
import com.study.collect.business.testcase.model.param.DeleteParam;
import com.study.collect.business.testcase.model.param.QueryParam;
import com.study.collect.business.testcase.model.response.AsyncResponse;
import com.study.collect.business.testcase.service.UriCollectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/collect")
@RequiredArgsConstructor
@Api(tags = "URI Collection API")
public class UriCollectController {
    private final UriCollectService collectService;

    @PostMapping("/sync")
    @ApiOperation("Start data collection")
    public ResponseEntity<AsyncResponse<String>> syncData(
            @RequestBody @Valid CollectParam param) {
        log.info("Received collect request for rootNode: {}", param.getRootNode());
        return ResponseEntity.ok(collectService.collectData(param));
    }

    @PostMapping("/delete")
    @ApiOperation("Delete URI data")
    public ResponseEntity<AsyncResponse<Long>> deleteData(
            @RequestBody @Valid DeleteParam param) {
        log.info("Received delete request for {} URIs", param.getUris().size());
        return ResponseEntity.ok(collectService.deleteData(param));
    }

    @GetMapping("/query")
    @ApiOperation("Query URI data")
    public ResponseEntity<Page<UriEntity>> queryUri(
            @Valid QueryParam param) {
        return ResponseEntity.ok(collectService.queryUri(param));
    }

    @PostMapping("/batch-query")
    @ApiOperation("Batch query URIs")
    public ResponseEntity<List<UriEntity>> batchQueryUri(
            @RequestBody @NotEmpty(message = "URIs cannot be empty") List<String> uris,
            @RequestParam(required = false, defaultValue = "false") Boolean includeDeleted) {
        return ResponseEntity.ok(collectService.batchQueryUri(uris, includeDeleted));
    }

    @GetMapping("/task/{taskId}")
    @ApiOperation("Get task status")
    public ResponseEntity<AsyncResponse<Void>> getTaskStatus(
            @PathVariable @NotNull String taskId) {
        return ResponseEntity.ok(collectService.getTaskStatus(taskId));
    }

    @DeleteMapping("/task/{taskId}")
    @ApiOperation("Cancel task")
    public ResponseEntity<Boolean> cancelTask(
            @PathVariable @NotNull String taskId) {
        return ResponseEntity.ok(collectService.cancelTask(taskId));
    }

    @PutMapping("/task/{taskId}/priority/{priority}")
    @ApiOperation("Update task priority")
    public ResponseEntity<Boolean> updateTaskPriority(
            @PathVariable @NotNull String taskId,
            @PathVariable @ApiParam(value = "New priority (higher number = higher priority)") int priority) {
        return ResponseEntity.ok(collectService.updateTaskPriority(taskId, priority));
    }

    @GetMapping("/tasks")
    @ApiOperation("Get active tasks")
    public ResponseEntity<List<AsyncResponse<Void>>> getActiveTasks() {
        return ResponseEntity.ok(collectService.getActiveTasks());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        log.error("Error processing request", e);
        return ResponseEntity.internalServerError()
                .body("Error processing request: " + e.getMessage());
    }
}