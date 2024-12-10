package com.study.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.UUID;

/**
 * 文件工具类
 */
public class FileUtils {
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".pdf", ".doc", ".docx", ".xls", ".xlsx"};
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    public static boolean isValidFile(String fileName, long fileSize) {
        if (StringUtils.isEmpty(fileName)) {
            return false;
        }

        String extension = getFileExtension(fileName);
        if (!Arrays.asList(ALLOWED_EXTENSIONS).contains(extension.toLowerCase())) {
            logger.warn("Invalid file extension: {}", extension);
            return false;
        }

        if (fileSize > MAX_FILE_SIZE) {
            logger.warn("File size exceeds limit: {}", fileSize);
            return false;
        }

        return true;
    }

    public static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex) : "";
    }

    public static String generateUniqueFileName(String originalFileName) {
        String extension = getFileExtension(originalFileName);
        return UUID.randomUUID().toString() + extension;
    }
}