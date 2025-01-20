package com.study.collect.business.testcase.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UriQueryCondition {
    private String rootNode;
    private String version;
    private LocalDateTime thirdPartyUpdateTimeStart;
    private LocalDateTime thirdPartyUpdateTimeEnd;
    private Boolean isDeleted;
    private boolean onlyDetail;

    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int size = 20;

    public Pageable getPageable() {
        return PageRequest.of(page - 1, size);
    }
}