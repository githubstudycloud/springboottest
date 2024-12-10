package com.study.collect.utils;

import com.sun.management.OperatingSystemMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.management.ManagementFactory;

public class SystemResourceUtil {
    private static final Logger logger = LoggerFactory.getLogger(SystemResourceUtil.class);
    private static final OperatingSystemMXBean osBean =
            (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    public static double getCpuUsage() {
        try {
            return osBean.getSystemCpuLoad() * 100;
        } catch (Exception e) {
            logger.error("Failed to get CPU usage", e);
            return -1;
        }
    }

    public static double getMemoryUsage() {
        try {
            long totalMemory = osBean.getTotalPhysicalMemorySize();
            long freeMemory = osBean.getFreePhysicalMemorySize();
            return ((double) (totalMemory - freeMemory) / totalMemory) * 100;
        } catch (Exception e) {
            logger.error("Failed to get memory usage", e);
            return -1;
        }
    }

    // 添加磁盘使用率监控
    public static double getDiskUsage() {
        try {
            File root = new File("/");
            long totalSpace = root.getTotalSpace();
            long usableSpace = root.getUsableSpace();
            return ((double) (totalSpace - usableSpace) / totalSpace) * 100;
        } catch (Exception e) {
            logger.error("Failed to get disk usage", e);
            return -1;
        }
    }
}