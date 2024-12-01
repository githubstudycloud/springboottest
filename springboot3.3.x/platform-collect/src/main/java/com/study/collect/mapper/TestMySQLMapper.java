package com.study.collect.mapper;

import com.study.collect.entity.TestEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TestMySQLMapper {

    @Insert("INSERT INTO test_table (name, description, create_time, update_time) " +
            "VALUES (#{name}, #{description}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TestEntity entity);

    @Select("SELECT * FROM test_table WHERE id = #{id}")
    TestEntity findById(Long id);

    @Select("SELECT * FROM test_table")
    List<TestEntity> findAll();

    @Update("UPDATE test_table SET name = #{name}, description = #{description}, " +
            "update_time = #{updateTime} WHERE id = #{id}")
    int update(TestEntity entity);

    @Delete("DELETE FROM test_table WHERE id = #{id}")
    int deleteById(Long id);
}