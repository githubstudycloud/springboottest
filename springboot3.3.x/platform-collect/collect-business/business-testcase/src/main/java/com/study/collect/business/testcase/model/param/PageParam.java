package com.study.collect.business.testcase.model.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageParam {
    @Min(value = 1, message = "page must be greater than 0")
    private int page = 1;

    @Min(value = 1, message = "size must be greater than 0")
    @Max(value = 1000, message = "size must be less than 1000")
    private int size = 20;
}