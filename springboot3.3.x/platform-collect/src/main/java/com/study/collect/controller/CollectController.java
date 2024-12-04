package com.study.collect.controller;

import com.study.collect.service.CollectService;
import com.study.common.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/collect")
public class CollectController {

    @Autowired
    private CollectService collectService;

    @PostMapping("/data")
    public Result<com.study.collect.entity.CollectData> saveData(@RequestBody com.study.collect.entity.CollectData data) {
        collectService.saveData(data);
        return Result.success(data);
    }

    @GetMapping("/data")
    public Result<List<com.study.collect.entity.CollectData>> queryData(
            @RequestParam String deviceId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime) {
        List<com.study.collect.entity.CollectData> data = collectService.queryData(deviceId, startTime, endTime);
        return Result.success(data);
    }

    @GetMapping("/data/high-temperature")
    public Result<List<com.study.collect.entity.CollectData>> findHighTemperatureData(
            @RequestParam(defaultValue = "30.0") Double threshold) {
        List<com.study.collect.entity.CollectData> data = collectService.findHighTemperatureData(threshold);
        return Result.success(data);
    }
}