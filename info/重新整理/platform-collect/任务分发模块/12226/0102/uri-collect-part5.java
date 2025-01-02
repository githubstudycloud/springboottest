// service/impl/UriCollectServiceImpl.java
package com.study.collect.service.impl;

import com.google.common.collect.Lists;
import com.study.collect.api.response.PageResponse;
import com.study.collect.api.response.VersionResponse;
import com.study.collect.core.constant.VersionType;
import com.study.collect.domain.entity.UriEntity;
import com.study.collect.domain.param.CollectParam;
import com.study.collect.domain.param.PageParam;
import com.study.collect.repository.UriRepository;
import com.study.collect.service.UriCollectService;
import com.study.collect.service.http.UriHttpService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.ObjectPool;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.util.concurrent.ExecutorService;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UriCollectServiceImpl implements UriCollectService {
    private final UriHttpService httpService;
    private final UriRepository repository;
    private final ObjectPool<UriEntity> entityPool;
    private final ExecutorService executorService;
    
    private static final int BATCH_SIZE = 200;
    private static final int PAGE_SIZE = 200;
    
    public UriCollectServiceImpl(
            UriHttpService httpService,
            UriRepository repository,
            ObjectPool<UriEntity> entityPool,
            @Qualifier("collectExecutor") ExecutorService executorService) {
        this.httpService = httpService;
        this.