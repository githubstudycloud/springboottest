// DataParserUtil.java
package com.platform.fluxcore.util;

import com.platform.fluxcore.exception.FluxCoreException;
import com.platform.fluxcore.parser.CsvDataParser;
import com.platform.fluxcore.parser.DataParser;
import com.platform.fluxcore.parser.JsonDataParser;
import com.platform.fluxcore.parser.XmlDataParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据解析工具类
 */
@Slf4j
@Component
public class DataParserUtil {
    
    private final Map<String, DataParser<?>> parserRegistry = new HashMap<>();
    
    @Autowired
    public DataParserUtil(JsonDataParser jsonDataParser, XmlDataParser xmlDataParser, CsvDataParser csvDataParser) {
        registerParser(jsonDataParser);
        registerParser(xmlDataParser);
        registerParser(csvDataParser);
    }
    
    /**
     * 注册数据解析器
     * @param parser 解析器实例
     */
    public void registerParser(DataParser<?> parser) {
        parserRegistry.put(parser.getType(), parser);
        log.info("Registered data parser for format: {}", parser.getType());
    }
    
    /**
     * 根据数据格式获取对应的解析器
     * @param formatType 数据格式类型
     * @return 解析器实例
     */
    public DataParser<?> getParser(String formatType) {
        DataParser<?> parser = parserRegistry.get(formatType.toLowerCase());
        if (parser == null) {
            throw new FluxCoreException("不支持的数据格式类型: " + formatType);
        }
        return parser;
    }
    
    /**
     * 解析JSON数据
     * @param content JSON字符串
     * @return 解析后的Map
     */
    public Map<String, Object> parseJson(String content) {
        return ((JsonDataParser) getParser("json")).parse(content);
    }
    
    /**
     * 解析JSON数据至指定类型
     * @param content JSON字符串
     * @param clazz 目标类型
     * @param <T> 目标类型泛型
     * @return 解析后的对象
     */
    public <T> T parseJsonToObject(String content, Class<T> clazz) {
        return ((JsonDataParser) getParser("json")).parseToObject(content, clazz);
    }
    
    /**
     * 解析XML数据
     * @param content XML字符串
     * @return 解析后的Map
     */
    public Map<String, Object> parseXml(String content) {
        return ((XmlDataParser) getParser("xml")).parse(content);
    }
    
    /**
     * 解析XML数据至指定类型
     * @param content XML字符串
     * @param clazz 目标类型
     * @param <T> 目标类型泛型
     * @return 解析后的对象
     */
    public <T> T parseXmlToObject(String content, Class<T> clazz) {
        return ((XmlDataParser) getParser("xml")).parseToObject(content, clazz);
    }
    
    /**
     * 解析CSV数据
     * @param content CSV字符串
     * @return 解析后的记录列表
     */
    public List<Map<String, String>> parseCsv(String content) {
        return ((CsvDataParser) getParser("csv")).parse(content);
    }
    
    /**
     * 从对象序列化为JSON
     * @param data 数据对象
     * @return JSON字符串
     */
    public String toJson(Object data) {
        return ((JsonDataParser) getParser("json")).serialize((Map<String, Object>) data);
    }
    
    /**
     * 从对象序列化为XML
     * @param data 数据对象
     * @return XML字符串
     */
    public String toXml(Object data) {
        return ((XmlDataParser) getParser("xml")).serialize((Map<String, Object>) data);
    }
    
    /**
     * 根据格式类型自动解析数据
     * @param content 数据内容
     * @param formatType 格式类型
     * @return 解析后的对象
     */
    public Object parse(String content, String formatType) {
        DataParser<?> parser = getParser(formatType);
        return parser.parse(content);
    }
    
    /**
     * 根据格式类型自动解析输入流
     * @param inputStream 输入流
     * @param formatType 格式类型
     * @return 解析后的对象
     */
    public Object parseStream(InputStream inputStream, String formatType) {
        DataParser<?> parser = getParser(formatType);
        return parser.parseStream(inputStream);
    }
    
    /**
     * 判断是否支持指定的格式类型
     * @param formatType 格式类型
     * @return 是否支持
     */
    public boolean isSupported(String formatType) {
        return parserRegistry.containsKey(formatType.toLowerCase());
    }
    
    /**
     * 获取所有支持的格式类型
     * @return 格式类型列表
     */
    public List<String> getSupportedFormats() {
        return List.copyOf(parserRegistry.keySet());
    }
}

// FluxCoreException.java
package com.platform.fluxcore.exception;

/**
 * FluxCore自定义异常
 */
public class FluxCoreException extends RuntimeException {
    
    public FluxCoreException(String message) {
        super(message);
    }
    
    public FluxCoreException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public FluxCoreException(Throwable cause) {
        super(cause);
    }
}
