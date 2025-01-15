package com.study.collect.business.testcase.controller;

import com.study.collect.business.testcase.entity.UriEntity;
import com.study.collect.business.testcase.model.param.CollectParam;
import com.study.collect.business.testcase.service.UriCollectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collect")
@RequiredArgsConstructor
@Slf4j
public class UriCollectController {
    private final UriCollectService collectService;

    @PostMapping("/sync")
    public ResponseEntity<Void> syncData(@RequestBody CollectParam param) {
        collectService.collectData(param);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/uri")
    public ResponseEntity<List<UriEntity>> queryUri(
            @RequestParam(required = false) String rootNode,
            @RequestParam(required = false) String version,
            @RequestParam(required = false) String versionType) {
        return ResponseEntity.ok(collectService.queryUri(rootNode, version, versionType));
    }
}