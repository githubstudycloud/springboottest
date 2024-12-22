
package com.study.collect.core.processor;

public interface IProcessor<T> {
    /**
     * 处理数据
     */
    T process(T data);

    /**
     * 获取处理器类型
     */
    String getType();

    /**
     * 获取处理顺序
     */
    int getOrder();
}
