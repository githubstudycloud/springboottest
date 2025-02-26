// DataParser.java
package com.platform.fluxcore.parser;

import java.io.InputStream;
import java.util.Map;

/**
 * 数据解析器接口
 */
public interface DataParser<T> {
    
    /**
     * 解析字符串数据
     * @param content 数据内容
     * @return 解析后的数据对象
     */
    T parse(String content);
    
    /**
     * 解析输入流数据
     * @param inputStream 输入流
     * @return 解析后的数据对象
     */
    T parseStream(InputStream inputStream);
    
    /**
     * 将对象转换为字符串
     * @param data 数据对象
     * @return 转换后的字符串
     */
    String serialize(T data);
    
    /**
     * 获取解析器支持的数据类型
     * @return 数据类型，通常为文件扩展名，如json、xml、csv等
     */
    String getType();
}

// JsonDataParser.java
package com.platform.fluxcore.parser;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.fluxcore.exception.FluxCoreException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * JSON数据解析器
 */
@Slf4j
@Component
public class JsonDataParser implements DataParser<Map<String, Object>> {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public Map<String, Object> parse(String content) {
        try {
            return objectMapper.readValue(content, HashMap.class);
        } catch (Exception e) {
            log.error("Failed to parse JSON string: {}", content, e);
            throw new FluxCoreException("JSON解析失败: " + e.getMessage());
        }
    }
    
    @Override
    public Map<String, Object> parseStream(InputStream inputStream) {
        try {
            return objectMapper.readValue(inputStream, HashMap.class);
        } catch (Exception e) {
            log.error("Failed to parse JSON from input stream", e);
            throw new FluxCoreException("JSON流解析失败: " + e.getMessage());
        }
    }
    
    @Override
    public String serialize(Map<String, Object> data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            log.error("Failed to serialize object to JSON: {}", data, e);
            throw new FluxCoreException("JSON序列化失败: " + e.getMessage());
        }
    }
    
    @Override
    public String getType() {
        return "json";
    }
    
    /**
     * 将JSON字符串解析为指定类型的对象
     * @param content JSON字符串
     * @param clazz 目标类型
     * @param <T> 目标类型
     * @return 解析后的对象
     */
    public <T> T parseToObject(String content, Class<T> clazz) {
        try {
            return objectMapper.readValue(content, clazz);
        } catch (Exception e) {
            log.error("Failed to parse JSON to object of type {}: {}", clazz.getName(), content, e);
            throw new FluxCoreException("JSON解析为对象失败: " + e.getMessage());
        }
    }
    
    /**
     * 将JSON字符串解析为泛型容器对象
     * @param content JSON字符串
     * @param containerClass 容器类型
     * @param elementClass 元素类型
     * @param <C> 容器类型
     * @param <E> 元素类型
     * @return 解析后的对象
     */
    public <C, E> C parseToGenericObject(String content, Class<C> containerClass, Class<E> elementClass) {
        try {
            JavaType type = objectMapper.getTypeFactory().constructParametricType(containerClass, elementClass);
            return objectMapper.readValue(content, type);
        } catch (Exception e) {
            log.error("Failed to parse JSON to generic object of type {}<{}>: {}", 
                    containerClass.getName(), elementClass.getName(), content, e);
            throw new FluxCoreException("JSON解析为泛型对象失败: " + e.getMessage());
        }
    }
}

// XmlDataParser.java
package com.platform.fluxcore.parser;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.platform.fluxcore.exception.FluxCoreException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * XML数据解析器
 */
@Slf4j
@Component
public class XmlDataParser implements DataParser<Map<String, Object>> {
    
    private final XmlMapper xmlMapper = new XmlMapper();
    
    @Override
    public Map<String, Object> parse(String content) {
        try {
            return xmlMapper.readValue(content, HashMap.class);
        } catch (Exception e) {
            log.error("Failed to parse XML string: {}", content, e);
            throw new FluxCoreException("XML解析失败: " + e.getMessage());
        }
    }
    
    @Override
    public Map<String, Object> parseStream(InputStream inputStream) {
        try {
            return xmlMapper.readValue(inputStream, HashMap.class);
        } catch (Exception e) {
            log.error("Failed to parse XML from input stream", e);
            throw new FluxCoreException("XML流解析失败: " + e.getMessage());
        }
    }
    
    @Override
    public String serialize(Map<String, Object> data) {
        try {
            return xmlMapper.writeValueAsString(data);
        } catch (Exception e) {
            log.error("Failed to serialize object to XML: {}", data, e);
            throw new FluxCoreException("XML序列化失败: " + e.getMessage());
        }
    }
    
    @Override
    public String getType() {
        return "xml";
    }
    
    /**
     * 将XML字符串解析为指定类型的对象
     * @param content XML字符串
     * @param clazz 目标类型
     * @param <T> 目标类型
     * @return 解析后的对象
     */
    public <T> T parseToObject(String content, Class<T> clazz) {
        try {
            return xmlMapper.readValue(content, clazz);
        } catch (Exception e) {
            log.error("Failed to parse XML to object of type {}: {}", clazz.getName(), content, e);
            throw new FluxCoreException("XML解析为对象失败: " + e.getMessage());
        }
    }
}

// CsvDataParser.java
package com.platform.fluxcore.parser;

import com.platform.fluxcore.exception.FluxCoreException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CSV数据解析器
 */
@Slf4j
@Component
public class CsvDataParser implements DataParser<List<Map<String, String>>> {
    
    @Override
    public List<Map<String, String>> parse(String content) {
        try {
            CSVParser parser = CSVParser.parse(content, CSVFormat.DEFAULT.withFirstRecordAsHeader());
            return convertToMapList(parser);
        } catch (Exception e) {
            log.error("Failed to parse CSV string: {}", content, e);
            throw new FluxCoreException("CSV解析失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<Map<String, String>> parseStream(InputStream inputStream) {
        try {
            CSVParser parser = CSVParser.parse(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8),
                    CSVFormat.DEFAULT.withFirstRecordAsHeader()
            );
            return convertToMapList(parser);
        } catch (Exception e) {
            log.error("Failed to parse CSV from input stream", e);
            throw new FluxCoreException("CSV流解析失败: " + e.getMessage());
        }
    }
    
    @Override
    public String serialize(List<Map<String, String>> data) {
        if (data == null || data.isEmpty()) {
            return "";
        }
        
        try {
            StringWriter writer = new StringWriter();
            // 获取第一条记录的所有键作为CSV列头
            String[] headers = data.get(0).keySet().toArray(new String[0]);
            
            try (CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(headers))) {
                for (Map<String, String> row : data) {
                    List<String> values = new ArrayList<>();
                    for (String header : headers) {
                        values.add(row.getOrDefault(header, ""));
                    }
                    printer.printRecord(values);
                }
            }
            
            return writer.toString();
        } catch (IOException e) {
            log.error("Failed to serialize data to CSV: {}", data, e);
            throw new FluxCoreException("CSV序列化失败: " + e.getMessage());
        }
    }
    
    @Override
    public String getType() {
        return "csv";
    }
    
    /**
     * 将CSVParser转换为Map列表
     * @param parser CSVParser对象
     * @return Map列表
     */
    private List<Map<String, String>> convertToMapList(CSVParser parser) {
        List<Map<String, String>> result = new ArrayList<>();
        
        for (CSVRecord record : parser) {
            Map<String, String> row = new HashMap<>();
            parser.getHeaderMap().forEach((header, index) -> row.put(header, record.get(header)));
            result.add(row);
        }
        
        return result;
    }
}
