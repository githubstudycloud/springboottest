package com.study.collect.business.testcase.model.response.parse;

import java.io.IOException;

public interface HttpResponseParser<T> {
    T parse(String response) throws IOException;
}
