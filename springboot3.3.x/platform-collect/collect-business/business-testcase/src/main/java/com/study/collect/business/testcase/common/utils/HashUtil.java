package com.study.collect.business.testcase.common.utils;

import org.apache.commons.codec.digest.DigestUtils;

//
public class HashUtil {
    public static String hash(String input) {
        return DigestUtils.sha256Hex(input);
    }
}
