package com.study.collect.business.enterprise.processor;

import com.study.collect.business.enterprise.model.Enterprise;
import com.study.collect.core.processor.AbstractProcessor;
import com.study.collect.core.processor.annotation.Processor;
import com.study.collect.core.processor.model.ProcessContext;
import org.springframework.stereotype.Component;

@Processor(type = "enterprise", order = 100)
@Component
public class EnterpriseProcessor extends AbstractProcessor<Enterprise> {

    @Override
    protected Enterprise doProcess(Enterprise data, ProcessContext context) {
        // 简单的数据处理
        if (data != null) {
            // 处理电话格式
            if (data.getPhone() != null) {
                data.setPhone(formatPhone(data.getPhone()));
            }
            // 处理地址格式
            if (data.getAddress() != null) {
                data.setAddress(formatAddress(data.getAddress()));
            }
        }
        return data;
    }

    @Override
    public String getType() {
        return "enterprise";
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private String formatPhone(String phone) {
        // 电话号码格式化逻辑
        return phone.replaceAll("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3");
    }

    private String formatAddress(String address) {
        // 地址格式化逻辑
        return address.trim().replaceAll("\\s+", " ");
    }


}
