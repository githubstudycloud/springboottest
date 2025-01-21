package com.study.collect.business.enterprise.service;

import com.study.collect.business.enterprise.collector.EnterpriseCollector;
import com.study.collect.business.enterprise.model.Enterprise;
import com.study.collect.business.enterprise.model.request.EnterpriseQueryRequest;
import com.study.collect.business.enterprise.model.response.EnterpriseQueryResponse;
import com.study.collect.business.enterprise.repository.EnterpriseRepository;
import com.study.collect.core.mq.message.TaskMessage;
import com.study.collect.core.mq.producer.TaskProducer;
import com.study.collect.core.task.entity.TaskInstance;
import com.study.collect.core.task.model.TaskContext;
import com.study.collect.core.task.service.TaskExecuteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnterpriseService {

    private final EnterpriseRepository repository;
    private final EnterpriseCollector collector;
    private final TaskProducer taskProducer;
    private final TaskExecuteService taskExecuteService;
    private final MongoTemplate mongoTemplate;
    private final Random random = new Random();
    private final String[] INDUSTRIES = {"制造业", "服务业", "零售业", "建筑业", "科技业"};
    private final String[] AUTHORITIES = {"北京", "上海", "广州", "深圳", "杭州"};

    /**
     * 生成测试数据
     */
    public List<String> generateEnterprises(Integer startCode, Integer count,
                                            String industry, String regAuthority) {
        List<String> codes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String code = String.format("%06d", startCode + i);
            Enterprise enterprise = generateOne(code, industry, regAuthority);
            repository.save(enterprise);
            codes.add(code);
        }
        return codes;
    }

    /**
     * 启动采集任务
     */
    public String startCollect(String code) {
        // 创建任务实例
        TaskInstance instance;
        if (StringUtils.hasText(code)) {
            // 单个企业采集
            instance = taskExecuteService.createTaskInstance(
                    "enterprise",
                    0,
                    "{\"code\":\"" + code + "\"}"
            );
        } else {
            // 全量采集,获取总数计算分片
            long total = repository.count();
            int shardTotal = calculateShardTotal(total);
            instance = taskExecuteService.createTaskInstance(
                    "enterprise",
                    0,
                    "{\"shardTotal\":" + shardTotal + "}"
            );
        }

        // 发送任务消息
        TaskMessage message = new TaskMessage();
        message.setTaskId("enterprise");
        message.setInstanceId(instance.getInstanceId());
        message.setShardIndex(instance.getShardIndex());
        message.setShardTotal(instance.getShardTotal());
        message.setShardParam(instance.getShardParam());
        taskProducer.sendTask(message);

        return instance.getInstanceId();
    }

    /**
     * 分页查询
     */
    public EnterpriseQueryResponse queryPage(EnterpriseQueryRequest request) {
        // 构建查询条件
        Query query = new Query();
        Criteria criteria = new Criteria();

        if (StringUtils.hasText(request.getCode())) {
            criteria.and("code").is(request.getCode());
        }
        if (StringUtils.hasText(request.getName())) {
            criteria.and("name").regex(request.getName());
        }
        if (StringUtils.hasText(request.getIndustry())) {
            criteria.and("industry").is(request.getIndustry());
        }
        if (StringUtils.hasText(request.getRegAuthority())) {
            criteria.and("regAuthority").is(request.getRegAuthority());
        }
        if (request.getEstDateStart() != null) {
            criteria.and("estDate").gte(request.getEstDateStart());
        }
        if (request.getEstDateEnd() != null) {
            criteria.and("estDate").lte(request.getEstDateEnd());
        }

        query.addCriteria(criteria);

        // 执行分页查询
        long total = mongoTemplate.count(query, Enterprise.class);
        PageRequest pageRequest = PageRequest.of(request.getPageNum() - 1,
                request.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createTime"));
        query.with(pageRequest);
        List<Enterprise> list = mongoTemplate.find(query, Enterprise.class);

        // 构建分页结果
        Page<Enterprise> page = new PageImpl<>(list, pageRequest, total);
        return EnterpriseQueryResponse.of(page);
    }

    /**
     * 查询任务进度
     */
    public Object queryProgress(String taskId) {
        return taskExecuteService.getTaskLogs(taskId);
    }

    /**
     * 获取增量数据
     */
    public List<Enterprise> getIncrementalData(String version) {
        if (!StringUtils.hasText(version)) {
            return Collections.emptyList();
        }
        return repository.findByVersionCodeGreaterThan(version);
    }

    /**
     * 根据编码获取数据
     */
    public Enterprise getByCode(String code) {
        return repository.findByCode(code);
    }

    /**
     * 生成单个企业测试数据
     */
    private Enterprise generateOne(String code, String industry, String regAuthority) {
        Enterprise enterprise = new Enterprise();
        enterprise.setCode(code);
        enterprise.setName("企业" + code);
        enterprise.setAddress("测试地址" + code);
        enterprise.setContact("联系人" + code);
        enterprise.setPhone("1234567" + code.substring(code.length() - 4));
        enterprise.setIndustry(industry != null ? industry : randomIndustry());
        enterprise.setRegCapital(new BigDecimal(random.nextInt(1000000)));
        enterprise.setRegAuthority(regAuthority != null ? regAuthority : randomAuthority());
        enterprise.setEstDate(LocalDate.now().minusDays(random.nextInt(3650)));
        enterprise.setCreateTime(LocalDateTime.now());
        enterprise.setUpdateTime(LocalDateTime.now());
//        enterprise.setVersion("V" + System.currentTimeMillis());
        enterprise.setDeleted(false);
        return enterprise;
    }

    private String randomIndustry() {
        return INDUSTRIES[random.nextInt(INDUSTRIES.length)];
    }

    private String randomAuthority() {
        return AUTHORITIES[random.nextInt(AUTHORITIES.length)];
    }

    /**
     * 计算分片数量
     */
    private int calculateShardTotal(long total) {
        if (total <= 1000) return 1;
        if (total <= 5000) return 2;
        if (total <= 10000) return 4;
        if (total <= 50000) return 8;
        return 16;
    }
}