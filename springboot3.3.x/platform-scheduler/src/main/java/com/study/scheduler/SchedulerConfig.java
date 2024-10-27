package com.study.scheduler;


import com.study.scheduler.listener.SchedulerJobListener;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.quartz.spi.TriggerFiredBundle;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Configuration
public class SchedulerConfig {

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource) throws IOException {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setQuartzProperties(quartzProperties());
        factory.setJobFactory(new CustomJobFactory());
        factory.setOverwriteExistingJobs(true);
        factory.setStartupDelay(10); // 延迟10秒启动
        return factory;
    }

    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

    @Bean
    public Scheduler scheduler(SchedulerFactoryBean factory, SchedulerJobListener jobListener)
            throws SchedulerException {
        Scheduler scheduler = factory.getScheduler();
        scheduler.getListenerManager().addJobListener(jobListener);
        return scheduler;
    }
}

@Configuration
class RedissonConfig {
    @Bean
    public RedissonClient redisson() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://localhost:6379");
        return Redisson.create(config);
    }
}

class CustomJobFactory extends SpringBeanJobFactory implements ApplicationContextAware {
    private AutowireCapableBeanFactory beanFactory;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        beanFactory = context.getAutowireCapableBeanFactory();
    }

    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        Object job = super.createJobInstance(bundle);
        beanFactory.autowireBean(job);
        return job;
    }
}
