package com.example.utils;

import org.springframework.util.StringUtils;
import java.util.regex.Pattern;

/**
 * HTML标签处理工具类
 * 用于将HTML文本转换为纯文本，支持基本格式保留
 */
public class HtmlStripUtil {
    
    /**
     * 移除HTML标签，保留文本内容
     * 会将以下HTML元素转换为相应的文本格式：
     * - <br> 转换为换行符
     * - <p> 转换为段落（双换行）
     * - 列表项保留缩进
     * 
     * @param html HTML文本
     * @return 处理后的纯文本
     */
    public static String stripHtml(String html) {
        if (!StringUtils.hasText(html)) {
            return "";
        }
        
        // 如果不包含HTML标签，直接返回原字符串
        if (!containsHtml(html)) {
            return html;
        }
        
        // 预处理：统一换行符
        String text = html.replaceAll("\\r\\n", "\n");
        
        // 处理特殊标签为对应格式
        text = text.replaceAll("<br\\s*/?>", "\n")               // 处理换行
                  .replaceAll("</p>\\s*<p>", "\n\n")            // 段落间双换行
                  .replaceAll("<li>", "\n • ")                  // 列表项加圆点
                  .replaceAll("</li>", "")
                  .replaceAll("<ul>|</ul>|<ol>|</ol>", "\n");  // 列表前后加换行
        
        // 移除其他HTML标签
        text = Pattern.compile("<[^>]*>").matcher(text).replaceAll("");
        
        // 处理HTML实体
        text = text.replaceAll("&nbsp;", " ")
                  .replaceAll("&amp;", "&")
                  .replaceAll("&lt;", "<")
                  .replaceAll("&gt;", ">")
                  .replaceAll("&quot;", "\"")
                  .replaceAll("&#39;", "'");
        
        // 清理多余空白
        text = text.replaceAll("\\s*\n\\s*", "\n")      // 清理换行符周围空白
                  .replaceAll("[ \t]+", " ")           // 多个空格合并为一个
                  .trim();                             // 去除首尾空白
        
        return text;
    }
    
    /**
     * 移除HTML标签，保留纯文本（简单版本，不保留格式）
     * 
     * @param html HTML文本
     * @return 处理后的纯文本
     */
    public static String stripHtmlSimple(String html) {
        if (!StringUtils.hasText(html)) {
            return "";
        }
        
        // 如果不包含HTML标签，直接返回原字符串
        if (!containsHtml(html)) {
            return html;
        }
        return Pattern.compile("<[^>]*>").matcher(html)
                     .replaceAll("")
                     .replaceAll("&nbsp;", " ")
                     .trim();
    }
    
    /**
     * 检查字符串是否包含HTML标签
     * 
     * @param text 待检查文本
     * @return 是否包含HTML标签
     */
    public static boolean containsHtml(String text) {
        if (!StringUtils.hasText(text)) {
            return false;
        }
        return Pattern.compile("<[^>]*>").matcher(text).find();
    }
}
