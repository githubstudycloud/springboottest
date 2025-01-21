package com.study.collect.business.testcase.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse {
    private String code;
    private String message;
    private String status;

    public static BaseResponseBuilder builder() {
        return new BaseResponseBuilder();
    }

    public static class BaseResponseBuilder {
        protected String code;
        protected String message;
        protected String status;

        public BaseResponseBuilder code(String code) {
            this.code = code;
            return this;
        }

        public BaseResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public BaseResponseBuilder status(String status) {
            this.status = status;
            return this;
        }

        public BaseResponse build() {
            return new BaseResponse(code, message, status);
        }
    }
}