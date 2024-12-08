package com.study.collect.annotation;


import java.lang.annotation.*;
import com.study.collect.enums.CollectorType;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CollectorFor {
    CollectorType value();
}
