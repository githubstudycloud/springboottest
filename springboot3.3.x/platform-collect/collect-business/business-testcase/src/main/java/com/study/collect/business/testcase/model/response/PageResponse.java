package com.study.collect.business.testcase.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private String code;
    private String message;
    private Long total;
    private List<T> items;
    private int page;
    private int size;
    private int totalPages;

    public static <T> PageResponseBuilder<T> builder() {
        return new PageResponseBuilder<>();
    }

    public static class PageResponseBuilder<T> {
        private String code;
        private String message;
        private Long total;
        private List<T> items;
        private int page;
        private int size;
        private int totalPages;

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

        public PageResponseBuilder<T> page(int page) {
            this.page = page;
            return this;
        }

        public PageResponseBuilder<T> size(int size) {
            this.size = size;
            return this;
        }

        public PageResponseBuilder<T> totalPages(int totalPages) {
            this.totalPages = totalPages;
            return this;
        }

        public PageResponse<T> build() {
            return new PageResponse<>(code, message, total, items, page, size, totalPages);
        }
    }
}