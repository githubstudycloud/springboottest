// 1. 实体类 - UserEntity.java
package com.example.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    private Long id;
    
    @NotBlank(message = "用户名不能为空")
    @Length(min = 2, max = 20, message = "用户名长度必须在2-20个字符之间")
    private String username;
    
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String mobile;
    
    private Integer age;
    private Integer status; // 状态: 0-禁用, 1-启用
    private String remark;
    private Date createTime;
    private Date updateTime;
    private Integer isDeleted; // 0-未删除, 1-已删除
}

// 2. Mapper接口 - UserMapper.java
package com.example.mapper;

import com.example.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {
    // 查询接口
    List<UserEntity> selectByCondition(Map<String, Object> params);
    
    // 批量插入
    int batchInsert(@Param("list") List<UserEntity> users);
    
    // 批量更新
    int batchUpdate(@Param("list") List<UserEntity> users);
    
    // 软删除
    int softDelete(@Param("ids") List<Long> ids);
    
    // 硬删除
    int hardDelete(@Param("ids") List<Long> ids);
}

// 3. MyBatis XML - UserMapper.xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.mapper.UserMapper">
    <!-- 结果映射 -->
    <resultMap id="BaseResultMap" type="com.example.entity.UserEntity">
        <id column="id" property="id" />
        <result column="username" property="username" />
        <result column="mobile" property="mobile" />
        <result column="age" property="age" />
        <result column="status" property="status" />
        <result column="remark" property="remark" />
        <result column="create_time" property="createTime" />
        <result column="update_time" property="updateTime" />
        <result column="is_deleted" property="isDeleted" />
    </resultMap>
    
    <!-- 公共列 -->
    <sql id="Base_Column_List">
        id, username, mobile, age, status, remark, create_time, update_time, is_deleted
    </sql>
    
    <!-- 动态条件查询 -->
    <select id="selectByCondition" parameterType="java.util.Map" resultMap="BaseResultMap">
        SELECT
        <include refid="Base_Column_List" />
        FROM t_user
        <where>
            <if test="id != null">AND id = #{id}</if>
            <if test="username != null and username != ''">AND username LIKE CONCAT('%', #{username}, '%')</if>
            <if test="mobile != null and mobile != ''">AND mobile = #{mobile}</if>
            <if test="ageMin != null">AND age >= #{ageMin}</if>
            <if test="ageMax != null">AND age &lt;= #{ageMax}</if>
            <if test="status != null">AND status = #{status}</if>
            <if test="isDeleted != null">AND is_deleted = #{isDeleted}</if>
            <if test="startTime != null">AND create_time >= #{startTime}</if>
            <if test="endTime != null">AND create_time &lt;= #{endTime}</if>
        </where>
        <if test="orderBy != null and orderBy != ''">
            ORDER BY ${orderBy}
        </if>
        <if test="orderBy == null or orderBy == ''">
            ORDER BY id DESC
        </if>
        <if test="pageSize != null and pageNum != null">
            LIMIT #{pageOffset}, #{pageSize}
        </if>
    </select>
    
    <!-- 批量插入 -->
    <insert id="batchInsert" parameterType="java.util.List" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO t_user
        (username, mobile, age, status, remark, create_time, update_time, is_deleted)
        VALUES
        <foreach collection="list" item="item" separator=",">
            (
            #{item.username}, 
            #{item.mobile}, 
            #{item.age}, 
            #{item.status}, 
            #{item.remark}, 
            NOW(), 
            NOW(), 
            0
            )
        </foreach>
    </insert>
    
    <!-- 批量更新 -->
    <update id="batchUpdate" parameterType="java.util.List">
        <foreach collection="list" item="item" separator=";">
            UPDATE t_user
            <set>
                <if test="item.username != null">username = #{item.username},</if>
                <if test="item.mobile != null">mobile = #{item.mobile},</if>
                <if test="item.age != null">age = #{item.age},</if>
                <if test="item.status != null">status = #{item.status},</if>
                <if test="item.remark != null">remark = #{item.remark},</if>
                update_time = NOW()
            </set>
            WHERE id = #{item.id} AND is_deleted = 0
        </foreach>
    </update>
    
    <!-- 软删除 -->
    <update id="softDelete" parameterType="java.util.List">
        UPDATE t_user
        SET is_deleted = 1, update_time = NOW()
        WHERE id IN
        <foreach collection="ids" item="id" open="(" separator="," close=")">
            #{id}
        </foreach>
        AND is_deleted = 0
    </update>
    
    <!-- 硬删除 -->
    <delete id="hardDelete" parameterType="java.util.List">
        DELETE FROM t_user
        WHERE id IN
        <foreach collection="ids" item="id" open="(" separator="," close=")">
            #{id}
        </foreach>
    </delete>
</mapper>

// 4. Service接口 - UserService.java
package com.example.service;

import com.example.entity.UserEntity;
import com.example.vo.BatchResult;
import com.example.vo.PageResult;

import java.util.List;
import java.util.Map;

public interface UserService {
    // 查询方法
    PageResult<UserEntity> queryByPage(Map<String, Object> params);
    List<UserEntity> queryByCondition(Map<String, Object> params);
    
    // 批量处理方法
    BatchResult batchInsert(List<UserEntity> users);
    BatchResult batchUpdate(List<UserEntity> users);
    BatchResult batchDelete(List<Long> ids, boolean isHardDelete);
}

// 5. 返回结果类 - BatchResult.java
package com.example.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchResult {
    private boolean success;
    private int affectedRows;
    private String message;
    private List<String> errors;
    
    public static BatchResult success(int affectedRows) {
        return new BatchResult(true, affectedRows, "操作成功", null);
    }
    
    public static BatchResult fail(String message) {
        return new BatchResult(false, 0, message, null);
    }
    
    public static BatchResult fail(List<String> errors) {
        return new BatchResult(false, 0, "操作失败", errors);
    }
}

// 6. 分页结果类 - PageResult.java
package com.example.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    private List<T> list;
    private long total;
    private int pageNum;
    private int pageSize;
}

