package com.study.collect.business.testcase.model.response;

import java.io.IOException;

public interface HttpResponseParser<T> {
    T parse(String response) throws IOException;
}
