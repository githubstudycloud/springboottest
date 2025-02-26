// DataSourceController.java
package com.platform.fluxcore.controller;

import com.platform.fluxcore.service.DataSourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据源控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/datasource")
public class DataSourceController {
    
    @Autowired
    private DataSourceService dataSourceService;
    
    /**
     * 获取所有数据源
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> listDataSources() {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, String> dataSources = dataSourceService.getAllDataSources();
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", dataSources);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Failed to list datasources", e);
            result.put("code", 500);
            result.put("message", e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }
    
    /**
     * 添加数据源
     */
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addDataSource(
            @RequestParam String alias,
            @RequestParam String url,
            @RequestParam String username,
            @RequestParam String password) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = dataSourceService.addDataSource(alias, url, username, password);
            result.put("code", success ? 200 : 400);
            result.put("message", success ? "添加成功" : "添加失败");
            result.put("data", success);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Failed to add datasource: {}", alias, e);
            result.put("code", 500);
            result.put("message", e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }
    
    /**
     * 获取数据源配置
     */
    @GetMapping("/config/{alias}")
    public ResponseEntity<Map<String, Object>> getDataSourceConfig(@PathVariable String alias) {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, String> config = dataSourceService.getDataSourceConfig(alias);
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", config);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Failed to get datasource config: {}", alias, e);
            result.put("code", 500);
            result.put("message", e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }
    
    /**
     * 删除数据源
     */
    @DeleteMapping("/remove/{alias}")
    public ResponseEntity<Map<String, Object>> removeDataSource(@PathVariable String alias) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = dataSourceService.removeDataSource(alias);
            result.put("code", success ? 200 : 400);
            result.put("message", success ? "删除成功" : "删除失败");
            result.put("data", success);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Failed to remove datasource: {}", alias, e);
            result.put("code", 500);
            result.put("message", e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }
}

// DataTransferController.java
package com.platform.fluxcore.controller;

import com.platform.fluxcore.entity.DataEntity;
import com.platform.fluxcore.entity.SourceData;
import com.platform.fluxcore.service.DataTransferService;
import com.platform.fluxcore.util.DataParserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据传输控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/data")
public class DataTransferController {
    
    @Autowired
    private DataTransferService dataTransferService;
    
    @Autowired
    private DataParserUtil dataParserUtil;
    
    /**
     * 收集数据
     */
    @PostMapping("/collect")
    public ResponseEntity<Map<String, Object>> collectData(
            @RequestParam String sourceType,
            @RequestParam String content,
            @RequestParam String contentFormat,
            @RequestParam(required = false) String sourceLocation) {
        Map<String, Object> result = new HashMap<>();
        try {
            SourceData sourceData = dataTransferService.collectData(
                    sourceType, content, contentFormat, sourceLocation);
            result.put("code", 200);
            result.put("message", "数据收集成功");
            result.put("data", sourceData);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Failed to collect data", e);
            result.put("code", 500);
            result.put("message", e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }
    
    /**
     * 解析数据
     */
    @GetMapping("/parse/{sourceDataId}")
    public ResponseEntity<Map<String, Object>> parseData(@PathVariable Long sourceDataId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Object parsedData = dataTransferService.parseData(sourceDataId);
            result.put("code", 200);
            result.put("message", "数据解析成功");
            result.put("data", parsedData);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Failed to parse data: {}", sourceDataId, e);
            result.put("code", 500);
            result.put("message", e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }
    
    /**
     * 处理并存储数据
     */
    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processAndStore(
            @RequestParam Long sourceDataId,
            @RequestParam String targetDbAlias) {
        Map<String, Object> result = new HashMap<>();
        try {
            DataEntity dataEntity = dataTransferService.processAndStore(sourceDataId, targetDbAlias);
            result.put("code", 200);
            result.put("message", "数据处理并存储成功");
            result.put("data", dataEntity);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Failed to process and store data: {}", sourceDataId, e);
            result.put("code", 500);
            result.put("message", e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }
    
    /**
     * 获取并转换数据
     */
    @GetMapping("/convert")
    public ResponseEntity<Map<String, Object>> retrieveAndConvert(
            @RequestParam Long dataId,
            @RequestParam String dbAlias,
            @RequestParam String targetFormat) {
        Map<String, Object> result = new HashMap<>();
        try {
            String convertedData = dataTransferService.retrieveAndConvert(dataId, dbAlias, targetFormat);
            result.put("code", 200);
            result.put("message", "数据获取并转换成功");
            result.put("data", convertedData);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Failed to retrieve and convert data: {}", dataId, e);
            result.put("code", 500);
            result.put("message", e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }
    
    /**
     * 查询业务数据
     */
    @GetMapping("/business")
    public ResponseEntity<Map<String, Object>> queryBusinessData(
            @RequestParam String dbAlias,
            @RequestParam(required = false) String format) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<DataEntity> data = dataTransferService.queryBusinessData(dbAlias, format);
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", data);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Failed to query business data", e);
            result.put("code", 500);
            result.put("message", e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }
    
    /**
     * 查询采集数据
     */
    @GetMapping("/source")
    public ResponseEntity<Map<String, Object>> querySourceData(
            @RequestParam(required = false) String sourceType) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<SourceData> data = dataTransferService.querySourceData(sourceType);
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", data);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Failed to query source data", e);
            result.put("code", 500);
            result.put("message", e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }
    
    /**
     * 获取支持的数据格式
     */
    @GetMapping("/formats")
    public ResponseEntity<Map<String, Object>> getSupportedFormats() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<String> formats = dataParserUtil.getSupportedFormats();
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", formats);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Failed to get supported formats", e);
            result.put("code", 500);
            result.put("message", e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }
}
