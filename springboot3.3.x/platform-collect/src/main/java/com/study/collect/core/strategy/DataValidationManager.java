package com.study.collect.core.strategy;

import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.validationcontext.ValidationContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 数据校验管理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataValidationManager {

    private final ValidationRuleRepository ruleRepository;
    private final DataRepository dataRepository;
    private final MetricsCollector metricsCollector;

    /**
     * 执行数据校验
     */
    public ValidationResult validate(ValidationRequest request) {
        String validationId = UUID.randomUUID().toString();
        Timer.Sample timer = metricsCollector.startTimer("data_validation");

        try {
            // 1. 加载校验规则
            List<ValidationRule> rules = loadValidationRules(request);

            // 2. 创建校验上下文
            ValidationContext context = createValidationContext(request, rules);

            // 3. 执行校验规则
            executeValidationRules(context);

            // 4. 分析校验结果
            return analyzeValidationResult(context);

        } catch (Exception e) {
            log.error("Data validation failed: {}", validationId, e);
            throw new ValidationException("Validation failed: " + e.getMessage());
        } finally {
            metricsCollector.stopTimer(timer);
        }
    }

    /**
     * 执行校验规则
     */
    private void executeValidationRules(ValidationContext context) {
        List<ValidationRule> rules = context.getRules();
        ParallelValidationExecutor executor = new ParallelValidationExecutor(
                rules.size()
        );

        try {
            // 1. 并行执行校验规则
            List<Future<RuleResult>> futures = rules.stream()
                    .map(rule -> executor.submit(() -> executeRule(rule, context)))
                    .collect(Collectors.toList());

            // 2. 收集校验结果
            List<RuleResult> results = new ArrayList<>();
            for (Future<RuleResult> future : futures) {
                try {
                    results.add(future.get(
                            context.getTimeout(),
                            TimeUnit.MILLISECONDS
                    ));
                } catch (TimeoutException e) {
                    handleRuleTimeout(context);
                } catch (Exception e) {
                    handleRuleError(context, e);
                }
            }

            // 3. 更新校验上下文
            context.setRuleResults(results);

        } finally {
            executor.shutdown();
        }
    }

    /**
     * 执行单个校验规则
     */
    private RuleResult executeRule(ValidationRule rule, ValidationContext context) {
        String ruleId = rule.getId();
        log.debug("Executing validation rule: {}", ruleId);
        Timer.Sample ruleSample = metricsCollector.startTimer("rule_execution");

        try {
            // 1. 准备规则执行
            prepareRuleExecution(rule, context);

            // 2. 执行规则逻辑
            RuleResult result = rule.execute(context);

            // 3. 验证规则结果
            verifyRuleResult(result, rule);

            // 4. 记录执行日志
            logRuleExecution(rule, result);

            return result;

        } catch (Exception e) {
            log.error("Rule execution failed: {}", ruleId, e);
            return RuleResult.failed(rule, e.getMessage());
        } finally {
            metricsCollector.stopTimer(ruleSample);
        }
    }

    /**
     * 分析校验结果
     */
    private ValidationResult analyzeValidationResult(ValidationContext context) {
        try {
            // 1. 收集校验统计
            ValidationStats stats = collectValidationStats(context);

            // 2. 分析失败原因
            List<ValidationIssue> issues = analyzeValidationIssues(context);

            // 3. 生成建议
            List<ValidationSuggestion> suggestions = generateSuggestions(issues);

            // 4. 构建校验报告
            ValidationReport report = buildValidationReport(
                    context, stats, issues, suggestions
            );

            return ValidationResult.builder()
                    .success(isValidationSuccessful(stats))
                    .stats(stats)
                    .issues(issues)
                    .suggestions(suggestions)
                    .report(report)
                    .build();

        } catch (Exception e) {
            log.error("Analyze validation result failed", e);
            throw new ValidationException(
                    "Failed to analyze validation result: " + e.getMessage()
            );
        }
    }

    /**
     * 收集校验统计
     */
    private ValidationStats collectValidationStats(ValidationContext context) {
        List<RuleResult> results = context.getRuleResults();

        return ValidationStats.builder()
                .totalRules(results.size())
                .passedRules(countPassedRules(results))
                .failedRules(countFailedRules(results))
                .validatedRecords(context.getProcessedRecords())
                .invalidRecords(countInvalidRecords(results))
                .executionTime(context.getExecutionTime())
                .build();
    }

    /**
     * 分析验证问题
     */
    private List<ValidationIssue> analyzeValidationIssues(ValidationContext context) {
        return context.getRuleResults().stream()
                .filter(result -> !result.isSuccess())
                .map(this::createValidationIssue)
                .collect(Collectors.toList());
    }

    /**
     * 生成验证建议
     */
    private List<ValidationSuggestion> generateSuggestions(List<ValidationIssue> issues) {
        return issues.stream()
                .map(this::createSuggestion)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 构建验证报告
     */
    private ValidationReport buildValidationReport(
            ValidationContext context,
            ValidationStats stats,
            List<ValidationIssue> issues,
            List<ValidationSuggestion> suggestions) {

        return ValidationReport.builder()
                .validationId(context.getValidationId())
                .startTime(context.getStartTime())
                .endTime(LocalDateTime.now())
                .stats(stats)
                .issues(issues)
                .suggestions(suggestions)
                .build();
    }
}