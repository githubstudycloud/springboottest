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
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class SchedulerConfig {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerConfig.class);
    private final ApplicationContext applicationContext;
    @Autowired
    private DataSource dataSource;

    public SchedulerConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public Properties quartzProperties() {
        logger.info("Configuring Quartz properties...");
        Properties properties = new Properties();

        // 调度器属性
        properties.put("org.quartz.scheduler.instanceName", "QuartzScheduler");
        properties.put("org.quartz.scheduler.instanceId", "AUTO");
        properties.put("org.quartz.scheduler.makeSchedulerThreadDaemon", "true");

        // 线程池属性
        properties.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        properties.put("org.quartz.threadPool.threadCount", "10");
        properties.put("org.quartz.threadPool.threadPriority", "5");
        properties.put("org.quartz.threadPool.makeThreadsDaemons", "true");

        // JobStore属性
//        properties.put("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
        properties.put("org.quartz.jobStore.class", "org.springframework.scheduling.quartz.LocalDataSourceJobStore");
        properties.put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
        properties.put("org.quartz.jobStore.tablePrefix", "QRTZ_");
        properties.put("org.quartz.jobStore.isClustered", "true");
        properties.put("org.quartz.jobStore.clusterCheckinInterval", "20000");
        properties.put("org.quartz.jobStore.misfireThreshold", "60000");
        properties.put("org.quartz.jobStore.useProperties", "false");

        // 集群配置
        properties.put("org.quartz.scheduler.rmi.export", "false");
        properties.put("org.quartz.scheduler.rmi.proxy", "false");

        logger.info("Quartz properties configured successfully");
        return properties;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        logger.info("Initializing Quartz SchedulerFactoryBean...");

        SchedulerFactoryBean factory = new SchedulerFactoryBean();

        try {
            // 设置数据源
            factory.setDataSource(dataSource);
            logger.info("DataSource set successfully");

            // 设置Quartz属性
            factory.setQuartzProperties(quartzProperties());
            logger.info("Quartz properties set successfully");

            // 设置JobFactory
            CustomJobFactory jobFactory = new CustomJobFactory();
            jobFactory.setApplicationContext(applicationContext);
            factory.setJobFactory(jobFactory);
            logger.info("JobFactory set successfully");

            // 其他配置
            factory.setOverwriteExistingJobs(true);
            factory.setStartupDelay(10);
            factory.setAutoStartup(true);
            factory.setWaitForJobsToCompleteOnShutdown(true);

            // 事务配置
            factory.setTransactionManager(null); // 使用默认的事务管理

            logger.info("SchedulerFactoryBean initialized successfully");

        } catch (Exception e) {
            logger.error("Error initializing SchedulerFactoryBean", e);
            throw new RuntimeException("Failed to initialize SchedulerFactoryBean", e);
        }

        return factory;
    }

    @Bean
    public Scheduler scheduler(SchedulerFactoryBean factory, SchedulerJobListener jobListener)
            throws SchedulerException {
        logger.info("Creating Quartz Scheduler...");
        Scheduler scheduler = factory.getScheduler();
        scheduler.getListenerManager().addJobListener(jobListener);
        logger.info("Scheduler created and configured with JobListener");
        return scheduler;
    }
}

class CustomJobFactory extends SpringBeanJobFactory implements ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(CustomJobFactory.class);
    private AutowireCapableBeanFactory beanFactory;

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