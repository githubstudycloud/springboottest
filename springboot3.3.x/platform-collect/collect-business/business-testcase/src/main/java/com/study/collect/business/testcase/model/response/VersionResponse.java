package com.study.collect.business.testcase.model.response;


import lombok.Data;

//@Data
//@SuperBuilder
//@EqualsAndHashCode(callSuper = true)
//public class VersionResponse extends BaseResponse {
//    private String version;
//    private String versionType;
//    private String description;
//}
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class VersionResponse {
    private String code;            // 响应码
    private String message;         // 响应消息
    private String version;         // 版本号
    private String versionType;     // 版本类型 (TRUNK/BRANCH)
    private String description;     // 版本描述
    private LocalDateTime createTime;  // 创建时间
    private LocalDateTime updateTime;  // 更新时间
    private String status;          // 版本状态
    private Integer sort;           // 排序号

    public static VersionResponseBuilder builder() {
        return new VersionResponseBuilder();
    }

    public static class VersionResponseBuilder {
        private String code;
        private String message;
        private String version;
        private String versionType;
        private String description;
        private LocalDateTime createTime;
        private LocalDateTime updateTime;
        private String status;
        private Integer sort;

        VersionResponseBuilder() {
        }

        public VersionResponseBuilder code(String code) {
            this.code = code;
            return this;
        }

        public VersionResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public VersionResponseBuilder version(String version) {
            this.version = version;
            return this;
        }

        public VersionResponseBuilder versionType(String versionType) {
            this.versionType = versionType;
            return this;
        }

        public VersionResponseBuilder description(String description) {
            this.description = description;
            return this;
        }

        public VersionResponseBuilder createTime(LocalDateTime createTime) {
            this.createTime = createTime;
            return this;
        }

        public VersionResponseBuilder updateTime(LocalDateTime updateTime) {
            this.updateTime = updateTime;
            return this;
        }

        public VersionResponseBuilder status(String status) {
            this.status = status;
            return this;
        }

        public VersionResponseBuilder sort(Integer sort) {
            this.sort = sort;
            return this;
        }

        public VersionResponse build() {
            VersionResponse response = new VersionResponse();
            response.setCode(code);
            response.setMessage(message);
            response.setVersion(version);
            response.setVersionType(versionType);
            response.setDescription(description);
            response.setCreateTime(createTime);
            response.setUpdateTime(updateTime);
            response.setStatus(status);
            response.setSort(sort);
            return response;
        }

        public String toString() {
            return "VersionResponse.VersionResponseBuilder(code=" + this.code +
                    ", message=" + this.message +
                    ", version=" + this.version +
                    ", versionType=" + this.versionType +
                    ", description=" + this.description +
                    ", createTime=" + this.createTime +
                    ", updateTime=" + this.updateTime +
                    ", status=" + this.status +
                    ", sort=" + this.sort + ")";
        }
    }
}