package com.study.scheduler;

import com.study.scheduler.listener.SchedulerJobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.TriggerFiredBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
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

    private static final Logger logger = LoggerFactory.getLogger(SchedulerConfig.class);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
        logger.info("Initializing Quartz Scheduler...");
        SchedulerFactoryBean factory = new SchedulerFactoryBean();

        // 设置数据源
        factory.setDataSource(dataSource);
        logger.info("Configured Quartz DataSource");

        // 设置Quartz属性
        factory.setQuartzProperties(quartzProperties());
        logger.info("Configured Quartz Properties");

        // 设置JobFactory
        CustomJobFactory jobFactory = new CustomJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        factory.setJobFactory(jobFactory);
        logger.info("Configured Custom JobFactory");

        // 其他配置
        factory.setOverwriteExistingJobs(true);
        factory.setStartupDelay(10); // 延迟10秒启动

        logger.info("Quartz SchedulerFactoryBean initialized successfully");
        return factory;
    }

    @Bean
    public Properties quartzProperties() throws IOException {
        logger.info("Loading Quartz properties...");
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        Properties properties = propertiesFactoryBean.getObject();

        // 打印关键配置信息
        if (properties != null) {
            logger.info("Quartz Configuration:");
            logger.info("  Instance Name: {}", properties.getProperty("org.quartz.scheduler.instanceName"));
            logger.info("  Instance ID: {}", properties.getProperty("org.quartz.scheduler.instanceId"));
            logger.info("  Thread Count: {}", properties.getProperty("org.quartz.threadPool.threadCount"));
            logger.info("  Job Store: {}", properties.getProperty("org.quartz.jobStore.class"));
            logger.info("  Clustered: {}", properties.getProperty("org.quartz.jobStore.isClustered"));
        }

        return properties;
    }

    @Bean
    public Scheduler scheduler(SchedulerFactoryBean factory, SchedulerJobListener jobListener)
            throws SchedulerException {
        logger.info("Initializing Quartz Scheduler with JobListener...");
        Scheduler scheduler = factory.getScheduler();
        scheduler.getListenerManager().addJobListener(jobListener);
        logger.info("JobListener added to Scheduler");
        return scheduler;
    }
}

class CustomJobFactory extends SpringBeanJobFactory implements ApplicationContextAware {
    private AutowireCapableBeanFactory beanFactory;
    private static final Logger logger = LoggerFactory.getLogger(CustomJobFactory.class);

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        beanFactory = context.getAutowireCapableBeanFactory();
        logger.info("CustomJobFactory initialized with ApplicationContext");
    }

    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        Object job = super.createJobInstance(bundle);
        beanFactory.autowireBean(job);
        logger.info("Created and autowired job instance: {}", job.getClass().getSimpleName());
        return job;
    }
}