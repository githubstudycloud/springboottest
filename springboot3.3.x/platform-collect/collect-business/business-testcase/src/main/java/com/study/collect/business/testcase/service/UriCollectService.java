package com.study.collect.business.testcase.service;



import com.study.collect.business.testcase.entity.UriEntity;
import com.study.collect.business.testcase.model.param.CollectParam;

import java.util.List;

public interface UriCollectService {
    void collectData(CollectParam param);
    List<UriEntity> queryUri(String rootNode, String version, String versionType);
}