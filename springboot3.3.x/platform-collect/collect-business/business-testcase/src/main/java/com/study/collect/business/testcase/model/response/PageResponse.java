package com.study.collect.business.testcase.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
//
//
// PageResponse.java
@Data
@NoArgsConstructor
public class PageResponse<T> {
    private String code;
    private String message;
    private Long total;
    private List<T> items;

    public static <T> PageResponseBuilder<T> builder() {
        return new PageResponseBuilder<>();
    }

    public static class PageResponseBuilder<T> {
        private String code;
        private String message;
        private Long total;
        private List<T> items;

        PageResponseBuilder() {
        }

        public PageResponseBuilder<T> code(String code) {
            this.code = code;
            return this;
        }

        public PageResponseBuilder<T> message(String message) {
            this.message = message;
            return this;
        }

        public PageResponseBuilder<T> total(Long total) {
            this.total = total;
            return this;
        }

        public PageResponseBuilder<T> items(List<T> items) {
            this.items = items;
            return this;
        }

        public PageResponse<T> build() {
            PageResponse<T> response = new PageResponse<>();
            response.setCode(code);
            response.setMessage(message);
            response.setTotal(total);
            response.setItems(items);
            return response;
        }
    }
}