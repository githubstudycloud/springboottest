private void processVersion(String rootNode, String version) {
    try {
        List<String> allUris = getAllUrisForVersion(version);
        int batchSize = 200;
        final CountDownLatch latch = new CountDownLatch(
            (allUris.size() + batchSize - 1) / batchSize);

        // 分批并提交到线程池
        for (int i = 0; i < allUris.size(); i += batchSize) {
            final int start = i;
            final int end = Math.min(start + batchSize, allUris.size());
            List<String> batch = allUris.subList(start, end);
            
            executorService.execute(() -> {
                try {
                    processBatch(rootNode, version, batch);
                } catch (Exception e) {
                    log.error("Error processing batch for version: {}", version, e);
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // 等待所有批次处理完成
        if (!latch.await(1, TimeUnit.HOURS)) {
            log.warn("Processing timeout for version: {}", version);
        }
            
        log.info("Completed processing version: {}, processed URI count: {}", 
                version, allUris.size());
    } catch (Exception e) {
        log.error("Error processing version: {}", version, e);
        throw new RuntimeException("Version processing failed", e);
    }
}

private void processBatch(String rootNode, String version, List<String> uriBatch) {
    List<UriEntity> entities = new ArrayList<>(uriBatch.size());
    List<UriEntity> borrowedEntities = new ArrayList<>(uriBatch.size());
    
    try {
        // 获取URI详情
        List<Map<String, Object>> details = retryWithBackoff(() -> httpService.getUriDetails(uriBatch));
        
        // 使用对象池获取实体对象并处理
        for (Map<String, Object> detail : details) {
            UriEntity entity = null;
            try {
                entity = entityPool.borrowObject();
                borrowedEntities.add(entity);
                fillEntity(entity, rootNode, version, detail);
                entities.add(entity);
            } catch (Exception e) {
                log.error("Error processing detail: {}", detail, e);
                if (entity != null) {
                    try {
                        entityPool.returnObject(entity);
                        borrowedEntities.remove(entity);
                    } catch (Exception ex) {
                        log.error("Error returning object to pool", ex);
                    }
                }
            }
        }
        
        // 批量保存
        if (!entities.isEmpty()) {
            repository.saveAll(entities);
        }
        
    } catch (Exception e) {
        log.error("Error processing URI batch of size {}", uriBatch.size(), e);
        throw new RuntimeException("Batch processing failed", e);
    } finally {
        // 确保所有借出的对象都返回池中
        borrowedEntities.forEach(entity -> {
            try {
                entityPool.returnObject(entity);
            } catch (Exception e) {
                log.error("Error returning object to pool", e);
            }
        });
    }
}