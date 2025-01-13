package com.study.collect.business.testcase.repository;

import com.study.collect.business.testcase.model.Enterprise;
import com.study.collect.core.storage.repository.IRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface EnterpriseRepository extends IRepository<Enterprise>, MongoRepository<Enterprise, String> {

    /**
     * 根据编码查询
     */
    Enterprise findByCode(String code);

    /**
     * 根据名称模糊查询
     */
    List<Enterprise> findByNameLike(String name);

    /**
     * 根据行业查询
     */
    List<Enterprise> findByIndustry(String industry);

    /**
     * 根据注册机构查询
     */
    List<Enterprise> findByRegAuthority(String regAuthority);

    /**
     * 根据成立日期范围查询
     */
    List<Enterprise> findByEstDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * 根据版本号获取增量数据
     */
    @Query("{'version': {$gt: ?0}}")
    List<Enterprise> findByVersionCodeGreaterThan(String version);

    /**
     * 分片查询
     * ABS(HASH(code) % total) = index
     */
    @Query(value = "{'$where': 'Math.abs(this.code.hashCode() % ?1) == ?0'}")
    Page<Enterprise> findBySharding(int shardIndex, int shardTotal, Pageable pageable);

    /**
     * 多条件组合查询
     */
    @Query("{ $and: [ " +
            "?#{ [0] == null ? { $where : '1'} : { 'code': [0] } }, " +
            "?#{ [1] == null ? { $where : '1'} : { 'name': {$regex: [1]} } }, " +
            "?#{ [2] == null ? { $where : '1'} : { 'industry': [2] } }, " +
            "?#{ [3] == null ? { $where : '1'} : { 'regAuthority': [3] } }, " +
            "?#{ [4] == null ? { $where : '1'} : { 'estDate': { $gte: [4] } } }, " +
            "?#{ [5] == null ? { $where : '1'} : { 'estDate': { $lte: [5] } } } " +
            "] }")
    Page<Enterprise> findByConditions(String code,
                                      String name,
                                      String industry,
                                      String regAuthority,
                                      LocalDate estDateStart,
                                      LocalDate estDateEnd,
                                      Pageable pageable);

    /**
     * 按行业统计企业数量
     */
    @Query(value = "{'industry': ?0}", count = true)
    long countByIndustry(String industry);

    /**
     * 按注册机构统计企业数量
     */
    @Query(value = "{'regAuthority': ?0}", count = true)
    long countByRegAuthority(String regAuthority);

    /**
     * 软删除
     */
    @Override
    default void softDelete(String id) {
        // 实现父接口的软删除方法
        update(id, "deleted", true);
    }

    /**
     * 更新指定字段
     */
    @Query(value = "{'_id': ?0}", fields = "{ ?1: ?2 }")
    void update(String id, String field, Object value);
}