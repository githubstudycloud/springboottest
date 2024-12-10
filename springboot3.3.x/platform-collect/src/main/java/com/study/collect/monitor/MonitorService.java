package com.study.collect.monitor;

import com.study.collect.entity.*;
import com.study.collect.enums.TaskStatus;
import com.study.collect.repository.TaskRepository;
import com.study.collect.utils.SystemResourceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MonitorService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public SystemMetrics collectSystemMetrics() {
        SystemMetrics metrics = new SystemMetrics();

        // 收集系统资源使用情况
        metrics.setCpuUsage(SystemResourceUtil.getCpuUsage());
        metrics.setMemoryUsage(SystemResourceUtil.getMemoryUsage());
        metrics.setDiskUsage(SystemResourceUtil.getDiskUsage());

        // 收集任务统计信息
        metrics.setTotalTasks(taskRepository.count());
        metrics.setRunningTasks(taskRepository.countByStatus(TaskStatus.RUNNING));
        metrics.setFailedTasks(taskRepository.countByStatus(TaskStatus.FAILED));

        metrics.setCreateTime(new Date());

        // 保存指标数据
        return mongoTemplate.save(metrics);
    }

    public TaskStatusStatistics getTaskStatusStatistics() {
        TaskStatusStatistics stats = new TaskStatusStatistics();

        stats.setTotalTasks(taskRepository.count());
        stats.setRunningTasks(taskRepository.countByStatus(TaskStatus.RUNNING));
        stats.setCompletedTasks(taskRepository.countByStatus(TaskStatus.COMPLETED));
        stats.setFailedTasks(taskRepository.countByStatus(TaskStatus.FAILED));
        stats.setStoppedTasks(taskRepository.countByStatus(TaskStatus.STOPPED));
        stats.setStatisticsTime(new Date());

        return stats;
    }

    public List<AlertMessage> getActiveAlerts() {
        Query query = new Query();
        query.addCriteria(Criteria.where("acknowledged").is(false));
        query.with(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createTime"));

        return mongoTemplate.find(query, AlertMessage.class);
    }

    // 系统资源监控
    private double getDiskUsage() {
        return SystemResourceUtil.getDiskUsage();
    }

    private double getCpuUsage() {
        return SystemResourceUtil.getCpuUsage();
    }

    private double getMemoryUsage() {
        return SystemResourceUtil.getMemoryUsage();
    }

    public void acknowledgeAlert(String alertId) {
        Query query = new Query(Criteria.where("_id").is(alertId));
        Update update = new Update()
                .set("acknowledged", true)
                .set("acknowledgeTime", new Date());

        // 更新告警状态
        mongoTemplate.updateFirst(query, update, AlertMessage.class);

        // 记录操作日志
        AlertLog alertLog = new AlertLog();
        alertLog.setAlertId(alertId);
        alertLog.setOperation("ACKNOWLEDGE");
        alertLog.setOperateTime(new Date());
        mongoTemplate.save(alertLog);
    }

    // 告警规则评估
    public class AlertRuleImpl extends AlertRule {

        public boolean evaluate(SystemMetrics metrics) {
            switch (getMetric()) {
                case "cpu":
                    return evaluateThreshold(metrics.getCpuUsage());
                case "memory":
                    return evaluateThreshold(metrics.getMemoryUsage());
                case "disk":
                    return evaluateThreshold(metrics.getDiskUsage());
                case "failedTasks":
                    return evaluateThreshold((double) metrics.getFailedTasks());
                default:
                    return false;
            }
        }

        private boolean evaluateThreshold(double value) {
            switch (getCondition()) {
                case ">":
                    return value > getThreshold();
                case ">=":
                    return value >= getThreshold();
                case "<":
                    return value < getThreshold();
                case "<=":
                    return value <= getThreshold();
                case "==":
                    return value == getThreshold();
                default:
                    return false;
            }
        }

        public String generateAlertMessage(SystemMetrics metrics) {
            return String.format("Alert: %s - %s %s %s (current value: %s)",
                    getName(),
                    getMetric(),
                    getCondition(),
                    getThreshold(),
                    getMetricValue(metrics));
        }

        private double getMetricValue(SystemMetrics metrics) {
            switch (getMetric()) {
                case "cpu":
                    return metrics.getCpuUsage();
                case "memory":
                    return metrics.getMemoryUsage();
                case "disk":
                    return metrics.getDiskUsage();
                case "failedTasks":
                    return metrics.getFailedTasks();
                default:
                    return 0.0;
            }
        }
    }
}