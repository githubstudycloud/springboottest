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

    private final ApplicationContext applicationContext;

    public SchedulerConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
        logger.info("Initializing Quartz SchedulerFactoryBean...");

        SchedulerFactoryBean factory = new SchedulerFactoryBean();

        // 设置数据源
        factory.setDataSource(dataSource);

        // 设置Quartz属性
        Properties properties = quartzProperties();
        factory.setQuartzProperties(properties);

        // 设置JobFactory
        CustomJobFactory jobFactory = new CustomJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        factory.setJobFactory(jobFactory);

        // 其他配置
        factory.setOverwriteExistingJobs(true);
        factory.setStartupDelay(10); // 延迟10秒启动
        factory.setAutoStartup(true);
        factory.setWaitForJobsToCompleteOnShutdown(true);

        logger.info("SchedulerFactoryBean initialized successfully");
        return factory;
    }

    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        Properties properties = propertiesFactoryBean.getObject();

        // 添加必要的属性
        if (properties != null) {
            properties.put("org.quartz.scheduler.instanceId", "AUTO");
            properties.put("org.quartz.scheduler.instanceName", "QuartzScheduler");
            properties.put("org.quartz.jobStore.isClustered", "true");
            properties.put("org.quartz.jobStore.clusterCheckinInterval", "20000");
            properties.put("org.quartz.jobStore.misfireThreshold", "60000");

            logger.info("Quartz properties configured successfully");
        }

        return properties;
    }

    @Bean
    public Scheduler scheduler(SchedulerFactoryBean factory, SchedulerJobListener jobListener)
            throws SchedulerException {
        Scheduler scheduler = factory.getScheduler();
        scheduler.getListenerManager().addJobListener(jobListener);
        logger.info("Scheduler configured with JobListener");
        return scheduler;
    }
}

class CustomJobFactory extends SpringBeanJobFactory implements ApplicationContextAware {
    private AutowireCapableBeanFactory beanFactory;
    private static final Logger logger = LoggerFactory.getLogger(CustomJobFactory.class);

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        beanFactory = context.getAutowireCapableBeanFactory();
    }

    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        Object job = super.createJobInstance(bundle);
        beanFactory.autowireBean(job);
        logger.info("Created job instance: {}", job.getClass().getName());
        return job;
    }
}