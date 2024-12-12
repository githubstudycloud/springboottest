package com.study.common.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * 分布式锁工具测试类
 */
class DistributedLockUtilTest {

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RLock rLock;

    private DistributedLockUtil lockUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        lockUtil = new DistributedLockUtil(redissonClient);
    }

    @Test
    void executeWithLock_Success() throws InterruptedException {
        // 配置锁模拟
        when(rLock.tryLock(anyLong(), anyLong(), eq(TimeUnit.SECONDS))).thenReturn(true);

        // 执行测试
        String result = lockUtil.executeWithLock("testKey", () -> "success");

        // 验证结果
        assertEquals("success", result);
        verify(rLock).unlock();
    }

    @Test
    void executeWithLock_FailToAcquire() throws InterruptedException {
        // 配置锁获取失败
        when(rLock.tryLock(anyLong(), anyLong(), eq(TimeUnit.SECONDS))).thenReturn(false);

        // 验证异常抛出
        assertThrows(RuntimeException.class, () ->
                lockUtil.executeWithLock("testKey", () -> "success")
        );

        // 验证锁没有被解除
        verify(rLock, never()).unlock();
    }

    @Test
    void executeWithLock_ExceptionInExecution() throws InterruptedException {
        // 配置锁模拟
        when(rLock.tryLock(anyLong(), anyLong(), eq(TimeUnit.SECONDS))).thenReturn(true);

        // 验证异常传递
        assertThrows(RuntimeException.class, () ->
                lockUtil.executeWithLock("testKey", () -> {
                    throw new RuntimeException("test exception");
                })
        );

        // 验证锁被正确释放
        verify(rLock).unlock();
    }
}