// 7. Service实现类 - UserServiceImpl.java
package com.example.service.impl;

import com.example.entity.UserEntity;
import com.example.mapper.UserMapper;
import com.example.service.UserService;
import com.example.vo.BatchResult;
import com.example.vo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Value("${batch.size:500}")
    private int batchSize;
    
    @Override
    public PageResult<UserEntity> queryByPage(Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        
        // 处理分页参数
        Integer pageNum = (Integer) params.getOrDefault("pageNum", 1);
        Integer pageSize = (Integer) params.getOrDefault("pageSize", 10);
        params.put("pageOffset", (pageNum - 1) * pageSize);
        
        // 默认查询未删除记录
        if (!params.containsKey("isDeleted")) {
            params.put("isDeleted", 0);
        }
        
        // 执行查询 
        List<UserEntity> list = userMapper.selectByCondition(params);
        
        // 查询总数（实际项目中可能需要单独的count查询）
        long total = list.size(); // 简化处理，实际应该有count查询
        
        return new PageResult<>(list, total, pageNum, pageSize);
    }
    
    @Override
    public List<UserEntity> queryByCondition(Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        
        // 默认查询未删除记录
        if (!params.containsKey("isDeleted")) {
            params.put("isDeleted", 0);
        }
        
        return userMapper.selectByCondition(params);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchResult batchInsert(List<UserEntity> users) {
        // 参数验证
        if (CollectionUtils.isEmpty(users)) {
            return BatchResult.fail("用户列表为空");
        }
        
        List<String> errors = validateInsertUsers(users);
        if (!CollectionUtils.isEmpty(errors)) {
            return BatchResult.fail(errors);
        }
        
        // 分批处理
        int totalAffected = 0;
        try {
            for (int i = 0; i < users.size(); i += batchSize) {
                List<UserEntity> batch = users.subList(
                    i, Math.min(i + batchSize, users.size()));
                int affected = userMapper.batchInsert(batch);
                totalAffected += affected;
            }
            return BatchResult.success(totalAffected);
        } catch (Exception e) {
            log.error("批量新增用户失败", e);
            throw new RuntimeException("批量新增用户异常: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchResult batchUpdate(List<UserEntity> users) {
        // 参数验证
        if (CollectionUtils.isEmpty(users)) {
            return BatchResult.fail("用户列表为空");
        }
        
        List<String> errors = validateUpdateUsers(users);
        if (!CollectionUtils.isEmpty(errors)) {
            return BatchResult.fail(errors);
        }
        
        // 分批处理
        int totalAffected = 0;
        try {
            for (int i = 0; i < users.size(); i += batchSize) {
                List<UserEntity> batch = users.subList(
                    i, Math.min(i + batchSize, users.size()));
                int affected = userMapper.batchUpdate(batch);
                totalAffected += affected;
            }
            return BatchResult.success(totalAffected);
        } catch (Exception e) {
            log.error("批量更新用户失败", e);
            throw new RuntimeException("批量更新用户异常: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchResult batchDelete(List<Long> ids, boolean isHardDelete) {
        // 参数验证
        if (CollectionUtils.isEmpty(ids)) {
            return BatchResult.fail("ID列表为空");
        }
        
        // 分批处理
        int totalAffected = 0;
        try {
            for (int i = 0; i < ids.size(); i += batchSize) {
                List<Long> batch = ids.subList(
                    i, Math.min(i + batchSize, ids.size()));
                
                int affected;
                if (isHardDelete) {
                    affected = userMapper.hardDelete(batch);
                } else {
                    affected = userMapper.softDelete(batch);
                }
                totalAffected += affected;
            }
            return BatchResult.success(totalAffected);
        } catch (Exception e) {
            log.error("批量删除用户失败", e);
            throw new RuntimeException("批量删除用户异常: " + e.getMessage());
        }
    }
    
    // 新增用户验证
    private List<String> validateInsertUsers(List<UserEntity> users) {
        List<String> errors = new ArrayList<>();
        
        for (int i = 0; i < users.size(); i++) {
            UserEntity user = users.get(i);
            String prefix = "第" + (i + 1) + "条记录: ";
            
            // 基础验证
            if (user == null) {
                errors.add(prefix + "用户信息为空");
                continue;
            }
            
            // ID应为空
            if (user.getId() != null) {
                errors.add(prefix + "新增用户不应指定ID");
            }
            
            // 用户名验证
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                errors.add(prefix + "用户名不能为空");
            } else if (user.getUsername().length() < 2 || user.getUsername().length() > 20) {
                errors.add(prefix + "用户名长度必须在2-20个字符之间");
            }
            
            // 手机号验证
            if (user.getMobile() == null || user.getMobile().trim().isEmpty()) {
                errors.add(prefix + "手机号不能为空");
            } else if (!user.getMobile().matches("^1[3-9]\\d{9}$")) {
                errors.add(prefix + "手机号格式不正确");
            }
            
            // 状态验证
            if (user.getStatus() != null && (user.getStatus() != 0 && user.getStatus() != 1)) {
                errors.add(prefix + "状态值只能是0或1");
            }
            
            // 其他业务验证...
        }
        
        return errors;
    }
    
    // 更新用户验证
    private List<String> validateUpdateUsers(List<UserEntity> users) {
        List<String> errors = new ArrayList<>();
        
        for (int i = 0; i < users.size(); i++) {
            UserEntity user = users.get(i);
            String prefix = "第" + (i + 1) + "条记录: ";
            
            // 基础验证
            if (user == null) {
                errors.add(prefix + "用户信息为空");
                continue;
            }
            
            // ID必须有值
            if (user.getId() == null || user.getId() <= 0) {
                errors.add(prefix + "用户ID不能为空");
            }
            
            // 至少有一个字段更新
            boolean hasUpdate = user.getUsername() != null || 
                                user.getMobile() != null || 
                                user.getAge() != null || 
                                user.getStatus() != null || 
                                user.getRemark() != null;
            
            if (!hasUpdate) {
                errors.add(prefix + "没有需要更新的字段");
            }
            
            // 用户名长度验证
            if (user.getUsername() != null && 
                (user.getUsername().length() < 2 || user.getUsername().length() > 20)) {
                errors.add(prefix + "用户名长度必须在2-20个字符之间");
            }
            
            // 手机号格式验证
            if (user.getMobile() != null && !user.getMobile().isEmpty() && 
                !user.getMobile().matches("^1[3-9]\\d{9}$")) {
                errors.add(prefix + "手机号格式不正确");
            }
            
            // 状态验证
            if (user.getStatus() != null && (user.getStatus() != 0 && user.getStatus() != 1)) {
                errors.add(prefix + "状态值只能是0或1");
            }
            
            // 其他业务验证...
        }
        
        return errors;
    }
}

// 8. Controller - UserController.java
package com.example.controller;

import com.example.entity.UserEntity;
import com.example.service.UserService;
import com.example.vo.BatchResult;
import com.example.vo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Slf4j
@Validated
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 分页查询用户
     */
    @GetMapping("/page")
    public ResponseEntity<PageResult<UserEntity>> queryByPage(
            @RequestParam(required = false) Map<String, Object> params) {
        log.info("分页查询用户参数: {}", params);
        return ResponseEntity.ok(userService.queryByPage(params));
    }
    
    /**
     * 条件查询用户
     */
    @GetMapping
    public ResponseEntity<List<UserEntity>> queryByCondition(
            @RequestParam(required = false) Map<String, Object> params) {
        log.info("条件查询用户参数: {}", params);
        return ResponseEntity.ok(userService.queryByCondition(params));
    }
    
    /**
     * 批量新增用户
     */
    @PostMapping("/batch")
    public ResponseEntity<BatchResult> batchInsert(
            @RequestBody @Valid List<UserEntity> users) {
        log.info("批量新增用户数量: {}", users.size());
        return ResponseEntity.ok(userService.batchInsert(users));
    }
    
    /**
     * 批量更新用户
     */
    @PutMapping("/batch")
    public ResponseEntity<BatchResult> batchUpdate(
            @RequestBody List<UserEntity> users) {
        log.info("批量更新用户数量: {}", users.size());
        return ResponseEntity.ok(userService.batchUpdate(users));
    }
    
    /**
     * 批量删除用户(软删除)
     */
    @DeleteMapping("/batch")
    public ResponseEntity<BatchResult> batchSoftDelete(
            @RequestBody List<Long> ids) {
        log.info("批量软删除用户数量: {}", ids.size());
        return ResponseEntity.ok(userService.batchDelete(ids, false));
    }
    
    /**
     * 批量物理删除用户(硬删除)
     */
    @DeleteMapping("/batch/hard")
    public ResponseEntity<BatchResult> batchHardDelete(
            @RequestBody List<Long> ids) {
        log.info("批量硬删除用户数量: {}", ids.size());
        return ResponseEntity.ok(userService.batchDelete(ids, true));
    }
}
