package com.study.collect.annotation;


import com.study.collect.enums.CollectorType;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CollectorFor {
    CollectorType value();
}